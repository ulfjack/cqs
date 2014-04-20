package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Time;
import net.cqs.engine.Fleet;

public final class FleetNOPCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public static final int WAIT_ONE_SECOND = 0;
public static final int DEFEND          = 1;

private int type;

public FleetNOPCommand()
{ this.type = WAIT_ONE_SECOND; }

public FleetNOPCommand(int type)
{ this.type = type; }

public static String englishTranslation()
{ return "{0,choice,0#wait 1 second|1#defend}"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, Integer.valueOf(type)); }

@Override
public FleetCommand copy()
{ return new FleetNOPCommand(type); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

public int getType()
{ return type; }

@Override
public int check(Fleet f)
{ return 1; }

@Override
public long prepare(Fleet f)
{ return Time.seconds(1); }

@Override
public void execute(Fleet f)
{/*Do Nothing!*/}

}
