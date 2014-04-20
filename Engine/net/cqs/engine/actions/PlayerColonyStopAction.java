package net.cqs.engine.actions;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;

public final class PlayerColonyStopAction extends Action
{

private final Player player;

public PlayerColonyStopAction(Player player)
{ this.player = player; }

@Override
public void execute(Galaxy galaxy)
{
	player.disbandAllFleets();
}

}
