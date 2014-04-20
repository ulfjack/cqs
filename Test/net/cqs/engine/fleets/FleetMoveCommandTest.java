package net.cqs.engine.fleets;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import net.cqs.config.units.TestUnitImpl;
import net.cqs.engine.*;
import net.cqs.engine.base.UnitMap;

public class FleetMoveCommandTest extends FleetCommandTest
{

Player player;
Fleet f;
FleetMoveCommand fmc;
Position pos;

@Before @Override
public void setUp()
{
	super.setUp();
	pos = Position.decode("0:0");
	fmc = new FleetMoveCommand(pos, false);
	
	player = galaxy.createTestPlayer(Locale.GERMANY);
	
	Planet p = galaxy.getSystem(0).getPlanet(0);
	Colony c = p.createColony(player, 0);
	
	UnitMap um = new UnitMap();
	um.set(TestUnitImpl.INFANTRY, 3);
	
	c.addUnits(0, um);
	f = Fleet.createFleet(c, 0, "Flotte", um, new FleetCommandList());
}

@Test
public void testGetDestination()
{ assertSame(pos, fmc.getDestination()); }

@Test
public void testCheck()
{ assertEquals(0, fmc.check(f)); }

/*public void testPrepareForPlanet()
{ fmc.prepareForPlanet(f, planets[0]); }*/

}
