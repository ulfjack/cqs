package net.cqs.engine.battles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.Fleet;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;

public final class BattleSide implements Serializable, Iterable<FleetEntry>
{

private static final long serialVersionUID = 2L;

private final Battle battle;
private final int side;
private UnitSelector selector;

private float[] damage; // remaining damage for this side
List<FleetEntry> fleets;

public UnitMap allunits;
UnitMap losses;

BattleSide(Battle battle, int side, UnitSelector selector)
{
	this.battle = battle;
	this.side = side;
	this.selector = selector;
	fleets = new ArrayList<FleetEntry>();
	damage = new float[battle.getUnitSystem().groupCount(isSpace())];
	
	allunits = new UnitMap();
	losses = new UnitMap();
}

public Battle getBattle()
{ return battle; }

public UnitSystem getUnitSystem()
{ return battle.getUnitSystem(); }

public boolean isSpace()
{ return battle.isSpace(); }

public Iterator<FleetEntry> iterator()
{ return fleets.iterator(); }

public int size()
{ return fleets.size(); }

public Fleet getFleet(int i)
{ return fleets.get(i).fleet; }

public FleetEntry get(int i)
{ return fleets.get(i); }

public UnitMap getAllUnits()
{ return allunits; }

public boolean contains(Fleet f)
{
	for (int i = 0; i < fleets.size(); i++)
		if (fleets.get(i).fleet == f) return true;
	return false;
}

public void notifyRemove(long time, FleetEntry entry, RemoveReason reason)
{
	entry.adaptor.decFleet(time);
	try
	{
		switch (reason)
		{
			case WON  :     entry.listener.won(entry.fleet, time); break;
			case LOST :     entry.listener.lost(entry.fleet, time); break;
			case WITHDRAW : entry.listener.withdraw(entry.fleet, time); break;
		}
	}
	catch (Throwable e)
	{
		Battle.logger.log(Level.SEVERE, "Ignored Exception", e);
		entry.fleet.reset(time);
	}
	
	try
	{ entry.fleet.subtractUnitsFrom(allunits); }
	catch (Exception e)
	{
		Battle.logger.log(Level.SEVERE, "Invalid unit count!", e);
		recalculateAllUnits();
	}
}

public void remove(long time, FleetEntry entry, RemoveReason reason)
{
	fleets.remove(entry);
	notifyRemove(time, entry, reason);
}

public void act(long time, FleetEntryActor actor)
{
	Iterator<FleetEntry> it = fleets.iterator();
	while (it.hasNext())
	{
		FleetEntry entry = it.next();
		RemoveReason reason = actor.act(entry);
		if (reason != null)
		{
			it.remove();
			notifyRemove(time, entry, reason);
		}
	}
}

private void chooseUnits(FleetEntry entry, float maxPower)
{
	float totalPower = entry.fleet.getPower(selector);
	UnitIterator it = entry.fleet.getUnitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		float random = battle.getRandomFloat()*0.5f+0.5f;
		int count = Math.round(random*it.value());
		int goal = Math.round(count*(maxPower/totalPower));
		if (goal < 1) goal = 1;
		else if (goal > count) goal = count;
		entry.activeUnits.set(it.key(), goal);
	}
}

void chooseUnits(UnitMap activeUnits, float maxPower)
{
	battle.battleLogger.info("maximum power="+maxPower);
	float[] powers = new float[size()];
	float totalPower = 0;
	for (int i = 0; i < size(); i++)
	{
		powers[i] = getFleet(i).getPower(selector);
		totalPower += powers[i];
	}
	
	for (int i = 0; i < size(); i++)
	{
		FleetEntry entry = fleets.get(i);
		chooseUnits(entry, maxPower*(powers[i]/totalPower));
		activeUnits.add(entry.activeUnits);
		battle.battleLogger.info(getFleet(i)+" chooses="+entry.activeUnits.serialize());
	}
}

void chooseAllUnits(UnitMap activeUnits)
{
	battle.battleLogger.info("maximum power=+INFTY");
	
	for (int i = 0; i < size(); i++)
	{
		FleetEntry entry = fleets.get(i);
		entry.chooseAllUnits(selector);
		activeUnits.add(entry.activeUnits);
		battle.battleLogger.info(getFleet(i)+" chooses="+entry.activeUnits.serialize());
	}
}

protected float modifiedAttackValue(Unit unit, int group)
{ return unit.getAttack(group); }

protected float modifiedDefenseValue(Unit unit)
{ return unit.getDefense(); }

public float calculatePower()
{ return allunits.getPower(selector); }

void distributeDamage(float[] result, float[] damageDone, FleetEntry entry, int[] groupUnitCount)
{
	//int[] fleetGroupUnitCount = f.units.calculateGroupCount(selector);
	UnitMap unitsLost = new UnitMap();
	
	battle.battleLogger.unitsActive(side, entry);
	UnitIterator it = entry.activeUnits.unitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		Unit unit = it.key();
		int group = unit.getGroup();
		float defense = modifiedDefenseValue(unit);
//		if ((group >= 0) && (group < result.length))
//		{
		int amountLost = (int) Math.floor(
				result[group]                                // Schaden
				       * it.value() / groupUnitCount[group] // Anteil an allen d. Klasse
				                                     / defense                                    // Panzerung
			);
		damageDone[group] += amountLost*defense;
		if (amountLost > it.value()) amountLost = it.value();
		//int amount = it.value() - amountLost;
		
//		Battle.logger.println(f+" losses: " + it.key() + " - " + amountLost); 
	
		entry.fleet.decreaseUnit(it.key(), amountLost);
		losses.increase(it.key(), amountLost);
		allunits.decrease(it.key(), amountLost);
		unitsLost.increase(it.key(), amountLost);
		it.decrease(amountLost);
//		}
		/*else
		{
			battle.galaxy.logger.log(Level.SEVERE,
					"BattleSide: ArrayIndexOutOfBounds in distributeDamage!!! owner:"
					+battle.getOwner()+" attacker: "+(this == battle.sides[battle.ATTACKER_SIDE])
					+" unit: "+unit.getName(Locale.GERMANY)+" group: "+group
					+" landingBattle: "+(battle instanceof LandingBattle)
					+" PlanetBattle: "+(battle instanceof PlanetBattle)
					+" SurfaceBattle: "+(battle instanceof SurfaceBattle)
					+" SimulatorBattle: "+(battle instanceof SimulatorBattle)
					+" result.length: "+result.length
					+" damageDone.length: "+damageDone.length
					+" groupUnitCount.length "+ groupUnitCount.length);
		}*/
	}
	battle.battleLogger.unitsLost(side, entry.fleet, unitsLost);
}

private void distributeDamage(float[] damageNew, int[] activeGroupCount, List<FleetEntry> tofleets)
{
	UnitSystem us = battle.getUnitSystem();
	if (damage == null) damage = new float[us.groupCount(isSpace())];
	for (int i = 0; i < us.groupCount(isSpace()); i++)
		damage[i] += damageNew[i];
	
	float[] damageDone = new float[us.groupCount(isSpace())];
	
	for (int i = 0; i < tofleets.size(); i++)
		distributeDamage(damage, damageDone, tofleets.get(i), activeGroupCount);
	
	for (int i = 0; i < us.groupCount(isSpace()); i++)
	{
		damage[i] -= damageDone[i];
		if (activeGroupCount[i] == 0)
			damage[i] = 0;
	}
	
/*	battle.battleLogger.info("Schadensbilanz von "+this);
	for (int i = 0; i < Unit.GROUP_COUNT; i++)
		battle.battleLogger.info(damage_done[i]+"   "+damage[i]);*/
}

void distributeDamage(float[] damageNew, int[] activeGroupCount)
{ distributeDamage(damageNew, activeGroupCount, fleets); }

public void addUnits(UnitMap other)
{ allunits.add(other); }

public void removeUnits(UnitMap other)
{ allunits.subtract(other); }

public void recalculateAllUnits()
{
	allunits.clear();
	for (int i = 0; i < fleets.size(); i++)
		fleets.get(i).fleet.addUnitsTo(allunits);
}

public void join(FleetEntry entry)
{
	if (entry == null) throw new NullPointerException("Entry == null");
	fleets.add(entry);
	entry.fleet.addUnitsTo(allunits);
	entry.activeUnits.clear();
	battle.battleLogger.info(entry.fleet+" joins "+this);
}

void reset(long time, boolean removeFleets)
{
	if (removeFleets) fleets.clear();
	
	if (losses == null) losses = new UnitMap();
	losses.clear();
	
	for (int i = 0; i < damage.length; i++)
		damage[i] = 0;
}

/* Simulator Hack */
public void setKind(boolean space)
{
	if (space) selector = UnitSelector.SPACE_ONLY;
	else       selector = UnitSelector.GROUND_ONLY;
	recalculateAllUnits();
}

}

