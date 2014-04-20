package net.cqs.engine.battles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyBuilder;
import net.cqs.engine.Player;
import net.cqs.engine.actions.PlayerColonyStartAction;
import net.cqs.engine.actions.PlayerCreateAction;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.units.UnitEnum;

import org.junit.Test;

public class ComplexBattleTest
{

@Test
public void testComplex1()
{
	Galaxy galaxy = GalaxyBuilder.buildTestGalaxy();
	galaxy.schedex(new PlayerCreateAction(null, "p0", Locale.GERMANY));
	Player p0 = galaxy.findPlayerByName("p0");
	galaxy.schedex(new PlayerCreateAction(null, "p1", Locale.GERMANY));
	Player p1 = galaxy.findPlayerByName("p1");
	
	galaxy.schedex(new PlayerColonyStartAction(p0));
	Colony c0 = p0.getColonies().get(0);
	galaxy.schedex(new PlayerColonyStartAction(p1));
	Colony c1 = p1.getColonies().get(0);
	
	UnitMap units = new UnitMap();
	int count = 1000;
	units.increase(UnitEnum.INFANTRY, count);
	c0.addUnits(1, units);
	c1.addUnits(1, units);
	
	Fleet f1 = Fleet.createFleet(c1, 2, "xxx", units, null);
	assertNotNull(f1);
	
	SurfaceBattle battle = c0.createSurfaceBattle(1);
	assertNotNull(battle);
	
	battle.execute(new JoinAction(Battle.ATTACKER_SIDE, FleetState.FIGHTING, f1, new BattleFleetAdapter(), true), 2);
	assertEquals(1, battle.getSide(Battle.ATTACKER_SIDE).size());
	assertEquals(1, battle.getSide(Battle.DEFENDER_SIDE).size());
	
	Fleet f0 = battle.getSide(Battle.DEFENDER_SIDE).get(0).fleet;
	assertNotNull(f0);
	assertEquals(750+count, f0.getTotalUnits());
	
	battle.execute(new FightAction(), 3);
	battle.execute(new FightAction(), 4);
}

@Test
public void testCreateMilitia()
{
	Galaxy galaxy = GalaxyBuilder.buildTestGalaxy();
	galaxy.schedex(new PlayerCreateAction(null, "p0", Locale.GERMANY));
	Player p0 = galaxy.findPlayerByName("p0");
	galaxy.schedex(new PlayerCreateAction(null, "p1", Locale.GERMANY));
	Player p1 = galaxy.findPlayerByName("p1");
	
	galaxy.schedex(new PlayerColonyStartAction(p0));
	Colony c0 = p0.getColonies().get(0);
	galaxy.schedex(new PlayerColonyStartAction(p1));
	Colony c1 = p1.getColonies().get(0);
	
	UnitMap units = new UnitMap();
	units.increase(UnitEnum.INFANTRY, 100);
	c1.addUnits(1, units);
	
	Fleet f1 = Fleet.createFleet(c1, 2, "xxx", units, null);
	assertNotNull(f1);
	
	SurfaceBattle battle = c0.createSurfaceBattle(1);
	assertNotNull(battle);
	
	battle.execute(new JoinAction(Battle.ATTACKER_SIDE, FleetState.FIGHTING, f1, new BattleFleetAdapter(), true), 2);
	assertEquals(1, battle.getSide(Battle.ATTACKER_SIDE).size());
	assertEquals(1, battle.getSide(Battle.DEFENDER_SIDE).size());
	
	Fleet f0 = battle.getSide(Battle.DEFENDER_SIDE).get(0).fleet;
	assertNotNull(f0);
	assertEquals(750, f0.getTotalUnits());
}

}
