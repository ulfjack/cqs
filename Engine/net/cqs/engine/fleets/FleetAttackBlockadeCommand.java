package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Planet;
import net.cqs.engine.Position;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BattleFleetListener;
import net.cqs.engine.battles.PlanetBattle;
import net.cqs.engine.diplomacy.DiplomacyEntry;

public final class FleetAttackBlockadeCommand extends FleetCommand implements BattleFleetListener
{

private static final long serialVersionUID = 1L;

public FleetAttackBlockadeCommand()
{
	// Ok
}

public static String englishTranslation()
{ return "attack blockade"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetAttackBlockadeCommand(); }

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
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() < Position.PLANET)
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_BLOCK_NO_PLANET);
	
	if (f.getPosition().specificity() == Position.COLONY)
	{
		if (f.mayLeavePlanet())
			f.setLanded(false);
		else throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_BLOCK_IS_LANDED);
	}
	
	Planet p = f.findPlanet();
	if (p.getSpaceBattle() == null)
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_BLOCK_NO_BLOCK);
	
	DiplomacyEntry entry = f.getGalaxy().getDiplomaticRelation().getEntry(f.getOwner(), p.getSpaceBattle().getOwner());
	if (!entry.getStatus().canAttack())
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_DIPLOMACY);
	if (!entry.getStatus().canAttack(f.getGalaxy().getTime(), entry.getAttackTime()))
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_DIPLOMACY_WAIT);

	
	
	if (f.getOwner().isRestricted())
		throw new FleetAbortException(ErrorCode.RESTRICTED_ACCESS);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	logger.entering("FleetAttackBlockadeCommand", "prepare");
	checkAllowed(f);
	return Constants.ATTACK_DELAY;
}

@Override
public void execute(Fleet f) throws FleetException
{
	logger.entering("FleetAttackBlockadeCommand", "execute");
	PlanetBattle battle = f.findPlanet().createSpaceBattle(f.getNextEventTime());
	throw new FleetJoinBattleException(battle, Battle.ATTACKER_SIDE, FleetState.FIGHTING, this, true);
}

@Override
public void won(Fleet f, long time)
{
	f.reset(time);
	if (f.getPosition().specificity() == Position.COLONY)
		f.setPosition(f.getPosition().toPlanetPosition());
	else
	{
		Colony c = f.getPosition().findPlanet(f.getGalaxy()).findAny();
		if (c != null) f.setPosition(c.getPosition());
	}
	f.resume(time, true);
}

@Override
public void lost(Fleet f, long time)
{ f.reset(time); }

@Override
public void withdraw(Fleet f, long time)
{ f.withdraw(time); }

}
