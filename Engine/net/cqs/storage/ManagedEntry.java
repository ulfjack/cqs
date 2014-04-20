package net.cqs.storage;

final class ManagedEntry
{

private final Long id;
private Object value;

ManagedEntry(Long id, Object value)
{
	this.id = id;
	this.value = value;
}

ManagedEntry(Long id)
{ this(id, null); }

public Long getId()
{ return id; }

public Object getValue()
{ return value; }

}
