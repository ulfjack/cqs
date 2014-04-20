package net.cqs.engine.repair;

import net.cqs.engine.Fleet;
import net.cqs.engine.Galaxy;
import net.cqs.engine.actions.Action;

public final class EmptyFleetRemoveAction extends Action
{

private final Fleet f;

public EmptyFleetRemoveAction(Fleet f)
{ this.f = f; }

@Override
public void execute(Galaxy galaxy)
{
	Galaxy.logger.warning("Empty fleet!");
	f.findSystem().removeFleet(f);
	if (f.getOwner() != null)
		f.getOwner().getFleets().remove(f);
	f.notifyRemove();
	Galaxy.logger.warning("fleet "+f.getId()+" of "+f.getOwner());
}

}
