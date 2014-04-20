package net.cqs.engine.battles;

import java.io.Serializable;
import java.util.logging.Level;

import net.cqs.engine.Fleet;

public final class BlockadeFleetBattleListener implements BattleFleetListener, Serializable
{

private static final long serialVersionUID = 1L;

public BlockadeFleetBattleListener()
{/*OK*/}

public void won(Fleet f, long time)
{
	Battle.logger.log(Level.SEVERE, "Cannot win a Blockade!", new IllegalStateException());
	f.reset(time);
}

public void lost(Fleet f, long time)
{
	Battle.logger.entering("BlockadeFleetBattleListener", "lost");
	f.reset(time);
}

public void withdraw(Fleet f, long time)
{
	f.withdraw(time);
}

}
