package net.cqs.engine.battles;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import net.cqs.config.units.TestUnitImpl;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyBuilder;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.fleets.FleetJoinBattleException;

import org.junit.Before;
import org.junit.Test;

public class BattleTest
{

private Galaxy galaxy;
private Player player;
private Position position;
private TestBattle battle;
private UnitMap units;

@Before
public void setUp()
{
	galaxy = GalaxyBuilder.buildTestGalaxy();
	
	player = galaxy.createTestPlayer(Locale.GERMANY);
	position = galaxy.getSystem(0).getPlanet(0).getPosition();
	battle = new TestBattle(galaxy, position);
	
	units = new UnitMap();
	units.increase(TestUnitImpl.INFANTRY, 1);
}

@Test
public void testJoin()
{
	Fleet f = Fleet.createSimulationFleet(galaxy, position, player, 0, new UnitMap(units));
	int side = Battle.DEFENDER_SIDE;
	battle.execute(new JoinAction(f, new FleetJoinBattleException(battle, side, FleetState.FIGHTING, 
			new BattleFleetAdapter(), false)), 0);
	
	assertEquals(1, battle.getSide(side).size());
	assertEquals(1, battle.getSide(side).allunits.get(TestUnitImpl.INFANTRY));
	assertEquals(FleetState.FIGHTING, f.getState());
}

@Test
public void testFight()
{
	Fleet f = Fleet.createSimulationFleet(galaxy, position, player, 0, new UnitMap(units));
	int side = Battle.DEFENDER_SIDE;
	battle.execute(new JoinAction(f, new FleetJoinBattleException(battle, side, FleetState.FIGHTING, 
			new BattleFleetAdapter(), false)), 0);
	
	assertEquals(1, battle.getSide(side).size());
	assertEquals(1, battle.getSide(side).allunits.get(TestUnitImpl.INFANTRY));
	assertEquals(FleetState.FIGHTING, f.getState());
	assertEquals(0, battle.removeCalled);
	
	battle.execute(new FightAction(), 1);
	assertEquals(1, battle.removeCalled);
}

}
