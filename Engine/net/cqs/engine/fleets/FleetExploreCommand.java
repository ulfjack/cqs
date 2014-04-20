package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.Resource;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.Fleet;
import net.cqs.engine.Planet;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitSelector;

public final class FleetExploreCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

private Unit exploreUnit;

public FleetExploreCommand()
{/*OK*/}

public static String englishTranslation()
{ return "explore planet"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetExploreCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 0; }

private boolean hasUnit(Fleet f)
{
	UnitIterator it = f.getUnitIterator(UnitSelector.ALL);
	while (it.hasNext())
	{
		it.next();
		if (it.key().hasSpecial(UnitSpecial.EXPLORATION))
		{
			exploreUnit = it.key();
			return true;
		}
	}
	return false;
}

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	Position pos = f.getPosition();
	if (pos.specificity() < Position.PLANET)
		throw new FleetAbortException(ErrorCode.CANNOT_EXPLORE_NO_PLANET);
	
	// side effect: sets exploreUnit
	if (!hasUnit(f))
		throw new FleetAbortException(ErrorCode.CANNOT_EXPLORE_NO_UNIT);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return Constants.EXPLORE_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	long time = f.getNextEventTime();
	checkAllowed(f);
	
	f.decreaseUnit(exploreUnit);
	
	Planet p = f.findPlanet();
	
/*	int data[] = new int[Resource.MAX+2];
	for (int i = 0; i < data.length-1; i++)
		data[i] = p.modifiers.get(i);
	data[data.length-1] = (int) p.populationGrowthModifier;*/
	
	f.getGalaxy().dropInfo(f.getOwner(), time, InfoEnum.FLEET_EXPLORED, p.getPosition(), p.getType(),
			Integer.valueOf(p.getDisplaySize()),
			Integer.valueOf(p.getModifier(Resource.STEEL)), Integer.valueOf(p.getModifier(Resource.OIL)), 
			Integer.valueOf(p.getModifier(Resource.SILICON)), Integer.valueOf(p.getModifier(Resource.DEUTERIUM)),
			Long.valueOf(p.getPopulationGrowthModifierPercent()));
}

}
