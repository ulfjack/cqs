package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.engine.Fleet;

public final class FleetDisbandCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetDisbandCommand()
{/*OK*/}

public static String englishTranslation()
{ return "disband fleet"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetDisbandCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 1; }

@Override
public long prepare(Fleet f)
{
	return Constants.DISBAND_TIME;
}

@Override
public void execute(Fleet f) throws FleetException
{
	f.disband();
}

}
