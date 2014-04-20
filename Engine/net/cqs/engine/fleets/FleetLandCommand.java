package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;
import net.cqs.engine.diplomacy.DiplomaticRelation;
import net.cqs.engine.diplomacy.DiplomaticStatus;

public final class FleetLandCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public static final long LANDING_TIME = Constants.LAND_TIME;

public FleetLandCommand()
{/*OK*/}

public static String englishTranslation()
{ return "land"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetLandCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f)
{ return 0; }

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (f.isLanded())
		throw new FleetAbortException(ErrorCode.CANNOT_LAND_ALREADY_GROUND);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_LAND_NO_COLONY);
	
	if (f.findColony().getSurfaceBattle() != null)
		throw new FleetAbortException(ErrorCode.CANNOT_LAND_BATTLE);
	
	DiplomaticRelation relation = f.getGalaxy().getDiplomaticRelation();
	DiplomaticStatus status = relation.getEntry(f.findColony().getOwner(), f.getOwner()).getStatus();
	if (!status.canLand())
		throw new FleetAbortException(ErrorCode.CANNOT_LAND_DIPLOMACY);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return LANDING_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	f.land();
}

@Override
public boolean mayAbort(Fleet f)
{ return true; }

@Override
public void abort(Fleet f)
{/*OK*/}

}
