package net.cqs.engine.fleets;

import java.util.Locale;

public final class FleetAttackCommand extends FleetFightCommand
{

private static final long serialVersionUID = 1L;

public FleetAttackCommand()
{ super(Mode.ATTACK); }

public static String englishTranslation()
{ return "attack colony"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetAttackCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

}
