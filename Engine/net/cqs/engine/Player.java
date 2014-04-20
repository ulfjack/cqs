package net.cqs.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;

import net.cqs.auth.Identity;
import net.cqs.config.Constants;
import net.cqs.config.EducationEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.ErrorState;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.MoneyReason;
import net.cqs.config.ResearchEnum;
import net.cqs.config.Sex;
import net.cqs.config.units.Unit;
import net.cqs.engine.ai.AIPlayerController;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.base.AttributeMap;
import net.cqs.engine.base.DependencyList;
import net.cqs.engine.base.Event;
import net.cqs.engine.base.EventHandle;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitStats;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.Contract;
import net.cqs.engine.diplomacy.ContractParty;
import net.cqs.engine.diplomacy.DiplomaticStatus;
import net.cqs.engine.diplomacy.Rank;
import net.cqs.engine.diplomacy.WaitContract;
import net.cqs.engine.human.HumanPlayerController;
import net.cqs.engine.messages.PlayerLogMessage;
import net.cqs.engine.scores.PlayerScore;
import net.cqs.engine.scores.ScoreManager;
import net.cqs.engine.scores.Scoreable;
import net.cqs.storage.Context;
import net.cqs.util.EnumIntMap;
import net.cqs.util.EnumQueue;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class Player implements ContractParty, Scoreable<Player>, Serializable, ObjectInputValidation
{

	private static class ResearchEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Player player;
		public ResearchEvent(Player player)
		{ this.player = player; }
		@Override
		public void activate(long time)
		{ player.completeResearch(time); }
		@Override
		public boolean check(Object o)
		{ return o == player; }
	}
	
	private static class UpkeepEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Player player;
		public UpkeepEvent(Player player)
		{ this.player = player; }
		@Override
		public void activate(long time)
		{ player.collectMoney(time); }
		@Override
		public boolean check(Object o)
		{ return o == player; }
	}

	private static class ContractEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Player player;
		public ContractEvent(Player player)
		{ this.player = player; }
		@Override
		public void activate(long time)
		{ player.cleanContracts(time); }
		@Override
		public boolean check(Object o)
		{ return o == player; }
	}
	
	private static class EventData<T extends Enum<T>> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		public EventHandle nextEventHandle;
		public long nextEventTime;
		public int data;
		public T token;
	}

private static final long serialVersionUID = 2L;

public static final Random RAND = new Random();

public static final int MAX_EVENTS = 10;
public static final int EVENT_LIFETIME = 5*60*1000;

private static long getMidRandom(long mid, long range)
{ return (long) (mid+2*(RAND.nextDouble()-0.5d)*range); }


private final Galaxy galaxy;
private final int pid;
private final PlayerController controller;
private final Identity primaryIdentity;
private String name;
private Sex sex = Sex.UNKNOWN;
private String[] desktops;
private int defaultDesktop = 0;
private Locale locale;

private AttributeMap attributes = new AttributeMap();

private transient PlayerScore score;

private long startTime;
private boolean invisible = false;
private boolean restricted = false;
private boolean autoStopUnits = true;
private boolean punish = false;

private int fleetID = 0;
private long sol = 0;
private long solSpent = 0;

private long points = 0;
private long pointAverage = 0;
private long alliPoints = 0;

//private long currentUnits = 0; // not calculated yet
//private long destroyedUnits = 0; // not calculated yet
private long unitPoints = 0;

private long resPoints  = 0;
private long resWasted  = 0; // Colony lost res
private long resLost    = 0; // Fleet lost res
private long resDonated = 0;
private long resGotten  = 0;

private long invDone   = 0;
private long invBurned = 0;
private long invBeen   = 0;

private EnumIntMap<ResearchEnum> research = EnumIntMap.of(ResearchEnum.class);
private EventData<ResearchEnum> researchEvent = new EventData<ResearchEnum>();

private int researchAmount;

private EventHandle nextUpkeepEventHandle;
private EventHandle nextContractEventHandle;

private long lastUpkeepTime;
private long lastUpkeep;
private long lastIncome;
private long lastTotal;

private transient GalaxyView galaxyView = new GalaxyView();
private transient Alliance alliance;
private Rank rank = Rank.NONE;
private int allianceVote = -1;
private long lastAllianceChangeTime;
private long allianceReadTime = 0;

private final List<Contract> contracts = new ArrayList<Contract>();
private final List<Colony> colonies = new ArrayList<Colony>();
private final List<Fleet> fleets = new ArrayList<Fleet>();
private final List<Battle> battles = new ArrayList<Battle>();
private final List<Agent> agents = new ArrayList<Agent>();

private PlayerLogMessage[] events;
private int nextEventPosition;

Player(Galaxy galaxy, long time, int pid, PlayerDescriptor desc)
{
	this.galaxy = galaxy;
	this.startTime = time;
	this.pid = pid;
	
	this.primaryIdentity = desc.id;
	this.name = desc.name;
	this.locale = desc.locale;
	
	this.lastUpkeepTime = time;
	this.lastAllianceChangeTime = -1000000;
	
	this.controller = desc.createPlayerController(this);
	initEvents();
//	collectMoney(time);
	nextUpkeepEventHandle = galaxy.addEvent(time+Constants.PLAYER_UPKEEP_TIME, new UpkeepEvent(this));
}

private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
{
	in.defaultReadObject();
	in.registerValidation(this, 40);
}

@Override
public void validateObject()
{
	for (int i = 0; i < colonies.size(); i++)
		colonies.get(i).owner = this;
	
	for (int i = 0; i < fleets.size(); i++)
		fleets.get(i).owner = this;
}

public void check() throws InvalidDatabaseException
{
	for (Battle b : battles)
		if (!b.isInBattle(this))
			throw new InvalidDatabaseException(this, "is not in Battle \""+b+"\"");
	
	for (Fleet f : fleets)
		f.check();
}

public Identity getPrimaryIdentity()
{ return primaryIdentity; }

public AccessLevel getAccessLevel(Identity identity)
{ return galaxy.getAccessLevel(identity, this); }

@Override
public PlayerScore getScore()
{ return score; }

@Override
public PlayerScore removeScore()
{
	PlayerScore result = score;
	score = null;
	return result;
}

@Override
public PlayerScore newScore(ScoreManager<Player> sm)
{ 
	score = new PlayerScore(sm, this);
	return score;
}

public Galaxy getGalaxy()
{ return galaxy; }

public int getPid()
{ return pid; }

@Override
public String getName()
{ return name; }

public void setName(String name)
{ this.name = name; }

public Sex getSex()
{ return sex; }

public void setSex(Sex sex)
{ this.sex = sex; }

public PlayerController getController()
{ return controller; }

public boolean advancedCSS()
{ return attributes.get(Attribute.ADVANCED_CSS).booleanValue(); }

public String buildingPreset()
{ return attributes.get(Attribute.BUILDING_PRESET); }

public String unitPreset()
{ return attributes.get(Attribute.UNIT_PRESET); }

public void setInvisible(boolean invisible)
{ this.invisible = invisible; }

public boolean isInvisible()
{ return invisible; }

public void setRestricted(boolean restricted)
{ this.restricted = restricted; }

public boolean isRestricted()
{ return restricted; }

public void checkRestricted(Identity identity, String operation)
{
	if (isRestricted() || (getAccessLevel(identity) != AccessLevel.FULL))
		throw new ErrorCodeException(operation, ErrorCode.RESTRICTED_ACCESS);
}

public void setAutoStopUnits(boolean autoStopUnits)
{ this.autoStopUnits = autoStopUnits; }

public boolean isAutoStopUnits()
{ return autoStopUnits; }

public long getPoints()
{ return points; }

public long getPointAverage()
{ return pointAverage; }

public long getAlliPoints()
{ return alliPoints; }

public void increaseUnitPoints(long p)
{ unitPoints += p; }

public long getUnitPoints()
{ return unitPoints; }

public long getResourcePoints()
{ return resPoints; }

public long getResourcesWasted()
{ return resWasted; }

public void increaseResourcesLost(long amount)
{ resLost = amount; }

public long getResourcesLost()
{ return resLost; }

public void increaseResourcesDonated(long amount)
{ resDonated += amount; }

public long getResourcesDonated()
{ return resDonated; }

public void increaseResourcesGotten(long amount)
{ resGotten += amount; }

public long getResourcesGotten()
{ return resGotten; }

public void increaseInvasionDone()
{ invDone += 1; }

public long getInvasionDone()
{ return invDone; }

public void increaseInvasionBurned()
{ invBurned += 1; }

public long getInvasionBurned()
{ return invBurned; }

public void increaseInvasionBeen()
{ invBeen += 1; }

public long getInvasionBeen()
{ return invBeen; }

public void setGalaxyView(GalaxyView galaxyView)
{ this.galaxyView = galaxyView; }

public GalaxyView getGalaxyView()
{ return galaxyView; }

public int getUsedBandwidth()
{
	int used = getAttr(Attribute.QUOTA_USED).intValue();
	used += getAttr(Attribute.GP_QUOTA_USED).intValue();
	return used/1024;
}

public int getTotalBandwidth()
{ return getAttr(Attribute.QUOTA).intValue(); }

public boolean isMulti()
{ return getAttr(Attribute.IS_MULTI).booleanValue(); }

public boolean mayLogin()
{ return getAttr(Attribute.LOGIN_ALLOWED).booleanValue(); }

public boolean showAds()
{ return getAttr(Attribute.SHOW_ADS).booleanValue(); }

public boolean isAutoReturn()
{ return getAttr(Attribute.AUTO_RETURN).booleanValue(); }

public boolean isAutoStation()
{ return getAttr(Attribute.AUTO_STATION).booleanValue(); }

public boolean isForwardEmail()
{ return getAttr(Attribute.FORWARD_EMAIL).booleanValue(); }

public String getGraphicPath()
{ return getAttr(Attribute.GRAPHIC_PATH); }

public boolean isCssInGp()
{ return getAttr(Attribute.CSS_IN_GP).booleanValue(); }

public boolean hasLogo()
{ return !punish && (getAttr(Attribute.PLAYER_LOGO).length() != 0); }

public String getLogo()
{ return getAttr(Attribute.PLAYER_LOGO); }

public String getDescription()
{ return Galaxy.defaultTextConverter.convert(getPlaintext()); }

public String getPlaintext()
{ return getAttr(Attribute.PLAYER_PLAINTEXT); }

public Alliance getAlliance()
{ return alliance; }

public void setAlliance(Alliance alliance)
{ this.alliance = alliance; }

public boolean isAllianceAdmin()
{ return (alliance != null) && (rank == alliance.getRanks().get(0)); }

public Rank getRank()
{ return rank; }

public void setRank(Rank rank)
{ this.rank = rank; }

public int getAllianceVote()
{ return allianceVote; }

public void setAllianceVote(int allianceVote)
{ this.allianceVote = allianceVote; }

public long getNextAllianceChangeTime()
{ return Constants.ALLIANCE_SWITCH_TIME-getGalaxy().getTime()+lastAllianceChangeTime; }

public long getLastAllianceChangeTime()
{ return lastAllianceChangeTime; }

public void setAllianceReadTime(long allianceReadTime)
{ this.allianceReadTime = allianceReadTime; }

public long getAllianceReadTime()
{ return allianceReadTime; }

private void notifyAllianceChange(Alliance a)
{
	galaxy.getDiplomaticRelation().signalAllianceChange(this, a);
	for (int i = 0; i < battles.size(); i++)
		battles.get(i).notifyAllianceChange(this);
}

public void notifyAllianceJoined(long time)
{
	galaxyView = alliance.getGalaxyView();
	rank = alliance.getStartRank();
	lastAllianceChangeTime = time;
	updateGalaxyView();
	allianceVote = -1;
	notifyAllianceChange(alliance);
}

public void notifyAllianceLeft(Alliance a)
{
	alliance = null;
	galaxyView = new GalaxyView();
	updateGalaxyView();
	notifyAllianceChange(a);
}

public List<Colony> getColonies()
{ return colonies; }

public List<Fleet> getFleets()
{ return fleets; }

public List<Fleet> getMergableFleets()
{
	List<Fleet> res = new ArrayList<Fleet>(fleets);
	Collections.sort(res, new Comparator<Fleet>()
		{
			@Override
      public int compare(Fleet f1, Fleet f2) 
			{ return f1.getPosition().compareTo(f2.getPosition()); }
		});
	for (int i = res.size()-1; i >= 0; i--)
	{
		// Remove all fleets that are not stopped, fighting, or alone at their position.
		if (!res.get(i).isStopped() || res.get(i).isFighting() ||
		    ((i == 0 || !res.get(i).getPosition().equals(res.get(i-1).getPosition())) &&
		    (i == res.size()-1 || !res.get(i).getPosition().equals(res.get(i+1).getPosition()))))
		{
			res.remove(i);
		}
	}
	return res;
}

public Battle getBattle(String battleId)
{
	Battle battle;
	Iterator<Battle> it = battles.iterator();
	while (it.hasNext())
	{
		battle = it.next();
		if (battleId.equals(battle.getId()))
			return battle;
	}
	return null;
}

public List<Battle> getBattles()
{ return battles; }

public Battle[] getSortedBattles()
{
	Battle[] sortedBattles = battles.toArray(new Battle[0]);
	Arrays.sort(sortedBattles,  new Comparator<Battle>()
		{
			@Override
      public int compare(Battle b1, Battle b2) 
			{ return (b1.position().compareTo(b2.position())); }
		});
	return sortedBattles;
}

@Override
public List<Contract> getContracts()
{ return contracts; }

@Override
public void addContract(Contract c)
{ contracts.add(c); }

@Override
public void removeContract(Contract c)
{ contracts.remove(c); }

public UnitStats calculateUnitStats()
{
	UnitMap map = new UnitMap();
	for (Colony c : colonies)
		map.add(c.getUnits());
	return new UnitStats(map);
}

@Override
public boolean isPlayer()
{ return true; }

public boolean isAI()
{ return controller instanceof AIPlayerController; }

public DiplomaticStatus getStatusTowards(ContractParty other)
{ return galaxy.getDiplomaticRelation().getStatus(this, other); }

public boolean isUnderAttack()
{
	for (Colony c : colonies)
	{
		if (c.isUnderAttack())
			return true;
	}
	return false;
}

public List<Agent> getAgents()
{ return agents; }

@Restricted
public void addAgent(Agent agent)
{ agents.add(agent); }

@Restricted
public void removeAgent(Agent agent)
{ agents.remove(agent); }

@Override
public ContractParty getSuperParty()
{ return alliance; }

public void setLocale(Locale locale)
{ this.locale = locale; }

public Locale getLocale()
{ return locale; }

public <T extends Serializable> T getAttr(Attribute<T> key)
{ return attributes.get(key); }

public <T extends Serializable> void setAttr(Attribute<T> key, T value)
{ attributes.set(key, value); }

public void removeAttr(Attribute<?> key)
{ attributes.remove(key); }

public Iterator<Entry<Attribute<?>,Object>> attributeIterator()
{ return attributes.iterator(); }

public int createFleetId()
{ return ++fleetID; }

private String getDefaultDesktopName(Locale loc, int pos)
{ return "Desktop "+pos; }

public String getDesktopName(Locale loc, int pos)
{
	if ((pos < 0) || (pos > Constants.MAX_DESKTOP))
		throw new IllegalArgumentException();
	if ((desktops != null) && (pos < desktops.length) && (desktops[pos] != null))
		return desktops[pos];
	return getDefaultDesktopName(loc, pos);
}

public void setDesktopName(int pos, String name)
{
	if ((pos < 0) || (pos > Constants.MAX_DESKTOP))
		throw new IllegalArgumentException();
	if (desktops == null)
		desktops = new String[Constants.MAX_DESKTOP+1];
	if (pos >= desktops.length)
	{
		String[] temp = new String[Constants.MAX_DESKTOP+1];
		System.arraycopy(desktops, 0, temp, 0, desktops.length);
		desktops = temp;
	}
	desktops[pos] = name;
}

public int getDefaultDesktop()
{ return defaultDesktop; }

public void setDefaultDesktop(int pos)
{
	if ((pos < -2) || (pos > Constants.MAX_DESKTOP))
		throw new IllegalArgumentException();
	defaultDesktop = pos;	
}

public void initEvents()
{
	events = new PlayerLogMessage[MAX_EVENTS];
	nextEventPosition = 0;
}

public void log(String msg)
{ log(new PlayerLogMessage("", msg)); }

public void log(PlayerLogMessage rec)
{
//	Galaxy.logger.fine("Filing Event for "+this+": "+rec);
	if (events == null) initEvents();
	events[nextEventPosition] = rec;
	nextEventPosition = (nextEventPosition+1) % MAX_EVENTS;
}

public void log(ErrorCode code, Object... objects)
{ log(PlayerLogMessage.createInstance(this, code, objects)); }

public int currentEventCount()
{
	long time = System.currentTimeMillis();
	int w = (nextEventPosition-1 + MAX_EVENTS) % MAX_EVENTS; //avoid ne-1=-1
	int count = 0;
	
	while ((events[w] != null) &&
	       (events[w].getRealtime() > (time - EVENT_LIFETIME)))
	{
		w = (w-1+MAX_EVENTS) % MAX_EVENTS;
		if (w == nextEventPosition) break;
		count++;
	}
	
	return (count > 4) ? 4 : count;
}

public boolean getEventSeen(int x)
{
	int w = (nextEventPosition-1 + MAX_EVENTS-x) % MAX_EVENTS; //avoid ne-1=-1
	return events[w].getState() == ErrorState.SEEN_SHOW;
}

public long getEventTime(int x)
{
	int w = (nextEventPosition-1 + MAX_EVENTS-x) % MAX_EVENTS; //avoid ne-1=-1
	return events[w].getRealtime();
}

public PlayerLogMessage getEvent(int x)
{
	int w = (nextEventPosition-1 + MAX_EVENTS-x) % MAX_EVENTS; //avoid ne-1=-1
	
	PlayerLogMessage rec = events[w];
	rec.mark();
	return rec;
}

public Fleet findFleetById(int id)
{
	for (int i = 0; i < fleets.size(); i++)
	{
		Fleet f = fleets.get(i);
		if (f.getId() == id)
			return f;
	}
	return null;
}

public Agent findAgentById(String s)
{
	Iterator<Agent> it = agents.iterator();
	while (it.hasNext())
	{
		Agent a = it.next();
		if (s.equals(a.getId()))
			return a;
	}
	return null;
}

public void disbandAllFleets()
{
	for (int i = fleets.size()-1; i > -1; i--)
		fleets.get(i).removeFleet();
}

public boolean canSeeSystem(Position pos)
{
	for (int i = 0; i < colonies.size(); i++)
	{
		if (colonies.get(i).getPosition().sameSystem(pos))
			return true;
	}
	
	for (int i = 0; i < fleets.size(); i++)
	{
		Position temp = fleets.get(i).getPosition();
		if (temp.specificity() >= Position.SYSTEM)
			if (temp.sameSystem(pos))
				return true;
	}
	
	return false;
}

public long neededPoints()
{ return Constants.neededPoints(colonies.size()); }

public boolean mayColonize()
{ return points >= neededPoints(); }

public boolean maySeeMap(long time)
{ return (Constants.PLAYER_BLIND_TIME < 0) || (time > startTime+Constants.PLAYER_BLIND_TIME); }

public boolean maySeeMap()
{ return maySeeMap(getGalaxy().getTime()); }

// Research
public int getResearchAmount()
{ return researchAmount; }

public int getResearchLevel(ResearchEnum what)
{ return research.get(what); }

public EnumQueue<ResearchEnum> getResearchQueue()
{ return ((HumanPlayerController) controller).getResearchQueue(); }

public boolean mayResearch(ResearchEnum topic)
{ return topic.mayResearchTopic(this); }

public boolean isResearching()
{ return researchEvent.nextEventHandle != null; }

public ResearchEnum getCurrentResearch()
{ return researchEvent.token; }

public long getCurrentResearchTime()
{ return researchEvent.nextEventTime; }

public void increaseResearch(ResearchEnum what)
{
	research.increase(what);
//	Infobox.dropInfo(this, time, Information.keyNewResearchProfessor, what);
}

public void completeResearch(long time)
{
	if (researchEvent.nextEventHandle == null) return;
	researchEvent.nextEventHandle = null;
	
	increaseResearch(researchEvent.token);
	
	controller.finishResearch(this, time);
	controller.nextResearch(this, time);
}

public boolean startResearch(long time, ResearchEnum topic, long duration)
{
	if (researchEvent.nextEventHandle != null)
		throw new IllegalStateException("Already building something!");
	if (topic.mayResearchTopic(this))
	{
		long researchTime = time+duration;
		researchEvent.nextEventHandle = galaxy.addEvent(researchTime, new ResearchEvent(this));
		researchEvent.nextEventTime = researchTime;
		researchEvent.token = topic;
		return true;
	}
	return false;
}

public boolean startResearch(long time, ResearchEnum topic)
{ return startResearch(time, topic, topic.timeNeeded(this)); }


public void resumeResearch()
{
	if (researchEvent.nextEventHandle != null) return;
	controller.nextResearch(this, getGalaxy().getTime());
}

public void abortResearch(long time)
{
	if (researchEvent.nextEventHandle == null)
	{
		log(ErrorCode.RESEARCH_ALREADY_ABORTED);
		return;
	}
	
	galaxy.deleteEvent(researchEvent.nextEventHandle);
	researchEvent.nextEventHandle = null;
	researchEvent.nextEventTime = 0;
	
//	ResearchEnum type = researchEvent.token;
	// TODO: What now?
	
	controller.abortResearch(this, time);
}

public void abortResearch()
{ abortResearch(getGalaxy().getTime()); }

public void updateResourcePoints()
{
	resPoints = 0;
	resWasted = 0;
	for (int i = 0; i < colonies.size(); i++)
	{
		Colony c = colonies.get(i);
		resPoints += c.getPaidResources();
		resWasted += c.getWastedResources();
	}
	
	if (score != null)
		score.signalChange();
	if ((alliance != null) && (alliance.getScore() != null))
		alliance.getScore().signalChange();
}

public void handleUniversity(long time)
{
	int newResearchAmount = 0;
	for (int i = 0; i < colonies.size(); i++)
		newResearchAmount += colonies.get(i).getResearchSpaces();

	if (isResearching() && newResearchAmount != researchAmount)
	{
		ResearchEnum rt = researchEvent.token;
		if (newResearchAmount == 0)
		{
			abortResearch(time);
			// Add entry to top of queue again.
			getResearchQueue().add(rt, 1);
			getResearchQueue().moveTopInverse(0);
		}
		else
		{
			// Calculate the next event time: now + remainingTime * newDur/oldDur.
			double modifier = ((double) rt.timeNeeded(newResearchAmount))
				/ rt.timeNeeded(researchAmount);
			long remainingDuration = (long) Math.ceil(
					(researchEvent.nextEventTime - time) * modifier);
			abortResearch(time);
			startResearch(time, rt, remainingDuration);
		}
	}
	researchAmount = newResearchAmount;
}

public int maxRemoveResearchProfs()
{ return researchAmount; }

public boolean isKnown(DependencyList<ResearchEnum> dependencies)
{ return dependencies.check(research); }

public boolean isKnown(Unit slot)
{ return slot.checkResearchDependencies(research); }

public boolean isKnown(EducationEnum topic)
{
	ResearchEnum dep = topic.getDep();
	if (dep == null) return true;
	return research.get(dep) >= topic.getDepCount();
}

// Money
public long calculateUpkeep()
{
	long sum = 0;
//	sum += research.sum * Colony.PROFESSOR_UPKEEP;
	return sum;
}

public long calculateIncome()
{
	long sum = 0;
	return sum;
}

public long getIncome(long time)
{
	long timeDiff = time-lastUpkeepTime;
	lastIncome = (calculateIncome()*timeDiff)/(60*60);
	return lastIncome;
}

public long getNextUpkeepTime()
{ return nextUpkeepEventHandle.getTime(); }

public long getMoney()
{ return sol; }

public long getLastIncome()
{ return lastIncome; }

public long getLastUpkeep()
{ return lastUpkeep; }

public long getLastTotal()
{ return lastTotal; }

public boolean hasMoney(long money)
{ return sol >= money; }

public boolean hasMoney()
{ return sol >= 0; }

public void getMoney(long time, MoneyReason reason, long money)
{
	if (money == 0) return;
//	Galaxy.logger.fine("remove money: "+money+" for "+reason);
	sol -= money;
	solSpent += money;
	galaxy.logMoney(this, time, reason, sol, -money);
}

public void addMoney(long time, MoneyReason reason, long money)
{
	if (money == 0) return;
//	Galaxy.logger.fine("add money: "+money+" for "+reason);
	sol += money;
	galaxy.logMoney(this, time, reason, sol, money);
}

public void collectMoney(long time)
{
//	Galaxy.logger.fine("Updating money for "+name);
	long nextUpkeepEventTime = time+getMidRandom(Constants.PLAYER_UPKEEP_TIME, Constants.PLAYER_UPKEEP_TIME/4);
	nextUpkeepEventHandle = galaxy.addEvent(nextUpkeepEventTime, new UpkeepEvent(this));
	
	long oldValue = sol;
	
	// global income
	addMoney(time, MoneyReason.PLAYER_INCOME, getIncome(time));
	
	// colony income
	long totalColonyIncome = 0;
	for (int i = 0; i < colonies.size(); i++)
		totalColonyIncome += colonies.get(i).getIncome(time);
	addMoney(time, MoneyReason.COLONY_INCOME, totalColonyIncome);
	
	// global upkeep
	if (!hasMoney())
	{
//		if (research.sum > 0)
//			decreaseRandomResearch(time);
	}
	long timeDiff = time-lastUpkeepTime;
	lastUpkeep = (calculateUpkeep()*timeDiff)/(60*60);
	getMoney(time, MoneyReason.PLAYER_UPKEEP, lastUpkeep);
	
	// colony upkeep
	long colonyTotal = 0;
	for (int i = 0; i < colonies.size(); i++)
	{
		Colony c = colonies.get(i);
		colonyTotal += c.payUpkeep(time);
	}
	getMoney(time, MoneyReason.COLONY_UPKEEP, colonyTotal);
	
	// fleet upkeep
	long fleetTotal = 0;
	for (int i = 0; i < fleets.size(); i++)
	{
		Fleet f = fleets.get(i);
		fleetTotal += f.payUpkeep(time);
	}
	getMoney(time, MoneyReason.FLEET_UPKEEP, fleetTotal);
	
	lastTotal = sol-oldValue;
	lastUpkeepTime = time;
	
	galaxy.logMoney(this, time, MoneyReason.HAVE, sol);
	
	// is this really necessary?
	calculatePoints();
}

public void cleanContracts(long time)
{
	long nextContractEventTime = time+getMidRandom(Constants.PLAYER_CONTRACT_TIME, Constants.PLAYER_CONTRACT_TIME/4);
	nextContractEventHandle = galaxy.addEvent(nextContractEventTime, new ContractEvent(this));
	for (int i = 0; i < contracts.size(); i++)
	{
		Contract contract = contracts.get(i);
		if (contract instanceof WaitContract)
			if (contract.getAttackTime()<time)
				contracts.remove(i);		
	}
}


public boolean alliedWith(Player p)
{
	if (this == p) return true;
	if ((this.alliance == null) || (p.alliance == null)) return false;
	if (this.alliance == p.alliance) return true;
	return false;
}

public boolean hasVotedAlliance()
{ return alliance.getSurvey().getId() == getAttr(Attribute.ALLIANCE_SURVEY).intValue(); }

public void calculatePoints()
{
	points = 0;
	for (int i = 0; i < colonies.size(); i++)
	{
		assert colonies.get(i).getPoints() >= 0 : 
		    "Kolonie " + i + " von " + name + " hat negative Punktzahl!";
		points += colonies.get(i).getPoints();
	}
	
	if (score != null)
		score.signalChange();
	if (alliance != null)
	{
		if (alliance.getScore() == null)
			Galaxy.logger.severe("Player \""+this+"\" has Alliance but no Score!");
		else
			alliance.getScore().signalChange();
	}
}

public void updateGalaxyView(Colony c)
{
	double maxDistance = c.getViewDistance();
	SolarSystem s1 = c.getPosition().findSystem(galaxy);
	Iterator<SolarSystem> it = c.getGalaxy().systemIterator();
	while (it.hasNext())
	{
		SolarSystem s2 = it.next();
		if (s2.isInvisible()) continue;
		double dist = s1.distance(s2);
		if (dist <= maxDistance) galaxyView.addSystem(s2);
	}
}

public void updateGalaxyView()
{
	if (galaxyView == null) galaxyView = new GalaxyView();
	for (int i = 0; i < colonies.size(); i++)
	{
		Colony c = colonies.get(i);
		updateGalaxyView(c);
	}
}

public void addColony(Colony c)
{
	colonies.add(c);
	galaxyView.addSystem(c.getPosition().findSystem(galaxy));
	calculatePoints();
//	points += c.points;
}

public void removeColony(Colony c)
{
	colonies.remove(c);
	calculatePoints();
//	points -= c.points;
}

/*public void removeAllColonies()
{
	Colony[] cs = new Colony[colonies.length()];
	for (int i = 0; i < cs.length; i++)
		cs[i] = colonies.getColony(i);
	
	for (int i = 0; i < cs.length; i++)
		cs[i].removeOwner();
	
	calculatePoints();
}*/

public Colony getColony(Position position)
{
	if (position.specificity() < Position.COLONY) return null;
	for (Colony c : colonies)
		if (position.equals(c.getPosition())) return c;
	throw new IllegalArgumentException();
}

public Colony getColonyOnPlanet(Planet planet)
{
	for (int i = 0; i < colonies.size(); i++)
	{
		Colony c = colonies.get(i);
		if (planet == c.getPlanet()) return c;
	}
	return null;
}

public boolean hasColonyOnPlanet(Planet planet)
{
	for (int i = 0; i < colonies.size(); i++)
	{
		Colony c = colonies.get(i);
		if (planet == c.getPosition().findPlanet(galaxy)) return true;
	}
	return false;
}

public boolean hasColonyOnSystem(Position pos)
{
	if (pos.specificity() < Position.SYSTEM) throw new IllegalArgumentException();
	for (int i = 0; i < colonies.size(); i++)
	{
		Colony c = colonies.get(i);
		if (pos.sameSystem(c.getPosition())) return true;
	}
	return false;
}

public void updateResearch(ResearchEnum topic, int amount)
{
	if (research.get(topic) < amount)
		research.set(topic, amount);
}

public int getSystemState(Position pos)
{
	if (pos.specificity() < Position.SYSTEM) throw new IllegalArgumentException();
	if (hasColonyOnSystem(pos)) return 1;
	return 0;
}

@Override
public int hashCode()
{ return super.hashCode(); }

@Override
public boolean equals(Object o)
{ return o == this; }

@Override
public String toString()
{ return name; }


public static Player getPlayer(int playerId)
{ return Context.getGalaxy().findPlayerByPid(playerId); }

public static Colony getColony(int playerId, Position position)
{ return Context.getGalaxy().findPlayerByPid(playerId).getColony(position); }

public static Fleet getFleet(int playerId, int fleetId)
{ return Context.getGalaxy().findPlayerByPid(playerId).findFleetById(fleetId); }

}
