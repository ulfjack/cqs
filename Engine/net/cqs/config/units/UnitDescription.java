package net.cqs.config.units;

import java.io.Serializable;

// FIXME: module sortieren und hashcode anders berechnen
// eigentlich @Deprecated, Unit/UnitSystem kann jetzt alles
public final class UnitDescription implements Serializable
{

private static final long serialVersionUID = 1L;

private final UnitClass chassis;
public final UnitModule[] modules;
public final int[] counts;
private int cachedHashCode = 0;

// For testing purposes only!
public UnitDescription()
{
	chassis = null;
	modules = null;
	counts = null;
}

public UnitDescription(UnitClass chassis, UnitModule[] umodules)
{
	this.chassis = chassis;
	this.modules = chassis.getModules();
	this.counts = new int[this.modules.length];
	for (int i = 0; i < umodules.length; i++)
	{
		for (int j = 0; j < this.modules.length; j++)
			if (this.modules[j] == umodules[i])
				this.counts[j]++;
	}
}

public UnitDescription(UnitClass chassis, int[] counts) throws BadDesignException
{
	this.chassis = chassis;
	this.modules = chassis.getModules();
	this.counts = counts.clone();
	
	if (this.modules.length != this.counts.length)
		throw new BadDesignException("Bad design!");
	
	if (getUnit() == null)
		throw new BadDesignException("Bad design!");
}

public UnitClass getChassis()
{ return chassis; }

public Unit getUnit()
{
	try
	{
		return chassis.getUnitSystem().getUnit(this);
	}
	catch (BadDesignException e)
	{ throw new RuntimeException(e); }
}

public boolean equals(UnitDescription other)
{
	if (chassis != other.chassis) return false;
	if (modules.length != other.modules.length) return false;
	for (int i = 0; i < modules.length; i++)
	{
		if (!modules[i].equals(other.modules[i]))
			return false;
		if (counts[i] != other.counts[i])
			return false;
	}
	return true;
}

@Override
public boolean equals(Object other)
{
	if (other == this) return true;
	if (other instanceof UnitDescription)
		return equals((UnitDescription) other);
	return false;
}

@Override
public int hashCode()
{
	if (cachedHashCode != 0) return cachedHashCode;
	int result = 33*chassis.hashCode();
	for (int i = 0; i < modules.length; i++)
	{
		result = 11*result+modules[i].hashCode();
		result = 7*result+counts[i];
	}
	return result;
}

@Override
public String toString()
{
	StringBuffer result = new StringBuffer();
	result.append('[');
	if (modules == null)
	{
		result.append("null]");
		return result.toString();
	}
	for (int i = 0; i < modules.length; i++)
	{
		result.append(counts[i]).append('*').append(modules[i]);
		if (i < modules.length-1)
			result.append(", ");
	}
	result.append(']');
	return result.toString();
}

}
