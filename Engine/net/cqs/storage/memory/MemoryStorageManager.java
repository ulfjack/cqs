package net.cqs.storage.memory;

import net.cqs.storage.AbstractStorageManager;
import net.cqs.storage.StorageManager;

public final class MemoryStorageManager extends AbstractStorageManager implements StorageManager
{

public MemoryStorageManager()
{ super(new MemoryStorage()); }

}
