package net.cqs.auth;

import java.io.Serializable;

public final class Identity implements Serializable
{

private static final long serialVersionUID = 1L;

private final String id;

public Identity(String id)
{
	this.id = id;
}

public String getName()
{ return id; }

@Override
public String toString()
{ return id; }

@Override
public int hashCode()
{ return id.hashCode(); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof Identity)) return false;
	Identity i = (Identity) o;
	return id.equals(i.id);
}

}