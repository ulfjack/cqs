package net.cqs.util;

import java.io.Serializable;

public final class HistorySeries implements Serializable, DataSeries
{

private static final long serialVersionUID = 1L;

	public static enum UpdateType
	{ SUM, AVERAGE, MAX; }

private final UpdateType type;
private final long[] values;
private final long resolution;
private long lastInterval = 0;
private long value = 0;
private long count = 0;

public HistorySeries(UpdateType type, int length, long resolution)
{
	this.type = type;
	this.values = new long[length];
	this.resolution = resolution;
	this.lastInterval = getCurrentInterval();
}

public HistorySeries(UpdateType type)
{
	this(type, 7*24, 1000L*60*60);
}

private long getCurrentInterval()
{ return System.currentTimeMillis() / resolution; }

@Override
public int size()
{ return values.length; }

@Override
public long get(int i)
{ return values[i]; }

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

public void update(long updateValue)
{
	long interval = getCurrentInterval();
	if (interval == lastInterval)
	{
		if (type == UpdateType.MAX)
			value = Math.max(value, updateValue);
		else
			value += updateValue;
		count++;
	}
	else
	{
		while (lastInterval < interval)
		{
			for (int i = 0; i < values.length-1; i++)
				values[i] = values[i+1];
			values[values.length-1] = 0;
			lastInterval++;
		}
		lastInterval = interval;
		value = updateValue;
		count = 1;
	}
	
	long result;
	if (type != UpdateType.AVERAGE)
		result = value;
	else
		result = value/count;
	values[values.length-1] = result;
}


}
