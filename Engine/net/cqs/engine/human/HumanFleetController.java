package net.cqs.engine.human;

import net.cqs.engine.Fleet;
import net.cqs.engine.FleetController;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.engine.fleets.FleetCommandList;

public final class HumanFleetController implements FleetController
{

private static final long serialVersionUID = 1L;

private FleetCommandList orders = new FleetCommandList();
private int nextCommand = -1;
private boolean loop = false;

public HumanFleetController(Fleet fleet)
{/*OK*/}

public FleetCommandList getOrders()
{ return orders; }

public void append(FleetCommand... commands)
{
	for (FleetCommand c : commands)
		orders.append(c);
	if (commands.length == orders.size())
		nextCommand = commands.length == 0 ? -1 : 0;
}

public void append(Iterable<FleetCommand> commands)
{
	int before = 0;
	for (FleetCommand c : commands)
		orders.append(c.copy());
	if (before == 0)
		nextCommand = orders.size() == 0 ? -1 : 0;
}

public FleetCommand getCommand(int which)
{ return orders.getCommand(which); }

public void setNextCommand(int which)
{
	if ((which < 0) || (which >= orders.size()))
		throw new IllegalArgumentException();
	if (which == nextCommand)
		nextCommand = -1;
	else
		nextCommand = which;
}

public int getNextCommand()
{ return nextCommand; }

@Override
public boolean isLoopEnabled()
{ return loop; }

@Override
public void enableLoop()
{ loop = true; }

@Override
public void disableLoop()
{
	loop = false;
	if (nextCommand == 0) nextCommand = -1;
}

public void removeCommand(int which)
{
	if (orders.delete(which))
	{
		if (which < nextCommand)
			nextCommand--;
	}
}

public void removeBefore()
{
	if (orders.deleteBefore(nextCommand))
		nextCommand = 0;
}

public void removeAfter()
{ orders.deleteAfter(nextCommand); }

public void removeAll()
{
	orders.clear();
	nextCommand = 0;
}

public void moveUpCommand(int which, int howfar)
{
	if (orders.moveUp(which, howfar))
	{
		if ((which > nextCommand) && (which-howfar < nextCommand))
			nextCommand++;
		else
			if ((which < nextCommand) && (which-howfar > nextCommand))
				nextCommand--;
			else
				if (which == nextCommand)
					nextCommand = which-howfar;
				else
					if (which-howfar == nextCommand)
						nextCommand = which;
	}
}


// Control
@Override
public void nextCommand(Fleet f, long time)
{
	if ((nextCommand < 0) || (nextCommand >= orders.size()))
		return;
	f.prepare(time, orders.getCommand(nextCommand));
	nextCommand++;
	if (loop && (nextCommand >= orders.size()))
		nextCommand = 0;
}

}
