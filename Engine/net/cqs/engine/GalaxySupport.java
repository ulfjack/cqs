package net.cqs.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.cqs.engine.actions.Action;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.DiplomaticRelation;
import net.cqs.engine.messages.InfoListener;
import net.cqs.engine.scores.ScoreManager;

public final class GalaxySupport
{

private final DiplomaticRelation relation;

private final ScoreManager<Player> playerHighscore;
private final ScoreManager<Alliance> allianceHighscore;

final List<InfoListener> infoListeners = new ArrayList<InfoListener>();
final LinkedList<Action> actionList = new LinkedList<Action>();

public GalaxySupport(Galaxy galaxy)
{
	playerHighscore = ScoreManager.wrap(galaxy.getPlayers());
	allianceHighscore = ScoreManager.wrap(galaxy.getAlliances());
	relation = new DiplomaticRelation();
	relation.setData(galaxy.getAlliances().toArray(new Alliance[0]),
			galaxy.getPlayers().toArray(new Player[0]));
}

public DiplomaticRelation getDiplomaticRelation()
{ return relation; }

public ScoreManager<Player> getPlayerHighscore()
{ return playerHighscore; }

public ScoreManager<Alliance> getAllianceHighscore()
{ return allianceHighscore; }

}
