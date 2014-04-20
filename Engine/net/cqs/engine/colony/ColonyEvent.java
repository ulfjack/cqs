package net.cqs.engine.colony;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.base.UnitMap;

public final class ColonyEvent
{

	public enum Type
	{
		ADD_BUILDING, REMOVE_BUILDING,
		ADD_EDUCATION, REMOVE_EDUCATION,
		ADD_BUILT_UNITS, ADD_UNITS, REMOVE_UNITS,
		INFO;
	}

private final Type type;
private final long time;
private final Object info;

public ColonyEvent(Type type, long time, Object info)
{
	if (type == null)
		throw new NullPointerException();
	this.time = time;
	this.type = type;
	this.info = info;
}

public Type getType()
{ return type; }

public long getTime()
{ return time; }

public Object getInfo()
{ return info; }

public void execute(ColonyEventListener logger)
{
	switch (type)
	{
		case ADD_BUILDING :
			logger.increaseBuilding(time, (BuildingEnum) info);
			break;
		case REMOVE_BUILDING :
			logger.decreaseBuilding(time, (BuildingEnum) info);
			break;
		case ADD_EDUCATION :
			logger.increaseEducation(time, (EducationEnum) info);
			break;
		case REMOVE_EDUCATION :
			logger.decreaseEducation(time, (EducationEnum) info);
			break;
		case ADD_BUILT_UNITS :
			logger.increaseBuiltUnits(time, (Unit) info);
			break;
		case ADD_UNITS :
			logger.increaseUnits(time, (UnitMap) info);
			break;
		case REMOVE_UNITS :
			logger.decreaseUnits(time, (UnitMap) info);
			break;
		case INFO :
			logger.info(time, (String) info);
			break;
		default :
			throw new RuntimeException("Argh!");
	}
}

}
