package net.cqs.util;

import java.io.Serializable;

/**
 * Defines a map from int to int.
 * 
 * Negative keys are not allowed. Negative values are allowed.
 */
public final class IntIntMap implements Serializable
{

private static final long serialVersionUID = 1L;
private static int[] EMPTY_INT = new int[0];

private int sum;
private int[] data;

public IntIntMap()
{
	data = EMPTY_INT;
	sum = 0;
}

public IntIntMap(int startlen)
{
	data = new int[startlen+1];
	sum = 0;
}

public IntIntMap(IntIntMap other)
{
	if (other.data == EMPTY_INT || other.data.length == 0)
		data = EMPTY_INT;
	else
		data = other.data.clone();
	sum = other.sum;
}

/**
 * Gibt die Summe der Werte zurueck.
 */
public int getSum()
{ return sum; }

public int set(int typ, int value)
{
	if (typ < 0)
		throw new IllegalArgumentException(typ+" < 0");
	
	if (typ >= data.length)
	{
		int[] temp = new int[typ+1];
		System.arraycopy(data, 0, temp, 0, data.length);
		data = temp;
	}
	sum += value-data[typ];
	data[typ] = value;
	return value;
}

public int get(int typ)
{
	if ((typ >= 0) && (typ < data.length))
		return data[typ];
	return 0;
}

public void clear()
{
	sum = 0;
	data = EMPTY_INT;
}

public void add(IntIntMap other)
{
	for (int i = 0; i < other.data.length; i++)
		increase(i, other.data[i]);
}

public void subtract(IntIntMap other)
{
	for (int i = 0; i < other.data.length; i++)
		decrease(i, other.data[i]);
}

public int increase(int typ, int amount)
{ return set(typ, get(typ)+amount); }

public int increase(int typ)
{ return increase(typ, 1); }

public int decrease(int typ, int amount)
{ return set(typ, get(typ)-amount); }

public int decrease(int typ)
{ return decrease(typ, 1); }

@Override
public String toString()
{
	StringBuilder result = new StringBuilder(data.length*4+2);
	result.append('[');
	boolean hasWritten = false;
	for (int i = 0; i < data.length; i++)
	{
		if (data[i] != 0)
		{
			if (hasWritten) result.append(", ");
			result.append(i).append(':').append(data[i]);
			hasWritten = true;
		}
	}
	result.append(']');
	return result.toString();
}

}
