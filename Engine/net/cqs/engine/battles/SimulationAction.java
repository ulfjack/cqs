package net.cqs.engine.battles;

import java.util.logging.Level;

import net.cqs.config.BattleStateEnum;

public final class SimulationAction extends FightAction
{

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
	}
	catch (Throwable e)
	{
		Battle.logger.log(Level.SEVERE, "Ignored Exception", e);
		battle.endBattle(time, BattleStateEnum.ALLKILLED);
	}
	finally
	{
		battle.battleLogger.endround();
	}
}

}
