package net.cqs.engine.fleets;

import static org.junit.Assert.assertEquals;
import net.cqs.engine.Position;

import org.junit.Test;

public class RoutePlannerTest
{

@Test
public void testDirectionTo()
{
	Position a = new Position(0);
	Position b = new Position(0, 0);
	Position c = new Position(0, 0, 0);
	Position d = new Position(0, 1);
	assertEquals(RoutePlanner.DOWN, RoutePlanner.directionTo(a, b));
	assertEquals(RoutePlanner.DOWN, RoutePlanner.directionTo(b, c));
	assertEquals(RoutePlanner.UP, RoutePlanner.directionTo(b, a));
	assertEquals(RoutePlanner.UP, RoutePlanner.directionTo(c, a));
	assertEquals(RoutePlanner.UP, RoutePlanner.directionTo(c, b));
	
	assertEquals(RoutePlanner.DOWN, RoutePlanner.directionTo(a, d));
	assertEquals(RoutePlanner.ALONG, RoutePlanner.directionTo(b, d));
	assertEquals(RoutePlanner.UP, RoutePlanner.directionTo(c, d));
	assertEquals(RoutePlanner.UP, RoutePlanner.directionTo(d, a));
	assertEquals(RoutePlanner.ALONG, RoutePlanner.directionTo(d, b));
	assertEquals(RoutePlanner.ALONG, RoutePlanner.directionTo(d, c));
}

@Test
public void testNextTo()
{
	Position a = new Position(0);
	Position b = new Position(0, 0);
	Position c = new Position(0, 0, 0);
	Position d = new Position(0, 1);
	assertEquals(b, RoutePlanner.nextTo(a, b));
	assertEquals(c, RoutePlanner.nextTo(b, c));
	assertEquals(a, RoutePlanner.nextTo(b, a));
	assertEquals(b, RoutePlanner.nextTo(c, a));
	assertEquals(b, RoutePlanner.nextTo(c, b));
	
	assertEquals(d, RoutePlanner.nextTo(a, d));
	assertEquals(d, RoutePlanner.nextTo(b, d));
	assertEquals(b, RoutePlanner.nextTo(c, d));
	assertEquals(a, RoutePlanner.nextTo(d, a));
	assertEquals(b, RoutePlanner.nextTo(d, b));
	assertEquals(b, RoutePlanner.nextTo(d, c));
}

@Test
public void testCalcSimple()
{
	Position a = new Position(0, 0);
	Position b = new Position(0, 0);
	Position[] route = new RoutePlanner().calculateRoute(a, b);
	assertEquals(1, route.length);
	assertEquals(a, route[0]);
}

@Test
public void testCalcComplex1()
{
	Position a = new Position(0, 0, 0);
	Position b = new Position(0, 1, 0);
	Position[] route = new RoutePlanner().calculateRoute(a, b);
	assertEquals(4, route.length);
	assertEquals(a, route[0]);
	assertEquals(b, route[3]);
	assertEquals(a.toPlanetPosition(), route[1]);
	assertEquals(b.toPlanetPosition(), route[2]);
}

@Test
public void testCalcComplex2()
{
	Position a = new Position(0, 0, 0);
	Position b = new Position(1, 0, 0);
	Position[] route = new RoutePlanner().calculateRoute(a, b);
	assertEquals(6, route.length);
	assertEquals(a, route[0]);
	assertEquals(b, route[5]);
	assertEquals(a.toPlanetPosition(), route[1]);
	assertEquals(a.toSystemPosition(), route[2]);
	assertEquals(b.toSystemPosition(), route[3]);
	assertEquals(b.toPlanetPosition(), route[4]);
}

}
