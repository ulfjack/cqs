package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Position;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.BattleFleetListener;
import net.cqs.engine.battles.LandingBattle;
import net.cqs.engine.battles.SurfaceBattle;
import net.cqs.engine.diplomacy.DiplomacyEntry;

public abstract class FleetFightCommand extends FleetCommand implements BattleFleetListener
{

private static final long serialVersionUID = 1L;

public static enum Mode
{
	ATTACK(FleetState.FIGHTING), ROB(FleetState.ROBBING), INVADE(FleetState.INVADING);
	private final FleetState state;
	private Mode(FleetState state)
	{ this.state = state; }
	public FleetState getState()
	{ return state; }
}

private final Mode mode;
//private boolean spaceBattle = false;

@Override
public String getEditorType()
{ return EDIT_NONE; }

public FleetFightCommand(Mode mode)
{
	this.mode = mode;
}

public Mode getMode()
{ return mode; }

@Override
public abstract String getName(Locale locale);

@Override
public int check(Fleet f) 
{
	return 0;
}

public void checkAllowed(Fleet f) throws FleetAbortException
{
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_NO_COLONY);
	
	Colony target = f.findColony();
	if (target == null)
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_NO_COLONY);
	
	// Feature?
	if (target.getOwner().alliedWith(f.getOwner()))
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_IS_ALLIED);
	
	// buggy
	// puren Raumflotten Raumstatus erteilen, sofern nicht gerade ein Raumkampf stattgefunden hat
/*	if (f.landed && !f.containsGroundUnits() && !spaceBattle)
		f.landed = false;*/
	if (f.isLanded())
	{
		if (!f.containsPlanetaryUnits())
			throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_SPACE_IS_GROUND);
	}
	
	if (f.isLanded() && (f.getPosition().specificity() != Position.COLONY))
		throw new FleetAbortException(ErrorCode.INTERNAL_ERROR);
	
	if (f.getOwner().isRestricted())
		throw new FleetAbortException(ErrorCode.RESTRICTED_ACCESS);
	
	DiplomacyEntry entry = f.getGalaxy().getDiplomaticRelation().getEntry(f.getOwner(), target.getOwner());
	if (!entry.getStatus().canAttack())
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_DIPLOMACY);
	if (!entry.getStatus().canAttack(f.getGalaxy().getTime(), entry.getAttackTime()))
		throw new FleetAbortException(ErrorCode.CANNOT_ATTACK_DIPLOMACY_WAIT);
}		

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	logger.entering("FleetAttackCommand", "prepare");
	checkAllowed(f);
	return Constants.ATTACK_DELAY;
}

@Override
public void execute(Fleet f) throws FleetException
{
	logger.entering("FleetAttackCommand", "execute");
	if (f.isLanded())
	{
		SurfaceBattle battle = f.findColony().createSurfaceBattle(f.getNextEventTime());
		throw new FleetJoinBattleException(battle, Battle.ATTACKER_SIDE, mode.getState(), this, true);
	}
	else
	{
		LandingBattle battle = f.findColony().createLandingBattle(f.getNextEventTime());
		throw new FleetJoinBattleException(battle, Battle.ATTACKER_SIDE, mode.getState(), this, true);
	}
}

public void resume(Fleet f, long time)
{
	logger.entering("FleetAttackCommand", "resume");
	f.reset(time);
	f.resume(time, true);
}

@Override
public void won(Fleet f, long time)
{
	logger.entering("FleetAttackCommand", "won");
	f.reset(time);
	if (!f.isLanded() && (f.getPosition().specificity() == Position.COLONY))
	{
		f.setLanded(true);
//		spaceBattle = true;
		f.prepare(time, this);
	}
//	else
//		spaceBattle = !f.landed;
}

@Override
public void lost(Fleet f, long time)
{
	logger.entering("FleetAttackCommand", "lost");
	f.reset(time);
//	spaceBattle = !f.landed;
}

@Override
public void withdraw(Fleet f, long time)
{
	f.withdraw(time);
//	spaceBattle = !f.landed;
}

}
