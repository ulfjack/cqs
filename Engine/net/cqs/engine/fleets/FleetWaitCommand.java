package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.engine.Fleet;

public final class FleetWaitCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public static final int WAIT_FOR   = 0;
public static final int WAIT_UNTIL = 1;

int mode;
int param;

public FleetWaitCommand(int mode, int param)
{
	this.mode = mode;
	this.param = param;
}

public static String englishTranslation()
{ return "wait {0,choice,0#for {1,number,integer}s|1#for next full {1,number,integer}s}"; }

@Override
public String getName(Locale locale)
{
	return format(englishTranslation(), locale,
		Integer.valueOf(mode), Integer.valueOf(param));
}

@Override
public FleetCommand copy()
{ return new FleetWaitCommand(mode, param); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

public int getMode()
{ return mode; }

public int getParam()
{ return param; }

@Override
public int check(Fleet f)
{ return 1; }

@Override
public long prepare(Fleet f)
{
	switch (mode)
	{
		case WAIT_FOR :
			return param;
		case WAIT_UNTIL :
			long t = f.getNextEventTime();
			if (param <= 0) param = 1;
			long waitTime = param-(t % param);
			if (waitTime < 1) waitTime = 1;
			return waitTime;
		default :
			return 1;
	}
}

@Override
public void execute(Fleet f)
{/*Do Nothing.*/}

@Override
public boolean mayAbort(Fleet f)
{ return true; }

@Override
public void abort(Fleet f)
{/*No cleanup necessary.*/}

}
