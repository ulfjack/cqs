package net.cqs.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import net.cqs.engine.base.UnitMap;
import net.cqs.engine.units.UnitEnum;

import org.junit.Before;
import org.junit.Test;

public class FleetTest
{

private Galaxy galaxy;

@Before
public void setUp()
{
	galaxy = GalaxyBuilder.buildTestGalaxy();
}

@Test
public void testCanAttackSpace()
{
	Planet p = galaxy.getSystem(0).getPlanet(0);
	Player player = galaxy.createTestPlayer(Locale.GERMANY);
	Colony c = p.createColony(player, 0);
	UnitMap um = new UnitMap();
	um.increase(UnitEnum.FIGHTER);
	Fleet f = Fleet.createSimulationFleet(galaxy, c.getPosition(), player, 0, um);
	
	assertTrue(f.canAttack());
}

@Test
public void testInvasionCalculation()
{
	Planet p = galaxy.getSystem(0).getPlanet(0);
	Player player = galaxy.createTestPlayer(Locale.GERMANY);
	Colony c = p.createColony(player, 0);
	UnitMap um = new UnitMap();
	um.increase(UnitEnum.INFANTRY, 19);
	um.increase(UnitEnum.INFANTRY_ATTACK, 1);
	Fleet f = Fleet.createSimulationFleet(galaxy, c.getPosition(), player, 0, um);
	
	int amount = c.subtractTroopsRequiredForInvasion(f);
	assertEquals(10, amount);
	amount = c.subtractTroopsRequiredForInvasion(f);
	assertEquals(10, amount);
}

}
