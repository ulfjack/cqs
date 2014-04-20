package net.cqs.config.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import net.cqs.config.PlanetEnum;

import org.junit.Test;

public class PlanetEnumTest
{

@Test
public void testId()
{ assertEquals("asteroids", PlanetEnum.ASTEROIDS.getId()); }

@Test
public void testValueOf1()
{ assertSame(PlanetEnum.DESERTWORLD, PlanetEnum.valueOf("DESERTWORLD")); }

@Test(expected=IllegalArgumentException.class)
public void testValueOf2()
{ PlanetEnum.valueOf("XXX"); }

}
