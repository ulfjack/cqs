package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetStationCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetStationCommand()
{/*OK*/}

@Override
public FleetCommand copy()
{ return new FleetStationCommand(); }

public static String englishTranslation()
{ return "station fleet"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 0; }

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	Position pos = f.getPosition();
	if (pos.specificity() < Position.PLANET)
		throw new FleetAbortException(ErrorCode.CANNOT_STATION_NO_PLANET);
	
	Colony c = f.getOwner().getColonyOnPlanet(f.getPosition().findPlanet(f.getGalaxy()));
	if (c == null)
		throw new FleetAbortException(ErrorCode.CANNOT_STATION_NOT_ABOVE_COLONY);
	
	if ((f.getPosition().specificity() == Position.COLONY) && !f.getPosition().equals(c.getPosition()))
		throw new FleetAbortException(ErrorCode.CANNOT_STATION_NOT_MY_COLONY);
}

private void station(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	
	Colony c = f.getOwner().getColonyOnPlanet(f.getPosition().findPlanet(f.getGalaxy()));
	f.setPosition(c.getPosition());
	f.stationFleet();
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	if (f.getPosition().specificity() == Position.PLANET)
		return Constants.PLANET_TO_COLONY + Constants.STATION_TIME;
	
	Colony c = f.getOwner().getColonyOnPlanet(f.getPosition().findPlanet(f.getGalaxy()));
	if (!f.getPosition().equals(c.getPosition()))
		return Constants.COLONY_TO_COLONY + Constants.STATION_TIME;
	
	return Constants.STATION_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	station(f);
}

}
