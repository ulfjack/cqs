package net.cqs.engine.battles;

import java.util.logging.Level;

import net.cqs.config.*;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Galaxy;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;
import net.cqs.engine.fleets.FleetRobCommand;

public final class SurfaceBattle extends Battle
{

private static final long serialVersionUID = 1L;

public SurfaceBattle(Galaxy galaxy, long time, Colony c)
{
	super(BattleTypeEnum.SURFACE_BATTLE, galaxy, time, c.getPosition(), UnitSelector.GROUND_ONLY, c.getOwner());
}

private Colony findColony()
{ return galaxy.findColony(position()); }

@Override
public void notifyRemove(long time)
{
	Colony c = findColony();
	if (c != null)
	{
		if (this == c.getSurfaceBattle())
			c.removeSurfaceBattle();
		else
			logger.log(Level.SEVERE, "cleanUp failed", 
					new IllegalStateException("SurfaceBattle is not set on Colony"));
	}
}

@Override
public void checkValidity()
{
	Colony c = findColony();
	if (c != null)
	{
		if (c.getSurfaceBattle() != this)
			throw new IllegalStateException("Not on colony!");
	}
}

@Override
public boolean isSpace()
{ return false; }

@Override
public void getAutoDefenders(long time)
{
	logger.entering("SurfaceBattle", "getAutoDefenders");
	Colony c = findColony();
	if (c == null) return;
	if (c.getSize() == 0) return;
	
	UnitMap units = new UnitMap();
	UnitIterator it = c.getUnitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		logger.fine(it.key()+" -- "+it.value());
		Unit unit = it.key();
		if (!unit.hasSpecial(UnitSpecial.AUTO_DEFENDER))
			continue;
		units.set(it.key(), it.value());
	}
	
	int militiaCount = 0;
	Unit militia = getUnitSystem().getMilitia();
	if (militia != null)
	{
		militiaCount = (int) (c.getDisplayPopulation(time) /
				(Math.sqrt(c.getSize()) * militia.getCost().getPopulation()));
	}
	
	if (units.sum()+militiaCount > 0)
	{
		Fleet f = Fleet.createAutoDefender(c, time, units, militiaCount);
		execute(new JoinAction(Battle.DEFENDER_SIDE, FleetState.FIGHTING, f,
				new AutoFleetBattleListener(), false), time);
	}
}

private boolean invade(long time, FleetEntry entry)
{
	Fleet f = entry.fleet;
	Colony c = findColony();
	if (c == null)
	{
		f.log(ErrorCode.CANNOT_INVADE_NO_COLONY, f);
		return false;
	}
	
	if (f.getOwner() == c.getOwner())
	{
		f.log(ErrorCode.CANNOT_INVADE_YOURSELF, f);
		return false;
	}
	
	Colony own = c.getPlanet().findColony(f.getOwner());
	if (own == null)
	{
		if (!f.getOwner().mayColonize()) 
		{
			f.log(ErrorCode.CANNOT_INVADE_CIVILPOINTS, f);
			return false;
		}
		own = c.getPlanet().createInvasionColony(f.getOwner(), time);
	}
	
	int lostUnits = 0;
	try
	{
		int max = c.getSize();
		if (max > Constants.INVASION_MAX_BUILDINGS) max = Constants.INVASION_MAX_BUILDINGS;
		if (max < 0) max = 0;
		for (int i = 0; i < max; i++)
		{
			int missing = c.missingTroopsForInvasion(f);
			if (missing > 0) 
			{
				f.log(ErrorCode.CANNOT_INVADE_NOT_ENOUGH_TROOPS, f, Integer.valueOf(missing));
				return false;
			}
			
			lostUnits += c.subtractTroopsRequiredForInvasion(f);
			
			float keep_probability = (float) c.getOwner().getPoints()/f.getOwner().getPoints();
			boolean keep = getRandomFloat()*Constants.INVASION_BURN_FACTOR + Constants.INVASION_BURN_NULLRATE < keep_probability;
			BuildingEnum result = own.steal(c, keep, time);
			if (result != null)
			{
				if (keep)
					battleLogger.invadeResult(f, result);
				else
					battleLogger.burnResult(f, result);
			}
		}
	}
	catch (Error e)
	{
		f.getOwner().log(ErrorCode.INTERNAL_ERROR, f);
		return false;
	}
	catch (RuntimeException e)
	{
		f.getOwner().log(ErrorCode.INTERNAL_ERROR, f);
		return false;
	}
	finally
	{
		own.checkSize(time);
		if (lostUnits > 0)
		{
			battleLogger.unitsInvaded(ATTACKER_SIDE, f, lostUnits);
			battleLogger.message(BattleMessageEnum.INVASION_SUCCESSFUL);
		}
	}
	return true;
}

private void rob(long time, FleetEntry entry)
{
	Fleet f = entry.fleet;
	Colony c = findColony();
	if (c == null)
	{
		f.log(ErrorCode.CANNOT_ROB_NO_COLONY, f);
		return;
	}
	
	if (f.getOwner() == c.getOwner())
	{
		f.log(ErrorCode.CANNOT_ROB_YOURSELF, f);
		return;
	}
	
	if (!(f.getExecutingCommand() instanceof FleetRobCommand))
	{
		f.log(ErrorCode.FLEET_INTERNAL_ERROR, f);
		return;
	}
	
	try
	{
		FleetRobCommand cmd = (FleetRobCommand) f.getExecutingCommand();
		Cost cost = new Cost(cmd.getAmounts());
		
		cost = c.rob(time, cost, f.getResourceSpace()-f.getCargoAmount());
		c.getOwner().getGalaxy().dropInfo(c.getOwner(), time, InfoEnum.RESOURCES_PLUNDERED,
				c.getPosition(),
				cost.getResource(ResourceEnum.STEEL), cost.getResource(ResourceEnum.OIL),
				cost.getResource(ResourceEnum.SILICON), cost.getResource(ResourceEnum.DEUTERIUM));
		f.getOwner().getGalaxy().dropInfo(f.getOwner(), time, InfoEnum.RESOURCES_PLUNDERED_FROM_ENEMY,
				c.getPosition(),
				cost.getResource(ResourceEnum.STEEL), cost.getResource(ResourceEnum.OIL),
				cost.getResource(ResourceEnum.SILICON), cost.getResource(ResourceEnum.DEUTERIUM));
		
		for (int i = Resource.MIN; i <= Resource.MAX; i++)
			f.addCargo(i, cost.getResource(i));
		f.checkAndFixCargo(time);
		
		battleLogger.message(BattleMessageEnum.ROB_SUCCESSFUL);
/*		if (result < 0)
			battleLogger.burnResult(f, -result);
		else if (result > 0)
			battleLogger.invadeResult(f, result);*/
	}
	catch (Error e)
	{
		f.getOwner().log(ErrorCode.INTERNAL_ERROR, f);
		return;
	}
	catch (RuntimeException e)
	{
		f.getOwner().log(ErrorCode.INTERNAL_ERROR, f);
		return;
	}
	return;
}

private void dropFleet(long time, FleetEntry entry, int side)
{
	logger.fine("Removing "+entry.fleet);
	battleLogger.leave(ATTACKER_SIDE, entry.fleet);
	try
	{
		sides[side].remove(time, entry, RemoveReason.LOST);
	}
	catch (Throwable e)
	{
		logger.log(Level.SEVERE, "Ignored Exception", e);
		entry.fleet.reset(time);
	}
	if (!mustContinue())
	{
		removeBattle(time, BattleStateEnum.ATTACKER);
		return;
	}
}

@Override
protected void adjustPunishmentDamage(int bySide, float[] damage)
{
	if (bySide == ATTACKER_SIDE)
	{
		for (int i = 0; i < damage.length; i++)
			damage[i] *= 0.5f;
	}
}

@Override
protected void adjustBattleDamage(float[][] damage)
{
	for (int i = 0; i < damage[DEFENDER_SIDE].length; i++)
		damage[DEFENDER_SIDE][i] *= 0.9f;
}

@Override
public void activateAttacker(long time, boolean hadDefenders)
{
	// only send out battle reports if we had defenders before
	if (hadDefenders)
	{
		completeBattle(time);
		dumpInactive(time, BattleStateEnum.ATTACKER);
		if (!mustContinue())
		{
			removeBattle(time, BattleStateEnum.ATTACKER);
			return;
		}
	}
	else
		dumpInactive(time, BattleStateEnum.ATTACKER);
	
	Colony c = findColony();
	if ((c == null) || (c.getSize() == 0))
	{
		removeBattle(time, BattleStateEnum.ATTACKER);
		return;
	}
	
	int winMax = 0;
	int robMax = 0;
	FleetEntry winFleet = null;
	FleetEntry robFleet = null;
	for (int i = 0; i < sides[ATTACKER_SIDE].size(); i++)
	{
		FleetEntry entry = sides[ATTACKER_SIDE].get(i);
		if ((entry.fleet.getState() == FleetState.INVADING) && (entry.fleet.getTotalUnits() > winMax))
		{
			winFleet = entry;
			winMax = entry.fleet.getTotalUnits();
		}
		if ((entry.fleet.getState() == FleetState.ROBBING) && (entry.fleet.getTotalUnits() > robMax))
		{
			robFleet = entry;
			robMax = entry.fleet.getTotalUnits();
		}
	}
	
	if (robFleet != null)
	{
		rob(time, robFleet);
		dropFleet(time, robFleet, ATTACKER_SIDE);
	}
	
	if (winFleet == null)
		return;
	
	if (!invade(time, winFleet))
	{
		// Wenn die Invasion nicht erfolgreich war,
		// dann wird die Flotte aus dem Kampf geworfen.
		dropFleet(time, winFleet, ATTACKER_SIDE);
	}
	else
	{
		if (findColony() == null)
		{
			removeBattle(time, BattleStateEnum.ATTACKER);
			return;
		}
	}
}

}
