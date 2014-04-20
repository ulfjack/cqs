package net.cqs.engine.human;

import net.cqs.config.BuildingEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.QueueEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.engine.base.UnitQueue;
import net.cqs.engine.colony.ColonyController;
import net.cqs.util.EnumQueue;

public final class HumanColonyController implements ColonyController
{

private static final long serialVersionUID = 1L;

private final Colony colony;
private final EnumQueue<BuildingEnum> buildingQueue = EnumQueue.of(BuildingEnum.class, false);
private final UnitQueue[] unitQueues;

public HumanColonyController(Colony colony)
{
	this.colony = colony;
	unitQueues = new UnitQueue[3];
	for (int i = 0; i < 3; i++)
		unitQueues[i] = new UnitQueue();
}

//	Buildings
@Override
public EnumQueue<BuildingEnum> getBuildingQueue()
{ return buildingQueue; }

@Override
public boolean addBuildingConstruction(BuildingEnum what, int howmany)
{
	if (howmany <= 0)
	{
		colony.getOwner().log(ErrorCode.INVALID_INPUT);
		return false;
	}
	if (howmany > MAX_QUEUE) howmany = MAX_QUEUE;
	buildingQueue.add(what, howmany);
	return buildingQueue.size() == 1;
}

@Override
public boolean addBuildingRemoval(BuildingEnum what, int howmany)
{
	if (howmany <= 0)
	{
		colony.getOwner().log(ErrorCode.INVALID_INPUT);
		return false;
	}
	if (howmany > MAX_QUEUE) howmany = MAX_QUEUE;
	buildingQueue.add(what, -howmany);
	return buildingQueue.size() == 1;
}

@Override
public void clearBuildingQueue()
{ buildingQueue.clear(); }

@Override
public void deleteBuildingEntry(int num)
{ buildingQueue.deleteInverse(num); }

@Override
public void modifyBuildingEntry(int num, int delta)
{ buildingQueue.modifyInverse(num, delta); }

@Override
public void moveBuildingEntry(int num, int delta)
{
	if (delta == 0)
		buildingQueue.moveTopInverse(num);
	else
		buildingQueue.moveUpInverse(num, delta);
}

@Override
public void nextBuilding(Colony c, long time, boolean isManualRequest)
{
	BuildingEnum type = null;
	int amount = -1;
	
	while (amount < 0)
	{
		if (buildingQueue.sum() == 0)
		{
			if (!isManualRequest)
			{
				Galaxy galaxy = colony.getGalaxy();
				galaxy.getInfoTranslator(colony.getOwner(), time).queueEmpty(colony.getPosition(), QueueEnum.BUILDING);
			}
			return;
		}
		type = buildingQueue.peek();
		amount = buildingQueue.peekAmount();
		
		if (amount < 0)
		{
			if (colony.getBuilding(type) > 0) break;
			buildingQueue.remove();
		}
	}
	
	if ((type == null) || (amount == 0)) return;
	if (amount > 0)
	{
		if (colony.startBuildingConstruction(time, type, isManualRequest))
			buildingQueue.remove();
	}
	else
	{
		if (colony.startBuildingRemoval(time, type, isManualRequest))
			buildingQueue.remove();
	}
}

@Override
public void finishBuilding(Colony c, long time)
{/*OK*/}

@Override
public void abortBuilding(Colony c, long time)
{/*OK*/}


// Units
@Override
public UnitQueue getUnitQueue(int i)
{ return unitQueues[i]; }

@Override
public int addUnitConstruction(Unit unit, int howmany)
{
	if (!colony.mayBuild(unit)) return -1;
	
	int queue = colony.getCorrectUnitQueue(unit);
	unitQueues[queue].add(unit, howmany);
	if (unitQueues[queue].size() == 1)
		return queue;
	return -1;
}

@Override
public void deleteUnitEntry(int queue, int num)
{
	if ((queue < 0) || (queue >= Colony.UNIT_QUEUES)) return;
	unitQueues[queue].deleteInverse(num);
}

@Override
public void modifyUnitEntry(int queue, int num, int delta)
{
	if ((queue < 0) || (queue >= Colony.UNIT_QUEUES)) return;
	unitQueues[queue].modifyInverse(num, delta);
}

@Override
public void moveUnitEntry(int queue, int num, int delta)
{
	if ((queue < 0) || (queue >= Colony.UNIT_QUEUES)) return;
	if (delta == 0)
		unitQueues[queue].moveTopInverse(num);
	else
		unitQueues[queue].moveUpInverse(num, delta);
}


// Unit
@Override
public void nextUnit(Colony c, long time, int queue, boolean isManualRequest)
{
	if ((colony.getEfficiencyApprox() < 0.5f) && colony.getOwner().isAutoStopUnits())
		return;
	if (unitQueues[queue].size() == 0)
	{
		if (!isManualRequest)
		{
			Galaxy galaxy = colony.getGalaxy();
			galaxy.dropInfo(colony.getOwner(), time, InfoEnum.QUEUE_EMPTY, colony.getPosition(), QueueEnum.getUnitQueue(queue));
		}
		return;
	}
	Unit unit = unitQueues[queue].get();
	if (colony.startUnit(time, unit, isManualRequest))
		unitQueues[queue].remove();
}

@Override
public void finishUnit(Colony c, long time, int queue)
{/*OK*/}

@Override
public void abortUnit(Colony c, long time, int queue)
{/*OK*/}

}
