package net.cqs.engine.scores;

import net.cqs.engine.Player;

public final class PlayerScore extends Score<Player>
{

private Player player;

public PlayerScore(ScoreManager<Player> hs, Player p)
{
	super(hs);
	player = p;
	updateInfo();
}

public Player getPlayer()
{ return player; }

@Override
public Player getScoreable()
{ return player; }

@Override
protected void updateInfo()
{
//	points = updatePoints(player.points, player.unitPoints, player.resPoints);
	points = updatePoints(player.getPoints(), player.getUnitPoints());
	secPoints = player.getColonies().size();
}

}
