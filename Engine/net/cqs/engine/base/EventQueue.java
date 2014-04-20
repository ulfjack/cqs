package net.cqs.engine.base;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * A queue of events, with insertion and deletion operations.
 * 
 * @see Event
 */
public final class EventQueue implements Serializable
{

public static final long serialVersionUID = 1L;

private TreeMap<EventHandle,Event> data = new TreeMap<EventHandle,Event>();
long id = 0;
long currentTime;

public EventQueue()
{/*OK*/}

public int size()
{ return data.size(); }

public EventHandle addEvent(long time, Event e)
{
	if (time < currentTime)
		throw new IllegalArgumentException("Cannot schedule an event in the past!");
	EventHandle result = new EventHandle(time, id++);
	while (data.containsKey(result))
	{
		result = new EventHandle(time, id++);
	}
	
	data.put(result, e);
	return result;
}

public Event getEvent(long time)
{
	if (data.size() == 0)
		return null;
	
	EventHandle handle = data.firstKey();
	
	if (handle.getTime() <= time)
	{
		Event e = data.remove(handle);
		currentTime = handle.getTime();
		return e;
	}
	
	return null;
}

public long getCurrentEventTime()
{ return currentTime; }

public void removeEvents(Object referer)
{
	Iterator<Event> it = data.values().iterator();
	while (it.hasNext())
	{
		Event event = it.next();
		if (event.check(referer))
			it.remove();
	}
}

public boolean deleteEvent(EventHandle handle)
{ return data.remove(handle) != null; }

}
