package net.cqs.storage;

import java.io.File;

public class BDBStorageManager extends AbstractStorageManager implements StorageManager
{

public BDBStorageManager(File f)
{ super(new BDBStorage(f)); }

}
