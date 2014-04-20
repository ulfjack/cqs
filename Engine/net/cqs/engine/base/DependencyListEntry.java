package net.cqs.engine.base;

public final class DependencyListEntry<T extends Enum<T>>
{

private final T type;
private final int amount;

public DependencyListEntry(T type, int amount)
{
	this.type = type;
	this.amount = amount;
}

public T type()
{ return type; }

public int amount()
{ return amount; }

}
