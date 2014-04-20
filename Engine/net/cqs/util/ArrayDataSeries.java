package net.cqs.util;

import java.io.Serializable;

public final class ArrayDataSeries implements Serializable, DataSeries
{

private static final long serialVersionUID = 1L;

private final long[] values;

public ArrayDataSeries(long[] values)
{
	this.values = values;
}

@Override
public int size()
{ return values.length; }

@Override
public long get(int index)
{ return values[index]; }

@Override
public long max()
{
	long result = values[0];
	for (int i = 0; i < values.length; i++)
		if (values[i] > result) result = values[i];
	return result;
}

@Override
public long min()
{
	long result = values[0];
	for (int i = 0; i < values.length; i++)
		if (values[i] < result) result = values[i];
	return result;
}

@Override
public String toCSV()
{
	StringBuilder result = new StringBuilder();
	for (int i = 0; i < values.length; i++)
	{
		if (i != 0) result.append(',');
		result.append(values[i]);
	}
	return result.toString();
}

}