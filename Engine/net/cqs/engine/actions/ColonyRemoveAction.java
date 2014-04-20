package net.cqs.engine.actions;

import net.cqs.config.BattleStateEnum;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Planet;
import net.cqs.engine.Player;

public final class ColonyRemoveAction extends Action
{

private final Colony colony;

public ColonyRemoveAction(Colony colony)
{ this.colony = colony; }

@Override
public void execute(Galaxy galaxy)
{
	Player owner = colony.getOwner();
	Planet planet = colony.getPlanet();
	
	owner.removeColony(colony);
	
	colony.removeAllAgents();
	
	galaxy.removeAllEvents(colony);
	colony.getPlanet().removeColony(colony);
	if ((planet.amount() == 0) && (planet.getSpaceBattle() != null))
		// remove blockade (not allowed to block empty planets)
		planet.getSpaceBattle().endBattle(galaxy.getTime(), BattleStateEnum.BLOCKING_EMPTY_PLANET);	
	colony.notifyRemove();
	if (owner.getColonies().size() == 0)
		galaxy.schedule(new PlayerColonyStopAction(owner));
}

}
