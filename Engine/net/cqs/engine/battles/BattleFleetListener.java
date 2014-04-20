package net.cqs.engine.battles;

import net.cqs.engine.Fleet;

public interface BattleFleetListener
{

void won(Fleet f, long time);
void lost(Fleet f, long time);
void withdraw(Fleet f, long time);

}
