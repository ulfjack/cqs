package net.cqs.engine.empty;

import net.cqs.engine.battles.AbstractBattleLogger;
import net.cqs.engine.battles.Battle;

public final class NullBattleLogger extends AbstractBattleLogger
{

private static final long serialVersionUID = 1L;

public NullBattleLogger(Battle battle)
{ super(battle); }

@Override
protected void log(String msg)
{/*Do nothing!*/}

}
