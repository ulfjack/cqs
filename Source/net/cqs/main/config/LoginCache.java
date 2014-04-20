package net.cqs.main.config;

import java.util.HashMap;
import java.util.Iterator;

import net.cqs.main.signals.StrobeAdapter;
import net.cqs.main.signals.StrobeListener;

final class LoginCache implements StrobeListener
{

private final HashMap<Integer,Long> infoMap = new HashMap<Integer,Long>();

public LoginCache(FrontEnd frontEnd)
{
	frontEnd.addStrobeListener(this);
}

public synchronized int size()
{ return infoMap.size(); }

public synchronized void access(int playerId)
{ infoMap.put(Integer.valueOf(playerId), Long.valueOf(System.currentTimeMillis())); }

public synchronized boolean isOnline(int playerId)
{ return infoMap.containsKey(Integer.valueOf(playerId)); }

@Override
public long getTimeInterval()
{ return StrobeAdapter.seconds(5); }

@Override
public synchronized void strobe()
{
	long time = System.currentTimeMillis()-5L*60L*1000L;
	Iterator<Long> it = infoMap.values().iterator();
	while (it.hasNext())
	{
		Long info = it.next();
		if (info.longValue() < time)
			it.remove();
	}
}

}
