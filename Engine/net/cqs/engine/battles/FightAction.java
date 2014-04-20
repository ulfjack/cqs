package net.cqs.engine.battles;

import java.util.logging.Level;

import net.cqs.config.BattleMessageEnum;
import net.cqs.config.BattleStateEnum;
import net.cqs.config.Constants;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;

public class FightAction extends BattleAction
{

public boolean hadDefenders = false;

/**
 * Fixes units and cargo.
 * Removes all empty fleets, sending reports to the loosers.
 * Determines the outcome of the battle by looking at who's left.
 */
protected BattleStateEnum cleanUpAfterFight(final Battle battle, final long time)
{
	BattleSide[] sides = battle.sides;
//	boolean attackerStart = sides[Battle.ATTACKER_SIDE].size() != 0;
	boolean defenderStart = sides[Battle.DEFENDER_SIDE].size() != 0;
	
	for (int s = Battle.DEFENDER_SIDE; s >= Battle.ATTACKER_SIDE; s--)
	{
		sides[s].act(time, new FleetEntryActor()
			{
				public RemoveReason act(FleetEntry entry)
				{
					UnitMap map = new UnitMap();
					entry.fleet.checkAndFixUnits(time, map);
					battle.battleLogger.unitsFixed(entry.fleet, map);
					entry.fleet.checkAndFixCargo(time);
					if (battle.mustRemoveFleet(entry.fleet))
					{
						Battle.logger.fine("Removing "+entry.fleet);
						return RemoveReason.LOST;
					}
					return null;
				}
			});
	}
	
	boolean attackerLeft = sides[Battle.ATTACKER_SIDE].size() != 0;
	boolean defenderLeft = sides[Battle.DEFENDER_SIDE].size() != 0;
	
	if (attackerLeft && defenderLeft) return BattleStateEnum.UNDECIDED;
	if (attackerLeft)
	{
		if (!defenderStart)
			battle.battleLogger.message(BattleMessageEnum.NO_DEFENDER_WIN);
		return BattleStateEnum.ATTACKER;
	}
	if (defenderLeft) return BattleStateEnum.DEFENDER;
	return BattleStateEnum.ALLDEAD;
}

protected void chooseBattleUnits(Battle battle, UnitMap[] activeUnits)
{
	BattleSide[] sides = battle.sides;
	float defenderPower = sides[Battle.DEFENDER_SIDE].calculatePower();
	float attackerPower = sides[Battle.ATTACKER_SIDE].calculatePower();
	
	float minPower = Math.min(defenderPower, attackerPower);
	float modifier = Math.max(0, 1 - minPower/Constants.BATTLE_THRESHOLD);
	if (modifier > 1) modifier = 0; // fix for overflow
	float maxPower = minPower*(3 + 22*modifier);
/*	float maxPower = Constants.BATTLE_POWER_FACTOR*Math.min(defenderPower, attackerPower);*/
	if (maxPower < Constants.BATTLE_MIN_POWER)
		maxPower = Constants.BATTLE_MIN_POWER;
	
	if (attackerPower <= maxPower)
		sides[Battle.ATTACKER_SIDE].chooseAllUnits(activeUnits[Battle.ATTACKER_SIDE]);
	else
		sides[Battle.ATTACKER_SIDE].chooseUnits(activeUnits[Battle.ATTACKER_SIDE], maxPower);
	
	if (defenderPower <= maxPower)
		sides[Battle.DEFENDER_SIDE].chooseAllUnits(activeUnits[Battle.DEFENDER_SIDE]);
	else
		sides[Battle.DEFENDER_SIDE].chooseUnits(activeUnits[Battle.DEFENDER_SIDE], maxPower);
}

private void calculateDamage(BattleSide side, UnitMap units, float[] result)
{
	Battle.logger.entering("FightEvent", "calculateDamage");
	UnitSystem us = side.getUnitSystem();
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		for (int i = 0; i < us.groupCount(side.isSpace()); i++)
			result[i] += it.value()*side.modifiedAttackValue(it.key(), i);
	}
}

protected BattleStateEnum fight(Battle battle, long time)
{
	UnitSystem us = battle.getUnitSystem();
	BattleSide[] sides = battle.sides;
	hadDefenders = sides[Battle.DEFENDER_SIDE].size() > 0;
	
	// choose active units
	UnitMap[] activeUnits = new UnitMap[2];
	activeUnits[0] = new UnitMap();
	activeUnits[1] = new UnitMap();
	
	chooseBattleUnits(battle, activeUnits);
	
	if (sides[Battle.ATTACKER_SIDE].allunits.sum() == 0) return cleanUpAfterFight(battle, time);
	if (sides[Battle.DEFENDER_SIDE].allunits.sum() == 0) return cleanUpAfterFight(battle, time);
	
	// calculate damage
	float[][] damage = new float[2][us.groupCount(battle.isSpace())];
	for (int s = Battle.ATTACKER_SIDE; s <= Battle.DEFENDER_SIDE; s++)
		calculateDamage(sides[1-s], activeUnits[1-s], damage[s]);
	battle.adjustBattleDamage(damage);
	
	// calculate size proportion
	float[][] proportion = new float[2][];
	for (int s = Battle.ATTACKER_SIDE; s <= Battle.DEFENDER_SIDE; s++)
		proportion[s] = activeUnits[s].calculateGroupProportion(battle.selector);
	
	// multiply with proportion on damaged side and random
	for (int s = Battle.ATTACKER_SIDE; s <= Battle.DEFENDER_SIDE; s++)
	{
		for (int i = 0; i < us.groupCount(battle.isSpace()); i++)
		{
			damage[s][i] *= battle.getRandom();
			damage[s][i] *= proportion[s][i];
		}
	}
	
//	printDamageStats(damage);
	// distribute damage
	for (int s = Battle.ATTACKER_SIDE; s <= Battle.DEFENDER_SIDE; s++)
		sides[s].distributeDamage(damage[s], activeUnits[s].calculateGroupCount(battle.selector));
	
	return cleanUpAfterFight(battle, time);
}

@Override
public void execute(Battle battle, long time)
{
	battle.numTic++;
	battle.battleLogger.round(battle.numTic);
	try
	{
		battle.checkValidity();
		BattleStateEnum winner = fight(battle, time);
		battle.battleLogger.info("winner=\""+winner+"\"");
		Battle.logger.fine("The winner is: "+winner);
		switch (winner)
		{
			case UNDECIDED:
				battle.activateUndecided(time);
				break;
			case ATTACKER :
				battle.activateAttacker(time, hadDefenders);
				break;
			case DEFENDER :
				battle.activateDefender(time);
				break;
			case ALLDEAD :
				battle.activateAllDead(time);
				break;
			default :
				battle.endBattle(time, BattleStateEnum.ALLKILLED);
				break;
		}
	}
	catch (Throwable e)
	{
		Battle.logger.log(Level.SEVERE, "Battle killed", e);
		battle.endBattle(time, BattleStateEnum.ALLKILLED);
	}
	finally
	{
		battle.battleLogger.endround();
	}
}

}
