package net.cqs.engine;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import net.cqs.config.BuildingEnum;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.InvalidPositionException;
import net.cqs.config.PlanetEnum;
import net.cqs.config.Resource;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.PlanetBattle;
import net.cqs.engine.diplomacy.DiplomaticRelation;
import net.cqs.engine.diplomacy.DiplomaticStatus;
import net.cqs.util.IntIntMap;

public final class Planet implements Iterable<Colony>, Serializable
{

private static final long serialVersionUID = 1L;

private final Galaxy galaxy;
private final Position position;
private final PlanetEnum type;

private final long radius;
private final long speed;
private final long theta;

private final int size;
private int used;
private Colony[] data;
private PlanetBattle battle;

private int maxPlayers;
private final IntIntMap modifiers;
private final long populationGrowthModifier;

Planet(SolarSystem s, int planet, PlanetEnum type, Random rand, long radius, long theta)
{
	this.galaxy = s.getGalaxy();
	this.type = type;
	position = new Position(s.getSystemNumber(), planet);
	data = new Colony[0];
	modifiers = new IntIntMap();
	
	size = type.getSize(rand);
	maxPlayers = type.getMaxStart();
	
	populationGrowthModifier = type.getPopulationModifier(rand);
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		modifiers.set(i, type.getResourceModifier(i, rand));
	modifiers.set(Resource.MONEY, 100);
	
	this.radius = radius;
	this.speed = SolarSystem.radiusToSpeed(radius);
	this.theta = theta;
}

public void check() throws InvalidDatabaseException
{
	for (Colony c : data)
		if (c != null) c.check();
	
	if (battle != null) battle.check();
	
	int nused = 0;
	for (int i = 0; i < data.length; i++)
	{
		if (data[i] != null)
			nused += data[i].getSize();
	}
	if (used != nused)
		throw new InvalidDatabaseException("used isn't correct!");
}

private int firstColony()
{
	for (int i = 0; i < data.length; i++)
		if (data[i] != null) return i;
	return data.length;
}

@Override
public Iterator<Colony> iterator()
{
	return new Iterator<Colony>()
		{
			private int next = firstColony();
			@Override
			public boolean hasNext()
			{ return next < data.length; }
			@Override
			public Colony next()
			{
				if (next >= data.length) throw new NoSuchElementException();
				Colony c = data[next++];
				while ((next < data.length) && (data[next] == null)) next++;
				return c;
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

public Galaxy getGalaxy()
{ return galaxy; }

public Position getPosition()
{ return position; }

public SolarSystem getSolarSystem()
{ return position.findSystem(galaxy); }

public boolean isBlocked()
{ return (battle != null) && (battle.getOwner() != null); }

public Player getBlockadePlayer()
{ return battle.getOwner(); }

public int getBlockadeUnitCount()
{ return battle.getSide(Battle.DEFENDER_SIDE).allunits.sum(); }

public PlanetBattle getSpaceBattle()
{ return battle; }

public void removeSpaceBattle()
{ battle = null; }

public PlanetBattle createSpaceBattle(long time)
{
	if (battle == null)
	{
		battle = new PlanetBattle(galaxy, time, this);
		battle.init();
	}
	return battle;
}

public PlanetEnum getType()
{ return type; }

public int getMaxPlayers()
{ return maxPlayers; }

public void decreaseMaxPlayers()
{ maxPlayers--; }

public int getUsed()
{ return used; }

public void decreaseUsed()
{ used--; }

public void increaseUsed()
{ used++; }

public int getSize()
{ return size; }

public int getDisplaySize()
{ return 5*size; }

public int getModifier(int resource)
{ return modifiers.get(resource); }

public long getPopulationGrowthModifier()
{ return populationGrowthModifier; }

public int getPopulationGrowthModifierPercent()
{ return (int) ((100*populationGrowthModifier)/PlanetEnum.MAX_POPULATION_MODIFIER); }

public boolean mayPassBlockade(Fleet f, boolean incoming)
{
	if (battle == null) return true;
	return battle.mayPass(f, incoming);
}

public Colony getColony(int which)
{
	if ((which < 0) || (which >= data.length))
		throw new InvalidPositionException("Invalid Colony: "+which);
	return data[which];
}

public DiplomaticStatus diplomaticStatus(Player p)
{
	DiplomaticStatus status = null;
	DiplomaticRelation relation = galaxy.getDiplomaticRelation();
	for (Colony c : data)
	{
		if (c != null)
		{
			DiplomaticStatus other = relation.getEntry(c.getOwner(), p).getStatus();
			if (status == null)
				status = other;
			else
				status = status.worseOf(other);
		}
	}
	return status == null ? DiplomaticStatus.NEUTRAL : status;
}

public Colony findAny()
{
	for (int i = 0; i < data.length; i++)
		if (data[i] != null) return data[i];
	return null;
}

public boolean hasSpace()
{
	return used*2 < size;
}

public int amount()
{
	int result = 0;
	for (int i = 0; i < data.length; i++)
		if (data[i] != null) result++;
	return result;
}

public int length()
{ return data.length; }

public Colony findColony(Player p)
{
	for (int i = 0; i < data.length; i++)
	{
		if (data[i] != null)
		{
			if (data[i].getOwner() == p)
				return data[i];
		}
	}
	return null;
}

public void removeColony(Colony c)
{
	for (int i = 0; i < data.length; i++)
		if (data[i] == c) data[i] = null;
}

private double normalize(double phi)
{ return phi % (2*Math.PI); }

public RotationState getRotation()
{ return new RotationState(radius, speed, theta); }

public long getRadius()
{ return radius; }

public long getSpeed()
{ return speed; }

public long getTheta()
{ return theta; }

public double x(long time)
{
	double phi = normalize((10.0*time)/speed - theta/SolarSystem.thetaFactor);
	return Math.cos(phi)*radius;
}

public long longX(long time)
{ return (long) x(time); }

public double y(long time)
{
	double phi = normalize((10.0*time)/speed - theta/SolarSystem.thetaFactor);
	return Math.sin(phi)*radius;
}

public long longY(long time)
{ return (long) y(time); }

// Returns the normalized distance (between 0 and 1)
public double distance(Planet p, long time)
{
	double x1 = x(time);
	double y1 = y(time);
	double x2 = p.x(time);
	double y2 = p.y(time);
	double dx = x1 - x2;
	double dy = y1 - y2;
	return Math.sqrt(dx*dx+dy*dy) / (2*position.findSystem(galaxy).getMaxRadius());
}

public Colony createColony(Player owner, long time, boolean isInvasion, boolean isStart)
{
	int j = data.length;
	for (int i = 0; i < data.length; i++)
		if (data[i] == null) j = i;
	
	if (j == data.length)
	{
		Colony[] temp = new Colony[data.length+1];
		for (int i = 0; i < data.length; i++)
			temp[i] = data[i];
		data = temp;
	}
	Colony c = new Colony(this, owner, time, j, isInvasion, isStart);
	data[j] = c;
	if (owner != null) owner.addColony(c);
	
	if (!isInvasion)
		c.increaseBuilding(time, BuildingEnum.RESIDENCE);
	c.update(time);
	return c;
}

public Colony createColony(Player owner, long time)
{ return createColony(owner, time, false, false); }

public Colony createInvasionColony(Player owner, long time)
{ return createColony(owner, time, true, false); }

public Colony createStartColony(Player owner, long time)
{ return createColony(owner, time, false, true); }

@Override
public String toString()
{ return "Planet "+position; }

}
