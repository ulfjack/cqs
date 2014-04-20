package net.cqs.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class GalaxyTest
{

private Galaxy galaxy;

@Before
public void setUp() throws Exception
{
	galaxy = GalaxyBuilder.buildTestGalaxy();
	galaxy.check();
}

@Test
public void testEmpty()
{ assertNotNull(galaxy.getSystem(0).getPosition()); }

@Test
public void testDecode()
{
	assertEquals(Position.decode("0"), galaxy.getSystem(0).getPosition());
}

}
