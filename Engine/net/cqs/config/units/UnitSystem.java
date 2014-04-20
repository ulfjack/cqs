package net.cqs.config.units;

public interface UnitSystem
{

UnitClass[] getUnitClasses();
Unit[] getUniqueUnits();
Unit[] getDefaultUnits();
Unit getMilitia();
Unit getUnit(UnitDescription design) throws BadDesignException;
int groupCount(boolean isSpace);

Unit parseUnit(String s);
UnitClass parseUnitClass(String s);

}
