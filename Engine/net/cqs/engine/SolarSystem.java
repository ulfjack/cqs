package net.cqs.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.cqs.config.Constants;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.InvalidPositionException;

public final class SolarSystem implements Serializable
{

private static final long serialVersionUID = 2L;

public static final double thetaFactor = 10000.0;

public static long radiusToSpeed(long R)
{ return (long) (50*Math.sqrt(R*R*R) / (2*3.14159)); }

private final Position position;

private long maxRadius = 284;

private long x;
private long y;
private long z;

private final Galaxy galaxy;
private final Planet[] data;
private final List<Fleet> fleets = new ArrayList<Fleet>();
private final List<Fleet> incomingFleets = new ArrayList<Fleet>();

private boolean invisible = false;

SolarSystem(GalaxyBuilder builder, Galaxy galaxy, int system)
{
	this.galaxy = galaxy;
	this.position = new Position(system);
	data = builder.createPlanets(this);
}

public void check(Galaxy g) throws InvalidDatabaseException
{
	if (galaxy != g) throw new InvalidDatabaseException("Invalid galaxy!");
	for (Planet p : data)
		p.check();
}

public Galaxy getGalaxy()
{ return galaxy; }

public Position getPosition()
{ return position; }

public int getSystemNumber()
{ return position.getSystemNumber(); }

public long getMaxRadius()
{ return maxRadius; }

public void setVisible()
{ invisible = false; }

public boolean isInvisible()
{ return invisible; }

public void setXYZ(long x, long y, long z)
{
	this.x = x;
	this.y = y;
	this.z = z;
}

public long getX()
{ return x; }

public long getY()
{ return y; }

public long getZ()
{ return z; }

public Planet[] getPlanets()
{ return data; }

public Planet getPlanet(int which)
{
	if ((which < 0) || (which >= data.length))
		throw new InvalidPositionException("Invalid Planet: "+which);
	return data[which];
}

public int length()
{ return data.length; }

public List<Fleet> getFleets()
{ return fleets; }

public Fleet getFleet(int which)
{ return fleets.get(which); }


public void addFleet(Fleet f)
{ fleets.add(f); }

public void removeFleet(Fleet f)
{ fleets.remove(f); }

public boolean knowsFleet(Fleet f)
{ return fleets.contains(f); }


public void addIncomingFleet(Fleet f)
{ incomingFleets.add(f); }

public void removeIncomingFleet(Fleet f)
{ incomingFleets.remove(f); }


/**
 * Returns the distance as a number between 0 and 1.
 */
public double distance(SolarSystem other)
{
	if (this == other) return 0.0;
	long dx = other.x - x;
	long dy = other.y - y;
	long dz = other.z - z;
	double dist = Math.sqrt(dx*dx+dy*dy+dz*dz);
	return dist / Constants.GALAXY_DIAMETER;
}

@Override
public String toString()
{
	return "System "+position;
}

}
