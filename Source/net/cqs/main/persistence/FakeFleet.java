package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.engine.base.UnitMap;

public final class FakeFleet implements Serializable
{

private static final long serialVersionUID = 1L;

public String id;
public String name;
public FakePlayer owner;
public UnitMap joinUnits;

public String getName()
{ return name; }

public String getId()
{ return id; }

public FakePlayer getOwner()
{ return owner; }

public UnitMap getUnits()
{ return joinUnits; }

}
