package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Planet;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitSelector;

public final class FleetColonizeCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

private Unit colonizeUnit;

public FleetColonizeCommand()
{/*OK*/}

public static String englishTranslation()
{ return "found colony"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetColonizeCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{
	if (f.getOwner().mayColonize())
		return 0;
	return -1;
}

private boolean hasUnit(Fleet f)
{
	UnitIterator it = f.getUnitIterator(UnitSelector.ALL);
	while (it.hasNext())
	{
		it.next();
		if (it.key().hasSpecial(UnitSpecial.SETTLEMENT))
		{
			colonizeUnit = it.key();
			return true;
		}
	}
	return false;
}

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (!f.getOwner().mayColonize())
		throw new FleetAbortException(ErrorCode.CANNOT_COLONIZE_CIVILPOINTS);
	
	if (f.getPosition().specificity() < Position.PLANET)
		throw new FleetAbortException(ErrorCode.CANNOT_COLONIZE_NO_PLANET);
	
	Planet planet = f.findPlanet();
	if (f.getOwner().hasColonyOnPlanet(planet))
		throw new FleetAbortException(ErrorCode.CANNOT_COLONIZE_ALREADY_HAVE);
	
	if (!planet.mayPassBlockade(f, true))
		throw new FleetAbortException(ErrorCode.CANNOT_COLONIZE_PLANET_BLOCKED);
	
	if (!planet.hasSpace())
		throw new FleetAbortException(ErrorCode.CANNOT_COLONIZE_NO_SPACE);
	
	// Seiteneffekt: setzt colonizeUnit
	if (!hasUnit(f))
		throw new FleetAbortException(ErrorCode.CANNOT_COLONIZE_NO_UNIT);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return Constants.COLONIZE_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	long time = f.getNextEventTime();
	checkAllowed(f);
	
	assert !f.getOwner().hasColonyOnPlanet(f.findPlanet());
	
	f.decreaseUnit(colonizeUnit);
	
	Colony c = f.findPlanet().createColony(f.getOwner(), time);
	f.getGalaxy().dropInfo(f.getOwner(), time, InfoEnum.FLEET_COLONIZED, c.getPosition());
}

}
