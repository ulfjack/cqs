package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.ErrorCode;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetDonateCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

private boolean checkAlly;

public FleetDonateCommand(boolean checkAlly)
{ this.checkAlly = checkAlly; }

public static String englishTranslation()
{ return "{0,choice,0#donate fleet|1#donate to alliance member}"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, Integer.valueOf(checkAlly ? 1 : 0)); }

public boolean getCheckAlly()
{ return checkAlly; }

@Override
public FleetCommand copy()
{ return new FleetDonateCommand(checkAlly); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 0; }

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_DONATE_NO_COLONY);
	
	if (!f.isCivil())
		throw new FleetAbortException(ErrorCode.CANNOT_DONATE_ATTACK_UNITS);
	
	if (checkAlly)
	{
		if (!f.findColony().getOwner().alliedWith(f.getOwner()))
			throw new FleetAbortException(ErrorCode.CANNOT_DONATE_NOT_ALLIED);
	}
	
	if (f.getOwner().isRestricted())
		throw new FleetAbortException(ErrorCode.RESTRICTED_ACCESS);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	
	long sum = f.getTotalUnits();
//	long result = sum*FleetStationCommand.STATION_TIME;
	double result = Math.log(sum+1)/Math.log(1.00048)-1434.0d;
	return (long) result;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	f.stationFleet();
}

@Override
public boolean mayAbort(Fleet f)
{ return true; }

@Override
public void abort(Fleet f)
{/*OK*/}

}
