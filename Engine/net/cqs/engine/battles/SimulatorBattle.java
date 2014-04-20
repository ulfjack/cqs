package net.cqs.engine.battles;

import net.cqs.config.BattleStateEnum;
import net.cqs.config.BattleTypeEnum;
import net.cqs.config.ResourceEnum;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;
import net.cqs.util.EnumLongMap;

public final class SimulatorBattle extends Battle
{

public static final int MAX_FIXED = 1;

private static final long serialVersionUID = 1L;

private Player player;
private EnumLongMap<ResourceEnum>[] res;

@SuppressWarnings("unchecked")
public SimulatorBattle(long time, boolean space, Player player)
{
	super(BattleTypeEnum.SIMULATION_BATTLE, null, time, new Position(0,0), UnitSelector.SPACE_ONLY, null);
	this.player = player;
	res = new EnumLongMap[2];
	res[0] = EnumLongMap.of(ResourceEnum.class);
	res[1] = EnumLongMap.of(ResourceEnum.class);
	
	setKind(space);
}

@Override
public void notifyRemove(long time)
{/*OK*/}

@Override
public void checkValidity()
{/*OK*/}

// HACK!!!
@Override
public boolean isSpace()
{ return selector == UnitSelector.SPACE_ONLY; }


@Override
public boolean mayFileReport()
{ return false; }

@Override
public boolean mayShowBattle()
{ return false; }

@Override
public boolean mustRemoveFleet(Fleet f)
{ return f.getTotalUnits() == 0; }

@Override
protected float getRandom()
{ return 1.0f; }

public void addFleet(UnitMap units, EnumLongMap<ResourceEnum> resources, int side)
{
	if (units.sum() == 0) return;
	res[side].add(resources);
	
	if (sides[side].size() > 0)
	{
		Fleet dummyFleet = sides[side].getFleet(0);
		dummyFleet.addUnits(units);
		sides[side].addUnits(units);
	}
	else
	{
		Fleet dummyFleet = Fleet.createSimulationFleet(galaxy, position(), player, 0, units);
		execute(new JoinAction(side, FleetState.FIGHTING, dummyFleet), 0);
	}
}

public void removeFleet(UnitMap units, EnumLongMap<ResourceEnum> resources, int side)
{
	if (units.sum() == 0) return;
	
	if (sides[side].size() > 0)
	{
		FleetEntry dummyEntry = sides[side].get(0);
		UnitMap temp = dummyEntry.fleet.getUnitsCopy();
		try
		{
			temp.subtract(units);
			res[side].subtract(resources);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		dummyEntry.activeUnits.clear();
		dummyEntry.fleet.subtractUnits(units);
		sides[side].removeUnits(units);
	}
}

@Override
protected void getAutoDefenders(long time)
{/*No AutoDefenders!*/}

@Override
protected void scheduleBattle(long currenttime)
{/*DO NOT SCHEDULE BATTLE!*/}

@Override
public void endBattle(long time, BattleStateEnum reason)
{
	throw new UnsupportedOperationException();
}

public long getResource(int side, int which)
{ return res[side].get(ResourceEnum.get(which)); }

public void doTick(long time)
{
	execute(new SimulationAction(), time);
}

}
