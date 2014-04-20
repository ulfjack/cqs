package net.cqs.engine.ai;

import java.util.Random;

import net.cqs.config.BuildingEnum;
import net.cqs.config.Resource;
import net.cqs.config.units.Unit;
import net.cqs.engine.*;
import net.cqs.engine.base.UnitQueue;
import net.cqs.engine.colony.ColonyController;
import net.cqs.util.EnumQueue;

public final class AIPlayerController implements PlayerController, ColonyController, FleetController
{

private static final long serialVersionUID = 1L;
private final Random rand = new Random();

private final Player player;

public AIPlayerController(Player player)
{
	this.player = player;
}


// PlayerController
@Override
public ColonyController createColonyController(Colony colony)
{ return this; }

@Override
public FleetController createFleetController(Fleet fleet)
{ return this; }

@Override
public void nextResearch(Player p, long time)
{
	if (p != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}

@Override
public void finishResearch(Player p, long time)
{
	if (p != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}

@Override
public void abortResearch(Player p, long time)
{
	if (p != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}


// ColonyController -> Building
@Override
public EnumQueue<BuildingEnum> getBuildingQueue()
{ return new EnumQueue<BuildingEnum>(BuildingEnum.class, false); }
@Override
public boolean addBuildingConstruction(BuildingEnum what, int howmany)
{ return false; }
@Override
public boolean addBuildingRemoval(BuildingEnum what, int howmany)
{ return false; }
@Override
public void clearBuildingQueue()
{/*OK*/}
@Override
public void deleteBuildingEntry(int num)
{/*OK*/}
@Override
public void modifyBuildingEntry(int num, int delta)
{/*OK*/}
@Override
public void moveBuildingEntry(int num, int delta)
{/*OK*/}

@Override
public void nextBuilding(Colony colony, long time, boolean isManualRequest)
{
	if (colony.getOwner() != player) throw new IllegalArgumentException();
	BuildingEnum type = null;
	// money
	if ((colony.calculateIncome() < 0) || (!colony.getOwner().hasMoney()))
		type = BuildingEnum.TRADE_CENTER;
	// efficiency
	else if (colony.getEfficiencyApprox() < 0.95)
		type = BuildingEnum.INFRASTRUCTURE;
	// steel oil
	else if (colony.getDisplayResource(time, Resource.STEEL)
						< 0.1*colony.getStorage(Resource.STEEL))
		type = BuildingEnum.STEEL_MILL;
	else if (colony.getDisplayResource(time, Resource.OIL)
						< 0.1*colony.getStorage(Resource.OIL))
		type = BuildingEnum.REFINERY;
	// steel oil population silicon deuterium: storage
	else if (colony.getDisplayResource(time, Resource.STEEL) 
						== colony.getStorage(Resource.STEEL))
		type = BuildingEnum.STEEL_DEPOT;
	else if (colony.getDisplayResource(time, Resource.OIL)
						== colony.getStorage(Resource.OIL))
		type = BuildingEnum.OIL_TANKS;
	else if (colony.getDisplayPopulation(time) > 0.75 * colony.getPopulationLimit())
		type = BuildingEnum.RESIDENCE;
	else if (colony.getDisplayResource(time, Resource.SILICON)
						== colony.getStorage(Resource.SILICON))
		type = BuildingEnum.SILICON_DEPOT;
	else if (colony.getDisplayResource(time, Resource.DEUTERIUM)
						== colony.getStorage(Resource.DEUTERIUM))
		type = BuildingEnum.DEUTERIUM_DEPOT;
	// random building
	if (type == null)
	{
		BuildingEnum[] types = BuildingEnum.values();
		type = types[rand.nextInt(types.length)];
	}
	if (colony.canBuild(type))
		colony.startBuildingConstruction(time, type, isManualRequest);
}

@Override
public void finishBuilding(Colony colony, long time)
{
	if (colony.getOwner() != player) throw new IllegalArgumentException();
	nextBuilding(colony, time, false);
}

@Override
public void abortBuilding(Colony colony, long time)
{
	if (colony.getOwner() != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}


// ColonyController -> Unit
@Override
public UnitQueue getUnitQueue(int i)
{ return new UnitQueue(); }
@Override
public int addUnitConstruction(Unit unit, int howmany)
{ return 0; }
@Override
public void deleteUnitEntry(int queue, int num)
{/*OK*/}
@Override
public void modifyUnitEntry(int queue, int num, int delta)
{/*OK*/}
@Override
public void moveUnitEntry(int queue, int num, int delta)
{/*OK*/}

@Override
public void nextUnit(Colony colony, long time, int queue, boolean isManualRequest)
{
	if (colony.getOwner() != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}

@Override
public void finishUnit(Colony colony, long time, int queue)
{
	if (colony.getOwner() != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}

@Override
public void abortUnit(Colony colony, long time, int queue)
{
	if (colony.getOwner() != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}


// FleetController
@Override
public void nextCommand(Fleet fleet, long time)
{
	if (fleet.getOwner() != player) throw new IllegalArgumentException();
	// TODO Auto-generated method stub
}

@Override
public boolean isLoopEnabled()
{ return false; }

@Override
public void enableLoop()
{/*OK*/}

@Override
public void disableLoop()
{/*OK*/}

}
