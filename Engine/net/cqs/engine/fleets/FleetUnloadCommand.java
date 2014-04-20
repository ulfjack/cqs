package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.Resource;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;
import net.cqs.engine.diplomacy.DiplomaticStatus;

public final class FleetUnloadCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

	public static enum Mode
	{
		UNLOAD
	}

private final Mode mode = Mode.UNLOAD;
private final int[] amount = new int[4];
private final int[] rest    = new int[4];

public FleetUnloadCommand()
{/*OK*/}

public static String englishTranslation()
{ return "unload [{0,number,integer};{1,number,integer};{2,number,integer};{3,number,integer}] leave [{4,number,integer};{5,number,integer};{6,number,integer};{7,number,integer}]"; }

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
	FleetUnloadCommand result = new FleetUnloadCommand();
	result.setAmounts(amount[0], amount[1], amount[2], amount[3]);
	result.setRests(rest[0], rest[1], rest[2], rest[3]);
	return result;
}

@Override
public String getEditorType()
{ return EDIT_UNLOAD; }

public Mode getMode()
{ return mode; }

public int[] getAmounts()
{ return amount; }

public void setAmounts(int r0, int r1, int r2, int r3)
{
	amount[0] = r0 < 0 ? -1 : r0;
	amount[1] = r1 < 0 ? -1 : r1;
	amount[2] = r2 < 0 ? -1 : r2;
	amount[3] = r3 < 0 ? -1 : r3;
}

public void setAmount(int i, int what)
{
	if (what < 0) what = -1;
	amount[i] = what;
}

public int[] getRests()
{ return rest; }

public void setRests(int r0, int r1, int r2, int r3)
{
	rest[0] = r0 < 0 ? 0 : r0;
	rest[1] = r1 < 0 ? 0 : r1;
	rest[2] = r2 < 0 ? 0 : r2;
	rest[3] = r3 < 0 ? 0 : r3;
}

@Override
public int check(Fleet f)
{ return 0; }

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_UNLOAD_NOT_LANDED);
	
	Colony c = f.findColony();
	if (c == null)
		throw new FleetAbortException(ErrorCode.CANNOT_UNLOAD_NO_COLONY);
	
	DiplomaticStatus status = f.getGalaxy().getDiplomaticRelation().getEntry(f.getOwner(), c.getOwner()).getStatus();
	if (!status.canTrade())
		throw new FleetAbortException(ErrorCode.CANNOT_UNLOAD_DIPLOMACY);
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
	{
		long cargo = f.getCargoAmount(i);
		long value = amount[i] < 0 ? cargo : amount[i];
		if (value > cargo-rest[i]) value = cargo-rest[i];
		if (value > cargo) value = cargo;
		if (value < 0) value = 0;
		f.unloadCargoDo(f.getNextEventTime(), i, value);
	}
}

}
