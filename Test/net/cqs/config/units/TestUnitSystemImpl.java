package net.cqs.config.units;

public class TestUnitSystemImpl implements UnitSystem
{

@Override
public int groupCount(boolean isSpace)
{
	if (isSpace) return 4;
	else return 3;
}

@Override
public UnitClass[] getUnitClasses()
{ return new UnitClass[0]; }

@Override
public Unit[] getUniqueUnits()
{ return TestUnitImpl.values(); }

@Override
public Unit[] getDefaultUnits()
{ return TestUnitImpl.values(); }

@Override
public Unit getMilitia()
{ return null; }

@Override
public Unit getUnit(UnitDescription design) throws BadDesignException
{ throw new BadDesignException("Argh!"); }

@Override
public Unit parseUnit(String s)
{ return TestUnitImpl.valueOf(s); }

@Override
public UnitClass parseUnitClass(String s)
{ return null; }

}
