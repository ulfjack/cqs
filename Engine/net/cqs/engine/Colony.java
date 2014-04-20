package net.cqs.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import de.ofahrt.ulfscript.annotations.Restricted;

import net.cqs.config.BattleStateEnum;
import net.cqs.config.BuildingEnum;
import net.cqs.config.CheckResult;
import net.cqs.config.Constants;
import net.cqs.config.EducationEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.MoneyReason;
import net.cqs.config.PlanetEnum;
import net.cqs.config.QueueEnum;
import net.cqs.config.Resource;
import net.cqs.config.ResourceEnum;
import net.cqs.config.units.EducationModifier;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.actions.ColonyRemoveAction;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.base.AttributeMap;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.Event;
import net.cqs.engine.base.EventHandle;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitQueue;
import net.cqs.engine.base.UnitSelector;
import net.cqs.engine.base.UnitStats;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BattleSide;
import net.cqs.engine.battles.LandingBattle;
import net.cqs.engine.battles.SurfaceBattle;
import net.cqs.engine.colony.ColonyController;
import net.cqs.engine.colony.ColonyEventListener;
import net.cqs.engine.human.HumanColonyController;
import net.cqs.engine.units.UnitEnum;
import net.cqs.util.EnumIntMap;
import net.cqs.util.EnumQueue;
import net.cqs.util.IntIntMap;
import net.cqs.util.JavaRandom;
import net.cqs.util.RandomNumberGenerator;

public final class Colony implements Serializable
{

	private static class EducationEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Colony colony;
		public EducationEvent(Colony colony)
		{ this.colony = colony; }
		@Override
		public void activate(long time)
		{ colony.completeEducation(time); }
		@Override
		public boolean check(Object o)
		{ return o == colony; }
	}
	
	private static class BuildingEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Colony colony;
		public BuildingEvent(Colony colony)
		{ this.colony = colony; }
		@Override
		public void activate(long time)
		{ colony.completeBuilding(time); }
		@Override
		public boolean check(Object o)
		{ return o == colony; }
	}
	
	private static class UnitEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Colony colony;
		private final int queue;
		public UnitEvent(Colony colony, int queue)
		{ this.colony = colony; this.queue = queue; }
		@Override
		public void activate(long time)
		{ colony.completeUnit(time, queue); }
		@Override
		public boolean check(Object o)
		{ return o == colony; }
	}

private static final long serialVersionUID = 1L;

private static final Random rand = new Random();
private static final RandomNumberGenerator rng = new JavaRandom(rand);

public static final int OVERFLOW_CHECK = Integer.MAX_VALUE / 4;

public static final int UNIT_QUEUES = 3;
public static final int GROUND_QUEUE = 0;
public static final int SPACE_QUEUE  = 1;
public static final int WARP_QUEUE   = 2;

private static final int[] BASE_COST = new int[] { 100, 100, 0, 0 };

public static final double LAMBDA_FACTOR = 12*5*1.349e-7;

public static final float SPY_SUCCESS_MAX = 0.7f;

private static long getMidRandom(long mid, long range)
{ return (long) (mid+2*(rng.nextDouble()-0.5d)*range); }

	public static class EventData<T extends Enum<T>> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		public EventHandle nextEventHandle;
		public long nextEventTime;
		public int data;
		public T token;
		public Unit unit;
		public Cost actualCost;
	}


private final Position position;
private final ColonyController controller;
private transient ColonyEventListener colonyLogger;

private final long startTime;

private final Planet planet;

private boolean isInvaded;
private boolean isInhabited;
private boolean isStartColony;

transient Player owner; // is set by Player.validateObject
private String name;
private int points;

private final AttributeMap attributes = new AttributeMap();

private final IntIntMap modifiers;
private final double populationGrowthPlanetaryModifier;

private final ResourceList resources;
private final EnumIntMap<BuildingEnum> buildings;
private final EventData<BuildingEnum> buildingEvent;

private final EnumIntMap<EducationEnum> education;
private final EnumIntMap<EducationEnum> pendingEducation;
private final EventData<EducationEnum> educationEvent;

private final UnitMap units;
private final EventData<?>[] unitEvents = new EventData[UNIT_QUEUES];

private long lastUpkeepTime;
private long lastUpkeep;

private int educationAmount;
private int researchAmount;

private float agentSuccessProbability = SPY_SUCCESS_MAX;
private long agentTime = 0;

private final List<Agent> agents = new ArrayList<Agent>();


private SurfaceBattle battle;
private LandingBattle landingBattle;

Colony(Planet parent, Player who, long time, int colony, boolean isInvasion, boolean isStart)
{
	startTime = time;
	
	Position ppos = parent.getPosition();
	position = new Position(ppos.getSystemNumber(), ppos.getPlanetNumber(), colony);
	colonyLogger = parent.getGalaxy().createColonyLogger(this);
	
	planet = parent;
	isInvaded = isInvasion;
	isInhabited = false;
	isStartColony = isStart;
	
	owner = who;
	name = "Kolonie";
	points = 0;
	
	controller = owner.getController().createColonyController(this);
	
	lastUpkeepTime = time;
	
	resources = new ResourceList(time);
	modifiers = new IntIntMap();
	
	buildings = EnumIntMap.of(BuildingEnum.class);
	education = EnumIntMap.of(EducationEnum.class);
	units     = new UnitMap();
	
	pendingEducation = EnumIntMap.of(EducationEnum.class);
	
	isInhabited = true;
	
	buildingEvent = new EventData<BuildingEnum>();
	for (int i = 0; i < unitEvents.length; i++)
		unitEvents[i] = new EventData<UnitEnum>();
	educationEvent = new EventData<EducationEnum>();
	
	populationGrowthPlanetaryModifier = parent.getPopulationGrowthModifier();
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		modifiers.set(i, parent.getModifier(i));
	modifiers.set(Resource.MONEY, parent.getModifier(Resource.MONEY));
	
	updateAfterEvent();
}

public void check() throws InvalidDatabaseException
{
	if (owner == null)
		throw new InvalidDatabaseException("Colony "+position+" has no owner!");
	if (battle != null) battle.check();
	if (landingBattle != null) landingBattle.check();
	colonyLogger = getGalaxy().createColonyLogger(this);
}

public <T extends Serializable> T getAttr(Attribute<T> key)
{ return attributes.get(key); }

public Position getPosition()
{ return position; }

public ColonyController getController()
{ return controller; }

public long getStartTime()
{ return startTime; }

public void removeSurfaceBattle()
{ battle = null; }

public SurfaceBattle getSurfaceBattle()
{ return battle; }

public SurfaceBattle createSurfaceBattle(long time)
{
	if (battle == null)
	{
		battle = new SurfaceBattle(getGalaxy(), time, this);
		battle.init();
	}
	return battle;
}

public void removeLandingBattle()
{ landingBattle = null; }

public LandingBattle getLandingBattle()
{ return landingBattle; }

public LandingBattle createLandingBattle(long time)
{
	if (landingBattle == null)
	{
		landingBattle = new LandingBattle(getGalaxy(), time, this);
		landingBattle.init();
	}
	return landingBattle;
}

public Player getOwner()
{ return owner; }

public Galaxy getGalaxy()
{ return planet.getGalaxy(); }

public Planet getPlanet()
{ return planet; }

public SolarSystem getSolarSystem()
{ return planet.getSolarSystem(); }

public boolean isInvaded()
{ return isInvaded; }

public boolean isUnderAttack()
{
	if (battle != null)
	{
		BattleSide side = battle.getSide(Battle.ATTACKER_SIDE);
		if ((side != null) && (side.size() > 0))
			return true;
	}
	if (landingBattle != null)
	{
		BattleSide side = landingBattle.getSide(Battle.ATTACKER_SIDE);
		if ((side != null) && (side.size() > 0))
			return true;
	}
	return false;
}

public int getSize()
{ return buildings.getSum(); }

public void setName(String name)
{ this.name = name; }

public String getName()
{ return name; }

public int getPoints()
{ return points; }

public int getBuilding(BuildingEnum building)
{ return buildings.get(building); }

public void notifyRemove()
{
	owner = null;
	battle = null;
	isInhabited = false;
	points = 0;
}

public int getProfessors()
{ return Constants.PROFESSORS_PER_UNIVERSITY*buildings.get(BuildingEnum.UNIVERSITY); }

public int getFreeProfessors()
{ return getProfessors()-educationAmount-researchAmount; }

public int getEducationSpaces()
{ return educationAmount; }

public int maxRemoveEducationProfs()
{ return educationAmount - education.getSum() - pendingEducation.getSum(); }

public int getUsedEducationSpaces()
{ return education.getSum(); }

public void flushEducation()
{
	education.clear();
	pendingEducation.clear();
}

public int getResearchSpaces()
{ return researchAmount; }

public int getEducation(EducationEnum topic)
{ return education.get(topic); }

public long getEducationModifierPercent(EducationEnum topic, int res)
{
	int level = education.get(topic);
	return (long) (100.0*topic.getModifier(res)*EducationModifier.factor(level));
}

public long getConstructionModifierPercent()
{
	double current = getTimeModifier(education.get(EducationEnum.CONSTRUCTION));
	double normal = getTimeModifier(0);
	return Math.round(100*current/normal);
}

public int getPendingEducation(EducationEnum topic)
{ return pendingEducation.get(topic); }

public long getCurrentEducationTime()
{ return educationEvent.nextEventTime; }

public int baseBuildCost(int restyp)
{
	assert (restyp >= Resource.MIN) && (restyp <= Resource.MAX);
	int used = planet.getUsed();
	int size = planet.getSize();
	
	if (isStartColony) used = buildings.getSum();
	
	if (used < size)
	{
		double factor = 1.0d;
		return (int) (factor*BASE_COST[restyp]);
	}
	
	if (used < size*2)
	{
		double ratio = ((1.0d*used) / size)-1.0d;
		double factor = 15.0d*ratio+1.0d;
		return (int) (factor*BASE_COST[restyp]);
	}
	
	double ratio = ((4.0d*used) / size)-7.0d;
	double factor = 16.0d*ratio*ratio;
	return (int) (factor*BASE_COST[restyp]);
}

public double getViewDistance()
{
	int rt = buildings.get(BuildingEnum.RADIO_TELESCOPE);
	return Constants.PLAYER_RT_FACTOR*Math.sqrt(rt);
}

public long getGalaxyViewDistance()
{
	return (long) (getViewDistance() * Constants.GALAXY_DIAMETER);	
}

public int getUnits(Unit unit)
{ return units.get(unit); }

public UnitMap getUnits()
{ return units; }

public UnitMap getUnitsCopy()
{ return new UnitMap(units); }

public UnitIterator getUnitIterator(UnitSelector selector)
{ return units.unitIterator(selector); }

public UnitStats calculateUnitStats()
{ return new UnitStats(units); }

@Restricted
public void silentIncreaseUnits(Unit unit, int amount)
{ units.increase(unit, amount); }

public void addUnits(long time, UnitMap fleetunits)
{
	colonyLogger.increaseUnits(time, fleetunits);
	units.add(fleetunits);
}

public void removeUnits(long time, UnitMap fleetunits)
{
	colonyLogger.decreaseUnits(time, fleetunits);
	units.subtract(fleetunits);
}

// Resources
public Cost rob(long time, Cost cost, long maxAmount)
{ return resources.rob(time, cost, maxAmount); }

public double getEfficiencyApprox()
{ return resources.getEfficiencyApprox(); }

public long getEfficiencyPercent()
{ return Math.round(100*getEfficiencyApprox()); }

public long getPopulationGrowthModifierPercent()
{ return (long) Math.floor(100*resources.getPopulationGrowthTotalModifier()/PlanetEnum.MAX_POPULATION_MODIFIER); }

public long getPopulationGrowth()
{ return (long) Math.floor(resources.getPopulationGrowthApprox()); }

public long getPaidResources()
{ return resources.getPaidResources(); }

public long getWastedResources()
{ return resources.getWastedResources(); }

public long getDisplayResource(long time, int typ)
{ return resources.getDisplayResource(time, typ); }

public long getDisplayResource(int typ)
{ return resources.getDisplayResource(getGalaxy().getTime(), typ); }

public long getDisplayPopulation(long time)
{ return resources.getDisplayPopulation(time); }

public long getDisplayPopulation()
{ return resources.getDisplayPopulation(getGalaxy().getTime()); }

public long getPopulationLimit()
{ return resources.getPopulationLimit(); }

public long getRate(int typ)
{ return resources.getRate(typ); }

public long getStorage(int typ)
{ return resources.getLimit(typ); }

public int getModifier(int typ)
{ return modifiers.get(typ); }

public long getAdministrativeJobs()
{ return resources.getAdministrativeJobs(); }

public long getConstructionJobs()
{ return resources.getConstructionJobs(); }

public long getRegularJobs()
{ return resources.getRegularJobs(); }


public int addPeople(long time, int amount)
{ return resources.addPeople(time, amount); }

public long addResources(long time, int typ, long amount)
{ return resources.addResources(time, typ, amount); }

public long removeResources(long time, int typ, long amount, long leaverest)
{ return resources.removeResources(time, typ, amount, leaverest); }


public List<Agent> getAgents()
{ return agents; }

public float getAgentSuccessProbability()
{ return agentSuccessProbability; }

public void notifyAgentPlanted()
{ agentSuccessProbability *= 0.75; }

public void regularEvent(long time)
{
	if (time-agentTime > Constants.AGENT_INTERVAL)
	{
		agentTime = time;
		agentSuccessProbability *= 2.0f;
		if (agentSuccessProbability > SPY_SUCCESS_MAX)
			agentSuccessProbability = SPY_SUCCESS_MAX;
	}
}


// Money
public long calculateUnitUpkeep()
{
	double sum = 0;
	
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		sum += it.value() * it.key().getUpkeep();
	}
	
	double result = Constants.UNIT_UPKEEP * sum / 100.0d;
	
	if (Double.isNaN(sum) || Double.isNaN(result))
	{
		Galaxy.logger.log(Level.SEVERE, "Upkeep number is a NaN for "+this+" with sum="+sum+" result="+result);
		result = 0;
	}
	
	if (result > OVERFLOW_CHECK)
	{
		Galaxy.logger.log(Level.SEVERE, "Upkeep number overflow for "+this+" with sum="+sum+" result="+result);
		result = OVERFLOW_CHECK;
	}
	
	long longResult = Math.round(result);
	if (longResult > OVERFLOW_CHECK)
	{
		Galaxy.logger.log(Level.SEVERE, "Upkeep number overflow for "+this+" with sum="+sum+" result="+result);
		longResult = OVERFLOW_CHECK;
	}
	
	if (longResult < 0)
	{
		Galaxy.logger.log(Level.SEVERE, "Upkeep number underflow for "+this+" with sum="+sum+" result="+result);
		longResult = 0;
	}
	
	return longResult/20;
}

public long calculateUpkeep()
{
	long sum = 0;
	for (BuildingEnum building : BuildingEnum.values())
		sum += buildings.get(building) * building.upkeepNeeded();
	
	sum += (long) education.getSum() * (long) Constants.PROFESSOR_UPKEEP;
	sum += calculateUnitUpkeep();
	return sum;
}

public long calculateIncome()
{ return resources.calculateIncome(); }

public long payUpkeep(long time)
{
	if (!owner.hasMoney())
	{
		if (education.getSum() > 0)
			decreaseRandomEducation(time);
	}
	
	regularEvent(time);
	
	updateBeforeEvent(time);
	updateAfterEvent();
	
	long timeDiff = time-lastUpkeepTime;
	lastUpkeep = (calculateUpkeep()*timeDiff) / (60*60);
//	owner.getMoney(time, MoneyReason.COLONY_UPKEEP, lastUpkeep);
	lastUpkeepTime = time;
	return lastUpkeep;
}

public long getIncome(long time)
{
/*	int money = getRessource(Resource.GELD, time);
	ressources.decrease(Resource.GELD, money);
	lastIncome = money;
	return lastIncome;*/
	return resources.getIncome(time);
}

private int calcMoneyRate()
{
	if (!isInhabited) return 0;
	
	double x = buildings.get(BuildingEnum.TRADE_CENTER);
	double result = x * ResourceEnum.MONEY.getRateFactor();
	if (!isInvaded) result += ResourceEnum.MONEY.getNullRate();
	double realresult = (result/100.0) * modifiers.get(Resource.MONEY);
	
	if (Double.isNaN(realresult))
	{
		Galaxy.logger.log(Level.SEVERE, "Invalid result", new RuntimeException("Rate number is a NaN!"));
	}
	
	long longResult = Math.round(realresult);
	
	if (longResult > Integer.MAX_VALUE)
	{
		Galaxy.logger.log(Level.SEVERE, "Invalid result", new RuntimeException("Rate number overflow with result="+realresult));
		longResult = Integer.MAX_VALUE;
	}
	
	if (longResult < 0)
	{
		Galaxy.logger.log(Level.SEVERE, "Invalid result", new RuntimeException("Rate number underflow!"));
		longResult = 0;
	}
	
	return (int) longResult;
}

private int calcRate(ResourceEnum typ)
{
	if (typ == null) throw new NullPointerException();
	if (typ == ResourceEnum.MONEY) throw new IllegalArgumentException();
	
	if (!isInhabited) return 0;
	
	double x = buildings.get(BuildingEnum.valueOf(BuildingEnum.STEEL_MILL.ordinal() + typ.index()));
	double result = x * typ.getRateFactor();
	if (!isInvaded) result += typ.getNullRate();
	double realresult = (result/100.0) * modifiers.get(typ.index());
	
	if (Double.isNaN(realresult))
	{
		Galaxy.logger.log(Level.SEVERE, "Invalid result", new RuntimeException("Rate number is a NaN!"));
	}
	
	long longResult = Math.round(realresult);
	
	if (longResult > Integer.MAX_VALUE)
	{
		Galaxy.logger.log(Level.SEVERE, "Invalid result", new RuntimeException("Rate number overflow with result="+realresult));
		longResult = Integer.MAX_VALUE;
	}
	
	if (longResult < 0)
	{
		Galaxy.logger.log(Level.SEVERE, "Invalid result", new RuntimeException("Rate number underflow!"));
		longResult = 0;
	}
	
	return (int) longResult;
}

private long calcStorage(ResourceEnum typ)
{
	if (typ == null) throw new NullPointerException();
	if (typ == ResourceEnum.MONEY) throw new IllegalArgumentException();
	
	long x = buildings.get(BuildingEnum.valueOf(BuildingEnum.STEEL_DEPOT.ordinal() + typ.index()));
	long result = x * typ.getStorageFactor() + typ.getNullStorage();
	
	return result;
}

private void updatePopulation()
{
	int residence = buildings.get(BuildingEnum.RESIDENCE); 
	resources.setPopulationLimit(residence*Constants.PEOPLE_PER_RESIDENCE+Constants.PEOPLE_PER_COLONY);
	
	double is = buildings.get(BuildingEnum.INFRASTRUCTURE)*4.0+2;
	double hz = buildings.get(BuildingEnum.TRADE_CENTER)/5.0+1;
	resources.setPopulationModifier((is/hz)*populationGrowthPlanetaryModifier);
}

private void updateJobs()
{
	long sum;
	
	// calculate regular jobs
	sum = 0;
	for (BuildingEnum building : BuildingEnum.values())
		sum += (long) buildings.get(building) * (long) building.regularJobs();
	resources.setRegularJobs(sum);
	
	// calculate construction jobs
	sum = 0;
	if (buildingEvent.nextEventHandle != null)
	{
		int data = buildingEvent.data;
		BuildingEnum building = buildingEvent.token;
		if (data > 0)
			sum = building.constructionJobs();
		else
			sum = building.removeJobs();
	}
	for (int i = 0; i < UNIT_QUEUES; i++)
	{
		if (unitEvents[i].nextEventHandle != null)
			sum += getUnitCost(unitEvents[i].unit).getConstructionJobs();
	}
	resources.setConstructionJobs(sum);
	
	
	int size = buildings.getSum();
	resources.setAdministrativeJobs(Math.round(Math.max(20, (size+1)*3*Math.log(size+1))));
}

private void updateResources()
{
	for (ResourceEnum res : ResourceEnum.realResources())
	{
		int i = res.index();
		resources.setRate(i, calcRate(res));
		resources.setLimit(i, calcStorage(res));
	}
	
	resources.setRate(Resource.MONEY, calcMoneyRate());
	resources.setLimit(Resource.MONEY, 1000000000);
}

private void updateResourceRatesAndLimits()
{
	updatePopulation();
	updateJobs();
	updateResources();
}

private void updatePoints()
{
	if (owner == null) return;
	points = 0;
	for (BuildingEnum building : BuildingEnum.values())
		points += buildings.get(building) * building.getPoints();
	owner.calculatePoints();
}

public void updateBeforeEvent(long time)
{ resources.updateBeforeEvent(time); }

public void updateAfterEvent()
{
	updateResourceRatesAndLimits();
	resources.updateAfterEvent();
}

public void update(long time)
{
	updateBeforeEvent(time);
	updateAfterEvent();
}

public void checkSize(long time)
{
	if (owner == null)
		throw new IllegalStateException("Colony is already gone!");
	if (buildings.getSum() == 0)
	{
		if ((planet.amount() == 1) && (planet.getSpaceBattle() != null))
			// remove blockade (not allowed to block empty planets)
			planet.getSpaceBattle().endBattle(time, BattleStateEnum.BLOCKING_EMPTY_PLANET);	
		getGalaxy().schedule(new ColonyRemoveAction(this));
	}
}

public BuildingEnum steal(Colony other, boolean keep, long time)
{
	if (other.buildings.getSum() == 0) return null;
	
	other.updateBeforeEvent(time);
	updateBeforeEvent(time);
	
	try
	{
		BuildingEnum whatConq = other.buildings.chooseRandom(rng);
		
		if (keep)
			owner.increaseInvasionDone();
		else
			owner.increaseInvasionBurned();
		other.owner.increaseInvasionBeen();
		
		other.decreaseBuilding(time, whatConq);
		if (keep) increaseBuilding(time, whatConq);
		
//		Infobox.dropInfo(owner, position, time, Information.keyBattleSuccInvasion, report_data);
//		if (other.owner != null)
//			Infobox.dropInfo(other.owner, position, time, Information.keyBattleBeenInvaded, report_data);
		
		return whatConq;
	}
	catch (Throwable e)
	{
		Galaxy.logger.log(Level.SEVERE, "Exception caught", e);
		return null;
	}
	finally
	{
		other.updateAfterEvent();
		updateAfterEvent();
	}
}

public int requiredInvasionEggis()
{
	if (isStartColony)
		return Constants.INVASION_REQ_TROOPS_HOMEWORLD;
	else
		return Constants.INVASION_REQ_TROOPS;
}

public int missingTroopsForInvasion(Fleet f)
{
	int availableEggis = 0;
	UnitIterator it = f.getUnitIterator(UnitSelector.GROUND_ONLY);
	while (it.hasNext())
	{
		it.next();
		if (!it.key().hasSpecial(UnitSpecial.INVASION)) continue;
		availableEggis += it.value();
	}
	
	int requiredEggis = requiredInvasionEggis();
	if (availableEggis < requiredEggis) 
		return requiredEggis - availableEggis;
	
	return 0;
}

public int subtractTroopsRequiredForInvasion(Fleet f)
{
	int requiredEggis = requiredInvasionEggis();
	int tempEggis = requiredEggis;
	
	UnitIterator it = f.getUnitIterator(UnitSelector.GROUND_ONLY);
	while (it.hasNext())
	{
		it.next();
		if (!it.key().hasSpecial(UnitSpecial.INVASION)) continue;
		if (tempEggis > it.value())
		{
			tempEggis -= it.value();
			it.remove();
		}
		else
		{
			it.setValue(it.value() - tempEggis);
			return requiredEggis;
		}
	}
	throw new IllegalStateException("Not enough invasion troops!");
}


// Research & Education
private void setResearchAmount(long time, int newResearchAmount)
{
	if (researchAmount == newResearchAmount) return;
	if (newResearchAmount < 0) newResearchAmount = 0;
	
	researchAmount = newResearchAmount;
	owner.handleUniversity(time);
}

private void setEducationAmount(long time, int newEducationAmount)
{
	if (educationAmount == newEducationAmount) return;
	if (newEducationAmount < 0) newEducationAmount = 0;
	
	educationAmount = newEducationAmount;
	
	while ((education.getSum()+pendingEducation.getSum() > educationAmount) &&
	       (education.getSum()+pendingEducation.getSum() > 0))
		removeRandomEducation(time);
	
/*	while (education.sum+pendingEducation.sum < educationAmount)
		chooseRandomEducation();*/
}

public void addResearchProfs(int amount)
{
	long time = getGalaxy().getTime();
	if (amount > 0)
	{
		int available = getFreeProfessors();
		if (amount > available) amount = available;
		setResearchAmount(time, researchAmount+amount);
	}
	else
	{
		amount = -amount;
		int maxAmount = owner.maxRemoveResearchProfs();
		if (amount > maxAmount) amount = maxAmount;
		if (amount > researchAmount) amount = researchAmount;
		setResearchAmount(time, researchAmount-amount);
	}
}

public void addEducationProfs(int amount)
{
	long time = getGalaxy().getTime();
	if (amount > 0)
	{
		int available = getFreeProfessors();
		if (amount > available) amount = available;
		setEducationAmount(time, educationAmount+amount);
	}
	else
	{
		amount = -amount;
		int maxAmount = maxRemoveEducationProfs();
		if (amount > maxAmount) amount = maxAmount;
		if (amount > educationAmount) amount = educationAmount;
		setEducationAmount(time, educationAmount-amount);
	}
}

private void handleUniversity(long time)
{
	int available = getProfessors();
	
	if (educationAmount+researchAmount > available)
	{
		int newResearchAmount = researchAmount;
		int newEducationAmount = educationAmount;
		
		if (available >= researchAmount)
			newEducationAmount = available-researchAmount;
		else
		{
			newEducationAmount = 0;
			newResearchAmount = available;
		}
		
		setResearchAmount(time, newResearchAmount);
		setEducationAmount(time, newEducationAmount);
	}
}

private void handleResidence(long time)
{
	// TODO: improve me!
	long population = resources.getDisplayPopulation(time);
	updatePopulation();
	long limit = resources.getPopulationLimit();
	if (population > limit)
	{
		long irrevocableJobs = resources.getIrrevocableJobs();
		if (irrevocableJobs > resources.getDisplayPopulation(time))
		{
			abortBuilding(time);
		}
		
		irrevocableJobs = resources.getIrrevocableJobs();
		if (irrevocableJobs > resources.getDisplayPopulation(time))
		{
			Galaxy.logger.warning("checkPopulation: Was nu?");
		}
	}
}


// Education
public void checkEducation()
{
	for (EducationEnum topic : EducationEnum.values())
	{
		if (!owner.isKnown(topic) && (education.get(topic) > 0))
			education.set(topic, 0);
	}
}

public void increaseEducation(long time, EducationEnum topic)
{
	education.increase(topic, 1);
	colonyLogger.increaseEducation(time, topic);
}

private void addEducationEvent(long time)
{
	assert educationEvent.nextEventHandle == null;
//	Environment.logger.println("Add Education Event");
	long keepEducationTime = getMidRandom(Constants.PROFESSOR_INTERVAL, Constants.PROFESSOR_INTERVAL/6);
	educationEvent.nextEventTime = time+keepEducationTime;
	educationEvent.nextEventHandle = getGalaxy().addEvent(educationEvent.nextEventTime, new EducationEvent(this));
}

private void removeEducationEvent()
{
	// FIXME: Exception geflogen hier!
	assert educationEvent.nextEventHandle != null;
	Galaxy.logger.entering("Colony", "removeEducationEvent");
	getGalaxy().deleteEvent(educationEvent.nextEventHandle);
	educationEvent.nextEventHandle = null;
	educationEvent.nextEventTime = 0;
}

private void startEducation(long time, EducationEnum topic)
{
	assert topic != null;
	
	if (!owner.isKnown(topic))
		return;
	
	if (education.getSum() + pendingEducation.getSum() >= educationAmount)
		return;
	
	pendingEducation.increase(topic);
	if (educationEvent.nextEventHandle == null) addEducationEvent(time);
}

public void startEducation(EducationEnum topic, int amount)
{
	long time = getGalaxy().getTime();
	for (int i = 0; i < amount; i++)
		startEducation(time, topic);
}

static EducationEnum chooseRandomEducation(EnumIntMap<EducationEnum> edu, EnumIntMap<EducationEnum> pen, RandomNumberGenerator rng2)
{
	// FIXME: change this into an IllegalStateException (or IllegalArgumentException)
	assert edu.getSum() + pen.getSum() > 0;
	EducationEnum topic;
	EducationEnum a = edu.chooseRandom(rng2);
	EducationEnum b = pen.chooseRandom(rng2);
	if (a == null)
		topic = b;
	else if (b == null)
		topic = a;
	else
		topic = rng2.nextBoolean() ? a : b;
	return topic;
}

public void removeRandomEducation(long time)
{
	assert education.getSum() + pendingEducation.getSum() > 0;
	EducationEnum topic = chooseRandomEducation(education, pendingEducation, rng);
	
	if (pendingEducation.get(topic) > 0)
		pendingEducation.decrease(topic);
	else	
	{
		education.decrease(topic);
		colonyLogger.decreaseEducation(time, topic);
	}
	
	if ((pendingEducation.getSum() == 0) && (educationEvent.nextEventHandle != null))
		removeEducationEvent();
}

public void decreaseRandomEducation(long time)
{
	assert education.getSum() > 0;
	EducationEnum topic = education.chooseRandom(rng);
	
	education.decrease(topic);
	colonyLogger.decreaseEducation(time, topic);
	
	pendingEducation.increase(topic);
	if (educationEvent.nextEventHandle == null)
		addEducationEvent(time);
}

private void increaseRandomEducation(long time)
{
	assert pendingEducation.getSum() > 0;
	EducationEnum topic = pendingEducation.chooseRandom(rng);
	
	pendingEducation.decrease(topic);
	education.increase(topic);
	
	if (false)
		getGalaxy().dropInfo(owner, time, InfoEnum.NEW_EDUCATION_PROFESSOR, position, topic);
	// FIXME: make a short history report of the colony
}

public void completeEducation(long time)
{
	educationEvent.nextEventHandle = null;
	if (pendingEducation.getSum() == 0) return;
	if (!isInhabited) return;
	
//	Environment.logger.println("Education complete");
	
	updateBeforeEvent(time);
	
	int amount = pendingEducation.getSum() / 10 + 1;
	
	// first calculate how many professors can be added
	long realamount = owner.getMoney()/Constants.PROFESSOR_START;
	if (realamount > amount) realamount = amount;
	if (realamount < 0) realamount = 0;
	// subtract the money for all professors at once
	owner.getMoney(time, MoneyReason.PROFESSOR_START, realamount*Constants.PROFESSOR_START);
	
	// then add all the professors
	for (int i = 0; i < realamount; i++)
		increaseRandomEducation(time);
	
	updateAfterEvent();
	
	if (pendingEducation.getSum() > 0)
		addEducationEvent(time);
}

public void stopEducation(long time, EducationEnum topic)
{
	assert topic != null;
	
	if (!owner.isKnown(topic))
	{
		education.set(topic, 0);
		if (pendingEducation.get(topic) > 0)
		{
			pendingEducation.set(topic, 0);
			if (pendingEducation.getSum() == 0)
				removeEducationEvent();
		}
		return;
	}
	
	if (pendingEducation.get(topic) > 0)
	{
		pendingEducation.decrease(topic);
		if (pendingEducation.getSum() == 0)
			removeEducationEvent();
	}
	else
	{
		if (education.get(topic) > 0)
			{
				education.decrease(topic);
				colonyLogger.decreaseEducation(time, topic);
			}
	}
}

public void stopEducation(EducationEnum topic, int amount)
{
	if (!owner.isKnown(topic))
	{
		education.set(topic, 0);
		if (pendingEducation.get(topic) > 0)
		{
			pendingEducation.set(topic, 0);
			if (pendingEducation.getSum() == 0)
				removeEducationEvent();
		}
		return;
	}
	
	long time = getGalaxy().getTime();
	for (int i = 0; i < amount; i++)
		stopEducation(time, topic);
}


// Buildings
public boolean hasTransmitter()
{ return buildings.get(BuildingEnum.TRANSMITTER) > 0; }

public boolean isCurrentlyBuilding()
{ return buildingEvent.nextEventHandle != null; }

public boolean isBuildingRemoval()
{ return buildingEvent.data < 0; }

public BuildingEnum getCurrentBuilding()
{ return buildingEvent.token; }

public long getCurrentBuildingTime()
{ return buildingEvent.nextEventTime; }

public EnumQueue<BuildingEnum> getBuildingQueue()
{ return ((HumanColonyController) controller).getBuildingQueue(); }

public int getBuildingQueueSize()
{ return ((HumanColonyController) controller).getBuildingQueue().size(); }

public void increaseBuilding(long time, BuildingEnum what)
{
	buildings.increase(what, 1);
	planet.increaseUsed();
	
	colonyLogger.increaseBuilding(time, what);
	
	updatePoints();
	
	if (what == BuildingEnum.RADIO_TELESCOPE)
		owner.updateGalaxyView(this);
	
	if (what == BuildingEnum.UNIVERSITY)
		handleUniversity(time);
}

public void decreaseBuilding(long time, BuildingEnum what)
{
	buildings.decrease(what, 1);
	planet.decreaseUsed();
	
	colonyLogger.decreaseBuilding(time, what);
	
	updatePoints();
	
	if (what == BuildingEnum.UNIVERSITY)
		handleUniversity(time);
	
	if (what == BuildingEnum.RESIDENCE)
		handleResidence(time);
	
	checkSize(time);
}

void completeBuilding(long time)
{
	if (buildingEvent.nextEventHandle == null) return;
	buildingEvent.nextEventHandle = null;
	
	updateBeforeEvent(time);
	
	int type = buildingEvent.data;
	BuildingEnum building = buildingEvent.token;
	if (type > 0)
	{
		increaseBuilding(time, building);
/*		Infobox.dropInfo(owner, position, time, Information.keyBuildingCompleted,
			               new int[] {what, entities.get(what)} );*/
	}
	else
	{
		if (buildings.get(building) > 0)
		{
			decreaseBuilding(time, building);
			if (buildings.getSum() == 0)
				return;
		}
	}
	
	updateAfterEvent();
	
	controller.finishBuilding(this, time);
	controller.nextBuilding(this, time, false);
}

private void scheduleEvent(long time, int data, BuildingEnum building, Cost cost)
{
	buildingEvent.nextEventHandle = getGalaxy().addEvent(time, new BuildingEvent(this));
	buildingEvent.nextEventTime = time;
	buildingEvent.data = data;
	buildingEvent.token = building;
	buildingEvent.actualCost = cost;
}

public double getTimeModifier(int level)
{
	double min = Constants.BUILDING_MIN_PERCENT;
	return min + (1.0-min)*Constants.BUILDING_CONSTRUCTION_MODIFIER/(Constants.BUILDING_CONSTRUCTION_MODIFIER+level);
}

public long getBuildingCostTime(BuildingEnum building, int addUsedSpaces, int amount)
{
	int used = isStartColony ? buildings.getSum() : planet.getUsed();
	int size = planet.getSize();
	long result = building.timeNeeded(size, used + addUsedSpaces, amount);
	result = Math.round(result*getTimeModifier(education.get(EducationEnum.CONSTRUCTION)));
	return result;

}

public Cost getBuildingCost(BuildingEnum building)
{
	int used = isStartColony ? buildings.getSum() : planet.getUsed();
	int size = planet.getSize();

	Cost cost = building.getCost(size, used);
	
	// Bauplatzkosten
	int[] rcost = new int[Resource.MAX+1];
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		rcost[i] = cost.getResource(i)+baseBuildCost(i);
	
	// modifizierte Baudauer
	int level = education.get(EducationEnum.CONSTRUCTION);
	long result = Math.round(cost.getTime()*getTimeModifier(level));
	if (result < Constants.BUILDING_MIN) result = Constants.BUILDING_MIN;
	if (result < 1) result = 1;
	cost = new Cost(cost, rcost, result);

	return cost;
}

public Cost getBuildingRemovalCost(BuildingEnum building)
{
	Cost cost = building.getRemovalCost();
	
	// modifizierte Baudauer
	int level = education.get(EducationEnum.CONSTRUCTION);
	long result = Math.round(cost.getTime()*getTimeModifier(level));
	if (result < Constants.BUILDING_MIN) result = Constants.BUILDING_MIN;
	if (result < 1) result = 1;
	cost = new Cost(cost, result);
	
	return cost;
}

private boolean canBuild(long time, BuildingEnum building)
{
	Cost cost = getBuildingCost(building);
	return CheckResult.OK == resources.check(time, cost);
}

public boolean canBuild(BuildingEnum building)
{ return canBuild(getGalaxy().getTime(), building); }

public boolean canRemove(long time, BuildingEnum building)
{ return (buildings.get(building) > 0); }

public boolean startBuildingConstruction(long time, BuildingEnum building, boolean isManualRequest)
{
	if (building == null) throw new NullPointerException("No building?");
	if (buildingEvent.nextEventHandle != null)
		throw new IllegalStateException("Already building something!");
	
	Cost cost = getBuildingCost(building);
	CheckResult result = resources.buy(time, cost);
	if (CheckResult.OK == result)
	{
		owner.updateResourcePoints();
		owner.getMoney(time, MoneyReason.DESTROY_BUILDING, cost.getMoney());
		scheduleEvent(time+cost.getTime(), 1, building, cost);
		updateAfterEvent();
		return true;
	}
	else
	{
		if (isManualRequest)
			owner.log(result.getBuildingErrorCode(), this, building, result.getResource());
		else
		{
			if (result.getBuildingErrorCode() == ErrorCode.NOT_ENOUGH_RESOURCES)
				getGalaxy().dropInfo(owner, time, InfoEnum.BUILDING_QUEUE_STOPPED_RESOURCES_MISSING_FOR_CONSTRUCTION, position, QueueEnum.BUILDING, result.getResource(), building);
			else if (result.getBuildingErrorCode() == ErrorCode.NOT_ENOUGH_POPULATION)
				getGalaxy().dropInfo(owner, time, InfoEnum.BUILDING_QUEUE_STOPPED_POPULATION_MISSING_FOR_CONSTRUCTION, position, QueueEnum.BUILDING, building);
				
		}
		return false;
	}
}

public boolean startBuildingRemoval(long time, BuildingEnum building, boolean isManualRequest)
{
	if (buildingEvent.nextEventHandle != null)
		throw new IllegalStateException("Already building something!");
	
	if (buildings.get(building) <= 0)
	{
		owner.log(ErrorCode.CANNOT_REMOVE_NO_SUCH_BUILDING, this, building);
		return false;
	}
	
	Cost cost = getBuildingRemovalCost(building);
	// Always allow building removal. Assumes that building removal only has time and money costs.
	owner.getMoney(time, MoneyReason.DESTROY_BUILDING, cost.getMoney());
	scheduleEvent(time+cost.getTime(), -1, building, cost);
	updateAfterEvent();
	return true;
}

public void resumeBuilding(long time)
{
	if (buildingEvent.nextEventHandle != null) return;
	controller.nextBuilding(this, time, true);
}

public void resumeBuilding()
{ resumeBuilding(getGalaxy().getTime()); }

public boolean isBuildingInProgress()
{ return buildingEvent.nextEventHandle != null; }

public void abortBuilding(long time)
{
	if (buildingEvent.nextEventHandle == null)
	{
		owner.log(ErrorCode.BUILDING_ALREADY_ABORTED, this);
		return;
	}
	
	getGalaxy().deleteEvent(buildingEvent.nextEventHandle);
	buildingEvent.nextEventHandle = null;
	buildingEvent.nextEventTime = 0;
	
	// Kostenrueckzahlung (nur fuers Bauen, nicht fuers Abreissen)
	int type = buildingEvent.data;
	if (type > 0)
	{
		Cost cost = buildingEvent.actualCost;
		resources.payback(time, cost);
		owner.updateResourcePoints();
	}
	
	controller.abortBuilding(this, time);
}

public void abortBuilding()
{ abortBuilding(getGalaxy().getTime()); }


// Units
public boolean isCurrentlyUnit(int queue)
{ return unitEvents[queue].nextEventHandle != null; }

public Unit getCurrentUnit(int queue)
{ return unitEvents[queue].unit; }

public long getCurrentUnitTime(int queue)
{ return unitEvents[queue].nextEventTime; }

public UnitQueue getUnitQueue(int queue)
{ return ((HumanColonyController) controller).getUnitQueue(queue); }

public int getUnitQueueSize(int queue)
{ return ((HumanColonyController) controller).getUnitQueue(queue).size(); }

private void increaseUnit(long time, Unit typ)
{
	units.increase(typ, 1);
	colonyLogger.increaseBuiltUnits(time, typ);
	getGalaxy().buildUnit(typ);
}

public void completeUnit(long time, int queue)
{
	if ((queue < 0) || (queue >= UNIT_QUEUES)) return;
	if (unitEvents[queue].nextEventHandle == null) return;
	
	Unit slot = unitEvents[queue].unit;
	
	unitEvents[queue].nextEventHandle = null;
	unitEvents[queue].unit = null;
	
	updateBeforeEvent(time);
	
	increaseUnit(time, slot);
	// Frisst viel Speicher und CPU-Leistung
//	Infobox.dropInfo(owner, position, time, Information.keyUnitCompleted, new int[] {-1} );
	
	owner.increaseUnitPoints(slot.getScore());
	
//	Cost cost = unitEvents[queue].actualCost;
	
	updateAfterEvent();
	
	controller.finishUnit(this, time, queue);
	controller.nextUnit(this, time, queue, false);
}

public Cost getUnitCost(Unit unit)
{
	Cost cost = unit.getCost();
	EducationModifier modifier = unit.getEducationModifier();
	cost = modifier.modify(cost, education);
	
	// modifizierte Baudauer
	BuildingEnum num = BuildingEnum.valueOf(BuildingEnum.MILITARY_BASE.ordinal() + getCorrectUnitQueue(unit));
	// TODO: Factor constants into base costs. At least UNIT_FACTOR!
	long result =
	  Math.round((double) (cost.getTime()*Constants.UNIT_K) /
	       (Constants.UNIT_K-Constants.UNIT_L+buildings.get(num)));
	if (result < Constants.UNIT_MIN) result = Constants.UNIT_MIN;
	if (result < 1) result = 1;
	cost = new Cost(cost, result);
	
	return cost;
}

public int getCorrectUnitQueue(Unit unit)
{
	if (unit.isPlanetary())
		return 0;
	if (unit.hasSpecial(UnitSpecial.WARP))
		return 2;
	else
		return 1;
}

public boolean mayBuild(Unit unit)
{
	// Kein Gebaeude um das zu Bauen
	BuildingEnum num = BuildingEnum.valueOf(BuildingEnum.MILITARY_BASE.ordinal() + getCorrectUnitQueue(unit));
	if (buildings.get(num) < 1) return false;
	
	// Nicht genug Forschung zum Bauen
	if (!owner.isKnown(unit)) return false;
	
	return true;
}

private boolean canBuild(long time, Unit unit)
{
	if (!mayBuild(unit)) return false;
	Cost cost = getUnitCost(unit);
	return CheckResult.OK == resources.check(time, cost);
}

public boolean canBuild(Unit unit)
{ return canBuild(getGalaxy().getTime(), unit); }

public boolean startUnit(long time, Unit slot, boolean isManualRequest)
{
	final int queue = getCorrectUnitQueue(slot);
	if (unitEvents[queue].nextEventHandle != null)
		throw new IllegalStateException("Already building something!");
	
	// Kein Gebaeude um das zu Bauen
	BuildingEnum num = BuildingEnum.valueOf(BuildingEnum.MILITARY_BASE.ordinal() + queue);
	if (buildings.get(num) < 1)
	{
		owner.log(ErrorCode.NO_PRODUCING_BUILDING, this, num, slot);
		return false;
	}
	
	// Nicht genug Forschung zum Bauen
	if (!owner.isKnown(slot))
	{
		if (!isManualRequest)
		{
			owner.log(ErrorCode.UNKNOWN_UNIT, this, slot);
			getGalaxy().dropInfo(owner, time, InfoEnum.UNIT_QUEUE_STOPPED_UNIT_UNKNOWN, position, Integer.valueOf(queue), slot);
		}
		return false;
	}
	
	Cost cost = getUnitCost(slot);
	CheckResult result = resources.buy(time, cost);
	if (CheckResult.OK == result)
	{
		owner.updateResourcePoints();
		long buildTime = time+cost.getTime();
		unitEvents[queue].nextEventHandle = getGalaxy().addEvent(buildTime, new UnitEvent(this, queue));
		unitEvents[queue].nextEventTime = buildTime;
		unitEvents[queue].unit = slot;
		unitEvents[queue].actualCost = cost;
		
		updateAfterEvent();
		return true;
	}
	else
	{
		if (isManualRequest)
			owner.log(result.getUnitErrorCode(), this, slot, result.getResource());
		else
		{
			if (result.getBuildingErrorCode() == ErrorCode.NOT_ENOUGH_RESOURCES_UNIT)
				getGalaxy().dropInfo(owner, time, InfoEnum.UNIT_QUEUE_STOPPED_RESOURCES_MISSING, position, Integer.valueOf(queue), result.getResource(), slot);
			else if (result.getBuildingErrorCode() == ErrorCode.NOT_ENOUGH_POPULATION_UNIT)
				getGalaxy().dropInfo(owner, time, InfoEnum.UNIT_QUEUE_STOPPED_POPULATION_MISSING, position, Integer.valueOf(queue), slot);
				
		}
		return false;
	}
}

public void resumeUnit(long time, int queue)
{
	if ((queue < 0) || (queue >= UNIT_QUEUES)) return;
	if (unitEvents[queue].nextEventHandle != null) return;
	controller.nextUnit(this, time, queue, true);
}

public void resumeUnit(int queue)
{ resumeUnit(getGalaxy().getTime(), queue); }

public void abortUnit(long time, int queue)
{
	if ((queue < 0) || (queue >= UNIT_QUEUES)) 
	{
		owner.log(ErrorCode.INVALID_INPUT);
		return;
	}
	if (unitEvents[queue].nextEventHandle == null)
	{
		owner.log(ErrorCode.QUEUE_ALREADY_STOPPED, this, QueueEnum.getUnitQueue(queue));
		return;
	}
	
	getGalaxy().deleteEvent(unitEvents[queue].nextEventHandle);
	unitEvents[queue].nextEventHandle = null;
	unitEvents[queue].nextEventTime = 0;
	unitEvents[queue].unit = null;
	
	Cost cost = unitEvents[queue].actualCost;
	resources.payback(time, cost);
	owner.updateResourcePoints();
}

public void abortUnit(int queue)
{ abortUnit(getGalaxy().getTime(), queue); }


@Restricted
public void createAgent(Player who, long time)
{
	Agent a = null;
	for (int i = 0; i < agents.size(); i++)
		if (agents.get(i).getOwner() == who)
			a = agents.get(i);
	
	if (a != null)
	{
		a.increase();
	}
	else
	{
		a = new Agent(getGalaxy(), this, who, time);
		a.increase();
	}
}

@Restricted
public Iterator<Agent> agentIterator()
{ return agents.iterator(); }

@Restricted
public void addAgent(Agent agent)
{ agents.add(agent); }

@Restricted
public void removeAgent(Agent agent)
{ agents.remove(agent); }

@Restricted
public void removeAllAgents()
{
	Iterator<Agent> it = agents.iterator();
	while (it.hasNext())
	{
		Agent a = it.next();
		a.notifyRemove(getGalaxy().getTime());
		a.getOwner().removeAgent(a);
	}
	agents.clear();
}

@Override
public String toString()
{ return "Colony "+position+" - "+name+" from "+owner; }

}
