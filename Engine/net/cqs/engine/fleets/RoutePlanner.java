package net.cqs.engine.fleets;

import java.util.ArrayList;

import net.cqs.engine.Fleet;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Position;

public final class RoutePlanner
{

public static final int DOWN   = -1;
public static final int ALONG  =  0;
public static final int UP     = +1;
public static final int EQUALS = +2;

/**
 * Determines the direction into which a fleet has to fly to get from this
 * position to the other position. Returns either UP, DOWN, ALONG or EQUALS.
 */
static int directionTo(Position from, Position other)
{
	if (from.equals(other)) return EQUALS;
	if (from.specificity() > other.specificity()) return UP;
	
	int what = from.differs(other);
	if (from.specificity() > what) return UP;
	if (from.specificity() < what) return DOWN;
	return ALONG;
}

static Position nextTo(Position from, Position to)
{
	switch (directionTo(from, to))
	{
		case DOWN :
			switch (from.specificity())
			{
				case Position.PLANET : return to.toColonyPosition();
				case Position.SYSTEM : return to.toPlanetPosition();
			}
			break;
		case ALONG :
			switch (from.specificity())
			{
				case Position.COLONY : return to.toColonyPosition();
				case Position.PLANET : return to.toPlanetPosition();
				case Position.SYSTEM : return to.toSystemPosition();
			}
			break;
		case UP :
			switch (from.specificity())
			{
				case Position.COLONY : return from.toPlanetPosition();
				case Position.PLANET : return from.toSystemPosition();
			}
			break;
		case EQUALS : return from;
	}
	throw new IllegalArgumentException(from+" -> "+to);
}

public static long getTimeEstimate(Fleet fleet, long time, boolean isLanded, Position[] positions, int start)
{
	Galaxy galaxy = fleet.getGalaxy();
	Speed speed = fleet.getSpeed();
	long result = 0;
	for (int i = start; i < positions.length-1; i++)
		result += Movement.getTime(speed, galaxy, positions[i], i == start ? isLanded : false, positions[i+1], time+result);
	return result;
}


public RoutePlanner()
{/*OK*/}

public Position[] calculateRoute(Position from, Position to)
{
	ArrayList<Position> result = new ArrayList<Position>();
	Position current = from;
	while (!current.equals(to))
	{
		result.add(current);
		current = RoutePlanner.nextTo(current, to);
		if (result.size() > 5) throw new RuntimeException("ARGH!");
	}
	result.add(current);
	return result.toArray(new Position[0]);
}

}
