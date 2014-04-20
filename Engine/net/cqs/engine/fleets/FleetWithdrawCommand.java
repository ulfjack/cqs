package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.engine.Fleet;

public final class FleetWithdrawCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetWithdrawCommand()
{/*OK*/}

public static String englishTranslation()
{ return "withdraw"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetWithdrawCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 0; }

@Override
public long prepare(Fleet f)
{
	return Constants.WITHDRAW_DELAY;
}

@Override
public void execute(Fleet f) throws FleetException
{ throw new FleetStopException(); }

}
