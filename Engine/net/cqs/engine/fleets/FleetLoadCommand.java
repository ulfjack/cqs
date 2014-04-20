package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.Resource;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetLoadCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

	public static enum Mode
	{
		LOAD
	}

private final Mode mode = Mode.LOAD;
private final int[] amount = new int[4];
private final int[] rest   = new int[4];

public FleetLoadCommand()
{/*OK*/}

public static String englishTranslation()
{ return "load [{0,number,integer};{1,number,integer};{2,number,integer};{3,number,integer}] leave [{4,number,integer};{5,number,integer};{6,number,integer};{7,number,integer}]"; }

@Override
public String getName(Locale locale)
{
	return format(englishTranslation(), locale,
		Integer.valueOf(amount[0]), Integer.valueOf(amount[1]),
		Integer.valueOf(amount[2]), Integer.valueOf(amount[3]),
		Integer.valueOf(rest[0]), Integer.valueOf(rest[1]),
		Integer.valueOf(rest[2]), Integer.valueOf(rest[3]));
}

@Override
public FleetCommand copy()
{
	FleetLoadCommand result = new FleetLoadCommand();
	result.setAmounts(amount[0], amount[1], amount[2], amount[3]);
	result.setRests(rest[0], rest[1], rest[2], rest[3]);
	return result;
}

@Override
public String getEditorType()
{ return EDIT_LOAD; }

public Mode getMode()
{ return mode; }

public int[] getAmounts()
{ return amount; }

public void setAmounts(int r0, int r1, int r2, int r3)
{
	amount[0] = r0 < 0 ? 0 : r0;
	amount[1] = r1 < 0 ? 0 : r1;
	amount[2] = r2 < 0 ? 0 : r2;
	amount[3] = r3 < 0 ? 0 : r3;
}

public void setAmount(int i, int what)
{ amount[i] = what < 0 ? 0 : what; }

public int[] getRests()
{ return rest; }

public void setRests(int r0, int r1, int r2, int r3)
{
	rest[0] = r0 < 0 ? 0 : r0;
	rest[1] = r1 < 0 ? 0 : r1;
	rest[2] = r2 < 0 ? 0 : r2;
	rest[3] = r3 < 0 ? 0 : r3;
}

public void setRest(int i, int what)
{ rest[i] = what < 0 ? 0 : what; }

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
	return Constants.LOAD_UNLOAD_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		f.loadCargoDo(f.getNextEventTime(), i, amount[i], rest[i]);
}

}
