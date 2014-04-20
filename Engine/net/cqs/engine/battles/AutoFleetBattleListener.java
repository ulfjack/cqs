package net.cqs.engine.battles;

import java.io.Serializable;

import net.cqs.engine.Fleet;

public final class AutoFleetBattleListener implements BattleFleetListener, Serializable
{

private static final long serialVersionUID = 1L;

public AutoFleetBattleListener()
{/*OK*/}

@Override
public void won(Fleet f, long time)
{
	f.reset(time);
	f.stationFleet();
	f.removeFleet();
}

@Override
public void lost(Fleet f, long time)
{
	if (f.getTotalUnits() == 0)
		f.removeFleet();
	else
	{
		f.reset(time);
		f.stationFleet();
		f.removeFleet();
	}
}

@Override
public void withdraw(Fleet f, long time)
{
	f.withdraw(time);
}

}
