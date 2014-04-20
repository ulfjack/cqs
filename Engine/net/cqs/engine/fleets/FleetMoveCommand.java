package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.ErrorCode;
import net.cqs.engine.Fleet;
import net.cqs.engine.Planet;
import net.cqs.engine.Position;
import net.cqs.engine.RotationState;
import net.cqs.engine.SolarSystem;

public final class FleetMoveCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public Position dest;
public boolean destLanded;

private int current;
private Position[] route;

public FleetMoveCommand(Position dest, boolean destLanded)
{
	this.dest = dest;
	this.destLanded = dest.specificity() == Position.COLONY ? destLanded : false;
}

public static String englishTranslation()
{ return "move to {0,choice,2#system|3#planet|4#colony} {1}"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, Integer.valueOf(dest.specificity()), dest); }

@Override
public FleetCommand copy()
{
	FleetMoveCommand result = new FleetMoveCommand(dest, destLanded);
	result.current = current;
	result.route = route;
	return result;
}

@Override
public String getEditorType()
{ return EDIT_NONE; }

public Position getDestination()
{ return dest; }

public boolean getDestLanded()
{ return destLanded; }

private boolean valid(Fleet f)
{
	if (route == null) return false;
	if (!dest.equals(route[route.length-1])) return false;
	if (!f.getPosition().equals(route[current])) return false;
	return true;
}

private void validate(Fleet f) throws FleetAbortException
{
	if (valid(f)) return;
	route = new RoutePlanner().calculateRoute(f.getPosition(), dest);
	current = 0;
	if (!valid(f)) throw new FleetAbortException(ErrorCode.FLEET_INTERNAL_ERROR);
}

@Override
public int check(Fleet f)
{ return 0; }

public void setPlanetGoal(Fleet f, Planet p)
{
	f.setRotationGoal(p.getRotation());
	f.setLanded(false);
}

public void setSystemGoal(Fleet f, SolarSystem s)
{
	long radius = s.getMaxRadius();
	f.setRotationGoal(
			new RotationState(radius,
			SolarSystem.radiusToSpeed(radius),
			f.getCurrentRotation().getTheta()));
}


public long getTimeEstimate(Fleet f, long time)
{
	try
	{
		validate(f);
		return RoutePlanner.getTimeEstimate(f, time, f.isLanded(), route, current);
	}
	catch (FleetAbortException e)
	{ throw new RuntimeException(e); }
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	if (!dest.isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.CANNOT_MOVE_INVALID_DEST);
	
	validate(f);
	
	if (current == route.length-1) return 1;
	
	f.setRotationGoal(f.getCurrentRotation());
	
	Movement movement = Movement.getMovement(route[current], route[current+1]);
	
	// check if move is valid
	switch (movement)
	{
		case SYSTEM_TO_SYSTEM :
			if (!f.mayLeaveSystem())
				throw new FleetAbortException(ErrorCode.CANNOT_LEAVE_SYSTEM);
			SolarSystem s = dest.findSystem(f.getGalaxy());
			f.unregisterWithSystem();
			setSystemGoal(f, s);
			s.addIncomingFleet(f);
			break;
		case PLANET_TO_PLANET :
			setPlanetGoal(f, dest.findPlanet(f.getGalaxy()));
			break;
		case COLONY_TO_COLONY :
			// always valid
			break;
		case SYSTEM_TO_PLANET :
			setPlanetGoal(f, dest.findPlanet(f.getGalaxy()));
			break;
		case PLANET_TO_SYSTEM :
			if (!f.mayLeaveSystem())
				throw new FleetAbortException(ErrorCode.CANNOT_LEAVE_SYSTEM);
			setSystemGoal(f, f.findSystem());
			break;
		case PLANET_TO_COLONY :
			if (!f.findPlanet().mayPassBlockade(f, true))
				throw new FleetAbortException(ErrorCode.CANNOT_PASS_BLOCKADE_IN);
			break;
		case COLONY_TO_PLANET :
			if (f.isLanded())
			{
				if (!f.mayLeavePlanet())
					throw new FleetAbortException(ErrorCode.CANNOT_TAKEOFF);
			}
			if (!f.findPlanet().mayPassBlockade(f, false))
				throw new FleetAbortException(ErrorCode.CANNOT_PASS_BLOCKADE_OUT);
			break;
	}
	
	return movement.lookupTime(f.getSpeed(), f.getGalaxy(), route[current], f.isLanded(), route[current+1], f.getNextEventTime());
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	if (!dest.isValid(f.getGalaxy()) || !valid(f))
	{
		f.setRotationGoal(f.getCurrentRotation());
		throw new FleetAbortException(ErrorCode.CANNOT_MOVE_INVALID_DEST);
	}
	
	if (current == route.length-1) return;
	Movement movement = Movement.getMovement(route[current], route[current+1]);
	
	// move fleet
	f.setPosition(route[++current]);
	f.setCurrentRotation(f.getRotationGoal());
	
	// update after move
	switch (movement)
	{
		case SYSTEM_TO_SYSTEM :
			dest.findSystem(f.getGalaxy()).removeIncomingFleet(f);
			f.registerWithSystem();
			break;
		case PLANET_TO_PLANET :
			if (f.findPlanet().length() == 0)
				f.setLanded(destLanded);
			break;
		case COLONY_TO_COLONY :
			// Do nothing.
			break;
		case SYSTEM_TO_PLANET :
			if (f.findPlanet().length() == 0)
				f.setLanded(f.isLanded() || destLanded);
			break;
		case PLANET_TO_SYSTEM :
			f.setLanded(false);
			break;
		case PLANET_TO_COLONY :
			if (f.findColony().getOwner().alliedWith(f.getOwner()))
				f.setLanded(f.isLanded() || destLanded);
			break;
		case COLONY_TO_PLANET :
			f.setLanded(false);
			break;
	}
}

public boolean isDone(Fleet f)
{ return dest.equals(f.getPosition()); }

@Override
public boolean mayAbort(Fleet f)
{
	try
	{ validate(f); }
	catch (FleetAbortException e)
	{ return false; }
	return true;
}

@Override
public void abort(Fleet f) throws FleetContinueException
{
	dest = route[current+1];
	destLanded = false;
	f.setNextCompleteTime(f.getNextEventTime());
	throw new FleetContinueException();
}

}
