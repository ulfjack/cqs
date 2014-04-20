package net.cqs.engine.battles;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.config.BattleStateEnum;
import net.cqs.config.BattleTypeEnum;
import net.cqs.config.Constants;
import net.cqs.config.InfoEnum;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.base.Event;
import net.cqs.engine.base.EventHandle;
import net.cqs.engine.base.UnitSelector;
import net.cqs.engine.empty.NullBattleLogger;
import net.cqs.engine.units.UnitSystemImpl;

public abstract class Battle implements Serializable
{

private static final long serialVersionUID = 3L;

	private static class TicEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		private Battle battle;
		@Override
		public void activate(long time)
		{ battle.fight(time); }
		public TicEvent(Battle battle)
		{ this.battle = battle; }
	}

public static final Logger logger = Logger.getLogger("net.cqs.engine.battles");

public static final int ATTACKER_SIDE = 0;
public static final int DEFENDER_SIDE = 1;

// FIXME: make this a serializable field of Battle
private static final Random rand = new Random();

// non-static
private   final String id;
private   final BattleTypeEnum type;
protected final Galaxy galaxy;
private   final Position position;
protected transient BattleEventListener battleLogger;
protected UnitSelector selector;

protected final HashMap<Player,BattleAdaptor> adaptors = new HashMap<Player,BattleAdaptor>();
protected final BattleSide[] sides;
protected final BattleAdaptor visitorAdaptor;

long numTic;
EventHandle nextTicHandle;
public long nextTicTime;

private Player owner;

/**
 * The battle is automatically scheduled with the first joining fleet!
 */
public Battle(BattleTypeEnum type, Galaxy galaxy, long time, Position position, UnitSelector selector, Player visitor)
{
	this.type = type;
	if (type == BattleTypeEnum.SIMULATION_BATTLE)
	{
		this.id = Galaxy.uidGenerator.generateUniqueID();
		this.galaxy = galaxy == null ? Galaxy.dummyGalaxy() : galaxy;
		this.battleLogger = new NullBattleLogger(this);
	}
	else
	{
		this.id = Galaxy.uidGenerator.generateUniqueID();
		this.galaxy = galaxy;
		this.battleLogger = galaxy.createBattleLogger(this);
	}
	this.position = position;
	this.selector = selector;
	
	sides = new BattleSide[2];
	for (int s = 0; s <= 1; s++)
		sides[s] = new BattleSide(this, s, selector);
	
	if (visitor != null)
	{
		visitorAdaptor = new BattleAdaptor(this, visitor, true);
		adaptors.put(visitor, visitorAdaptor);
	}
	else
		visitorAdaptor = null;
	
	numTic = 0;
	nextTicTime = 0;
	nextTicHandle = null;
	
//	logger.exiting("Battle", "Battle");
}

public void check() throws InvalidDatabaseException
{
	if ((sides[DEFENDER_SIDE].size() == 0) && (sides[ATTACKER_SIDE].size() == 0))
	{
		logger.warning("Empty Battle: "+toString());
		logger.warning(visitorAdaptor.toString());
		throw new InvalidDatabaseException("Battle \""+toString()+"\" is empty!");
	}
	if (type == BattleTypeEnum.SIMULATION_BATTLE)
		this.battleLogger = new NullBattleLogger(this);
	else
		this.battleLogger = galaxy.createBattleLogger(this);
}


public BattleTypeEnum type()
{ return type; }

public String getId()
{ return id; }

@Deprecated
public Galaxy getGalaxy()
{ return galaxy; }

public UnitSystem getUnitSystem()
{ return UnitSystemImpl.getImplementation(); }

public long getNextTicTime()
{ return nextTicTime; }

protected abstract void notifyRemove(long time);
public abstract void checkValidity();
public abstract boolean isSpace();

public BattleSide getSide(int i)
{ return sides[i]; }

public Position position()
{ return position; }

public boolean mayFileReport()
{ return true; }

public boolean mayShowBattle()
{ return true; }

public boolean mustRemoveFleet(Fleet f)
{ return !f.containsUnits(selector); }

public Player getOwner()
{ return owner; }

public float getRandomFloat()
{ return rand.nextFloat(); }

protected float getRandom()
{
	float f = 0.2f*(float) rand.nextGaussian()+1;
	if (f < 0) f = 0;
	if (f > 2) f = 2;
	return f;
}


protected void scheduleBattle(long currenttime)
{
	logger.entering("Battle", "scheduleBattle");
	if (nextTicHandle != null)
		throw new IllegalStateException("Already scheduled!");
	nextTicTime = currenttime + Constants.BATTLE_TIC;
	nextTicHandle = galaxy.addEvent(nextTicTime, new TicEvent(this));
	for (int j = ATTACKER_SIDE; j <= DEFENDER_SIDE; j++)
	{
		for (int i = 0; i < sides[j].size(); i++)
			sides[j].getFleet(i).setNextCompleteTime(nextTicTime);
	}
}

protected void unscheduleBattle()
{
	logger.entering("Battle", "unscheduleBattle");
	if (nextTicHandle == null)
		throw new IllegalStateException("Not scheduled!");
	galaxy.deleteEvent(nextTicHandle);
	nextTicHandle = null;
	nextTicTime = 0;
}

protected final void tryUnscheduleBattle()
{ if (nextTicHandle != null) unscheduleBattle(); }

protected void fileReport(long time, Player who)
{
	logger.entering("Battle", "fileReport");
	if (mayFileReport())
	{
		try
		{
			galaxy.dropInfo(who, time, InfoEnum.BATTLE_REPORT, id, position(), type());
		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, "Ignoring exception", e);
		}
	}
	else
		logger.fine("No report filed!");
}

public boolean isInBattle(Player player)
{ return adaptors.containsKey(player); }

void notifyRemove(long time, Player player)
{
	adaptors.remove(player);
	fileReport(time, player);
	if (owner == player)
	{
		owner = null;
		if (sides[DEFENDER_SIDE].size() > 0)
		{
			FleetEntry entry = sides[DEFENDER_SIDE].get(0);
			owner = entry.fleet.getOwner();
		}
	}
}

void notifyJoin(int side, Player player)
{
	if ((side == DEFENDER_SIDE) && (owner == null))
		owner = player;
}

public boolean mayPass(Fleet f, boolean incoming)
{
	if (owner == null) return true;
	if (!f.canAttack() && !incoming) return true;
	return f.getOwner().getStatusTowards(owner).canPassBlockade(f.canAttack());
}

/**
 * Completes a battle:
 * resets losses/withdrawals.
 */
protected void completeBattle(long time)
{
	logger.entering("Battle", "completeBattle");
	
	// Notify both sides that the battle is complete.
	for (int s = 0; s <= 1; s++)
		sides[s].reset(time, false);
}

protected final void removeBattle(long time, BattleStateEnum reason)
{
	logger.entering("Battle", "removeBattle");
	battleLogger.end(reason);
	
	tryUnscheduleBattle();
	
	// Notify fleets that they have won / lost.
	for (int s = ATTACKER_SIDE; s <= DEFENDER_SIDE; s++)
		for (int i = 0; i < sides[s].size(); i++)
		{
			FleetEntry entry = sides[s].get(i);
			try
			{
				if (reason.isSide(s))
					sides[s].notifyRemove(time, entry, RemoveReason.WON);
				else
					sides[s].notifyRemove(time, entry, RemoveReason.LOST);
			}
			catch (Throwable e)
			{
				logger.log(Level.SEVERE, "Ignoring Exception", e);
				entry.fleet.reset(time);
			}
		}
	
	try
	{
		for (int s = ATTACKER_SIDE; s <= DEFENDER_SIDE; s++)
			sides[s].reset(time, true);
	}
	catch (Throwable e)
	{
		logger.log(Level.SEVERE, "Battle removal failed with Exception", e);
		if (sides == null) logger.info("adaptors is NULL");
		if (sides != null && sides[0] == null) logger.info("adaptors[0] is NULL");
		if (sides != null && sides[1] == null) logger.info("adaptors[1] is NULL");
		for (int s = ATTACKER_SIDE; s <= DEFENDER_SIDE; s++)
			sides[s] = new BattleSide(this, s, selector);
	}
	
	if (visitorAdaptor != null)
		visitorAdaptor.clearPersistent(time);
	notifyRemove(time);
	
	// Alle Spieler-Kaempfe entfernen
	Iterator<BattleAdaptor> it = adaptors.values().iterator();
	while (it.hasNext())
	{
		BattleAdaptor adaptor = it.next();
		if (adaptor.getFleetCount() != 0)
		{
			logger.log(Level.SEVERE, "BattleAdaptor still has fleets!");
			adaptor.reset(time);
		}
	}
}

public void endBattle(long time, BattleStateEnum reason)
{
	logger.entering("Battle", "endBattle");
	completeBattle(time);
	removeBattle(time, reason);
}

/**
 * When the fight is decided (one way or the other), this method will drop all fleets that do not 
 * continue fighting.
 * 
 * I.e. all non-invading, non-robbing, non-blocking fleets are dropped with the appropriate reason.
 */
protected void dumpInactive(final long time, final BattleStateEnum reason)
{
	for (int s = ATTACKER_SIDE; s <= DEFENDER_SIDE; s++)
	{
		final int finals = s;
		sides[s].act(time, new FleetEntryActor()
			{
				@Override
        public RemoveReason act(FleetEntry entry)
				{
					if (entry.fleet.getState().isWaitActFight())
						return reason.isSide(finals) ? RemoveReason.WON : RemoveReason.LOST;
					return null;
				}
			});
	}
}

protected boolean mustContinue()
{
	if (position.specificity() == Position.COLONY)
	{
		Colony c = galaxy.findColony(position);
		if (c == null) return false;
		if (c.getSize() == 0) return false;
	}
	return sides[DEFENDER_SIDE].size()+sides[ATTACKER_SIDE].size() > 0;
}

/**
 * May add automatic defenders to this battle.
 */
protected void getAutoDefenders(long time)
{
	logger.entering("Battle", "getAutoDefenders");
}

/*public final void join(long time, int side, int reason, Fleet f, BattleFleetListener bl)
{ join(time, side, reason, f, bl, true); }*/

public final void notifyAllianceChange(Player p)
{
	battleLogger.changePlayerAlliance(p);
}

public final Fleet find(Player p, int fid)
{
	logger.entering("Battle", "find");
	Fleet f;
	for (int j = ATTACKER_SIDE; j <= DEFENDER_SIDE; j++)
	{
		for (int i = 0; i < sides[j].size(); i++)
		{
			f = sides[j].getFleet(i); 
			if ((f.getOwner() == p) && (f.getId() == fid)) 
				return f;
		}
	}
	return null;
}

protected void adjustPunishmentDamage(int bySide, float[] damage)
{/*OK*/}

protected void adjustBattleDamage(float[][] damage)
{/*OK*/}


// Withdrawal outcomings
protected void withdrawUndecided(long time)
{/* Do nothing, battle is already scheduled! */}

protected void withdrawAttacker(long time)
{
	completeBattle(time);
	dumpInactive(time, BattleStateEnum.ATTACKER);
	if (!mustContinue())
		removeBattle(time, BattleStateEnum.ATTACKER);
}

protected void withdrawDefender(long time)
{
	completeBattle(time);
	dumpInactive(time, BattleStateEnum.DEFENDER);
	if (!mustContinue())
		removeBattle(time, BattleStateEnum.DEFENDER);
	else
	{
		/* Finish! Will reschedule when a new fleet joins! */
		tryUnscheduleBattle();
	}
}

protected void withdrawAllDead(long time)
{ endBattle(time, BattleStateEnum.ALLDEAD); }

/**
 * Convenience method that will remove the given fleet from this battle.
 */
public void withdraw(Fleet f, long time)
{
	for (int s = ATTACKER_SIDE; s <= DEFENDER_SIDE; s++)
	{
		for (int i = 0; i < sides[s].size(); i++)
		{
			FleetEntry entry = sides[s].get(i);
			if (entry.fleet == f)
			{
				execute(new WithdrawAction(entry, s), time);
				return;
			}
		}
	}
	throw new IllegalArgumentException("Fleet not found in this Battle!");
}

public void withdraw(Fleet f)
{ withdraw(f, galaxy.getTime()); }


// Fight outcomings
public void activateUndecided(long time)
{/* Do nothing, battle is already scheduled! */}

public void activateAttacker(long time, boolean hadDefenders)
{ endBattle(time, BattleStateEnum.ATTACKER); }

public void activateDefender(long time)
{
	completeBattle(time);
	dumpInactive(time, BattleStateEnum.DEFENDER);
	if (!mustContinue())
		removeBattle(time, BattleStateEnum.DEFENDER);
	else
	{
		/* Finish! Will reschedule when a new fleet joins! */
		tryUnscheduleBattle();
	}
}

public void activateAllDead(long time)
{ endBattle(time, BattleStateEnum.ALLDEAD); }


// Called by a TicEvent. You MUST NOT call this function.
private void fight(long time)
{
	nextTicHandle = null;
	nextTicTime = 0;
	scheduleBattle(time);
	execute(new FightAction(), time);
}


public void execute(BattleAction event, long time)
{
	event.execute(this, time);
}


/* Simulator Hack */
public void setKind(boolean space)
{
	if (space) selector = UnitSelector.SPACE_ONLY;
	else       selector = UnitSelector.GROUND_ONLY;
	for (int s = ATTACKER_SIDE; s <= DEFENDER_SIDE; s++)
		sides[s].setKind(space);
}

@Override
public String toString()
{ return type()+" "+System.identityHashCode(this)+" @ "+position; }

public void init()
{
	battleLogger.start();
}

}
