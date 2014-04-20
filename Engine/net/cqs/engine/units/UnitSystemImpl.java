package net.cqs.engine.units;

import java.util.ArrayList;
import java.util.HashMap;

import net.cqs.config.units.*;

public final class UnitSystemImpl implements UnitSystem
{

private static final UnitSystemImpl UNIT_SYSTEM = new UnitSystemImpl();

public static UnitSystemImpl getImplementation()
{ return UNIT_SYSTEM; }


private final HashMap<UnitDescription,Unit> map = new HashMap<UnitDescription,Unit>();
private final Unit[] uniqueUnits;
private final Unit[] defaultUnits;

private UnitSystemImpl()
{
	for (UnitEnum u : UnitEnum.values())
	{
		if (u.isDesign())
		{
			UnitDescription design = new UnitDescription(u.unitClass(), u.getUnitModules());
			map.put(design, u);
		}
	}
	
	ArrayList<Unit> list = new ArrayList<Unit>();
	for (UnitEnum u : UnitEnum.values())
	{
		if (!u.isDesign() && !u.isHidden())
			list.add(u);
	}
	uniqueUnits = list.toArray(new Unit[0]);
	
	
	list.clear();
	list.add(UnitEnum.INFANTRY_ATTACK);
	list.add(UnitEnum.VEHICLE_ATTACK);
	list.add(UnitEnum.AIRCRAFT_ATTACK);
	
	list.add(UnitEnum.FIGHTER_ATTACK);
	list.add(UnitEnum.CORVETTE_ATTACK);
	list.add(UnitEnum.DESTROYER_ATTACK);
	
	list.add(UnitEnum.CORVETTE_WARP);
	list.add(UnitEnum.DESTROYER_WARP);
	
	list.add(UnitEnum.TRANSPORTER);
	list.add(UnitEnum.FREIGHTER);
	
	list.add(UnitEnum.TRANSPORTER_WARP);
	list.add(UnitEnum.FREIGHTER_WARP);
	
	defaultUnits = list.toArray(new Unit[0]);
}

@Override
public int groupCount(boolean isSpace)
{
	//FIXME
	if (isSpace) return 4;
	else return 3;
}

@Override
public UnitClass[] getUnitClasses()
{ return UnitClassEnum.values(); }

@Override
public Unit[] getUniqueUnits()
{ return uniqueUnits; }

@Override
public Unit[] getDefaultUnits()
{ return defaultUnits; }

@Override
public Unit getMilitia()
{ return UnitEnum.MILITIA; }

@Override
public Unit getUnit(UnitDescription design) throws BadDesignException
{
	Unit result = map.get(design);
	if (result == null)
		throw new BadDesignException("Argh!");
	return result;
}


@Override
public Unit parseUnit(String s)
{ return UnitEnum.valueOf(s); }

@Override
public UnitClass parseUnitClass(String s)
{ return UnitClassEnum.valueOf(s); }

}
