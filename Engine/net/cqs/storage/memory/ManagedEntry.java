package net.cqs.storage.memory;

final class ManagedEntry
{

private final MemoryEntry original;
private Object value;

ManagedEntry(MemoryEntry original, Object value)
{
	this.original = original;
	this.value = value;
}

public MemoryEntry getOriginal()
{ return original; }

public Long getId()
{ return original.getId(); }

public Object getValue()
{ return value; }

}