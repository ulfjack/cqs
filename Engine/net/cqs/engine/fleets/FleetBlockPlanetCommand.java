package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Planet;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BlockadeFleetBattleListener;
import net.cqs.engine.battles.PlanetBattle;

public final class FleetBlockPlanetCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public FleetBlockPlanetCommand()
{/*OK*/}

public static String englishTranslation()
{ return "block planet"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetBlockPlanetCommand(); }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public int check(Fleet f) 
{
	return 0;
}

public void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_INVALID_POSITION);
	
	if (f.getPosition().specificity() < Position.PLANET)
		throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_NO_PLANET);
	
	if (!f.canAttackSpace())
		throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_NO_SPACE_POWER);
	
	Planet planet = f.getPosition().findPlanet(f.getGalaxy());
	if (planet == null)
		throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_NO_PLANET);
	
	if (planet.getSpaceBattle() != null)
	{
		Player pl = planet.getSpaceBattle().getOwner();
		if (pl != null)
		{
			if (!pl.alliedWith(f.getOwner()))
				throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_ALREADY_BLOCKED);
		}
	}
	
	if (planet.amount() == 0)
		throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_NO_COLONY_ON_PLANET);
	
	if (f.isLanded())
	{
		if (f.mayLeavePlanet())
			f.setLanded(false);
		else
			throw new FleetAbortException(ErrorCode.CANNOT_BLOCK_LANDED);
	}
	
	if (f.getOwner().isRestricted())
		throw new FleetAbortException(ErrorCode.RESTRICTED_ACCESS);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	Galaxy.logger.entering("FleetBlockadeCommand", "prepare");
	checkAllowed(f);
	return Constants.BLOCK_DELAY;
}

@Override
public void execute(Fleet f) throws FleetException
{
	Galaxy.logger.entering("FleetBlockPlanetCommand", "execute");
	checkAllowed(f);
	PlanetBattle battle = f.findPlanet().createSpaceBattle(f.getNextEventTime());
	throw new FleetJoinBattleException(battle, Battle.DEFENDER_SIDE, FleetState.BLOCKING,
			new BlockadeFleetBattleListener(), false);
}

}
