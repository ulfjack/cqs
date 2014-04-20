package net.cqs.engine.base;

public final class UnitStats
{

private final UnitMap map;
private final long transportable;
private final long carriable;

public UnitStats(UnitMap map)
{
	this.map = new UnitMap(map);
	long divider = map.getGroundUnitSize();
	if (divider == 0)
		transportable = 100;
	else
		transportable = (100*map.getGroundUnitCapacity())/divider;
	
	divider = map.getSpaceUnitSize();
	if (divider == 0)
		carriable = 100;
	else
		carriable = (100*map.getSpaceUnitCapacity())/divider;
}

@Deprecated
public UnitMap getMap()
{ return map; }

/** Returns the original unitmap. */
public UnitMap getUnits()
{ return map; }

/** Returns the fraction of transportable gfound units in percent (by space units). */
public long getTransportable()
{ return transportable; }

/** Returns the fraction of carriable space units in percent (by carriers). */
public long getCarriable()
{ return carriable; }
	
}
