package net.cqs.engine.rtevents;

import java.io.Serializable;
import java.util.Locale;

public final class RealTimeEvent implements Serializable, Comparable<RealTimeEvent>
{

private static final long serialVersionUID = 1L;

private final long time;
private final RealTimeEventType type;

public RealTimeEvent(long time, RealTimeEventType type)
{
	this.time = time;
	this.type = type;
}

public long getTime()
{ return time; }

public RealTimeEventType getType()
{ return type; }

public String getName(Locale locale)
{ return type.getName(locale); }

@Override
public int hashCode()
{ return (int) (time >> 32)+(int) time+type.hashCode(); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof RealTimeEvent)) return false;
	if (this == o) return true;
	RealTimeEvent other = (RealTimeEvent) o;
	return (time == other.time) && type.equals(other.type);
}

@Override
public int compareTo(RealTimeEvent o)
{
	if (time < o.time) return -1;
	if (time > o.time) return 1;
	return type.compareTo(o.type);
}

}
