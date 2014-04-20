package net.cqs.web.admin.auth;

import java.io.Serializable;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;

public final class AllowPersonRule implements Rule, Serializable
{

private static final long serialVersionUID = 1L;

private final String name;

public AllowPersonRule(String name)
{
	if (name == null) throw new NullPointerException();
	this.name = name;
}

public String getName()
{ return name; }

@Override
public boolean check(GroupProvider provider, Identity identity)
{ return name.equals(identity.getName()); }

@Override
public String toString()
{ return "allow person "+name; }

@Override
public int hashCode()
{ return name.hashCode(); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof AllowPersonRule)) return false;
	AllowPersonRule r = (AllowPersonRule) o;
	return name.equals(r.name);
}

}