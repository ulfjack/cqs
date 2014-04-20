package net.cqs.storage.memory;

import java.util.concurrent.locks.Lock;

final class MemoryBinding
{

private final String name;
private final Lock lock = new DetectingLock();
private Long id;

MemoryBinding(String name, Long id)
{
	this.name = name;
	this.id = id;
}

public String getName()
{ return name; }

public Lock getLock()
{ return lock; }

public Long getId()
{ return id; }

public void setId(Long newId)
{ this.id = newId; }

}