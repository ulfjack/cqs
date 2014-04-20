package net.cqs.engine.colony;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.base.UnitMap;

public interface ColonyEventListener
{

void increaseBuilding(long time, BuildingEnum what);
void decreaseBuilding(long time, BuildingEnum what);
void increaseEducation(long time, EducationEnum topic);
void decreaseEducation(long time, EducationEnum topic);
void increaseBuiltUnits(long time, Unit type);
void increaseUnits(long time, UnitMap units);
void decreaseUnits(long time, UnitMap units);
void info(long time, String data);

}