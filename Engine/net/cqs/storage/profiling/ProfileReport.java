package net.cqs.storage.profiling;

public final class ProfileReport
{

private final String taskType;
private final long startTime;
private int objectCount = 0;
private long stopTime;

public ProfileReport(String taskType, long startTime)
{
	this.taskType = taskType;
	this.startTime = startTime;
}

public String getTaskType()
{ return taskType; }

public long getDelay()
{ return stopTime-startTime; }

public int getObjectCount()
{ return objectCount; }

public void increaseObjectCount()
{ objectCount++; }

public void finish(long atTime)
{
	this.stopTime = atTime;
}

}
