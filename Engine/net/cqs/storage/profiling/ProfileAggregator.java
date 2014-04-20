package net.cqs.storage.profiling;

import java.io.Serializable;

import net.cqs.util.ArrayDataSeries;
import net.cqs.util.DataSeries;

public final class ProfileAggregator implements Serializable
{

private static final long serialVersionUID = 1L;

private final long[] objectCountAverages = new long[7*24];
private final long[] delayAverages = new long[7*24];
private final long[] taskCounts = new long[7*24];
private long lastHour = 0;
private long objectCountAverage = 0;
private long delayAverage = 0;
private long taskCount = 0;
private long count = 0;

public ProfileAggregator()
{
	lastHour = getCurrentHour();
}

private long getCurrentHour()
{ return System.currentTimeMillis() / (1000L*60*60); }

public synchronized int size()
{ return objectCountAverages.length; }

public synchronized long get(int row, int i)
{
	switch (row)
	{
		case 0 : return objectCountAverages[i];
		case 1 : return delayAverages[i];
		case 2 : return taskCounts[i];
		default :
			throw new IllegalArgumentException();
	}
}

public synchronized DataSeries getDataSeries(int row)
{
	final long[] values;
	switch (row)
	{
		case 0 : values = objectCountAverages.clone(); break;
		case 1 : values = delayAverages.clone(); break;
		case 2 : values = taskCounts.clone(); break;
		default :
			throw new IllegalArgumentException();
	}
	return new ArrayDataSeries(values);
}

public synchronized void add(long time, ProfileReport report)
{
	long hour = getCurrentHour();
	if (hour == lastHour)
	{
		objectCountAverage += report.getObjectCount();
		delayAverage = Math.max(delayAverage, report.getDelay());
		taskCount++;
		count++;
	}
	else
	{
		while (lastHour < hour)
		{
			for (int i = 0; i < objectCountAverages.length-1; i++)
			{
				objectCountAverages[i] = objectCountAverages[i+1];
				delayAverages[i] = delayAverages[i+1];
				taskCounts[i] = taskCounts[i+1];
			}
			objectCountAverages[objectCountAverages.length-1] = 0;
			delayAverages[delayAverages.length-1] = 0;
			taskCounts[taskCounts.length-1] = 0;
			lastHour++;
		}
		lastHour = hour;
		objectCountAverage = report.getObjectCount();
		delayAverage = report.getDelay();
		taskCount = 1;
		count = 1;
	}
	objectCountAverages[objectCountAverages.length-1] = objectCountAverage/count;
	delayAverages[delayAverages.length-1] = delayAverage; ///count;
	taskCounts[taskCounts.length-1] = taskCount;
}

}
