package net.cqs.engine.colony;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.base.UnitMap;

public final class ColonyEventAdapter implements ColonyEventListener
{

private final ColonyEventLogger logger;

public ColonyEventAdapter(ColonyEventLogger logger)
{
	this.logger = logger;
}

private void log(ColonyEvent event)
{ logger.eventHappened(event); }


@Override
public void increaseBuilding(long time, BuildingEnum what)
{ log(new ColonyEvent(ColonyEvent.Type.ADD_BUILDING, time, what)); }

@Override
public void decreaseBuilding(long time, BuildingEnum what)
{ log(new ColonyEvent(ColonyEvent.Type.REMOVE_BUILDING, time, what)); }

@Override
public void increaseEducation(long time, EducationEnum topic)
{ log(new ColonyEvent(ColonyEvent.Type.ADD_EDUCATION, time, topic)); }

@Override
public void decreaseEducation(long time, EducationEnum topic)
{ log(new ColonyEvent(ColonyEvent.Type.REMOVE_EDUCATION, time, topic)); }

@Override
public void increaseBuiltUnits(long time, Unit type)
{ log(new ColonyEvent(ColonyEvent.Type.ADD_BUILT_UNITS, time, type)); }

@Override
public void increaseUnits(long time, UnitMap units)
{ log(new ColonyEvent(ColonyEvent.Type.ADD_UNITS, time, new UnitMap(units))); }

@Override
public void decreaseUnits(long time, UnitMap units)
{ log(new ColonyEvent(ColonyEvent.Type.REMOVE_UNITS, time, new UnitMap(units))); }

@Override
public void info(long time, String data)
{ log(new ColonyEvent(ColonyEvent.Type.INFO, time, data)); }

}
