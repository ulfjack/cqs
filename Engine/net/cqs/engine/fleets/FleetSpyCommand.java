package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitSelector;

public final class FleetSpyCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

private Unit spyUnit;
private int amount;

public FleetSpyCommand(int amount)
{ this.amount = amount; }

public static String englishTranslation()
{ return "plant {0,number,integer} {0,choice,1#spy|1<spies}"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, Integer.valueOf(amount)); }

@Override
public FleetCommand copy()
{ return new FleetSpyCommand(amount); }

@Override
public String getEditorType()
{ return EDIT_SPY; }

public int getAmount()
{ return amount; }

public void setAmount(int howmany)
{
	if (howmany > 0)
		amount = howmany;
}


private boolean hasUnits(Fleet f)
{
	UnitIterator it = f.getUnitIterator(UnitSelector.ALL);
	int count = 0;
	while (it.hasNext())
	{
		it.next();
		if (it.key().hasSpecial(UnitSpecial.ESPIONAGE))
		{
			spyUnit = it.key();
			count += it.value();
		}
	}
	return (count >= amount);
}

@Override
public int check(Fleet f)
{
	if (!hasUnits(f)) return -1;
	return 0;
}

private void checkAllowed(Fleet f) throws FleetAbortException
{
	Position pos = f.getPosition();
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (pos.specificity() < Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_SPY_NO_COLONY);
	
	if (f.findColony().getOwner() == f.getOwner())
		throw new FleetAbortException(ErrorCode.CANNOT_SPY_OWN_COLONY);
	
	if (f.getOwner().isRestricted())
		throw new FleetAbortException(ErrorCode.RESTRICTED_ACCESS);

	// Seiteneffekt: setzt spyUnit
	if (!hasUnits(f))
		throw new FleetAbortException(ErrorCode.CANNOT_SPY_NO_UNIT);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return Constants.SPY_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	long time = f.getNextEventTime();
	checkAllowed(f);

	Colony c = f.findColony();
	for (int i = 0; i < amount; i++)
	{	
	
		f.decreaseUnit(spyUnit);
		
		Galaxy galaxy = f.getGalaxy();
		if (galaxy.getRandomFloat() <= c.getAgentSuccessProbability())
		{ // erfolgreich stationiert
			c.notifyAgentPlanted();
			c.createAgent(f.getOwner(), time);
			galaxy.dropInfo(f.getOwner(), time, InfoEnum.SPY_SET, f, f.getPosition());
		}
		else
		{ // nicht erfolgreich stationiert
			galaxy.dropInfo(f.getOwner(), time, InfoEnum.SPY_LOST, f, f.getPosition());
			galaxy.dropInfo(c.getOwner(), time, InfoEnum.SPY_FOUND, c.getPosition(), f.getOwner().getName());
		}
	}
}

}
