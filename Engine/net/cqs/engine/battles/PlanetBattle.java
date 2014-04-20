package net.cqs.engine.battles;

import java.util.logging.Level;

import net.cqs.config.BattleTypeEnum;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Planet;
import net.cqs.engine.base.UnitSelector;

public final class PlanetBattle extends Battle
{

private static final long serialVersionUID = 1L;

public PlanetBattle(Galaxy galaxy, long time, Planet p)
{
	super(BattleTypeEnum.SPACE_BATTLE, galaxy, time, p.getPosition(), UnitSelector.SPACE_ONLY, null);
}

private Planet findPlanet()
{ return galaxy.findPlanet(position()); }

public long getBlockingUnitCount()
{ return sides[Battle.DEFENDER_SIDE].allunits.sum(); }

@Override
public void notifyRemove(long time)
{
	Planet p = findPlanet();
	if (this == p.getSpaceBattle())
		p.removeSpaceBattle();
	else
		logger.log(Level.SEVERE, "cleanUp failed", new IllegalStateException("Battle is not set on Planet"));
}

@Override
public void checkValidity()
{
	Planet p = findPlanet();
	if (p != null)
	{
		if (p.getSpaceBattle() != this)
			throw new IllegalStateException("Not on planet!");
	}
}

@Override
public boolean isSpace()
{ return true; }

}
