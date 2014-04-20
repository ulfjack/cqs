package net.cqs.engine.base;

import java.io.Serializable;

/**
 * <code>EventHandle</code>s are returned by the EventQueue and can be used to 
 * remove an Event from the Queue.
 */
public final class EventHandle implements Serializable, Comparable<EventHandle>
{

private static final long serialVersionUID = 1L;

private final long time;
private final long id;

public EventHandle(long time, long id)
{
	this.time = time;
	this.id = id;
}

public long getTime()
{ return time; }

@Override
public int hashCode()
{ return (int) (time*10L+id); }

public boolean equals(EventHandle other)
{
	return (other.time == time) && (other.id == id);
}

@Override
public boolean equals(Object other)
{
	if (other == this)
		return true;
	if (other instanceof EventHandle)
		return equals((EventHandle) other);
	return false;
}

@Override
public int compareTo(EventHandle other)
{
	if (other == this) return 0;
	
	if (time < other.time) return -1;
	if (time > other.time) return +1;
	
	if (id < other.id) return -1;
	if (id > other.id) return +1;
	
	return 0;
}

@Override
public String toString()
{
	StringBuilder result = new StringBuilder();
	result.append("Event[time=").append(time).append(",id=").append(id).append(']');
	return result.toString();
}

}
