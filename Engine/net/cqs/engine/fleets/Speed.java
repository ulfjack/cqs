package net.cqs.engine.fleets;

import net.cqs.config.Constants;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Planet;
import net.cqs.engine.Position;
import net.cqs.engine.SolarSystem;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;

public final class Speed
{

private int planetarySpeed;
private int interplanetarySpeed;
private int interstellarSpeed;

public Speed()
{
	planetarySpeed = 1;
	interplanetarySpeed = 1;
	interstellarSpeed = 1;
}

public Speed(UnitMap units)
{
	if (isExtraSpeedPlanetary(units))
		planetarySpeed = 3;
	else if (isSpeedPlanetary(units))
		planetarySpeed = 2;
	else
		planetarySpeed = 1;
	
	if (isExtraSpeedInterplanetary(units))
		interplanetarySpeed = 3;
	else if (isSpeedInterplanetary(units))
		interplanetarySpeed = 2;
	else
		interplanetarySpeed = 1;
	
	if (isSpeedInterstellar(units))
		interstellarSpeed = 2;
	else
		interstellarSpeed = 1;
}

private boolean isSpeedPlanetary(UnitMap units)
{
	UnitIterator it = units.unitIterator(UnitSelector.GROUND_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasGroundSpeedModule())
			return false;
	}
	return true;
}

private boolean isExtraSpeedPlanetary(UnitMap units)
{
	UnitIterator it = units.unitIterator(UnitSelector.GROUND_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasDoubleGroundSpeedModule())
			return false;
	}
	return true;
}

private boolean isSpeedInterplanetary(UnitMap units)
{
	UnitIterator it = units.unitIterator(UnitSelector.SPACE_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasSpeedModule())
			return false;
	}
	return true;
}

private boolean isExtraSpeedInterplanetary(UnitMap units)
{
	UnitIterator it = units.unitIterator(UnitSelector.SPACE_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasDoubleSpeedModule())
			return false;
	}
	return true;
}

private boolean isSpeedInterstellar(UnitMap units)
{
	UnitIterator it = units.unitIterator(UnitSelector.SPACE_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && !it.key().hasDoubleWarpModule())
			return false;
	}
	return true;
}

public long getSystemToSystem(SolarSystem s1, SolarSystem s2)
{
	double dist = s1.distance(s2);
	double norm = ((float) Constants.SYSTEM_TO_SYSTEM)/interstellarSpeed;
	return (long) Math.ceil(dist * norm);
}

public long getPlanetToPlanet(Planet p1, Planet p2, long time)
{
	double dist = p1.distance(p2, time);
	double norm = ((float) Constants.PLANET_TO_PLANET)/interplanetarySpeed;
	return (long) Math.ceil(dist * norm);
}

public long getColonyToColony()
{ return (long) Math.ceil(((float) Constants.COLONY_TO_COLONY)/planetarySpeed); }

public long getPlanetToSystem()
{ return (long) Math.ceil(((float) Constants.PLANET_TO_SYSTEM)/interstellarSpeed); }

public long getSystemToPlanet()
{ return (long) Math.ceil(((float) Constants.SYSTEM_TO_PLANET)/interstellarSpeed); }

public long getPlanetToColony()
{ return (long) Math.ceil(((float) Constants.PLANET_TO_COLONY)/interplanetarySpeed); }

public long getColonyToPlanet()
{ return (long) Math.ceil(((float) Constants.COLONY_TO_PLANET)/interplanetarySpeed); }

public static long getMinimumFlightTime(Galaxy galaxy, Position p1, Position p2)
{
	if ((p1.specificity() == Position.GALAXY) || (p2.specificity() == Position.GALAXY))
		throw new IllegalArgumentException();
	if (p1.equals(p2))
		return 0;

	long result = 0;
	Speed speed = new Speed();
	if (p1.getSystemNumber() != p2.getSystemNumber())
	{
		// p1.colony to p1.planet
		if (p1.specificity() > Position.PLANET)
			result += speed.getColonyToPlanet();
		// p1.planet to p1.system
		if (p1.specificity() > Position.SYSTEM)
			result += speed.getPlanetToSystem();
		// p1.system to p2.system
		result += speed.getSystemToSystem(p1.findSystem(galaxy), p2.findSystem(galaxy));
		// p2.system to p2.planet
		if (p2.specificity() > Position.SYSTEM)
			result += speed.getSystemToPlanet();
		// p2.planet to p2.colony
		if (p2.specificity() > Position.PLANET)
			result += speed.getPlanetToColony();
	}
	else if (p1.getPlanetNumber() != p2.getPlanetNumber())
	{
		// p1.colony to p1.planet
		if (p1.specificity() > Position.PLANET)
			result += speed.getColonyToPlanet();
		// p1.planet to p2.planet
		// p2.planet to p2.colony
		if (p2.specificity() > Position.PLANET)
			result += speed.getPlanetToColony();
	}
	else
		result += speed.getColonyToColony();
		
	return result;
}

}
