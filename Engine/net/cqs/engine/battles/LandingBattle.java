package net.cqs.engine.battles;

import java.util.logging.Level;

import net.cqs.config.BattleTypeEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Galaxy;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.base.UnitSelector;

public final class LandingBattle extends Battle
{

private static final long serialVersionUID = 1L;

public LandingBattle(Galaxy galaxy, long time, Colony c)
{
	super(BattleTypeEnum.LANDING_BATTLE, galaxy, time, c.getPosition(), UnitSelector.SPACE_ONLY, c.getOwner());
}

private Colony findColony()
{ return galaxy.findColony(position()); }

@Override
protected void notifyRemove(long time)
{
	Colony c = findColony();
	if (c != null)
	{
		if (this == c.getLandingBattle())
			c.removeLandingBattle();
		else
			logger.log(Level.SEVERE, "cleanUp failed", 
					new IllegalStateException("LandingBattle is not set on Colony"));
	}
}

@Override
public void checkValidity()
{
	Colony c = findColony();
	if (c != null)
	{
		if (c.getLandingBattle() != this)
			throw new IllegalStateException("Not on colony!");
	}
}

@Override
public boolean isSpace()
{ return true; }

@Override
public void getAutoDefenders(long time)
{
	logger.entering("LandingBattle", "getAutoDefenders");
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
	
	if (units.sum() > 0)
	{
		Fleet f = Fleet.createAutoDefender(c, time, units, 0);
		execute(new JoinAction(DEFENDER_SIDE, FleetState.FIGHTING, f,
				new AutoFleetBattleListener(), false), time);
	}
}

}
