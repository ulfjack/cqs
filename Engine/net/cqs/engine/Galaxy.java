package net.cqs.engine;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.auth.Identity;
import net.cqs.config.BuildingEnum;
import net.cqs.config.Constants;
import net.cqs.config.InfoEnum;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.InvalidPositionException;
import net.cqs.config.MoneyReason;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.GalaxyACL.PlayerEntry;
import net.cqs.engine.actions.Action;
import net.cqs.engine.actions.PlayerColonyStartAction;
import net.cqs.engine.actions.PlayerCreateAction;
import net.cqs.engine.base.Event;
import net.cqs.engine.base.EventHandle;
import net.cqs.engine.base.EventQueue;
import net.cqs.engine.base.Survey;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BattleEventListener;
import net.cqs.engine.colony.ColonyEventAdapter;
import net.cqs.engine.colony.ColonyEventListener;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.ContractParty;
import net.cqs.engine.diplomacy.ContractProposal;
import net.cqs.engine.diplomacy.ContractProposalList;
import net.cqs.engine.diplomacy.DiplomacyEntry;
import net.cqs.engine.diplomacy.DiplomaticRelation;
import net.cqs.engine.human.HumanPlayerController;
import net.cqs.engine.messages.InfoListener;
import net.cqs.engine.messages.InfoTranslator;
import net.cqs.engine.messages.Information;
import net.cqs.engine.messages.MultiI18nMessage;
import net.cqs.engine.rtevents.RealTimeEvent;
import net.cqs.engine.rtevents.RealTimeEventType;
import net.cqs.engine.scores.ScoreManager;
import net.cqs.engine.units.UnitSystemImpl;
import net.cqs.history.GameBattleLogger;
import net.cqs.history.GameColonyLogger;
import net.cqs.main.persistence.AgentReport;
import net.cqs.main.persistence.MoneyData;
import net.cqs.main.persistence.ObserverReport;
import net.cqs.main.persistence.RegistrationHistory;
import net.cqs.storage.Storage;
import net.cqs.storage.Task;
import net.cqs.storage.memory.MemoryStorageManager;
import net.cqs.util.EnumIntMap;
import net.cqs.util.TextConverter;
import net.cqs.util.UIDGenerator;
import de.ofahrt.ulfscript.annotations.Restricted;

/**
 * If possible, use an Action object to modify the Galaxy
 * (works according to the Command pattern).
 * @see Action
 */
public final class Galaxy implements Iterable<SolarSystem>, Serializable
{

private static final long serialVersionUID = 3L;

public static final UIDGenerator uidGenerator = new UIDGenerator();

public static final Logger logger = Logger.getLogger("net.cqs.engine");

public static TextConverter defaultTextConverter;
public static TextConverter defaultLineConverter;

private static final int MAX_PLAYERS_PER_IDENTITY = 1;


private final byte[] galaxyId;

private transient Storage galaxyControl;
private final MetaGalaxy metaGalaxy = new MetaGalaxy();

private final GalaxyACL galaxyACL = new GalaxyACL();
private final Random rand;

private final PlayerStartConfiguration startConfiguration;

private SolarSystem[] data = new SolarSystem[0];
private SolarSystem[] sortedData = new SolarSystem[0];
private Planet[] settlePlanets;
private int settleSystem = 0;
private int settlePlanet = 0;

private int playerId = 0;
private int allianceId = 0;

private final List<Player> players     = new ArrayList<Player>();
private final List<Alliance> alliances = new ArrayList<Alliance>();

private final ContractProposalList contractProposals = new ContractProposalList();

private final UnitMap builtUnits = new UnitMap();

private long speed = 1;
private final EventQueue eventQueue = new EventQueue();
private long eventCounter = 0L;

private transient GalaxySupport galaxySupport;

private final ArrayList<RealTimeEvent> pastEvents = new ArrayList<RealTimeEvent>();
private final TreeSet<RealTimeEvent> futureEvents = new TreeSet<RealTimeEvent>();

private final long startTime = System.currentTimeMillis();
private boolean running = false;
private long wallClockOffset = 0;
private long simulationOffset = 0;
private boolean shouldRun = false;

private long time;

private boolean isAction = false;

public Galaxy(Storage galaxyControl, GalaxyBuilder builder, Random rand, PlayerStartConfiguration startConfiguration)
{
	this.galaxyId = new byte[128];
	new SecureRandom().nextBytes(galaxyId);
	
	this.galaxyControl = galaxyControl;
	this.data = builder.createSystems(this);
	this.rand = rand;
	this.startConfiguration = startConfiguration;
	
	generateCoordinates();
	pastEvents.add(new RealTimeEvent(startTime, RealTimeEventType.CREATE));
	galaxySupport = new GalaxySupport(this);
}

public static Galaxy dummyGalaxy()
{ return GalaxyBuilder.buildGalaxy(new MemoryStorageManager(), new Random(), 0, PlayerStartConfiguration.NOTHING); }


@Restricted
public void check() throws InvalidDatabaseException
{
	if (galaxyControl == null)
		throw new InvalidDatabaseException("galaxyControl must not be null!");
	if (galaxySupport == null)
		throw new InvalidDatabaseException("galaxySupport must not be null!");
	
	for (SolarSystem s : this)
		s.check(this);
	for (Player p : players)
		p.check();
	for (Alliance a : alliances)
		a.check();
}

@Restricted
public byte[] getGalaxyId()
{ return galaxyId.clone(); }

public long getTime()
{ return time; }

@Restricted
public BattleEventListener createBattleLogger(Battle battle)
{ return new GameBattleLogger(getStorageManager(), battle); }

@Restricted
public ColonyEventListener createColonyLogger(Colony colony)
{ return new ColonyEventAdapter(new GameColonyLogger(getStorageManager(), colony)); }


private void logMoney(final int pid, final long attime, final MoneyReason reason, final long amount)
{
	getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				MoneyData md = MoneyData.getMoneyData(pid);
				md.logMoney(attime, reason, amount);
			}
		});
}

@Restricted
public void logMoney(Player who, long attime, MoneyReason reason, long amount)
{ logMoney(who.getPid(), attime, reason, amount); }

@Restricted
public void logMoney(Player who, long attime, MoneyReason reason, long amount, long diff)
{ logMoney(who, attime, reason, amount); }

@Restricted
public void removeAgentPeer(Agent agent, final long attime)
{
	final String pid = agent.getId();
	final long realTime = System.currentTimeMillis();
	getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				ObserverReport.getAgentData(pid).setStopTime(attime, realTime);
			}
		});
}

@Restricted
public void addAgentPeer(Agent agent, long attime)
{
	final String pid = agent.getId();
	final String position = agent.getPosition().toString();
	final int id = agent.findColony().getOwner().getPid();
	final long astartTime = agent.getStartTime();
	final long astartRealTime = agent.getStartRealTime();
	final long seed = agent.getSeed();
	getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				ObserverReport.createAgentData(pid, position, id, astartTime, astartRealTime, seed);
			}
		});
}

@Restricted
public void requestAgentReport(Agent agent, long attime)
{
	Colony c = agent.findColony();
	String position = agent.getPosition().toString();
	int id = c.getOwner().getPid();
	long realtime = System.currentTimeMillis();
	UnitMap units = c.getUnitsCopy();
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		float error = (float) (1 + 0.3*(agent.getGalaxy().getRandomFloat() - 0.5)); //0.85 - 1.15
		units.set(it.key(), Math.round(it.value()*error));
	}
	
	final AgentReport agentData = new AgentReport(position, id, attime, realtime, units);
	final String binding = AgentReport.getBinding(agent.getId());
	getStorageManager().set(binding, agentData);
}

@Restricted
public Storage getStorageManager()
{ return galaxyControl; }

@Restricted
public void execute(Task task)
{ getStorageManager().execute(task); }

public void setInstantMessage(MultiI18nMessage message)
{ metaGalaxy.instantMessage = message; }

public MultiI18nMessage getInstantMessage()
{ return metaGalaxy.instantMessage; }

public int createSurveyId()
{ return metaGalaxy.getSurveyID(); }

public void setSurvey(Survey survey)
{ metaGalaxy.survey = survey; }

public Survey getSurvey()
{ return metaGalaxy.survey; }

public Survey getKomissionSurvey()
{ return metaGalaxy.kommission; }

@Restricted
public synchronized long beforeWriting()
{
	// Stop-Start-Sequenz
	simulationOffset = getSimulationTime();
	wallClockOffset = System.currentTimeMillis();
	shouldRun = running;
	if (running) pause();
	return getWallClockTime();
}

@Restricted
public long afterWriting(long beforeTime)
{
	if (shouldRun) resume();
	return getWallClockTime()-beforeTime;
}

@Restricted
public void afterReading(Storage newGalaxyControl) throws InvalidDatabaseException
{
	if (newGalaxyControl == null)
		throw new NullPointerException("Illegal argument!");
	galaxyControl = newGalaxyControl;
	galaxySupport = new GalaxySupport(this);
	check();
	
	time = getSimulationTime();
	execute();
	if (shouldRun) resume();
}

public int getRandomInt(int min, int max)
{ return rand.nextInt(max-min+1)+min; }

public long getRandomLong()
{ return rand.nextLong(); }

public float getRandomFloat()
{ return rand.nextFloat(); }

public double getRandomGaussian()
{ return rand.nextGaussian(); }

public String generateUniqueID()
{ return uidGenerator.generateUniqueID(); }

public UnitSystem getUnitSystem()
{ return UnitSystemImpl.getImplementation(); }

public int getMaxPlayers()
{ return 8000; }

@Restricted
public void buildUnit(Unit typ)
{ builtUnits.increase(typ); }

public UnitMap getBuiltUnits()
{ return builtUnits; }

public DiplomaticRelation getDiplomaticRelation()
{ return galaxySupport.getDiplomaticRelation(); }

public DiplomacyEntry getDiplomacyEntry(ContractParty a, ContractParty b)
{ return galaxySupport.getDiplomaticRelation().getEntry(a, b); }

public SolarSystem getSystem(int which)
{
  if ((which < 0) || (which >= data.length))
    throw new InvalidPositionException("Invalid System: "+which);
  return data[which];
}

public int size()
{ return data.length; }

public List<Player> getPlayers()
{ return players; }

public List<Alliance> getAlliances()
{ return alliances; }

public ContractProposalList getContractProposals()
{ return contractProposals; }

public ContractProposal getContractProposal(int id)
{ return contractProposals.get(id); }

@Restricted
public AccessLevel getAccessLevel(Identity id, Player player)
{ return galaxyACL.getAccessLevel(id, player); }

@Restricted
public PlayerEntry[] getAccessiblePlayers(Identity id)
{ return galaxyACL.getAccessiblePlayers(id); }

@Restricted
public boolean mayCreatePlayer(Identity id)
{ return getAccessiblePlayers(id).length < MAX_PLAYERS_PER_IDENTITY; }

@Restricted
public void allowAccess(Identity id, Player player, AccessLevel level)
{
	logger.fine("Allow \""+id+"\" access \""+level+"\" to player \""+player+"\"");
	galaxyACL.add(id, player, level);
}

public synchronized boolean createPlayer(Identity id, String nick, Locale locale)
{
	logger.fine("Identity \""+id+"\" tries to create player \""+nick+"\"");
	if (id == null)
		throw new NullPointerException();
	if (nick == null)
		throw new NullPointerException();
	if (!mayCreatePlayer(id))
		return false;
	if (countPlayers() > getMaxPlayers())
		return false;
	if (findPlayerByName(nick) != null)
		return false;
	
	try
	{
		schedex(new PlayerCreateAction(id, nick, locale));
		Player who = findPlayerByName(nick);
		return who != null;
	}
	catch (Exception e)
	{
		logger.log(Level.SEVERE, "Player creation failed", e);
		return false;
	}
}

public synchronized Player findPlayerByPid(int pid)
{
	Iterator<Player> it = players.iterator();
	while (it.hasNext())
	{
		Player p = it.next();
		if (pid == p.getPid())
			return p;
	}
	return null;
}

public synchronized Player findPlayerByName(String name)
{
	Iterator<Player> it = players.iterator();
	while (it.hasNext())
	{
		Player p = it.next();
		if (name.equalsIgnoreCase(p.getName()))
			return p;
	}
	return null;
}

public synchronized Alliance findAllianceById(int id)
{
	Iterator<Alliance> it = alliances.iterator();
	while (it.hasNext())
	{
		Alliance a = it.next();
		if (a.getId() == id)
			return a;
	}
	return null;
}

public synchronized Alliance findAllianceByName(String s)
{
	Iterator<Alliance> it = alliances.iterator();
	while (it.hasNext())
	{
		Alliance a = it.next();
		if (s.equalsIgnoreCase(a.getName()) || s.equalsIgnoreCase(a.getShortName()))
			return a;
	}
	return null;
}

public synchronized Colony findColonyByCoords(String coords)
{
	try
	{ return Position.decode(coords).findColony(this); }
	catch (InvalidPositionException e)
	{ return null; }
}

public synchronized Planet findPlanetByCoords(String coords)
{
	try
	{ return Position.decode(coords).findPlanet(this); }
	catch (InvalidPositionException e)
	{ return null; }
}

public synchronized SolarSystem findSystemByCoords(String coords)
{
	try
	{ return Position.decode(coords).findSystem(this); }
	catch (InvalidPositionException e)
	{ return null; }
}

public ScoreManager<Player> getPlayerHighscore()
{ return galaxySupport.getPlayerHighscore(); }

public ScoreManager<Alliance> getAllianceHighscore()
{ return galaxySupport.getAllianceHighscore(); }

public Colony findColony(Position position)
{ return position.findColony(this); }

public Planet findPlanet(Position position)
{ return position.findPlanet(this); }

@Override
public Iterator<SolarSystem> iterator()
{
  return new Iterator<SolarSystem>()
    {
      int currentSystem = 0;
      @Override
      public boolean hasNext()
      { return currentSystem < size(); }
      @Override
      public SolarSystem next()
      {
        if (!hasNext()) throw new NoSuchElementException("No more elements!");
        return getSystem(currentSystem++);
      }
      @Override
      public void remove()
      { throw new UnsupportedOperationException("Not supported!"); }
    };
}

public Iterator<SolarSystem> systemIterator()
{ return iterator(); }

public Iterator<Colony> colonyIterator()
{
  return new Iterator<Colony>()
    {
      int currentSystem = 0;
      int currentPlanet = 0;
      int currentColony = 0;
      Colony cache;
      
      @Override
      public boolean hasNext()
      {
        if (cache != null) return true;
        try
        {
          cache = next();
        }
        catch (NoSuchElementException ignored)
        { cache = null; }
        if (cache != null) return true;
        return false;
      }
      
      @Override
      public Colony next()
      {
        if (cache != null)
        {
        	Colony temp = cache;
          cache = null;
          return temp;
        }
        while (currentSystem < size())
        {
          SolarSystem s = data[currentSystem];
          while (currentPlanet < s.length())
          {
            Planet p = s.getPlanet(currentPlanet);
            while (currentColony < p.length())
            {
              Colony c = p.getColony(currentColony++);
              if (c != null)
                return c;
            }
            currentColony = 0;
            currentPlanet++;
          }
          currentPlanet = 0;
          currentSystem++;
        }
        throw new NoSuchElementException("No more elements!");
      }
      
      @Override
      public void remove()
      { throw new UnsupportedOperationException("Not supported!"); }
    };
}

public Iterator<Player> playerIterator()
{ return players.iterator(); }

public Iterator<Alliance> allianceIterator()
{ return alliances.iterator(); }

@Restricted
public synchronized void refreshGalaxyViews()
{
	Iterator<Alliance> ai = alliances.iterator();
	while (ai.hasNext()) ai.next().resetGalaxyView();
	Iterator<Player> pi = players.iterator();
	while (pi.hasNext()) pi.next().updateGalaxyView();
}

public synchronized int countPlayers()
{ return players.size(); }

public synchronized int getPlayerCount()
{ return players.size(); }

public synchronized int countAlliances()
{ return alliances.size(); }

public synchronized int countSystems()
{ return data.length; }

public synchronized int countColonies()
{ return 0; }

public synchronized int countPlanets()
{ return 0; }

public synchronized int countColonizedPlanets()
{ return 0; }


// time functions
public long getStartTime()
{ return startTime; }

public synchronized long getWallClockTime()
{ return System.currentTimeMillis() / 1000L; }

public synchronized long getSimulationTime()
{
	if (running)
		return (speed*(System.currentTimeMillis()-wallClockOffset)) / 1000L + simulationOffset;
	return simulationOffset;
}

private synchronized void resume()
{
	if (running) throw new IllegalStateException("Simulation already running!");
	wallClockOffset = System.currentTimeMillis();
	running = true;
}

private synchronized void pause()
{
	if (!running) throw new IllegalStateException("Simulation already stopped!");
	simulationOffset = getSimulationTime();
	running = false;
}

@Restricted
public synchronized void setSpeed(long newSpeed)
{
	if (newSpeed < 1) throw new IllegalArgumentException();
	if (newSpeed > 100) throw new IllegalArgumentException();
	// Start-Stop-Sequenz
	simulationOffset = getSimulationTime();
	wallClockOffset = System.currentTimeMillis();
	this.speed = newSpeed;
}

public synchronized long getSpeed()
{ return speed; }

public synchronized boolean isRunning()
{ return running; }

@Restricted
public synchronized void advanceTime(long howmuch)
{
	simulationOffset += howmuch;
	
	if (running)
	{
		pause();
		update();
		resume();
	}
	else
		update();
}

private void createSorted()
{
	sortedData = data.clone();
	
	Arrays.sort(sortedData, new Comparator<SolarSystem>()
		{
			private double fromCenter(SolarSystem s)
			{ return Math.sqrt((double)s.getX()*s.getX()+(double)s.getY()*s.getY()); }
			@Override
      public int compare(SolarSystem s1, SolarSystem s2) 
			{
				double r1 = fromCenter(s1);
				double r2 = fromCenter(s2);
				boolean outerRing1 = r1 > Constants.GALAXY_RADIUS/2;
				boolean outerRing2 = r2 > Constants.GALAXY_RADIUS/2;
				if (outerRing1 && !outerRing2) return -1;
				if (!outerRing1 && outerRing2) return 1;
				if (outerRing1)
				{ // Systems in the outer ring are sorted by their angle.
					double theta1 = Math.abs(Math.atan2(s1.getX(), s1.getY()));
					double theta2 = Math.abs(Math.atan2(s2.getX(), s2.getY()));
					if (theta1 < theta2) return -1;
					if (theta1 > theta2) return 1;
				}
				else
				{ // Systems in the inner ring are sorted by their distance.
					if (r1 < r2) return -1;
					if (r1 > r2) return 1;
				}
				// Resolve conflicts.
				if (s1.getX() < s2.getX()) return -1;
				if (s1.getX() > s2.getX()) return 1;
				if (s1.getY() < s2.getY()) return -1;
				if (s1.getY() > s2.getY()) return 1;
				throw new AssertionError("Better remake that Galaxy.");
			}
		});
}

private void generateCoordinates()
{
	SolarSystem sys;
	for (int i = 0; i < data.length; i++)
	{
		boolean invalid = true;
		sys = data[i];
		do
		{
			double r = rand.nextDouble();
			double theta = rand.nextDouble()*2*Math.PI;
			
			double x = Math.sin(theta) * r * Constants.GALAXY_RADIUS;
			double y = Math.cos(theta) * r * Constants.GALAXY_RADIUS;
			double z = 0; // 50*(2*rand.nextDouble()-1)*f(r);
			
			sys.setXYZ((long) x, (long) y, (long) z);
			
			invalid = false;
			
			for (int j = 0; j < i; j++)
				if ((Math.abs(data[j].getX()-sys.getX()) < 8) &&
					  (Math.abs(data[j].getY()-sys.getY()) < 8))
					invalid = true;
		} while (invalid);
	}
	
	createSorted();
}


// event handling
@Restricted
public void removeAllEvents(Object referer)
{ eventQueue.removeEvents(referer); }

@Restricted
public void deleteEvent(EventHandle handle)
{ eventQueue.deleteEvent(handle); }

@Restricted
public EventHandle addEvent(long attime, Event e)
{ return eventQueue.addEvent(attime, e); }


// information-message handling
@Restricted
public void addInfoListener(InfoListener l)
{ galaxySupport.infoListeners.add(l); }

@Restricted
public synchronized void dropInfo(Player who, Information info)
{
//	logger.fine("Filing information for "+who+": "+info);
	try
	{
		for (int i = 0; i < galaxySupport.infoListeners.size(); i++)
		{
			InfoListener l = galaxySupport.infoListeners.get(i);
			l.notifyInfo(who, info);
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
}

// FIXME: use time?
@Restricted
public void dropInfo(Player who, long attime, InfoEnum key, Object... objects)
{ dropInfo(who, Information.createInstance(who.getLocale(), attime, key, objects)); }

@Restricted
public InfoTranslator getInfoTranslator(final Player who, final long attime)
{
	Object result = Proxy.newProxyInstance(Galaxy.class.getClassLoader(),
			new Class<?>[] {InfoTranslator.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
			{
				InfoEnum key = method.getAnnotation(InfoTranslator.OldMeta.class).value();
				dropInfo(who, Information.createInstance(who.getLocale(), attime, key, args));
				return null;
			}
		});
	return (InfoTranslator) result;
}


public synchronized List<RealTimeEvent> getPastEvents()
{ return pastEvents; }

public synchronized Set<RealTimeEvent> getFutureEvents()
{ return futureEvents; }

@Restricted
public synchronized void execute(RealTimeEventType type)
{
	switch (type)
	{
		case CREATE : break;
		case PAUSE  : pause(); break;
		case RESUME : resume(); break;
		case UPDATE : break;
		case EMERGENCY_HALT : pause(); break;
	}
	pastEvents.add(new RealTimeEvent(System.currentTimeMillis(), type));
}

@Restricted
public synchronized long getEventCounterAndReset()
{
	long result = eventCounter;
	eventCounter = 0;
	return result;
}

@Restricted
public synchronized void update()
{
	long updatetime = getSimulationTime();
	Event event;
	while ((event = eventQueue.getEvent(updatetime)) != null)
	{
		eventCounter++;
		long eventTime = eventQueue.getCurrentEventTime();
		// Mehr als 60 Sekunden Verspaetung? Dann anhalten!
		if (running && (updatetime-eventTime > speed*60))
		{
			logger.fine("Simulation-Stop: "+updatetime+" "+eventTime);
			execute(RealTimeEventType.EMERGENCY_HALT);
		}
		
		try
		{
			event.activate(eventTime);
		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, "Exception caught", e);
		}
	}
	
	time = updatetime;
	
	if (futureEvents.size() > 0)
	{
		updatetime = System.currentTimeMillis();
		RealTimeEvent rtevent = futureEvents.first();
		if (rtevent.getTime() < updatetime)
		{
			futureEvents.remove(rtevent);
			RealTimeEventType type = rtevent.getType();
			execute(type);
		}
	}
	
	execute();
}

@Restricted
public synchronized void schedule(Action action)
{ galaxySupport.actionList.add(action); }

@Restricted
public synchronized void addRealTimeEvent(RealTimeEvent event)
{ futureEvents.add(event); }

@Restricted
public synchronized int execute()
{
	int count = 0;
	while (galaxySupport.actionList.size() > 0)
	{
		Action action = galaxySupport.actionList.removeFirst();
		isAction = true;
		try
		{
			count++;
			action.execute(this);
		}
		catch (Exception e)
		{ logger.log(Level.SEVERE, "Exception ignored!", e); }
		finally
		{ isAction = false; }
	}
	return count;
}

@Restricted
public synchronized void schedex(Action action)
{
	isAction = true;
	try
	{
		action.execute(this);
		execute();
	}
	finally
	{ isAction = false; }
}


// Action methods (must be public, unfortunately)
@Restricted
public Player createPlayer(PlayerDescriptor descriptor)
{
	if (!isAction) throw new IllegalStateException();
	int pid = playerId++;
	Player player = new Player(this, time, pid, descriptor);
	players.add(player);
	galaxySupport.getPlayerHighscore().addObject(player);
	if (descriptor.id != null)
		allowAccess(descriptor.id, player, AccessLevel.FULL);
	getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				RegistrationHistory.getRegistrationHistory().logRegistrations(players.size());
			}
		});
	// Set research.
	for (ResearchEnum research : ResearchEnum.values())
	{
		int level = startConfiguration.getResearch().get(research);
		for (int i = 0; i < level; i++)
			player.increaseResearch(research);
	}
	int colonies = startConfiguration.getColonies();
	if (colonies > 0)
	{
		// Create colonies.
		for (int i = 0; i < colonies; i++)
			schedex(new PlayerColonyStartAction(player));
		// Create buildings and units for each colony.
		EnumIntMap<BuildingEnum> buildings = startConfiguration.getBuildings();
		UnitMap units = startConfiguration.getUnits();
		for (int i = 0; i < player.getColonies().size(); i++)
		{
			Colony colony = player.getColonies().get(i);
			colony.updateBeforeEvent(time);
			for (BuildingEnum building : BuildingEnum.values())
			{
				int max = buildings.get(building);
				for (int level = 0; level < max; level++)
					colony.increaseBuilding(0, building);
			}
			for (UnitIterator it = units.unitIterator(); it.hasNext(); )
			{
				it.next();
				colony.silentIncreaseUnits(it.key(), it.value());
			}
			colony.updateAfterEvent();
			colony.updateBeforeEvent(time);
			colony.addPeople(time, startConfiguration.getPopulation());
			colony.updateAfterEvent();
		}
	}
	return player;
}

@Restricted
public Player createTestPlayer(Locale locale)
{
	PlayerDescriptor descriptor =
		new PlayerDescriptor(null, "Testplayer", locale, HumanPlayerController.class);
	try
	{
		isAction = true;
		return createPlayer(descriptor);
	}
	finally
	{ isAction = false; }
}

@Restricted
public void createAlliance(long ctime, Player who, String name, String shortName)
{
	if (!isAction) throw new IllegalStateException();
	if (ctime != time) throw new IllegalStateException("Argh, invalid time!");
	Alliance alliance = new Alliance(this, allianceId++, name, shortName);
	alliance.add(time, who);
	who.setRank(alliance.getRanks().get(0));
	alliances.add(alliance);
	galaxySupport.getAllianceHighscore().addObject(alliance);
}

private Planet findNextStartPlanet()
{
	for (int i = settleSystem; i < sortedData.length; i++)
	{
		for (int j = settlePlanet; j < sortedData[i].length(); j++)
		{
			Planet p = sortedData[i].getPlanet(j);
			if ((p.getMaxPlayers() > 0) && p.hasSpace())
			{
				settlePlanet = j+1;
				return p;
			}
		}
		settleSystem++;
		settlePlanet = 0;
	}
	return null;
}

@Restricted
public Colony findEmptyColony(Player who)
{
	if (!isAction) throw new IllegalStateException();
	if (settlePlanets == null)
	{
		settlePlanets = new Planet[40];
		for (int i = 0; i < settlePlanets.length; i++)
			settlePlanets[i] = findNextStartPlanet();
	}
	
	Planet p = null;
	for (int i = 0; i < settlePlanets.length; i++)
	{
		int tryMe = getRandomInt(0, settlePlanets.length-1);
		p = settlePlanets[tryMe];
		if ((p == null) || (p.getMaxPlayers() == 0) || !p.hasSpace())
		{
			p = findNextStartPlanet();
			settlePlanets[tryMe] = p;
		}
		if (p != null) break;
	}
	
	if (p == null) return null;
	
	p.decreaseMaxPlayers();
	p.getSolarSystem().setVisible();
	Colony c = p.createStartColony(who, time);
	return c;
}


// called by alliance when it is removed
@Restricted
public void removeAlliance(Alliance alliance)
{
	alliances.remove(alliance);
	if (galaxySupport.getAllianceHighscore() != null)
		galaxySupport.getAllianceHighscore().removeObject(alliance);
}

}
