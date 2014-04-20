package net.cqs.engine.battles;

import java.io.Serializable;
import java.util.logging.Level;

import net.cqs.engine.Player;

/**
 * Connection between a player and a battle.
 * All fleets from one player go into one BattleAdaptor.
 */
public final class BattleAdaptor implements Serializable
{

private static final long serialVersionUID = 1L;

public final Battle battle;
public final Player player;
private final boolean persistent;
private int numFleets = 0;

BattleAdaptor(Battle battle, Player player, boolean persistent)
{
	this.battle = battle;
	this.player = player;
	this.persistent = persistent;
	if (player.getBattles().contains(battle))
		throw new IllegalStateException();
	if (persistent && battle.mayShowBattle())
		player.getBattles().add(battle);
}

BattleAdaptor(Battle battle, Player player)
{ this(battle, player, false); }

public int getFleetCount()
{ return numFleets; }

void incFleet()
{
	numFleets++;
	if ((numFleets == 1) && battle.mayShowBattle() && !persistent)
		player.getBattles().add(battle);
}

void decFleet(long time)
{
	if (numFleets == 0)
		throw new IllegalStateException();
	numFleets--;
	if ((numFleets == 0) && !persistent)
	{
		player.getBattles().remove(battle);
		battle.notifyRemove(time, player);
	}
}

void clearPersistent(long time)
{
	if (numFleets != 0)
		Battle.logger.log(Level.SEVERE, "Not empty!", new IllegalStateException());
	player.getBattles().remove(battle);
	battle.notifyRemove(time, player);
}

void reset(long time)
{
	numFleets = 0;
	player.getBattles().remove(battle);
	battle.notifyRemove(time, player);
}

}
