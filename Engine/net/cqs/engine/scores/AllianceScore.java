package net.cqs.engine.scores;

import net.cqs.engine.Player;
import net.cqs.engine.diplomacy.Alliance;

public final class AllianceScore extends Score<Alliance>
{

private Alliance alliance;

public AllianceScore(ScoreManager<Alliance> hs, Alliance a)
{
	super(hs);
	alliance = a;
	updateInfo();
}

public Alliance getAlliance()
{ return alliance; }

@Override
public Alliance getScoreable()
{ return alliance; }

@Override
protected void updateInfo()
{
	long civPoints = 0;
	long unitPoints = 0;
//	long resPoints = 0;
	
	int amount = alliance.size();
	for (int i = 0; i < amount; i++)
	{
		Player p = alliance.get(i);
		civPoints += p.getPoints();
		unitPoints += p.getUnitPoints();
//		resPoints += p.resPoints;
	}
	
//	points = updatePoints(civPoints, unitPoints, resPoints);
	points = updatePoints(civPoints, unitPoints);
	if (amount == 0)
		secPoints = 0;
	else
		secPoints = Math.round((double) points / amount);
}

}
