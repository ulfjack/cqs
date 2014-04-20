package net.cqs.engine.battles;

import net.cqs.config.BattleTypeEnum;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitSelector;

public class TestBattle extends Battle
{

private static final long serialVersionUID = 1L;

int removeCalled = 0;

public TestBattle(Galaxy galaxy, Position position)
{
	super(BattleTypeEnum.SIMULATION_BATTLE, galaxy, 0L, position, UnitSelector.GROUND_ONLY, null);
}

@Override
protected void fileReport(long time, Player who)
{
	// Ok
}

@Override
public Position position()
{ throw new UnsupportedOperationException(); }

@Override
protected void scheduleBattle(long currenttime)
{ throw new UnsupportedOperationException(); }

@Override
protected void unscheduleBattle()
{ throw new UnsupportedOperationException(); }

@Override
protected void notifyRemove(long time)
{ removeCalled++; }

@Override
public void checkValidity()
{
	// Ok
}

@Override
public boolean isSpace()
{ return false; }

}
