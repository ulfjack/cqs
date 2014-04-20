package net.cqs.storage;

import net.cqs.storage.profiling.ProfileAggregator;

public abstract class AbstractStorageManager implements StorageManager
{

private final ExtendedDataManager storage;

public AbstractStorageManager(ExtendedDataManager storage)
{
	this.storage = storage;
}

@Override
public void execute(Task task)
{
	Context.execute(storage, task);
}

@Override
public <T> T execute(ValueTask<T> task)
{
	return Context.execute(storage, task);
}

@Override
public <T> T getCopy(final String id, final Class<T> type)
{
	return execute(new ValueTask<T>()
		{
			@Override
			public T call()
			{
				T result = Context.getDataManager().getBinding(id, type);
				return result;
			}
		});
}

@Override
public <T> T getAndRemove(final String id, final Class<T> type)
{
	return execute(new ValueTask<T>()
		{
			@Override
			public T call()
			{
				T result = Context.getDataManager().getBinding(id, type);
				Context.getDataManager().removeBinding(id);
				return result;
			}
		});
}

@Override
public void set(final String id, final Object o)
{
	execute(new Task()
		{
			@Override
			public void run()
			{
				Context.getDataManager().setBinding(id, o);
			}
		});
}

@Override
public void shutdown()
{ storage.shutdown(); }

@Override
public void sync()
{ storage.sync(); }

@Override
public ProfileAggregator getProfileAggregator()
{ return storage.getProfileAggregator(); }

@Override
public void setProfileAggregator(ProfileAggregator aggregator)
{ storage.setProfileAggregator(aggregator); }

}
