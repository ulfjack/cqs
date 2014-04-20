package net.cqs.engine.fleets;

import net.cqs.config.Constants;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Planet;
import net.cqs.engine.Position;
import net.cqs.engine.SolarSystem;

enum Movement
{

SYSTEM_TO_SYSTEM
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{
			SolarSystem s1 = from.findSystem(galaxy);
			SolarSystem s2 = to.findSystem(galaxy);
			return speed.getSystemToSystem(s1, s2);
		}
	},
PLANET_TO_PLANET
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{
			Planet p1 = from.findPlanet(galaxy);
			Planet p2 = to.findPlanet(galaxy);
			return speed.getPlanetToPlanet(p1, p2, time);
		}
	},
COLONY_TO_COLONY
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{ return speed.getColonyToColony(); }
	},
SYSTEM_TO_PLANET
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{ return speed.getSystemToPlanet(); }
	},
PLANET_TO_SYSTEM
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{ return speed.getPlanetToSystem(); }
	},
PLANET_TO_COLONY
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{ return speed.getPlanetToColony(); }
	},
COLONY_TO_PLANET
	{
		@Override
		long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
		{ return speed.getColonyToPlanet()+(isLanded ? Constants.TAKEOFF_TIME : 0); }
	};

abstract long lookupTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time);

static Movement getMovement(Position from, Position to)
{
	if (from.equals(to))
		throw new IllegalArgumentException("positions equal!");
	if (from.getSystemNumber() != to.getSystemNumber())
	{
		if (from.specificity() != Position.SYSTEM)
			throw new IllegalArgumentException("invalid from");
		if (to.specificity() != Position.SYSTEM)
			throw new IllegalArgumentException("invalid to");
		return Movement.SYSTEM_TO_SYSTEM;
	}
	if (from.getPlanetNumber() != to.getPlanetNumber())
	{
		if ((from.specificity() == Position.SYSTEM) && (to.specificity() == Position.PLANET))
			return Movement.SYSTEM_TO_PLANET;
		if ((from.specificity() == Position.PLANET) && (to.specificity() == Position.SYSTEM))
			return Movement.PLANET_TO_SYSTEM;
		if (from.specificity() != Position.PLANET)
			throw new IllegalArgumentException("invalid from");
		if (to.specificity() != Position.PLANET)
			throw new IllegalArgumentException("invalid to");
		return Movement.PLANET_TO_PLANET;
	}
	if ((from.specificity() == Position.PLANET) && (to.specificity() == Position.COLONY))
		return Movement.PLANET_TO_COLONY;
	if ((from.specificity() == Position.COLONY) && (to.specificity() == Position.PLANET))
		return Movement.COLONY_TO_PLANET;
	if (from.specificity() != Position.COLONY)
		throw new IllegalArgumentException("invalid from");
	if (to.specificity() != Position.COLONY)
		throw new IllegalArgumentException("invalid to");
	return Movement.COLONY_TO_COLONY;
}

public static long getTime(Speed speed, Galaxy galaxy, Position from, boolean isLanded, Position to, long time)
{
	return getMovement(from, to).lookupTime(speed, galaxy, from, isLanded, to, time);
}

}
