package net.cqs.engine;

import java.io.Serializable;

import net.cqs.config.BuildingEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.engine.base.UnitMap;
import net.cqs.util.EnumIntMap;

public final class PlayerStartConfiguration implements Serializable
{

private static final long serialVersionUID = 1L;

public static final PlayerStartConfiguration NOTHING = new PlayerStartConfiguration();

private final EnumIntMap<ResearchEnum> research;
private final int colonies;
private final int population;
private final EnumIntMap<BuildingEnum> buildings;
private final UnitMap units;

public PlayerStartConfiguration()
{
	this.research = EnumIntMap.of(ResearchEnum.class);
	this.colonies = 0;
	this.population = 0;
	this.buildings = EnumIntMap.of(BuildingEnum.class);
	this.units = new UnitMap();
}

public PlayerStartConfiguration(EnumIntMap<ResearchEnum> research)
{
	this.research = EnumIntMap.copyOf(research);
	this.colonies = 0;
	this.population = 0;
	this.buildings = EnumIntMap.of(BuildingEnum.class);
	this.units = new UnitMap();
}

public PlayerStartConfiguration(EnumIntMap<ResearchEnum> research, int colonies, int population,
		EnumIntMap<BuildingEnum> buildings, UnitMap units)
{
	this.research = EnumIntMap.copyOf(research);
	this.colonies = colonies;
	this.population = population;
	this.buildings = EnumIntMap.copyOf(buildings);
	this.units = UnitMap.copyOf(units);
}

public EnumIntMap<ResearchEnum> getResearch()
{ return EnumIntMap.copyOf(research); }

public int getColonies()
{ return colonies; }

public int getPopulation()
{ return population; }

public EnumIntMap<BuildingEnum> getBuildings()
{ return EnumIntMap.copyOf(buildings); }

public UnitMap getUnits()
{ return UnitMap.copyOf(units); }

}
