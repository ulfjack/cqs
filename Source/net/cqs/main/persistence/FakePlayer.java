package net.cqs.main.persistence;

import java.io.Serializable;

public final class FakePlayer implements Serializable
{

private static final long serialVersionUID = 1L;

public String name;
public String tag;
public long numFleets = 0;
public long numUnits = 0;

public FakePlayer()
{/*OK*/}

public String getName()
{ return name; }

public String getAllianceTag()
{ return tag; }

}
