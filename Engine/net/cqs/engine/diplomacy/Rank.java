package net.cqs.engine.diplomacy;

import java.io.Serializable;
import java.util.EnumSet;

import net.cqs.config.RightEnum;

public final class Rank implements Serializable
{

private static final long serialVersionUID = 1L;

public static final Rank NONE = new Rank("NONE", EnumSet.noneOf(RightEnum.class));

private String name;
private EnumSet<RightEnum> allianceRights;

public Rank(String name, EnumSet<RightEnum> allianceRights)
{
	this.name = name;
	this.allianceRights = allianceRights;
}

public void set(String newName, EnumSet<RightEnum> newAllianceRights)
{
	this.name = newName;
	this.allianceRights = newAllianceRights.clone();
}

public String getName()
{ return name; }

public boolean hasAllianceRight(RightEnum right)
{ return allianceRights.contains(right); }

public boolean hasRight(RightEnum right)
{ return allianceRights.contains(right); }

}
