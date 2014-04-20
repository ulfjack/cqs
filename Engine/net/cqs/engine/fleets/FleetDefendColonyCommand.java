package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Position;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BlockadeFleetBattleListener;
import net.cqs.engine.battles.LandingBattle;
import net.cqs.engine.battles.SurfaceBattle;
import net.cqs.engine.diplomacy.DiplomacyEntry;

public final class FleetDefendColonyCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetDefendColonyCommand()
{/*OK*/}

public static String englishTranslation()
{ return "defend colony"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetDefendColonyCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f) 
{ return 0; }

public void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_DEFEND_NO_COLONY);
	
	Colony c = f.findColony();
	if (c == null)
		throw new FleetAbortException(ErrorCode.CANNOT_DEFEND_NO_COLONY);
	
	if (f.containsPlanetaryUnits() && f.containsSpaceFleet())
		f.getOwner().log(ErrorCode.MIXED_DEFENSE_WARNING, f);
	else
	{
		if (!f.containsPlanetaryUnits())
			f.setLanded(false);
	}
	
	if (!f.isLanded() && f.containsPlanetaryUnits()) {
		// if the fleet owner has a colony on the planet, he may pass landing battles
		if (f.getOwner().hasColonyOnPlanet(c.getPlanet()))
				f.setLanded(true);
		// if there's a surface battle and no landing battle, join surface battle
		else if ((c.getLandingBattle() == null) && (c.getSurfaceBattle() != null)) {
			DiplomacyEntry entry = f.getGalaxy().getDiplomaticRelation().getEntry(f.getOwner(), c.getOwner());
			if (!entry.getStatus().canLandToDefend())
				f.getOwner().log(ErrorCode.CANNOT_LAND_TO_DEFEND_NO_NAP);
			else
				f.setLanded(true);
		}
	}
	
	if (f.isLanded())
	{
		if (!f.containsPlanetaryUnits())
			throw new FleetAbortException(ErrorCode.CANNOT_DEFEND_NO_GROUND_UNITS);
	}
	else
	{
		if (!f.containsSpaceFleet())
			throw new FleetAbortException(ErrorCode.CANNOT_DEFEND_NO_SPACE_UNITS);
	}
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	Galaxy.logger.entering("FleetDefendColonyCommand", "prepare");
	checkAllowed(f);
	return Constants.BLOCK_DELAY;
}

@Override
public void execute(Fleet f) throws FleetException
{
	Galaxy.logger.entering("FleetDefendColonyCommand", "execute");
	checkAllowed(f);
	Colony c = f.findColony();
	if (f.isLanded())
	{
		SurfaceBattle battle = c.createSurfaceBattle(f.getNextEventTime());
		throw new FleetJoinBattleException(battle, Battle.DEFENDER_SIDE, FleetState.BLOCKING,
				new BlockadeFleetBattleListener(), false);
	}
	else
	{
		LandingBattle battle = c.createLandingBattle(f.getNextEventTime());
		throw new FleetJoinBattleException(battle, Battle.DEFENDER_SIDE, FleetState.BLOCKING,
				new BlockadeFleetBattleListener(), false);
	}
}

}
