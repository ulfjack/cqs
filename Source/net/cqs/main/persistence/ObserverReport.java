package net.cqs.main.persistence;

import java.io.Serializable;
import java.util.Random;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.colony.ColonyEventListener;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;
import net.cqs.util.EnumIntMap;

/**
 * Persistente Agenten-Daten.
 * 
 * Das wichtigste ist, dass diese Klasse unter GAR KEINEN UMSTAENDEN Referenzen
 * auf die Datenbank hat.
 */
public final class ObserverReport implements Serializable, ColonyEventListener
{

private static final long serialVersionUID = 1L;

public String position;
public int id;
public long startTime;
public long startRealTime;
public long stopTime;
public long stopRealTime;
public long lastUpdateTime;
private final Random rand;
public long seed; // FIXME: this doesn't work right now!

public EnumIntMap<BuildingEnum> buildingsIncrease  = EnumIntMap.of(BuildingEnum.class);
public EnumIntMap<BuildingEnum> buildingsDecrease  = EnumIntMap.of(BuildingEnum.class);
public EnumIntMap<EducationEnum> educationIncrease = EnumIntMap.of(EducationEnum.class);
public EnumIntMap<EducationEnum> educationDecrease = EnumIntMap.of(EducationEnum.class);
public UnitMap unitsIncrease      = new UnitMap();
public UnitMap unitsBuiltIncrease = new UnitMap();
public UnitMap unitsDecrease      = new UnitMap();

/**
 * Creates AgentData for temporary reports (observation)
 * on colony with position with id showing owner, seed for random generator.
 */
public ObserverReport(String position, int id, long startTime, long startRealTime, long seed)
{
	this.position = position;
	this.id = id;
	this.startTime = startTime;
	this.startRealTime = startRealTime;
	this.seed = seed;
	this.rand = new Random(seed);
}

public String getPosition()
{ return position; }

public int getOwnerId()
{ return id; }

public long getStartTime()
{ return startTime; }

public long getStartRealTime()
{ return startRealTime; }

public boolean isStopped()
{ return stopTime != 0; }

public long getStopTime()
{ return stopTime; }

public long getStopRealTime()
{ return stopRealTime; }

public UnitMap getUnitsIncrease()
{ return unitsIncrease; }

public UnitMap getUnitsBuilt()
{ return unitsBuiltIncrease; }

public UnitMap getUnitsDecrease()
{ return unitsDecrease; }

public int getBuildingBuilt(BuildingEnum building)
{ return buildingsIncrease.get(building); }

public int getBuildingRemoved(BuildingEnum building)
{ return buildingsDecrease.get(building); }

public int getEducationAdded(EducationEnum topic)
{ return educationIncrease.get(topic); }

public int getEducationRemoved(EducationEnum topic)
{ return educationDecrease.get(topic); }



public float nextFloat()
{ return rand.nextFloat(); }

/**
 * Sets stopTime and stopRealTime (when agent is removed from colony)
 */
public void setStopTime(long stopTime, long stopRealTime)
{
	this.stopTime = stopTime;
	this.stopRealTime = stopRealTime;
}

/**
 * Return probability depending on the time the agent is already active
 * balanced to: starting with 0%, after 7 days 96.5%, max. 99.5%
 */
public float getProbability(long time)
{
	long t = (time - startTime); // time in seconds
	float c = 172477; // -1/Math.ln(0.98/0.95) * 60*60*24*7
	return (float) (0.995*(1 - Math.exp(-t/c)));
}

public long getProbabilityPercent(long time)
{ return Math.round(100*getProbability(time)); }

public boolean logEvent(long time)
{
	float prob = nextFloat();
	return prob < getProbability(time);
}

@Override
public void increaseBuilding(long time, BuildingEnum what)
{ buildingsIncrease.increase(what); }

@Override
public void decreaseBuilding(long time, BuildingEnum what)
{ buildingsDecrease.increase(what); }

@Override
public void increaseEducation(long time, EducationEnum topic)
{ educationIncrease.increase(topic); }

@Override
public void decreaseEducation(long time, EducationEnum topic)
{ educationDecrease.increase(topic); }

@Override
public void increaseBuiltUnits(long time, Unit type)
{
	unitsBuiltIncrease.increase(type);
	unitsIncrease.increase(type);
}

@Override
public void increaseUnits(long time, UnitMap units)
{ unitsIncrease.add(units); }

@Override
public void decreaseUnits(long time, UnitMap units)
{ unitsDecrease.add(units); }

@Override
public void info(long time, String data)
{/*OK*/}



public static String getBinding(String id)
{ return "AGENT-"+id; }

public static ObserverReport getAgentData(String pid)
{
	String binding = getBinding(pid);
	return Context.getDataManager().getBinding(binding, ObserverReport.class);
}

public static void createAgentData(String pid, String position, int id, long startTime, long startRealTime, long seed)
{
	String binding = getBinding(pid);
	ObserverReport data = new ObserverReport(position, id, startTime, startRealTime, seed);
	Context.getDataManager().setBinding(binding, data);
}

public static ObserverReport getObserverReportCopy(Storage storage, String filename)
{
	try
	{
		String binding = getBinding(filename);
		return storage.getCopy(binding, ObserverReport.class);
	}
	catch (NameNotBoundException e)
	{ return null; }
}

}
