package net.cqs.storage.memory;

import java.util.concurrent.locks.Lock;

final class MemoryEntry
{

private final Long id;
private final Lock lock = new DetectingLock();
private byte[] value;

MemoryEntry(Long id, byte[] value)
{
	this.id = id;
	this.value = value;
}

MemoryEntry(Long id)
{ this(id, null); }

public Long getId()
{ return id; }

public Lock getLock()
{ return lock; }

public byte[] getValue()
{ return value; }

public void setValue(byte[] newValue)
{ this.value = newValue; }

}