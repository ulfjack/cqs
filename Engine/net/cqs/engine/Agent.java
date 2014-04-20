package net.cqs.engine;

import java.io.Serializable;

import net.cqs.config.InfoEnum;

public final class Agent implements Serializable
{

private static final long serialVersionUID = 1L;

private final Galaxy galaxy;
private final Position position;
private final Player owner;

private final String id;
private final long seed;
private final long startTime;
private final long startRealTime;

private int amount;

/**
 * creates a new agent for player who on colony with starttime time
 */
public Agent(Galaxy galaxy, Colony colony, Player who, long time)
{
	this.galaxy = galaxy;
	this.position = colony.getPosition();
	this.owner = who;
	
	this.startTime = time;
	this.startRealTime = System.currentTimeMillis();
	this.amount = 0;
	this.seed = galaxy.getRandomLong();
	
	colony.addAgent(this);
	who.addAgent(this);
	
	id = galaxy.generateUniqueID();
	galaxy.addAgentPeer(this, time);
}

public Galaxy getGalaxy()
{ return galaxy; }

public Position getPosition()
{ return position; }

public Player getOwner()
{ return owner; }

public String getId()
{ return id; }

public long getSeed()
{ return seed; }

public long getStartTime()
{ return startTime; }

public long getStartRealTime()
{ return startRealTime; }

public int getAmount()
{ return amount; }

public Colony findColony()
{ return position.findColony(galaxy); }

/**
 * return probability depending on the time the agent is already active
 * balanced to: starting with 0%, after 7 days 96.5%, max. 99.5%
 */
public float getProbability(long time)
{
	long t = (time - startTime); // time in seconds
	float c = 172477; // -1/Math.ln(0.98/0.95) * 60*60*24*7
	return (float) (0.995*(1 - Math.exp(-t/c)));
}

public int getProbabilityPercent()
{
	return (int) Math.floor(100*getProbability(galaxy.getTime()));
}

public void notifyRemove(long time)
{
	galaxy.removeAgentPeer(this, time);
}

/**
 * removes Agent from colony and player
 */
public void remove(long time)
{
	notifyRemove(time);
	Colony c = findColony();
	if (c != null) c.removeAgent(this);
	owner.removeAgent(this);
}

/**
 * increases number of agents on colony by one
 */
public void increase()
{ amount++; }

/**
 * decreases number of agents on colony by one
 * when decreasing to zero, the agent is removed
 */
public void decrease(long time)
{
	amount--;
	if (amount == 0)
		remove(time);
}

/**
 * creates a report containing data about the stationed units on the colony spied on
 * error is 15 percent plus minus
 * agent number is decreased by one
 */
public void requestReport(long time)
{
	Colony c = findColony();
	if (galaxy.getRandomFloat() > 0.3)
	{
		galaxy.requestAgentReport(this, time);
		galaxy.dropInfo(owner, time, InfoEnum.SPY_REPORT, c.getPosition(), id);
	}
	else
		galaxy.dropInfo(owner, time, InfoEnum.SPY_NO_REPORT, c.getPosition());
	
	galaxy.dropInfo(c.getOwner(), time, InfoEnum.SPY_FOUND, c.getPosition(), c.getOwner().getName());
	decrease(time);
}

public void requestReport()
{ requestReport(getGalaxy().getTime()); }

@Override
public boolean equals(Object o)
{ return this == o; }

@Override
public int hashCode()
{ return id.hashCode(); }

}
