package net.cqs.web.admin.auth;

import java.io.Serializable;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;

public final class AllowGroupRule implements Rule, Serializable
{

private static final long serialVersionUID = 1L;

private final String name;

public AllowGroupRule(String name)
{
	if (name == null) throw new NullPointerException();
	this.name = name;
}

public String getName()
{ return name; }

@Override
public boolean check(GroupProvider provider, Identity identity)
{ return provider.isInGroup(identity, name); }

@Override
public String toString()
{ return "allow group "+name; }

@Override
public int hashCode()
{ return 100*name.hashCode(); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof AllowGroupRule)) return false;
	AllowGroupRule r = (AllowGroupRule) o;
	return name.equals(r.name);
}

}