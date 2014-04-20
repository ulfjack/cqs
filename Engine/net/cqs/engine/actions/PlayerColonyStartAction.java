package net.cqs.engine.actions;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;

public final class PlayerColonyStartAction extends Action
{

private final Player who;

public PlayerColonyStartAction(Player who)
{
	this.who = who;
}

@Override
public void execute(Galaxy galaxy)
{
	galaxy.findEmptyColony(who);
}

}
