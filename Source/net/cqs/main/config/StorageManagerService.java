package net.cqs.main.config;

import net.cqs.services.StorageService;
import net.cqs.storage.Storage;
import net.cqs.storage.Task;
import net.cqs.storage.ValueTask;

class StorageManagerService implements StorageService
{

private final Storage manager;

public StorageManagerService(Storage manager)
{
	this.manager = manager;
}

@Override
public void execute(Task task)
{ manager.execute(task); }

@Override
public <T> T execute(ValueTask<T> task)
{ return manager.execute(task); }

@Override
public <T> T getAndRemove(String id, Class<T> type)
{ return manager.getAndRemove(id, type); }

@Override
public <T> T getCopy(String id, Class<T> type)
{ return manager.getCopy(id, type); }

@Override
public void set(final String id, final Object o)
{ manager.set(id, o); }

}
