package net.cqs.main.signals;

public abstract class StrobeAdapter implements StrobeListener
{

public static long seconds(long secs)
{ return secs; }

public static long minutes(long min)
{ return 60L*min; }

public static long hours(long hs)
{ return 60L*60L*hs; }


long timeInterval;

public StrobeAdapter(long timeInterval)
{ this.timeInterval = timeInterval; }

@Override
public long getTimeInterval()
{ return timeInterval; }

}
