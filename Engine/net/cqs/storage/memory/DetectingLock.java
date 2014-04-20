package net.cqs.storage.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

final class DetectingLock implements Lock
{

private static final ConcurrentHashMap<Thread,Thread> waitForGraph = new ConcurrentHashMap<Thread,Thread>();

private Thread owner;

public DetectingLock()
{
	// Ok
}

private static boolean isDeadlocked(Thread target, Thread node, int depth)
{
	if (depth == 0) return false;
	Thread next = waitForGraph.get(node);
	if (next == null) return false;
	if (next == target) return true;
	boolean result = isDeadlocked(target, next, depth-1);
	if (waitForGraph.get(node) == next) return result;
	return false;
}

@Override
public synchronized void lock()
{
	Thread current = Thread.currentThread();
	if (owner == current) throw new IllegalStateException();
	while (owner != null)
	{
		try
		{
			waitForGraph.put(current, owner);
			if (isDeadlocked(current, owner, waitForGraph.size()))
				throw new DeadlockError("Detected deadlock!");
			this.wait();
		}
		catch (InterruptedException e)
		{ e.printStackTrace(); }
		finally
		{ waitForGraph.remove(current); }
	}
	owner = current;
}

@Override
public synchronized void unlock()
{
	if (owner != Thread.currentThread()) throw new IllegalStateException();
	owner = null;
	this.notifyAll();
}


@Override
public void lockInterruptibly() throws InterruptedException
{ throw new UnsupportedOperationException(); }

@Override
public Condition newCondition()
{ throw new UnsupportedOperationException(); }

@Override
public boolean tryLock()
{ throw new UnsupportedOperationException(); }

@Override
public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
{ throw new UnsupportedOperationException(); }

}
