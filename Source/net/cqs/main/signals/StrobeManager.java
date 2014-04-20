package net.cqs.main.signals;

import java.util.ArrayList;

/**
 * You have to call <code>start()</code> when you want the StrobeManager to start working.
 */
public final class StrobeManager implements Runnable, ShutdownListener
{

private final ExceptionListener exceptionListener;

private boolean isStarted = false;
private boolean runThread = true;
private int changeCount = 0;
private final ArrayList<StrobeListener> listeners = new ArrayList<StrobeListener>();

public StrobeManager()
{
	this(new ExceptionListener() {
		@Override
    public void exceptionRaised(Throwable e)
		{
			e.printStackTrace();
		}
	});
}

public StrobeManager(ExceptionListener el)
{
	exceptionListener = el;
}

public synchronized void add(StrobeListener l)
{
	listeners.add(l);
	changeCount++;
}

public synchronized void remove(StrobeListener l)
{
	listeners.remove(l);
	changeCount++;
}

@Override
public void run()
{
	long secondCounter = 0;
	int copyCount = 0;
	ArrayList<StrobeListener> copy = new ArrayList<StrobeListener>();
	while (true)
	{
		synchronized (this)
		{
			if (copyCount != changeCount)
			{
				copy.clear();
				copy.addAll(listeners);
				copyCount = changeCount;
			}
		}
		
		secondCounter++;
		for (int i = 0; i < copy.size(); i++)
		{
			StrobeListener l = copy.get(i);
			try
			{
				if (secondCounter % l.getTimeInterval() == 0)
					l.strobe();
			}
			catch (Throwable e)
			{
				exceptionListener.exceptionRaised(e);
			}
		}
		
		synchronized (this)
		{
			if (!runThread) return;
			try
			{ wait(1000); }
			catch (Exception e)
			{ exceptionListener.exceptionRaised(e); }
			if (!runThread) return;
		}
	}
}

public synchronized void start()
{
	if (isStarted) throw new IllegalStateException();
	new Thread(this, "StrobeManager").start();
}

@Override
public synchronized void shutdown()
{
	runThread = false;
	notifyAll();
}

}
