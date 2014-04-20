package net.cqs.engine.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import net.cqs.config.ErrorCodeException;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.main.setup.GalaxyCreator;
import net.cqs.main.setup.GameConfiguration.GalaxyDescription;
import net.cqs.storage.memory.MemoryStorageManager;

import org.junit.Before;
import org.junit.Test;

public class PlayerCreateActionTest
{

private Galaxy sim;

@Before
public void setUp()
{
	GalaxyDescription desc = new GalaxyDescription(2, 0);
	sim = new GalaxyCreator().createGalaxy(new MemoryStorageManager(), desc);
}

@Test
public void testPlayerCreate1()
{
	sim.schedex(new PlayerCreateAction(null, "test", Locale.GERMANY));
	assertEquals(1, sim.countPlayers());
	Player p = sim.findPlayerByName("test");
	assertNotNull(p);
	assertEquals("test", p.getName());
	assertFalse(p.isRestricted());
}

@Test
public void testPlayerCreate2()
{
	sim.schedex(new PlayerCreateAction(null, "test", Locale.GERMANY, true));
	assertEquals(1, sim.countPlayers());
	Player p = sim.findPlayerByName("test");
	assertTrue(p.isRestricted());
}

@Test(expected=ErrorCodeException.class)
public void testPlayerCreate3()
{ new PlayerCreateAction(null, null, Locale.GERMANY); }

@Test(expected=ErrorCodeException.class)
public void testPlayerCreate6()
{
	sim.schedex(new PlayerCreateAction(null, "x", Locale.GERMANY));
	sim.schedex(new PlayerCreateAction(null, "x", Locale.GERMANY));
}

}
