package net.cqs.engine.battles;

import java.io.Serializable;

import net.cqs.engine.Fleet;
import net.cqs.engine.Player;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;

public final class FleetEntry implements Serializable
{

private static final long serialVersionUID = 1L;

public final BattleAdaptor adaptor;
public final Fleet fleet;
public final BattleFleetListener listener;
public final UnitMap activeUnits = new UnitMap();

public FleetEntry(BattleAdaptor adaptor, Fleet fleet, BattleFleetListener listener)
{
	this.adaptor = adaptor;
	this.fleet = fleet;
	this.listener = listener;
	adaptor.incFleet();
}

void chooseAllUnits(UnitSelector selector)
{
	activeUnits.clear();
	UnitIterator it = fleet.getUnitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		activeUnits.set(it.key(), it.value());
	}
}

@Override
public String toString()
{ return fleet.toString(); }

public UnitMap getActiveUnits()
{ return activeUnits; }

public Fleet getFleet()
{ return fleet; }

public Player getOwner()
{ return fleet.getOwner(); }

public long getId()
{ return fleet.getId(); }

public String getName()
{ return fleet.getName(); }

}