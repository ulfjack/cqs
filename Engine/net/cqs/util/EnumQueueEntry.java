package net.cqs.util;

public final class EnumQueueEntry<T extends Enum<T>>
{

private final T type;
private final int amount;

public EnumQueueEntry(T type, int amount)
{
	this.type = type;
	this.amount = amount;
}

public T type()
{ return type; }

public int amount()
{ return amount; }

}
