package net.cqs.engine.scores;

import net.cqs.config.Constants;

/**
 * Nicht Bestandteil der abgespeicherten Datenbank. Nicht serialisierbar! Wird
 * neu erzeugt wenn die Datenbank geladen wird. Die entsprechenden Felder in
 * Player, Alliance und Simulation sind transient.
 */
public abstract class Score<T extends Scoreable<T>>
{

// variables needed for highscore
public final ScoreManager<T> list;
public int position;   // position in highscore
public long points;    // score points
public long secPoints; // secondary score points (in case of ties)

protected Score(ScoreManager<T> sm)
{
	list = sm;
}

public int getPosition()
{ return position; }

public long getPoints()
{ return points; }

protected long updatePoints(long civPoints, long unitPoints)
{
	return Math.round(civPoints + unitPoints*Constants.SCORE_MODIFIER);
}

public abstract T getScoreable();
protected abstract void updateInfo();

public void signalChange()
{
	updateInfo();
	list.signalChange(this);
}

}
