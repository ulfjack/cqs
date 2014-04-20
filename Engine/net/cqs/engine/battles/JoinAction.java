package net.cqs.engine.battles;

import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.fleets.FleetJoinBattleException;

public final class JoinAction extends BattleAction
{

private final int side;
private final FleetState reason;
private final Fleet fleet;
private final BattleFleetListener listener;
private final boolean schedule;

public JoinAction(Fleet fleet, FleetJoinBattleException e)
{
	this.side = e.getSide();
	this.reason = e.getReason();
	this.fleet = fleet;
	this.listener = e.getListener();
	this.schedule = e.shouldSchedule();
}

public JoinAction(int side, FleetState reason, Fleet fleet, BattleFleetListener listener, boolean schedule)
{
	this.side = side;
	this.reason = reason;
	this.fleet = fleet;
	this.listener = listener;
	this.schedule = schedule;
}

public JoinAction(int side, FleetState reason, Fleet fleet, BattleFleetListener listener)
{ this(side, reason, fleet, listener, false); }

public JoinAction(int side, FleetState reason, Fleet fleet)
{ this(side, reason, fleet, new BattleFleetAdapter(), false); }

@Override
public void execute(Battle battle, long time)
{
	if (fleet.getState() != FleetState.WAITING)
		throw new IllegalStateException("Can only join a waiting fleet!");
	
	if (battle.sides[side].contains(fleet))
	{
		fleet.setState(FleetState.FIGHTING);
		throw new IllegalStateException("Can only join a battle once!");
	}
	
	fleet.setState(reason);
	
	battle.battleLogger.joinPlayer(side, fleet.getOwner());
	battle.battleLogger.joinFleet(side, fleet);
	
	BattleAdaptor adaptor = battle.adaptors.get(fleet.getOwner());
	if (adaptor == null)
	{
		adaptor = new BattleAdaptor(battle, fleet.getOwner());
		battle.adaptors.put(adaptor.player, adaptor);
	}
	FleetEntry entry = new FleetEntry(adaptor, fleet, listener);
	battle.sides[side].join(entry);
	
	if (schedule && (battle.nextTicHandle == null))
	{
		battle.scheduleBattle(time);
		battle.getAutoDefenders(time);
	}
	
	fleet.setNextCompleteTime(battle.nextTicTime);
	battle.notifyJoin(side, fleet.getOwner());
}

}
