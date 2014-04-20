package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitMap;

public final class FleetLoadUnitsCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

	public static enum Mode
	{
		LOAD, UPTO
	}

private Mode mode = Mode.LOAD;
private UnitMap units;

public FleetLoadUnitsCommand(UnitMap units)
{
	if (units == null) throw new NullPointerException();
	this.units = units;
}

public static String englishTranslation()
{ return "load units {1}"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, Integer.valueOf(mode.ordinal()), units); }

@Override
public FleetCommand copy()
{ return new FleetLoadUnitsCommand(units); }

@Override
public String getEditorType()
{ return EDIT_LOAD_UNITS; }

public Mode getMode()
{ return mode; }

public UnitMap getUnitMap()
{ return units; }

@Override
public int check(Fleet f)
{ return 0; }

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_LOAD_NOT_LANDED);
	
	Colony c = f.findColony();
	if (c == null)
		throw new FleetAbortException(ErrorCode.CANNOT_LOAD_NO_COLONY);
	
	if (c.getOwner() != f.getOwner())
		throw new FleetAbortException(ErrorCode.CANNOT_LOAD_NOT_MY_COLONY);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return Constants.LOAD_UNITS_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	Colony c = f.findColony();
	c.removeUnits(f.getNextEventTime(), units);
	f.addUnits(units);
}

}
