package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetBreakBlockadeCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetBreakBlockadeCommand()
{
	// Ok
}

public static String englishTranslation()
{ return "break blockade"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetBreakBlockadeCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

public void checkTakeoff(Fleet f) throws FleetAbortException
{
	if (!f.mayLeavePlanet())
		throw new FleetAbortException(ErrorCode.CANNOT_TAKEOFF);
}

@Override
public int check(Fleet f)
{ return 0; }

@Override
public long prepare(Fleet f) throws FleetException
{
	if (f.getPosition().specificity() == Position.COLONY)
	{
		checkTakeoff(f);
		return Constants.BREAK_BLOCKADE_OUT;
	}
	else
		return Constants.BREAK_BLOCKADE_IN;
}

@Override
public void execute(Fleet f)
{
	float p = 1.0f/f.getTotalUnits();
	float val = f.getGalaxy().getRandomFloat();
	if (val < p)
	{
		// successfull
		if (f.getPosition().specificity() == Position.COLONY)
			f.setPosition(f.getPosition().toPlanetPosition());
		else
			f.setPosition(f.findPlanet().getColony(0).getPosition());
	}
	else
	{
		// not successfull
		// FIXME: punish -> get into battle
	}
}

}
