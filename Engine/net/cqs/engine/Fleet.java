package net.cqs.engine;

import java.io.Serializable;
import java.util.logging.Level;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.Resource;
import net.cqs.config.ResourceEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.base.Event;
import net.cqs.engine.base.EventHandle;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.JoinAction;
import net.cqs.engine.fleets.FleetAbortException;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.engine.fleets.FleetCommandList;
import net.cqs.engine.fleets.FleetContinueException;
import net.cqs.engine.fleets.FleetDisbandException;
import net.cqs.engine.fleets.FleetJoinBattleException;
import net.cqs.engine.fleets.FleetMoveCommand;
import net.cqs.engine.fleets.FleetNOPCommand;
import net.cqs.engine.fleets.FleetRepeatException;
import net.cqs.engine.fleets.FleetStopException;
import net.cqs.engine.fleets.FleetWithdrawCommand;
import net.cqs.engine.fleets.Speed;
import net.cqs.engine.human.HumanFleetController;
import net.cqs.engine.messages.PlayerLogMessage;
import net.cqs.engine.repair.EmptyFleetRemoveAction;
import net.cqs.util.EnumLongMap;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class Fleet implements Serializable
{

	private static class CommandEvent	extends Event
	{
		private static final long serialVersionUID = 1L;
		private final Fleet fleet;
		public CommandEvent(Fleet fleet)
		{ this.fleet = fleet; }
		@Override
		public void activate(long time)
		{ fleet.activate(time); }
	}

private static final long serialVersionUID = 1L;

/**
 * When adding variables, don't forget to add the initialisation
 * to the copy constructor (if necessary)
 */

private final Galaxy galaxy;
transient Player owner = null; // set by Player.validateObject

private final int id;
private Position position;
private int desktop = 0;
private String name = "Namenlos";
private FleetController controller;

private EventHandle nextEventHandle;
private long lastEventTime = 0;
private long nextEventTime = 0;
private long nextCompleteTime = 0; // Shown time. Should usually be = nextEventTime!
private PlayerLogMessage errormessage = null;

private boolean virtual = false; // simulation fleet?

// Current state of the fleet
private FleetState state = FleetState.WAITING;

private boolean cloaked   = false;
private boolean landed    = true;
private boolean autoFleet = false; // automatically generated defense fleet?
private boolean noUpkeep  = false; // pay no upkeep?

private FleetCommand executingCommand = null;

private UnitMap units  = new UnitMap();
private EnumLongMap<ResourceEnum> cargo = EnumLongMap.of(ResourceEnum.class);

private long radius;
private long speed;
private long theta;

private long radiusGoal;
private long speedGoal;
private long thetaGoal;

private long lastUpkeepTime;
private long lastUpkeep;
private long totalTime = 0;

private Fleet(Galaxy galaxy, Position position, Player owner, long time)
{
	this.galaxy = galaxy;
	this.lastUpkeepTime = time;
	
	this.position = position;
	this.owner = owner;
	this.controller = owner.getController().createFleetController(this);
	
	this.id = owner.createFleetId();
	this.name = ("ID: "+Integer.toString(id)).intern();
}

private Fleet(Fleet other)
{
	this.galaxy = other.galaxy;
	lastUpkeepTime = other.lastUpkeepTime;
	totalTime = other.totalTime;
	
	position = other.position;
	owner = other.owner;
	controller = owner.getController().createFleetController(this);
	
	id = owner.createFleetId();
	name = ("ID: "+Integer.toString(id)).intern();
	
	landed = other.landed;
	cloaked = other.cloaked;
	noUpkeep = other.noUpkeep;
}

public void check() //throws InvalidDatabaseException
{
	if (name == null || "Namenlos".equals(name))
		name = ("ID: "+Integer.toString(id)).intern();
	
	if (nextCompleteTime == 0)
		nextCompleteTime = nextEventTime;
	if (desktop > Constants.MAX_DESKTOP)
		desktop = 0;
	if (units.sum() == 0)
		galaxy.schedule(new EmptyFleetRemoveAction(this));
}

public int getId()
{ return id; }

public void setPosition(Position position)
{ this.position = position; }

public Galaxy getGalaxy()
{ return galaxy; }

public Player getOwner()
{ return owner; }

public Position getPosition()
{ return position; }

public long getFlightDuration()
{ return galaxy.getTime()-getFlightStartTime(); }

public long getFlightStartTime()
{ return lastUpkeepTime-totalTime; }

@Deprecated
public void setController(FleetController controller)
{ this.controller = controller; }

public FleetController getController()
{ return controller; }

public void setState(FleetState state)
{ this.state = state; }

public FleetState getState()
{ return state; }

public void setLanded(boolean landed)
{ this.landed = landed; }

public boolean isLanded()
{ return landed; }

public boolean isLoopEnabled()
{ return ((HumanFleetController) controller).isLoopEnabled(); }

public boolean showTimer()
{
	if (state == FleetState.WAITING) return false;
	if (state == FleetState.BLOCKING)
		return nextEventTime >= getGalaxy().getTime();
	return true;
}

public boolean isFighting()
{ return state.isHostileAction(); }

public boolean isInvading()
{ return (state == FleetState.INVADING); }

public boolean isStopped()
{ return state == FleetState.WAITING; }

public long getTimeEstimate()
{ return nextCompleteTime; }

public boolean isError()
{ return errormessage != null; }

public String getErrorMessage()
{ return errormessage.getI18nMessage(); }

public int getCommandListSize()
{ return ((HumanFleetController) controller).getOrders().size(); }

@Deprecated
public void setExecutingCommand(FleetCommand executingCommand)
{ this.executingCommand = executingCommand; }

public FleetCommand getExecutingCommand()
{ return executingCommand; }

public UnitMap getUnits()
{ return units; }

public int getUnits(Unit unit)
{ return units.get(unit); }

public FleetCommandList getOrders()
{ return ((HumanFleetController) controller).getOrders(); }

public int getNextCommand()
{ return ((HumanFleetController) controller).getNextCommand(); }

public UnitIterator getUnitIterator(UnitSelector selector)
{ return units.unitIterator(selector); }

public float getPower(UnitSelector selector)
{ return units.getPower(selector); }

public boolean containsUnits(UnitSelector selector)
{ return units.contains(selector); }

@Restricted
public void decreaseUnit(Unit unit)
{ units.decrease(unit); }

@Restricted
public void decreaseUnit(Unit unit, int amount)
{ units.decrease(unit, amount); }

@Restricted
public void addUnits(UnitMap map)
{ units.add(map); }

public void addUnitsTo(UnitMap target)
{ target.add(units); }

@Restricted
public void subtractUnits(UnitMap map)
{ units.subtract(map); }

public void subtractUnitsFrom(UnitMap target)
{ target.subtract(units); }

public UnitMap getUnitsCopy()
{ return new UnitMap(units); }

public String getSerializedUnits()
{ return units.serialize(); }

//public int getCargo(int resource)
//{ return cargo.get(ResourceEnum.get(resource)); }

public long getCargo(ResourceEnum resource)
{ return cargo.get(resource); }

public void addCargo(int resource, int amount)
{ cargo.increase(ResourceEnum.get(resource), amount); }

public void addCargo(ResourceEnum resource, long amount)
{ cargo.increase(resource, amount); }

public EnumLongMap<ResourceEnum> getCargoCopy()
{ return EnumLongMap.copyOf(cargo); }

public void setNextEventTime(long nextEventTime)
{ this.nextEventTime = nextEventTime; }

public long getNextEventTime()
{ return nextEventTime; }

public void setNextCompleteTime(long nextCompleteTime)
{ this.nextCompleteTime = nextCompleteTime; }

public long getNextCompleteTime()
{ return nextCompleteTime; }

public RotationState getCurrentRotation()
{ return new RotationState(radius, speed, theta); }

public void setCurrentRotation(RotationState state)
{
	radius = state.getRadius();
	speed = state.getSpeed();
	theta = state.getTheta();
}

public RotationState getRotationGoal()
{ return new RotationState(radiusGoal, speedGoal, thetaGoal); }

public void setRotationGoal(RotationState state)
{
	radiusGoal = state.getRadius();
	speedGoal = state.getSpeed();
	thetaGoal = state.getTheta();
}

public boolean isAtPlanet(Planet p)
{
	if (position.specificity() >= Position.PLANET)
		return position.getPlanetNumber() == p.getPosition().getPlanetNumber();
	return false;
}

public void notifyRemove()
{
	if (nextEventHandle != null)
		galaxy.deleteEvent(nextEventHandle);
}

public void log(ErrorCode code, Object... objects)
{
	PlayerLogMessage message = PlayerLogMessage.createInstance(owner, code, objects);
	errormessage = message;
	owner.log(message);
}

private double normalize(double phi)
{ return phi % (2*Math.PI); }

public double x(long time)
{
	if (time > nextEventTime)
	{
		double phi = normalize((10.0*time)/speedGoal - thetaGoal/SolarSystem.thetaFactor);
		return Math.cos(phi)*radiusGoal;
	}
	else if (time < lastEventTime)
	{
		double phi = normalize((10.0*time)/speed - theta/SolarSystem.thetaFactor);
		return Math.cos(phi)*radius;
	}
	else
	{
		double srcTheta = normalize((10.0*lastEventTime)/speed - theta/SolarSystem.thetaFactor);
		double destTheta = normalize((10.0*nextEventTime)/speedGoal - thetaGoal/SolarSystem.thetaFactor);
		double f = ((double) (nextEventTime-time))/(nextEventTime-lastEventTime);
		if (f < 0.5) f = 2*f*f;
		else f = -1 + 4*f - 2*f*f;
		double phi = srcTheta*f+destTheta*(1-f);
		double crad = radius*f+radiusGoal*(1-f);
		return Math.cos(phi)*crad;
	}
}

public long longX(long time)
{ return (long) x(time); }

public double y(long time)
{
	if (time > nextEventTime)
	{
		double phi = normalize((10.0*time)/speedGoal - thetaGoal/SolarSystem.thetaFactor);
		return Math.sin(phi)*radiusGoal;
	}
	else if (time < lastEventTime)
	{
		double phi = normalize((10.0*time)/speed - theta/SolarSystem.thetaFactor);
		return Math.sin(phi)*radius;
	}
	else
	{
		double srcTheta = normalize((10.0*lastEventTime)/speed - theta/SolarSystem.thetaFactor);
		double destTheta = normalize((10.0*nextEventTime)/speedGoal - thetaGoal/SolarSystem.thetaFactor);
		double f = ((double) (nextEventTime-time))/(nextEventTime-lastEventTime);
		if (f < 0.5) f = 2*f*f;
		else f = -1 + 4*f - 2*f*f;
		double phi = srcTheta*f+destTheta*(1-f);
		double crad = radius*f+radiusGoal*(1-f);
		return Math.sin(phi)*crad;
	}
}

public long longY(long time)
{ return (long) y(time); }

public void unscheduleEvent()
{
	if (nextEventHandle == null)
		throw new IllegalStateException("Not scheduled!");
	galaxy.deleteEvent(nextEventHandle);
	nextEventHandle = null;
	nextEventTime = 0;
	executingCommand = null;
}

public void tryUnscheduleEvent()
{ if (nextEventHandle != null) unscheduleEvent(); }

public boolean isPlanetaryFleet()
{ return units.interplanetarySum() == 0; }

public boolean containsPlanetaryUnits()
{ return units.planetarySum() > 0; }

public boolean containsInterplanetaryUnits()
{ return units.interplanetarySum() > 0; }

public boolean containsInterstellarUnits()
{ return units.interstellarSum() > 0; }

public void disableUpkeep(long time)
{
	if (noUpkeep) return;
	payUpkeep(time);
	noUpkeep = true;
}

public void enableUpkeep(long time)
{
	if (!noUpkeep) return;
	lastUpkeepTime = time;
	noUpkeep = false;
}

public void join(Fleet other)
{
	units.add(other.units);
	for (ResourceEnum resource : ResourceEnum.values())
		cargo.increase(resource, other.cargo.get(resource));
	
	other.units.clear();
	other.cargo.clear();
	
	if (other.totalTime > totalTime)
		totalTime = other.totalTime;
}

static final long OVERFLOW_CHECK = Long.MAX_VALUE/4;


// Money
public long calculateIncome()
{ return 0; }

public long calculateUpkeep()
{
	if (noUpkeep) return 0;
	
	long tempTime = totalTime;
	if (tempTime > 86400L*7L) tempTime = 86400L*7L;
	double costfactor = (tempTime/86400L) + 1.0d;
	if (costfactor > 7) costfactor = 7;
	
	double sum = 0;
	
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		sum += it.value() * it.key().getUpkeep();
	}
	
	double result = (Constants.UNIT_UPKEEP / 100.0d) * sum * costfactor;
	
	if (Double.isNaN(sum) || Double.isNaN(result))
	{
		Galaxy.logger.log(Level.SEVERE, "Illegal result",
				new RuntimeException("Upkeep number is a NaN 0 for "+this+" with sum="+sum+" costfactor="+costfactor+" result="+result));
		result = 0;
	}
	
	if (result > OVERFLOW_CHECK)
	{
		Galaxy.logger.log(Level.SEVERE, "Illegal result",
				new RuntimeException("Upkeep number overflow 1 for "+this+" with sum="+sum+" costfactor="+costfactor+" result="+result));
		result = OVERFLOW_CHECK;
	}
	
	long longResult = Math.round(result);
	if (longResult > OVERFLOW_CHECK)
	{
		Galaxy.logger.log(Level.SEVERE, "Illegal result",
				new RuntimeException("Upkeep number overflow 2 for "+this+" with sum="+sum+" costfactor="+costfactor+" result="+result));
		longResult = OVERFLOW_CHECK;
	}
	
	if (longResult < 0)
	{
		Galaxy.logger.log(Level.SEVERE, "Illegal result",
				new RuntimeException("Upkeep number overflow 3 for "+this+" with sum="+sum+" totaltime="+totalTime+" costfactor="+costfactor+" result="+result));
		longResult = 0;
	}
	
	return longResult;
}

public long payUpkeep(long time)
{
	if (noUpkeep) return 0;
	
	if (!owner.hasMoney())
	{
		// TODO: Remove Random Unit
	}
	
	long timeDiff = time-lastUpkeepTime;
	if (timeDiff <= 0) timeDiff = 1;
	long upkeep = calculateUpkeep();
	if (OVERFLOW_CHECK / timeDiff > upkeep)
	{
		double temp = (timeDiff / (60.0d*60.0d)) * upkeep;
		if (Double.isNaN(temp))
		{
			Galaxy.logger.log(Level.SEVERE, "Illegal result",
					new RuntimeException("Upkeep number is a NaN 4 for "+this+" with upkeep="+upkeep+" timeDiff="+timeDiff+" temp="+temp));
			temp = 0;
		}
		if (temp > OVERFLOW_CHECK)
		{
			Galaxy.logger.log(Level.SEVERE, "Illegal result",
					new RuntimeException("Upkeep number overflow 5 for "+this+" with upkeep="+upkeep+" temp="+temp));
			temp = OVERFLOW_CHECK;
		}
		lastUpkeep = (long) temp;
		if (lastUpkeep > OVERFLOW_CHECK)
		{
			Galaxy.logger.log(Level.SEVERE, "Illegal result",
					new RuntimeException("Upkeep number overflow 6 for "+this+" with upkeep="+upkeep+" temp="+temp));
			lastUpkeep = OVERFLOW_CHECK;
		}
		if (lastUpkeep < 0)
		{
			Galaxy.logger.log(Level.SEVERE, "Illegal result",
					new RuntimeException("Upkeep number overflow 7 for "+this+" with upkeep="+upkeep+" temp="+temp+" lastUpkeep="+lastUpkeep));
			lastUpkeep = 0;
		}
	}
	else
		lastUpkeep = (upkeep*timeDiff) / (60L*60L);
	if (time < lastUpkeepTime)
		Galaxy.logger.log(Level.SEVERE, "Illegal result", new RuntimeException("Time goes backwards!"));
	else
		lastUpkeepTime = time;
	
	totalTime += timeDiff;
	return lastUpkeep;
//	owner.getMoney(time, MoneyReason.FLEET_UPKEEP, lastUpkeep);
}

public boolean isCivil()
{
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		if (!it.key().hasSpecial(UnitSpecial.CIVIL) && (it.value() > 0))
			return false;
	}
	return true;	
}

public boolean canAttack()
{
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasSpecial(UnitSpecial.CIVIL))
			return true;
	}
	return false;
}

public boolean canAttackSpace()
{
	UnitIterator it = units.unitIterator(UnitSelector.SPACE_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasSpecial(UnitSpecial.CIVIL))
			return true;
	}
	return false;
}

public int getTotalUnits()
{ return units.sum(); }

public long getUnitAmount()
{ return units.sum(); }

public int getTotalGroundUnits()
{ return units.planetarySum(); }

public int getTotalSpaceUnits()
{ return units.interplanetarySum(); }

public long getTotalCargo()
{ return cargo.getSum(); }

public long getCargoAmount()
{ return cargo.getSum(); }

public long getCargoAmount(int which)
{ return cargo.get(ResourceEnum.get(which)); }

public long getCargoAmount(ResourceEnum resource)
{ return cargo.get(resource); }

public long getGroundUnitCapacity()
{ return units.getGroundUnitCapacity(); }

public long getSpaceUnitCapacity()
{ return units.getSpaceUnitCapacity(); }

public long getGroundUnitSize()
{ return units.getGroundUnitSize(); }

public long getSpaceUnitSize()
{ return units.getSpaceUnitSize(); }

public long getResourceSpace()
{ return units.getResourceSpace(); }

public long getStorageSpace()
{ return getResourceSpace(); }

public Speed getSpeed()
{ return new Speed(units); }

public long getDuration()
{ return nextEventTime - lastEventTime; }

public int getDesktop()
{ return desktop; }

public void setDesktop(int pos)
{
	if ((pos < 0) || (pos > Constants.MAX_DESKTOP))
		throw new IllegalArgumentException();
	desktop = pos;
}

public String getName()
{ return name; }

public void setName(String newName)
{ name = newName; }


public boolean mayLeavePlanet()
{ return getGroundUnitCapacity() >= getGroundUnitSize(); }

public boolean mayLeaveSystem()
{
  // Warp-units can only transport space units.
	// Guest account may not leave system.
  return !owner.isRestricted() &&
         (getGroundUnitCapacity() >= getGroundUnitSize()) &&
         (getSpaceUnitCapacity()  >= getSpaceUnitSize());
  
  // Warp-units may transport both space and ground units.
/*  long spaceUsed = getSpaceUnitSize();
  long spaceCapRemaining = getSpaceUnitCapacity()-spaceUsed;
  if (spaceCapRemaining < 0) return false;
  return (getGroundUnitCapacity()+spaceCapRemaining >= getGroundUnitSize());*/
}

public boolean mayCloak()
{
/*  int sum = 0;
  sum += data.get(Unit.spion);
  sum += data.get(Unit.satellit);
  sum += data.get(Unit.sonde);
  return sum == data.sum;*/
  return false;
}

public void registerWithSystem()
{
	SolarSystem s = findSystem();
	if (!s.knowsFleet(this))
		s.addFleet(this);
	if (position.specificity() >= Position.PLANET)
	{
		Planet p = findPlanet();
		setCurrentRotation(p.getRotation());
	}
	else
	{
		radius = s.getMaxRadius();
		speed = SolarSystem.radiusToSpeed(radius);
		theta = 0;
	}
	
	radiusGoal = radius;
	speedGoal = speed;
	thetaGoal = theta;
}

public void unregisterWithSystem()
{ findSystem().removeFleet(this); }

public boolean containsSpaceFleet()
{ return units.interplanetarySum() != 0; }

public Colony findColony()
{ return position.findColony(galaxy); }

public Planet findPlanet()
{ return position.findPlanet(galaxy); }

public SolarSystem findSystem()
{ return position.findSystem(galaxy); }

public void loadCargoDo(long time, int which, long howmuch, long leaverest)
{
	if (position.specificity() < Position.COLONY)
		throw new IllegalStateException("Cannot load - no colony!");
	Colony c = findColony();
	if (c.getOwner() != owner) return;
	
	long max = getStorageSpace()-cargo.getSum();
	if (max < 0) max = 0;
	
	if (howmuch < 0) howmuch = max;
	if (howmuch > max) howmuch = max;
	
	howmuch = c.removeResources(time, which, howmuch, leaverest);
	cargo.increase(ResourceEnum.get(which), howmuch);
}

public void unloadCargoDo(long time, int which, long howmuch)
{
	if (position.specificity() < Position.COLONY)
		throw new IllegalStateException("Cannot unload - no colony!");
	Colony c = findColony();
	Player p = c.getOwner();
	
	long i = cargo.get(ResourceEnum.get(which));
	if (howmuch < 0) howmuch = i;
	if (howmuch > i) howmuch = i;
	
	howmuch = c.addResources(time, which, howmuch);
	// FIXME: Integer overflow bug! (Can never actually happen...)
	cargo.decrease(ResourceEnum.get(which), (int) howmuch);
	
	if (p != owner)
	{
		owner.increaseResourcesDonated(howmuch);
		p.increaseResourcesGotten(howmuch);
	}
}

public void unloadCargoAll(long time)
{
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		unloadCargoDo(time, i, -1);
}

public void checkAndFixCargo(long time)
{
	long capacity = getStorageSpace();
	if (cargo.getSum() > capacity)
	{
		int which = Resource.MIN;
		//long amountLost = cargo.sum-capacity;
		int[] logdata = new int[4];
		while (cargo.getSum() > capacity)
		{
			if (which > Resource.MAX) return;
			long howmuch = cargo.get(ResourceEnum.get(which));
			long toomuch = cargo.getSum() - capacity;
			if (howmuch > toomuch) howmuch = toomuch;
			cargo.decrease(ResourceEnum.get(which), howmuch);
			logdata[which] += howmuch;
			which++;
		}
		galaxy.dropInfo(owner, time, InfoEnum.FLEET_LOST_CARGO, this,
				Integer.valueOf(logdata[Resource.STEEL]), Integer.valueOf(logdata[Resource.OIL]),
				Integer.valueOf(logdata[Resource.SILICON]), Integer.valueOf(logdata[Resource.DEUTERIUM]));
	}
}

public void checkAndFixUnits(long time, UnitMap unitsLost)
{
	if (landed) return;
	long capacity = getGroundUnitCapacity();
	if (capacity < getGroundUnitSize())
	{
		UnitIterator it = units.unitIterator();
		while (capacity < getGroundUnitSize())
		{
			if (!it.hasNext()) break;
			it.next();
			if (it.key().isPlanetary())
			{
				long size = it.key().getSize();
				long toomuch = (getGroundUnitSize()-capacity+size-1)/size; // aufrunden
				long howmuch = it.value()-toomuch;
				if (howmuch < 0) howmuch = 0;
				if (howmuch > it.value()) howmuch = it.value();
				int amountLost = it.value()-(int) howmuch;
				if (unitsLost != null) unitsLost.increase(it.key(), amountLost);
				it.setValue((int) howmuch);
			}
		}
	}
}

public void checkAndFixUnits(long time)
{
	UnitMap map = new UnitMap();
	checkAndFixUnits(time, map);
	if (map.sum() > 0)
		galaxy.dropInfo(owner, time, InfoEnum.FLEET_LOST_UNITS, this, Integer.valueOf(map.sum()));
}

public void stationFleet()
{
	Colony c = findColony();
	c.update(nextEventTime);
	
	// Milita doesn't return to colony.
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		if (it.key().hasSpecial(UnitSpecial.MILITIA))
			it.remove();
	}
	
	payUpkeep(nextEventTime);
	
	c.addUnits(nextEventTime, units);
	units.clear();
	
	unloadCargoAll(nextEventTime);
}

public void removeFleet()
{
	owner.increaseResourcesLost(cargo.getSum());
	unregisterWithSystem();
	owner.getFleets().remove(this);
	state = FleetState.WAITING;
}

public boolean checkFleet()
{
	if (units.sum() == 0)
	{
		removeFleet();
		return true;
	}
	return false;
}

public void disband() throws FleetDisbandException
{
	removeFleet();
	throw new FleetDisbandException();
}

private void emergencyDisableLoop(ErrorCode reason)
{
	if (controller instanceof HumanFleetController)
	{
		HumanFleetController c = (HumanFleetController) controller;
		if (c.isLoopEnabled()) owner.log(reason, this);
		c.disableLoop();
	}
}

public void prepare(long time, FleetCommand com)
{
	try
	{
		assert state == FleetState.WAITING;
		state = FleetState.WAITING;
		if (com == null) return;
		
		checkAndFixUnits(time);
		checkAndFixCargo(time);
		
		if (units.sum() == 0)
		{
			removeFleet();
			return;
		}
		
		try
		{
			executingCommand = com;
			
			nextEventTime = time;
			
			long timeDiff = executingCommand.prepare(this);
			
			assert timeDiff >= 0;
			
			if (timeDiff == 0)
				emergencyDisableLoop(ErrorCode.DISABLE_LOOP_UNKNOWN);
			
			lastEventTime = time;
			nextEventTime = time+timeDiff;
			nextCompleteTime = nextEventTime;
			
			if (executingCommand instanceof FleetMoveCommand)
			{
				try
				{
					long tempDiff = ((FleetMoveCommand) executingCommand).getTimeEstimate(this, time);
					nextCompleteTime = time+tempDiff;
				}
				catch (Exception e)
				{ Galaxy.logger.log(Level.SEVERE, "Caught exception", e); }
			}
			
			nextEventHandle = galaxy.addEvent(nextEventTime, new CommandEvent(this));
			errormessage = null;
			state = FleetState.ACTING;
		}
		catch (FleetStopException e)
		{
			executingCommand = null;
		}
		catch (FleetAbortException e)
		{
			executingCommand = null;
			Galaxy.logger.fine("Command aborted: "+e.getErrorCode());
			ErrorCode errorcode = e.getErrorCode();
			if (errorcode == ErrorCode.NONE)
			{
				errorcode = ErrorCode.UNDEFINED_ERROR;
				Galaxy.logger.log(Level.SEVERE, "No error code", e);
			}
			log(errorcode, this);
		}
		catch (Throwable e)
		{
			executingCommand = null;
			Galaxy.logger.log(Level.SEVERE, "Caught exception", e);
		}
	}
	catch (AssertionError e)
	{
		owner.log(ErrorCode.UNDEFINED_ERROR, this);
		executingCommand = null;
		Galaxy.logger.log(Level.SEVERE, "Caught exception", e);
		reset(time);
		throw e;
	}
}

private void prepare(long time)
{
	if (state.isHostileAction()) return;
	state = FleetState.WAITING;
	controller.nextCommand(this, time);
}

void activate(long time)
{
	try
	{
		assert state == FleetState.ACTING;
		state = FleetState.WAITING;
		nextEventHandle = null;
		assert nextEventTime == time : nextEventTime+" != "+time;
		boolean repeat = false;
		FleetCommand currentCommand = executingCommand;
		executingCommand = null;
		try
		{
			currentCommand.execute(this);
			errormessage = null;
		}
		catch (FleetStopException e)
		{
			return;
		}
		catch (FleetJoinBattleException e)
		{
			Galaxy.logger.fine("FleetJoinBattleExc caught ("+this+")");
			emergencyDisableLoop(ErrorCode.DISABLE_LOOP_BATTLE);
			executingCommand = currentCommand;
			payUpkeep(nextEventTime);
			Battle battle = e.getBattle();
			battle.execute(new JoinAction(this, e), time);
			return;
		}
		catch (FleetDisbandException e)
		{
			Galaxy.logger.fine("Fleet disbanded!");
			return;
		}
		catch (FleetAbortException e)
		{
			Galaxy.logger.fine("Command aborted!");
			ErrorCode errorcode = e.getErrorCode();
			if (errorcode == ErrorCode.NONE)
			{
				errorcode = ErrorCode.UNDEFINED_ERROR;
				Galaxy.logger.log(Level.SEVERE, "No error code", e);
			}
			log(errorcode, this);
		}
		catch (FleetRepeatException e)
		{
			Galaxy.logger.fine("Command repeat!");
			ErrorCode errorcode = e.getErrorCode();
			if (errorcode == ErrorCode.NONE)
			{
				errorcode = ErrorCode.UNDEFINED_ERROR;
				Galaxy.logger.log(Level.SEVERE, "No error code", e);
			}
			log(errorcode, this);
			repeat = true;
		}
		catch (Exception e)
		{
			Galaxy.logger.log(Level.SEVERE, "Caught exception", e);
			log(ErrorCode.FLEET_INTERNAL_ERROR, this);
		}
		
		if (units.sum() == 0)
		{
			removeFleet();
			return;
		}
		
		if (currentCommand instanceof FleetMoveCommand)
		{
			repeat = !((FleetMoveCommand) currentCommand).isDone(this);
		}
		
		if (repeat)
			prepare(time, currentCommand);
		else
			prepare(time);
	}
	catch (AssertionError e)
	{
		owner.log(ErrorCode.UNDEFINED_ERROR, this);
		Galaxy.logger.log(Level.SEVERE, "Caught exception", e);
		reset(time);
		throw e;
	}
}

public void reset(long time)
{
	tryUnscheduleEvent();
	if (units.sum() == 0)
	{
		removeFleet();
		return;
	}
	if (autoFleet)
	{
		enableUpkeep(time);
		autoFleet = false;
		
		// Milita doesn't return to colony.
		UnitIterator it = units.unitIterator();
		while (it.hasNext())
		{
			it.next();
			if (it.key().hasSpecial(UnitSpecial.MILITIA))
				it.remove();
		}
	}
	
	radiusGoal = radius;
	speedGoal = speed;
	thetaGoal = theta;
	
	state = FleetState.WAITING;
	nextEventTime = time;
	nextCompleteTime = time;
	enableUpkeep(time);
	checkAndFixUnits(time);
	checkAndFixCargo(time);
}

public void withdraw(long time)
{
	Galaxy.logger.entering("net.cqs.engine.Fleet", "withdraw");
	reset(time);
	prepare(time, new FleetWithdrawCommand());
}

/**
 * Macht einen entkoppelten Fleet-Resume. Der eigentliche Befehl wird erst mit
 * dem naechsten Event ausgefuehrt.
 */
public void resume(long time, boolean decoupled)
{
	if (state != FleetState.WAITING)
		throw new IllegalStateException();
	if (decoupled)
		prepare(time, new FleetNOPCommand());
	else
		prepare(time);
}

public void resume()
{ resume(getGalaxy().getTime(), false); }

/**
 * Wenn ein abbrechbarer Befehl ausgefuehrt wird, wird der Befehl abgebrochen.
 */
public void stop(long time)
{
	if ((executingCommand != null) && executingCommand.mayAbort(this))
	{
		FleetState oldstate = state;
		try
		{
			state = FleetState.WAITING;
			executingCommand.abort(this);
			unscheduleEvent();
		}
		catch (FleetContinueException e)
		{
			state = FleetState.ACTING;
			if (e.timeDiff != 0)
			{
				unscheduleEvent();
				nextEventTime = time+e.timeDiff;
				nextCompleteTime = nextEventTime;
				nextEventHandle = galaxy.addEvent(nextEventTime, new CommandEvent(this));
				errormessage = null;
			}
		}
		catch (UnsupportedOperationException e)
		{
			state = oldstate;
			Galaxy.logger.log(Level.SEVERE, "Caught exception", e);
			log(ErrorCode.FLEET_INTERNAL_ERROR, this);
		}
		catch (Exception e)
		{
			Galaxy.logger.log(Level.SEVERE, "Caught exception", e);
			log(ErrorCode.FLEET_INTERNAL_ERROR, this);
		}
	}
}

public void stop()
{ stop(getGalaxy().getTime()); }

public void takeoff()
{ landed = false; }

public void land()
{ landed = true; }

public Fleet splitFleet(UnitMap secondUnits, EnumLongMap<ResourceEnum> secondRes,
		UnitMap firstUnits, EnumLongMap<ResourceEnum> firstRes, boolean transferCmds)
{
	Fleet fnew = new Fleet(this);
	this.units = firstUnits;
	this.cargo = firstRes;
	
	fnew.units = secondUnits;
	fnew.cargo = secondRes;
	
	if (transferCmds)
	{
		FleetCommandList orders = ((HumanFleetController) this.controller).getOrders();
		((HumanFleetController) fnew.controller).append(orders);
	}
	
	owner.getFleets().add(fnew);
	fnew.registerWithSystem();
	
	return fnew;
}

@Override
public String toString()
{ return "fleet "+id+" of "+owner; }

@Override
public int hashCode()
{ return super.hashCode(); }

@Override
public boolean equals(Object o)
{ return this == o; }


public static Fleet createSimulationFleet(Galaxy galaxy, Position pos, Player owner, long time, UnitMap units)
{
	Fleet f = new Fleet(galaxy, pos, owner, time);
	f.controller = null;
	f.virtual = true;
	f.units = units;
	return f;
}

private static boolean hasUpkeep(UnitMap units)
{
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		if ((it.key().getUpkeep() > 0) && (it.value() > 0)) return true;
	}
	return false;
}

public static Fleet createFleet(Colony c, long time, String name, UnitMap units, Iterable<FleetCommand> orders)
{
	if (units.sum() <= 0) return null;
	if (orders == null) orders = new FleetCommandList();
	
	Fleet f = new Fleet(c.getGalaxy(), c.getPosition(), c.getOwner(), time);
	if (name != null) f.name = name;
	((HumanFleetController) f.controller).append(orders);
	
	Player who = f.owner;
	
	boolean civilOnly = true;
	
	// Genug Einheiten checken
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		Unit unit = it.key();
		if (c.getUnits(unit) < it.value())
		{
			who.log(ErrorCode.FLEET_CANNOT_START_NO_UNITS, c);
			return null;
		}
		if ((it.value() > 0) && !unit.hasSpecial(UnitSpecial.CIVIL))
			civilOnly = false;
	}
	
	// Geld checken
	if (!civilOnly && !who.hasMoney() && hasUpkeep(units))
	{
		who.log(ErrorCode.FLEET_CANNOT_START_NO_MONEY, c);
		return null;
	}
	
	c.removeUnits(time, units);
	f.units = units;
	f.owner.getFleets().add(f);
	f.registerWithSystem();
	return f;
}

public static Fleet createAutoDefender(Colony c, long time, UnitMap units, int militiaCount)
{
	Fleet f = new Fleet(c.getGalaxy(), c.getPosition(), c.getOwner(), time);
	f.executingCommand = new FleetNOPCommand(FleetNOPCommand.DEFEND);
	f.autoFleet = true;
	f.noUpkeep = true;
	f.setDesktop(1);
	
	c.removeUnits(time, units);
	UnitSystem us = c.getGalaxy().getUnitSystem();
	if (militiaCount != 0) units.increase(us.getMilitia(), militiaCount);
	f.units = units;
	f.owner.getFleets().add(f);
	f.registerWithSystem();
	return f;
}

}
