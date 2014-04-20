package net.cqs.storage.memory;

final class ManagedBinding
{

private final MemoryBinding original;
private Long id;

ManagedBinding(MemoryBinding original)
{
	this.original = original;
	this.id = original.getId();
}

public MemoryBinding getOriginal()
{ return original; }

public String getName()
{ return original.getName(); }

public Long getId()
{ return id; }

public void setId(Long newId)
{ this.id = newId; }

}
