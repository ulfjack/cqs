package net.cqs.engine.base;

import net.cqs.config.units.Unit;

public final class UnitEntry
{

private final Unit type;
private final int amount;

public UnitEntry(Unit type, int amount)
{
	this.type = type;
	this.amount = amount;
}

public Unit type()
{ return type; }

public int amount()
{ return amount; }

}
