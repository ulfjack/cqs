package net.cqs.engine;

public enum FleetState
{

WAITING,
ACTING,
FIGHTING,
BLOCKING,
INVADING,
ROBBING;

public boolean isHostileAction()
{
	return ((this == FIGHTING) || (this == BLOCKING) ||
	        (this == INVADING) || (this == ROBBING));
}

public boolean isWaitActFight()
{
	return ((this == WAITING) || (this == ACTING) || (this == FIGHTING));	
}

}
