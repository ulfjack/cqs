package net.cqs.engine;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.config.InvalidPositionException;

public final class Position implements Serializable, Comparable<Position>
{

private static final long serialVersionUID = 1L;

public static final int NONE   = 0;
public static final int GALAXY = 1;
public static final int SYSTEM = 2;
public static final int PLANET = 3;
public static final int COLONY = 4;

static Pattern positionPattern = Pattern.compile("(\\d+)([.,:;](\\d+)([.,:;](\\d+))?)?");

/**
 * Make sure that this is the inverse operation of toString()!!!
 */
public static Position decode(String value)
{
	Matcher m;
	m = positionPattern.matcher(value);
	if (m.matches())
	{
		try
		{
			int system = Integer.parseInt(m.group(1));
			if (m.group(3) == null)
				return new Position(system);
			
			int planet = Integer.parseInt(m.group(3));
			if (m.group(5) == null)
				return new Position(system, planet);
			
			int colony = Integer.parseInt(m.group(5));
			return new Position(system, planet, colony);
		}
		catch (NumberFormatException e)
		{ throw new InvalidPositionException("Invalid Position: "+value); }
		catch (InvalidPositionException e)
		{ throw new InvalidPositionException("Invalid Position: "+value); }
		catch (NullPointerException e)
		{ throw new InvalidPositionException("Invalid Position: "+value); }
	}
	else
		throw new InvalidPositionException("Invalid Position: "+value);
}

public static Position safeDecode(String value)
{
	try
	{ return decode(value); }
	catch (InvalidPositionException e)
	{ return null; }
}


private final int system;
private final int planet;
private final int colony;

public Position(int system)
{
	if (system < 0) throw new InvalidPositionException("Negatives system!");
  this.system = system;
  this.planet = -1;
  this.colony = -1;
}

public Position(int system, int planet)
{
	if (system < 0) throw new InvalidPositionException("Negatives system!");
	if (planet < 0) throw new InvalidPositionException("Negativer planet!");
  this.system = system;
  this.planet = planet;
  this.colony = -1;
}

public Position(int system, int planet, int colony)
{
	if (system < 0) throw new InvalidPositionException("Negatives system!");
	if (planet < 0) throw new InvalidPositionException("Negativer planet!");
	if (colony < 0) throw new InvalidPositionException("Negative kolonie!");
  this.system = system;
  this.planet = planet;
  this.colony = colony;
}

public int getSystemNumber()
{ return system; }

public int getPlanetNumber()
{ return planet; }

public int getColonyNumber()
{ return colony; }

@Override
public String toString()
{
	StringBuilder buffer = new StringBuilder();
	if (system >= 0) buffer.append(system);
	if (planet >= 0) buffer.append(":").append(planet);
	if (colony >= 0) buffer.append(":").append(colony);
	return buffer.toString();
}

@Override
public int hashCode()
{	return (system << 16)+(planet << 8)+colony; }

@Override
public boolean equals(Object other)
{
	if (!(other instanceof Position)) return false;
	Position p = (Position) other;
	return (system == p.system) && (planet == p.planet) && (colony == p.colony);
}

public void check(Galaxy galaxy)
{
	if ((system >= 0) && (findSystem(galaxy) == null))
		throw new InvalidPositionException("Position "+toString()+" is invalid in "+galaxy);
	if ((planet >= 0) && (findPlanet(galaxy) == null))
		throw new InvalidPositionException("Position "+toString()+" is invalid in "+galaxy);
	if ((colony >= 0) && (findColony(galaxy) == null))
		throw new InvalidPositionException("Position "+toString()+" is invalid in "+galaxy);
}

public int specificity()
{
	if (system < 0) return GALAXY;
	if (planet < 0) return SYSTEM;
	if (colony < 0) return PLANET;
	return COLONY;
}

/**
 * Determines the highest level at which this position differs from
 * the other position.
 * This method is symmetric!
 */
public int differs(Position other)
{
	if ((system < 0) && (other.system < 0)) return NONE;
  if (system != other.system) return SYSTEM;
  if ((planet < 0) && (other.planet < 0)) return NONE;
  if (planet != other.planet) return PLANET;
  if ((colony < 0) && (other.colony < 0)) return NONE;
  if (colony != other.colony) return COLONY;
 	return NONE;
}

@Override
public int compareTo(Position other)
{
	if (system < other.system) return -1;
	if (system > other.system) return 1;
	
	if (planet < other.planet) return -1;
	if (planet > other.planet) return 1;
	
	if (colony < other.colony) return -1;
	if (colony > other.colony) return 1;
	
	return 0;
}

public boolean isValid(Galaxy galaxy)
{
	if (system < 0) return true;
  if (system >= galaxy.size()) return false;
  SolarSystem s = galaxy.getSystem(system);
  if (s == null) return false;
  if (s.isInvisible()) return false;
  
  if (planet < 0) return true;
  if (planet >= s.length()) return false;
  Planet p = s.getPlanet(planet);
  if (p == null) return false;
  
  if (colony < 0) return true;
  if (colony >= p.length()) return false;
  Colony c = p.getColony(colony);
  if (c == null) return false;
  
  return true;
}

public Position toSystemPosition()
{
	if (system < 0) throw new UnsupportedOperationException();
	return planet < 0 ? this : new Position(system);
}

public Position toPlanetPosition()
{
	if (system < 0) throw new UnsupportedOperationException();
	if (planet < 0) throw new UnsupportedOperationException();
	return colony < 0 ? this : new Position(system, planet);
}

public Position toColonyPosition()
{
	if (system < 0) throw new UnsupportedOperationException();
	if (planet < 0) throw new UnsupportedOperationException();
	if (colony < 0) throw new UnsupportedOperationException();
	return this;
}

public boolean sameSystem(Position other)
{ return (system >= 0) && (system == other.system); }

public SolarSystem findSystem(Galaxy galaxy)
{
	if (system < 0) return null;
	return galaxy.getSystem(system);
}

public Planet findPlanet(Galaxy galaxy)
{
	SolarSystem s = findSystem(galaxy);
	if ((s == null) || (planet < 0)) return null;
	return s.getPlanet(planet);
}

public Colony findColony(Galaxy galaxy)
{
	Planet p = findPlanet(galaxy);
	if ((p == null) || (colony < 0)) return null;
	return p.getColony(colony);
}

}
