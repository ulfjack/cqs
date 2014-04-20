package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitIterator;
import net.cqs.engine.base.UnitSelector;

public final class FleetSettleCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

private Unit colonizeUnit;
private int amount;

public FleetSettleCommand(int amount)
{
	this.amount = amount;
}

public static String englishTranslation()
{ return "add {0,number,integer} {0,choice,1#settler|1<settlers} to colony"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, Integer.valueOf(amount)); }

@Override
public FleetCommand copy()
{ return new FleetSettleCommand(amount); }

@Override
public String getEditorType()
{ return EDIT_SETTLE; }

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
		if (it.key().hasSpecial(UnitSpecial.SETTLEMENT))
		{
			colonizeUnit = it.key();
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
	
	if ((pos.specificity() != Position.COLONY) || (f.findColony() == null)) 
		throw new FleetAbortException(ErrorCode.CANNOT_SETTLE_NO_COLONY);
	
	// Seiteneffekt: setzt colonizeUnit
	if (!hasUnits(f))
		throw new FleetAbortException(ErrorCode.CANNOT_SETTLE_NO_UNIT);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	return Constants.SETTLE_TIME;
}

@Override
public void execute(Fleet f) throws FleetAbortException
{
	long time = f.getNextEventTime();
	checkAllowed(f);
	
	Colony c = f.findColony();
	for (int i = 0; i < amount; i++)
	{
		f.decreaseUnit(colonizeUnit);
//		UnitSlot slot = c.owner.getSlot(colonizeUnit);
		c.addPeople(time, 30000);
		f.getGalaxy().dropInfo(f.getOwner(), time, InfoEnum.FLEET_SETTLED, f);
	}
}

}
