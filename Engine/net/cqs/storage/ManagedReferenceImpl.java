package net.cqs.storage;

import java.io.Serializable;

final class ManagedReferenceImpl implements Serializable, ManagedReference
{

private static final long serialVersionUID = 1L;

private final Long id;

public ManagedReferenceImpl(Long id)
{ this.id = id; }

Long getId()
{ return id; }

@Override
public <T> T get(Class<T> type)
{ return Context.getTransaction().get(id, type); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof ManagedReferenceImpl)) return false;
	ManagedReferenceImpl m = (ManagedReferenceImpl) o;
	return id.equals(m.id);
}

@Override
public int hashCode()
{ return id.hashCode(); }

}
