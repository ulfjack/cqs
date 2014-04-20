package net.cqs.storage.memory;

import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.storage.DataManager;
import net.cqs.storage.ManagedReference;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.SerializationHelper;
import net.cqs.storage.ExtendedDataManager;
import net.cqs.storage.Task;
import net.cqs.storage.ValueTask;
import net.cqs.storage.profiling.ProfileAggregator;

final class MemoryStorage implements ExtendedDataManager, DataManager //, TaskManager
{

private static final boolean DEBUG = false;
private static final Logger logger = Logger.getLogger(MemoryStorage.class.getName());

static {
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.ALL);
	logger.setLevel(Level.WARNING);
	logger.addHandler(handler);
	logger.setUseParentHandlers(false);
}

private final ConcurrentSkipListMap<Long,MemoryEntry> entries = new ConcurrentSkipListMap<Long,MemoryEntry>();
private final ConcurrentSkipListMap<String,MemoryBinding> bindings = new ConcurrentSkipListMap<String,MemoryBinding>();

private final ThreadLocal<MemoryTransactionLog> txns = new ThreadLocal<MemoryTransactionLog>();
private final AtomicLong entryIds = new AtomicLong(0);
private final AtomicReference<ProfileAggregator> aggregator =
	new AtomicReference<ProfileAggregator>(new ProfileAggregator());

public MemoryStorage()
{
	// Ok
}

private MemoryTransactionLog getLog()
{
	MemoryTransactionLog result = txns.get();
	return result;
}

private ManagedReferenceImpl createReference_(Object object)
{
	ManagedEntry entry = getLog().entryLookup.get(object);
	if (entry != null)
		return new ManagedReferenceImpl(entry.getId());
	Long id = Long.valueOf(entryIds.incrementAndGet());
	MemoryEntry mem = new MemoryEntry(id, null);
	entries.put(id, mem);
	mem.getLock().lock();
	entry = new ManagedEntry(mem, object);
	getLog().add(entry);
	return new ManagedReferenceImpl(id);
}

public ManagedReference createReference(Object object)
{ return createReference_(object); }

public <T> T get(Long id, Class<T> type)
{
	if (DEBUG) logger.fine("get "+id);
	ManagedEntry entry = getLog().entries.get(id);
	if (entry == null)
	{
		MemoryEntry e = entries.get(id);
		e.getLock().lock();
		Object result = SerializationHelper.deserialize(e.getValue());
		entry = new ManagedEntry(e, result);
		getLog().add(entry);
	}
	return type.cast(entry.getValue());
}

<T> T getForUpdate(Long id, Class<T> type)
{ return get(id, type); }

private ManagedBinding findBinding(String name)
{
	MemoryTransactionLog log = getLog();
	ManagedBinding binding = log.bindings.get(name);
	if (binding == null)
	{
		MemoryBinding mem = bindings.get(name);
		if (mem == null)
		{
			mem = new MemoryBinding(name, null);
			MemoryBinding other = bindings.putIfAbsent(name, mem);
			if (other != null) mem = other;
		}
		mem.getLock().lock();
		binding = new ManagedBinding(mem);
		log.bindings.put(name, binding);
	}
	return binding;
}

@Override
public <T> T getBinding(String name, Class<T> type)
{
	if (DEBUG) logger.fine("getBinding "+name);
	ManagedBinding binding = findBinding(name);
	Long id = binding.getId();
	if (id == null) throw new NameNotBoundException(name);
	return get(id, type);
}

@Override
public void removeBinding(String name)
{
	if (DEBUG) logger.fine("removeBinding "+name);
	ManagedBinding binding = findBinding(name);
	binding.setId(null);
}

@Override
public void setBinding(String name, Object object)
{
	ManagedBinding binding = findBinding(name);
	Long id = createReference_(object).getId();
	binding.setId(id);
	if (DEBUG) logger.fine("setBinding "+name+" = "+id);
}

public void scheduleTask(Runnable task)
{ scheduleTask(task, 0); }

public void scheduleTask(Runnable task, long delay)
{ throw new UnsupportedOperationException(); }

private void revert(MemoryTransactionLog log)
{
	for (ManagedEntry e : log.entries.values())
		e.getOriginal().getLock().unlock();
	for (ManagedBinding e : log.bindings.values())
		e.getOriginal().getLock().unlock();
}

private void writeBack(MemoryTransactionLog log)
{
	// convert all entries to byte arrays
	IdentityHashMap<MemoryEntry,byte[]> convertedEntries = new IdentityHashMap<MemoryEntry,byte[]>();
	try
	{
		for (ManagedEntry e : log.entries.values())
		{
			if (DEBUG) logger.fine("write "+e.getId());
			byte[] data = SerializationHelper.serialize(e.getValue());
			convertedEntries.put(e.getOriginal(), data);
		}
	}
	catch (Throwable e)
	{
		revert(log);
		if (e instanceof RuntimeException)
			throw (RuntimeException) e;
		if (e instanceof Error)
			throw (Error) e;
		throw new RuntimeException(e);
	}
	
	for (Entry<MemoryEntry,byte[]> e : convertedEntries.entrySet())
	{
		MemoryEntry entry = e.getKey();
		byte[] data = e.getValue();
		entry.setValue(data);
		entry.getLock().unlock();
	}
	for (ManagedBinding e : log.bindings.values())
	{
		MemoryBinding binding = e.getOriginal();
		Long id = e.getId();
		binding.setId(id);
		binding.getLock().unlock();
	}
	
	long time = getCurrentTime();
	log.report.finish(time);
	aggregator.get().add(time, log.report);
	
	if (DEBUG) logger.warning("Task took: "+log.report.getDelay()+"ms");
}

@Override
public <T> T call(ValueTask<T> task)
{
	if (txns.get() != null) throw new IllegalStateException();
	T result;
	MemoryTransactionLog log = new MemoryTransactionLog(getCurrentTime(), task.toString());
	try
	{
		// run the task with the log set as a ThreadLocal
		txns.set(log);
		try
		{ result = task.call(); }
		catch (RuntimeException e)
		{ throw e; }
		catch (Exception e)
		{ throw new RuntimeException(e); }
		finally
		{ txns.set(null); }
		
		MemoryTransactionLog log2 = log;
		log = null;
		writeBack(log2);
	}
	finally
	{ if (log != null) revert(log); }
	return result;
}

@Override
public void call(final Task task)
{
	if (txns.get() != null) throw new IllegalStateException();
	MemoryTransactionLog log = new MemoryTransactionLog(getCurrentTime(), task.toString());
	try
	{
		// run the task with the log set as a ThreadLocal
		txns.set(log);
		try
		{ task.run(); }
		finally
		{ txns.set(null); }
		
		MemoryTransactionLog log2 = log;
		log = null;
		writeBack(log2);
	}
	finally
	{ if (log != null) revert(log); }
}

public long getCurrentTime()
{ return System.currentTimeMillis(); }

@Override
public void shutdown()
{/* Do nothing! */}

@Override
public void sync()
{/* Do nothing! */}

@Override
public ProfileAggregator getProfileAggregator()
{ return aggregator.get(); }

@Override
public void setProfileAggregator(ProfileAggregator newAggregator)
{ this.aggregator.set(newAggregator); }

}
