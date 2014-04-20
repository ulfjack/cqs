package net.cqs.engine.fleets;

import java.io.Serializable;

import net.cqs.engine.Fleet;
import net.cqs.engine.battles.BattleFleetListener;

public final class NormalFleetBattleListener implements BattleFleetListener, Serializable
{

private static final long serialVersionUID = 1L;

public NormalFleetBattleListener()
{/*OK*/}

@Override
public void won(Fleet f, long time)
{
	f.reset(time);
	f.resume(time, true);
}

@Override
public void lost(Fleet f, long time)
{
	f.reset(time);
}

@Override
public void withdraw(Fleet f, long time)
{
	f.withdraw(time);
}

}
