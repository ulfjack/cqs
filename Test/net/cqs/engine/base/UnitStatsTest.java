package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import net.cqs.engine.units.UnitEnum;

import org.junit.Test;

public class UnitStatsTest
{

@Test
public void testSimple1()
{
	UnitStats stats = new UnitStats(new UnitMap());
	assertEquals(0, stats.getUnits().sum());
	assertEquals(100L, stats.getCarriable());
	assertEquals(100L, stats.getTransportable());
}

@Test
public void testSimple2()
{
	UnitMap map = new UnitMap();
	map.increase(UnitEnum.AIRCRAFT, 10);
	UnitStats stats = new UnitStats(map);
	assertEquals(10, stats.getUnits().sum());
	assertEquals(100L, stats.getCarriable());
	assertEquals(0L, stats.getTransportable());
}

}
