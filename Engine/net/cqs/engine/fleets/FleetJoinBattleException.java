package net.cqs.engine.fleets;

import net.cqs.engine.FleetState;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BattleFleetListener;

public final class FleetJoinBattleException extends FleetException
{

private static final long serialVersionUID = 1L;

private final Battle battle;
private final int side;
private final FleetState reason;
private final BattleFleetListener listener;
private final boolean schedule;

public FleetJoinBattleException(Battle battle, int side, FleetState reason, BattleFleetListener listener, boolean schedule)
{
	this.battle = battle;
	this.side = side;
	this.reason = reason;
	this.listener = listener;
	this.schedule = schedule;
}

public Battle getBattle()
{ return battle; }

public int getSide()
{ return side; }

public FleetState getReason()
{ return reason; }

public BattleFleetListener getListener()
{ return listener; }

public boolean shouldSchedule()
{ return schedule; }

}
