package net.cqs.storage;

import net.cqs.engine.Galaxy;

public final class Context
{

private static ThreadLocal<ExtendedDataManager> transactions = new ThreadLocal<ExtendedDataManager>();

public static DataManager getDataManager()
{ return transactions.get(); }

public static ExtendedDataManager getTransaction()
{ return transactions.get(); }

public static void execute(ExtendedDataManager storage, Task task)
{
//	if (fakeTransactions.get() != null)
//		throw new IllegalStateException();
	if (transactions.get() != null)
		throw new IllegalStateException();
	try
	{
		transactions.set(storage);
		storage.call(task);
	}
	finally
	{
		transactions.set(null);
	}
}

public static <T> T execute(ExtendedDataManager storage, ValueTask<T> task)
{
//	if (fakeTransactions.get() != null)
//		throw new IllegalStateException();
	if (transactions.get() != null)
		throw new IllegalStateException();
	try
	{
		transactions.set(storage);
		return storage.call(task);
	}
	finally
	{
		transactions.set(null);
	}
}


private static ThreadLocal<Galaxy> fakeTransactions = new ThreadLocal<Galaxy>();

public static Galaxy getGalaxy()
{ return fakeTransactions.get(); }

public static void execute(Galaxy galaxy, GalaxyTask task)
{
	if (fakeTransactions.get() != null)
		throw new IllegalStateException();
	if (transactions.get() != null)
		throw new IllegalStateException();
	try
	{
		fakeTransactions.set(galaxy);
		task.run();
	}
	finally
	{
		fakeTransactions.set(null);
	}
}

}
