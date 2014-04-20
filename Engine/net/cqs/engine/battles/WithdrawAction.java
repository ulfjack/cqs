package net.cqs.engine.battles;

import java.util.logging.Level;

import net.cqs.config.BattleMessageEnum;
import net.cqs.config.BattleStateEnum;
import net.cqs.config.Constants;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;

public final class WithdrawAction extends BattleAction
{

private final FleetEntry entry;
private final int side;

public WithdrawAction(FleetEntry entry, int side)
{
	this.entry = entry;
	this.side = side;
}

protected BattleStateEnum cleanUpAfterPunish(Battle battle, long time)
{
	Battle.logger.entering("Battle", "cleanUpAfterPunish");
//	boolean attackerStart = battle.sides[Battle.ATTACKER_SIDE].size() != 0;
	boolean defenderStart = battle.sides[Battle.DEFENDER_SIDE].size() != 0;
	
	Battle.logger.fine(entry.fleet+" has "+entry.fleet.getTotalUnits()+" units left.");
	UnitMap map = new UnitMap();
	entry.fleet.checkAndFixUnits(time, map);
	battle.battleLogger.unitsFixed(entry.fleet, map);
	entry.fleet.checkAndFixCargo(time);
	
	Battle.logger.fine("Removing "+entry.fleet);
	boolean survive = entry.fleet.containsUnits(battle.selector);
	if (survive)
		battle.battleLogger.withdrawSuccess(side, entry.fleet);
	else
		battle.battleLogger.withdrawFailure(side, entry.fleet);
	
	try
	{
		battle.sides[side].remove(time, entry, survive ? RemoveReason.WITHDRAW : RemoveReason.LOST);
	}
	catch (Throwable e)
	{
		Battle.logger.log(Level.SEVERE, "Ignored Exception", e);
		entry.fleet.reset(time);
	}
	
	boolean attackerLeft = battle.sides[Battle.ATTACKER_SIDE].size() != 0;
	boolean defenderLeft = battle.sides[Battle.DEFENDER_SIDE].size() != 0;
	
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

private void calculateDamage(BattleSide bySide, UnitMap units, float[] result)
{
	Battle.logger.entering("WithdrawEvent", "calculateDamage");
	UnitSystem us = bySide.getUnitSystem();
	UnitIterator it = units.unitIterator();
	while (it.hasNext())
	{
		it.next();
		for (int i = 0; i < us.groupCount(bySide.isSpace()); i++)
			result[i] += it.value()*bySide.modifiedAttackValue(it.key(), i);
	}
}

void distributeDamage(Battle battle, BattleSide toside, float[] damageNew)
{
	UnitSystem us = battle.getUnitSystem();
	float[] damageDone = new float[us.groupCount(battle.isSpace())];
	int[] groupUnitCount = entry.activeUnits.calculateGroupCount(battle.selector);
	toside.distributeDamage(damageNew, damageDone, entry, groupUnitCount);
}

public BattleStateEnum punish(Battle battle, long time)
{
	Battle.logger.entering("Battle", "punish");
	UnitSystem us = battle.getUnitSystem();
	
	BattleSide byside = battle.sides[1-side];
	BattleSide toside = battle.sides[side];
	
	if (byside.size() == 0) return cleanUpAfterPunish(battle, time);
	if (toside.size() == 0) return cleanUpAfterPunish(battle, time);
	
	// choose active units
	UnitMap activeUnits = new UnitMap();
	float targetPower = entry.fleet.getPower(battle.selector);
	float maxPower = Constants.BATTLE_POWER_FACTOR*targetPower;
	
	byside.chooseUnits(activeUnits, maxPower);
	entry.chooseAllUnits(battle.selector);
	
	// calculate damage
	float[] damage = new float[us.groupCount(battle.isSpace())];
	calculateDamage(byside, activeUnits, damage);
	battle.adjustPunishmentDamage(1-side, damage);
	
	// calculate size proportions
	float[] proportion = entry.activeUnits.calculateGroupProportion(battle.selector);
	
	// multiply with proportion on damaged side and random
	for (int i = 0; i < us.groupCount(battle.isSpace()); i++)
	{
		damage[i] *= battle.getRandom();
		damage[i] *= proportion[i];
	}
	
	// distribute damage
	distributeDamage(battle, toside, damage);
	return cleanUpAfterPunish(battle, time);
}

@Override
public void execute(Battle battle, long time)
{
	Battle.logger.entering("WithdrawEvent", "execute");
	try
	{
		battle.checkValidity();
		BattleStateEnum winner = punish(battle, time);
		Battle.logger.fine("The winner is: "+winner);
		switch (winner)
		{
			case UNDECIDED : battle.withdrawUndecided(time); break;
			case ATTACKER :  battle.withdrawAttacker(time);  break;
			case DEFENDER :  battle.withdrawDefender(time);  break;
			case ALLDEAD :   battle.withdrawAllDead(time);   break;
			default :
				battle.endBattle(time, BattleStateEnum.ALLKILLED);
				break;
		}
	}
	catch (Throwable e)
	{
		Battle.logger.log(Level.SEVERE, "Ignored Exception", e);
		battle.endBattle(time, BattleStateEnum.ALLKILLED);
	}
}

}
