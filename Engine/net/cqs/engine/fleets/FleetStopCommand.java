package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.engine.Fleet;

public final class FleetStopCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetStopCommand()
{/*OK*/}

@Override
public FleetCommand copy()
{ return this; }

public static String englishTranslation()
{ return "stop fleet"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 1; }

@Override
public long prepare(Fleet f) throws FleetStopException
{ throw new FleetStopException(); }

@Override
public void execute(Fleet f)
{ throw new IllegalStateException(); }

}
