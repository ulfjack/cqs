package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetTakeoffCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public static final long TAKEOFF_TIME = Constants.TAKEOFF_TIME;

public FleetTakeoffCommand()
{/*OK*/}

public static String englishTranslation()
{ return "take off"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetTakeoffCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 0; }

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.isLanded())
		throw new FleetAbortException(ErrorCode.CANNOT_LIFTOFF_ALREADY_SPACE);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_LIFTOFF_NO_COLONY);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return TAKEOFF_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	f.takeoff();
}

@Override
public boolean mayAbort(Fleet f)
{ return true; }

@Override
public void abort(Fleet f)
{/*OK*/}

}
