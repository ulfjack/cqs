package net.cqs.storage;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.storage.profiling.ProfileAggregator;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.util.DbBackup;

final class BDBStorage implements ExtendedDataManager, DataManager //, TaskManager
{

static final String ENTRYDB_NAME = "entries";
static final String BINDINGDB_NAME = "bindings";
static final String TASKDB_NAME = "tasks";

private static final boolean DEBUG = false;
private static final Logger logger = Logger.getLogger(BDBStorage.class.getName());

private final File dir;
private final Environment env;
private final Database entryDb;
private final Database bindingDb;
private final ThreadLocal<BDBTransactionLog> txns = new ThreadLocal<BDBTransactionLog>();
private final AtomicLong entryIds;
private final AtomicReference<ProfileAggregator> aggregator =
	new AtomicReference<ProfileAggregator>(new ProfileAggregator());

public BDBStorage(File f)
{
	this.dir = f;
	try
	{
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		env = new Environment(f, envConfig);
		
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);
		entryDb = env.openDatabase(null, "entries", dbConfig);
		bindingDb = env.openDatabase(null, "bindings", dbConfig);
		
		CursorConfig config = new CursorConfig();
		config.setReadUncommitted(true);
		Transaction t = env.beginTransaction(null, null);
		try
		{
			Cursor c = entryDb.openCursor(t, config);
			try
			{
				DatabaseEntry keyEntry = new DatabaseEntry();
				DatabaseEntry dataEntry = new DatabaseEntry();
				if (c.getLast(keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS)
					entryIds = new AtomicLong(LongBinding.entryToLong(keyEntry));
				else
					entryIds = new AtomicLong(0);
			}
			finally
			{ c.close(); }
			Transaction t2 = t;
			t = null;
			t2.commit();
		}
		finally
		{ if (t != null) t.abort(); }
	}
	catch (Exception e)
	{ throw new RuntimeException(e); }
}

private void closeIgnoreException(Closeable item)
{
	try
	{ item.close(); }
	catch (Exception e)
	{ logger.log(Level.WARNING, "Exception ignored", e); }
}

public void makeSnapshot()
{
	File snapshotDirectory = new File("SNAPSHOT");
	snapshotDirectory.mkdir();
	
	try
	{
		DbBackup backupHelper = new DbBackup(env);
		backupHelper.startBackup();
		try
		{
			String[] filesForBackup = backupHelper.getLogFilesInBackupSet();
			for (String s : filesForBackup)
			{
				if (DEBUG) logger.fine(s);
				File from = new File(dir, s);
				File to = new File(snapshotDirectory, s);
				FileInputStream in = null;
				FileOutputStream out = null;
				try
				{
					in = new FileInputStream(from);
					out = new FileOutputStream(to);
					byte[] block = new byte[1024];
					int i;
					while ((i = in.read(block)) != -1)
						out.write(block, 0, i);
				}
				finally
				{
					if (in != null)
						closeIgnoreException(in);
					if (out != null)
						closeIgnoreException(out);
				}
			}
//		lastFileCopiedInPrevBackup = backupHelper.getLastFileInBackupSet();
//        myApplicationSaveLastFile(lastFileCopiedInBackupSet);
		}
		finally
		{ backupHelper.endBackup(); }
	}
	catch (IOException e)
	{ throw new RuntimeException(e); }
	catch (DatabaseException e)
	{ throw new RuntimeException(e); }
}

private BDBTransactionLog getLog()
{
	BDBTransactionLog result = txns.get();
	return result;
}

private ManagedReferenceImpl createReference_(Object object)
{
	ManagedEntry entry = getLog().entryLookup.get(object);
	if (entry != null)
		return new ManagedReferenceImpl(entry.getId());
	Long id = Long.valueOf(entryIds.incrementAndGet());
	entry = new ManagedEntry(id, object);
	getLog().add(entry);
	return new ManagedReferenceImpl(id);
}

@Override
public ManagedReferenceImpl createReference(Object object)
{ return createReference_(object); }

@Override
public <T> T get(Long id, Class<T> type)
{
	if (DEBUG) logger.fine("get "+id);
	ManagedEntry entry = getLog().entries.get(id);
	if (entry == null)
	{
		try
		{
			DatabaseEntry keyEntry = new DatabaseEntry();
			DatabaseEntry dataEntry = new DatabaseEntry();
			LongBinding.longToEntry(id.longValue(), keyEntry);
			OperationStatus status = entryDb.get(getLog().transaction, keyEntry, dataEntry, LockMode.RMW);
			if (status == OperationStatus.NOTFOUND)
				throw new ObjectNotFoundException();
			Object result = SerializationHelper.deserialize(dataEntry.getData());
			entry = new ManagedEntry(id, result);
			getLog().add(entry);
		}
		catch (DatabaseException e)
		{ throw new TransactionException(e); }
	}
	return type.cast(entry.getValue());
}

<T> T getForUpdate(Long id, Class<T> type)
{ return get(id, type); }

@Override
public <T> T getBinding(String name, Class<T> type)
{
	if (DEBUG) logger.fine("getBinding "+name);
	try
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry();
		StringBinding.stringToEntry(name, keyEntry);
		OperationStatus status = bindingDb.get(getLog().transaction, keyEntry, dataEntry, LockMode.RMW);
		if (status == OperationStatus.NOTFOUND)
			throw new NameNotBoundException("'"+name+"' not bound");
		Long id = Long.valueOf(LongBinding.entryToLong(dataEntry));
		return get(id, type);
	}
	catch (DatabaseException e)
	{ throw new TransactionException(e); }
}

@Override
public void removeBinding(String name)
{
	if (DEBUG) logger.fine("removeBinding "+name);
	try
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		StringBinding.stringToEntry(name, keyEntry);
		OperationStatus status = bindingDb.delete(getLog().transaction, keyEntry);
		if (status == OperationStatus.NOTFOUND)
			throw new NameNotBoundException("'"+name+"' not bound");
	}
	catch (DatabaseException e)
	{ throw new TransactionException(e); }
}

@Override
public void setBinding(String name, Object object)
{
	Long id = createReference_(object).getId();
	if (DEBUG) logger.fine("setBinding "+name+" = "+id);
	try
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry();
		StringBinding.stringToEntry(name, keyEntry);
		LongBinding.longToEntry(id.longValue(), dataEntry);
		OperationStatus status = bindingDb.put(getLog().transaction, keyEntry, dataEntry);
		if (status != OperationStatus.SUCCESS) throw new TransactionException("no success");
	}
	catch (DatabaseException e)
	{ throw new TransactionException(e); }
}

public void scheduleTask(Runnable task)
{ scheduleTask(task, 0); }

public void scheduleTask(Runnable task, long delay)
{
/*	if (delay < 0)
		throw new IllegalArgumentException("cannot schedule tasks in the past");
	final Runnable storedTask;
	if (task instanceof ManagedObject)
		storedTask = new ReferenceTask(createReference((ManagedObject) task));
	else
		storedTask = task;
	try
	{
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry();
		LongBinding.longToEntry(getLog().startTime+delay, keyEntry);
		dataEntry.setData(SerializationHelper.serialize(storedTask));
		OperationStatus status = taskDb.put(getLog().transaction, keyEntry, dataEntry);
		if (status != OperationStatus.SUCCESS) throw new TransactionException("no success");
	}
	catch (DatabaseException e)
	{ throw new TransactionException(e); }*/
	throw new UnsupportedOperationException();
}

private void writeBack(BDBTransactionLog log) throws DatabaseException
{
	// write all modified entries back to the database
	DatabaseEntry keyEntry = new DatabaseEntry();
	DatabaseEntry dataEntry = new DatabaseEntry();
	for (ManagedEntry e : log.entries.values())
	{
		if (DEBUG) logger.fine("  write "+e.getId());
		LongBinding.longToEntry(e.getId().longValue(), keyEntry);
		byte[] data = SerializationHelper.serialize(e.getValue());
//		logger.fine("Bytes serialized: "+data.length+" "+e.getValue()+" "+e.getId());
		dataEntry.setData(data);
		OperationStatus status = entryDb.put(log.transaction, keyEntry, dataEntry);
		if (status != OperationStatus.SUCCESS) throw new TransactionException("no success");
	}
	long time = getCurrentTime();
	log.report.finish(time);
	aggregator.get().add(time, log.report);
}

@Override
public <T> T call(ValueTask<T> task)
{
	if (txns.get() != null) throw new IllegalStateException();
	T result;
	try
	{
		Transaction t = env.beginTransaction(null, null);
		try
		{
			BDBTransactionLog log = new BDBTransactionLog(getCurrentTime(), t, task.toString());
			
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
			
			writeBack(log);
			
			// commit the transaction
			Transaction t2 = t;
			t = null;
			t2.commit();
		}
		finally
		{ if (t != null) t.abort(); }
	}
	catch (DatabaseException e)
	{
		e.printStackTrace();
		return null;
	}
	
	return result;
}

@Override
public void call(final Task task)
{
	if (txns.get() != null) throw new IllegalStateException();
	try
	{
		Transaction t = env.beginTransaction(null, null);
		try
		{
			BDBTransactionLog log = new BDBTransactionLog(getCurrentTime(), t, task.toString());
			
			// run the task with the log set as a ThreadLocal
			txns.set(log);
			try
			{ task.run(); }
			finally
			{ txns.set(null); }
			
			writeBack(log);
			
			// commit the transaction
			Transaction t2 = t;
			t = null;
			t2.commit();
		}
		finally
		{ if (t != null) t.abort(); }
	}
	catch (DatabaseException e)
	{
		e.printStackTrace();
	}
}

public long getCurrentTime()
{ return System.currentTimeMillis(); }

@Override
public void shutdown()
{
	try
	{
		env.sync();
		entryDb.close();
		bindingDb.close();
		env.close();
	}
	catch (DatabaseException e)
	{ throw new RuntimeException(e); }
}

@Override
public void sync()
{
	try
	{ env.sync(); }
	catch (DatabaseException e)
	{ throw new RuntimeException(e); }
}

@Override
public ProfileAggregator getProfileAggregator()
{ return aggregator.get(); }

@Override
public void setProfileAggregator(ProfileAggregator newAggregator)
{ this.aggregator.set(newAggregator); }

}
