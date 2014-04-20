package net.cqs.engine.battles;

import java.io.Serializable;

import net.cqs.engine.Fleet;

public class BattleFleetAdapter implements BattleFleetListener, Serializable
{

private static final long serialVersionUID = 1L;

@Override
public void won(Fleet f, long time)
{/*OK*/}

@Override
public void lost(Fleet f, long time)
{/*OK*/}

@Override
public void withdraw(Fleet f, long time)
{/*OK*/}

}
