package net.cqs.main.setup;

import static net.cqs.config.BuildingEnum.DEUTERIUM_DEPOT;
import static net.cqs.config.BuildingEnum.INFRASTRUCTURE;
import static net.cqs.config.BuildingEnum.MILITARY_BASE;
import static net.cqs.config.BuildingEnum.OIL_TANKS;
import static net.cqs.config.BuildingEnum.PROCESSING_PLANT;
import static net.cqs.config.BuildingEnum.REFINERY;
import static net.cqs.config.BuildingEnum.RESIDENCE;
import static net.cqs.config.BuildingEnum.SHIPYARD;
import static net.cqs.config.BuildingEnum.SILICON_DEPOT;
import static net.cqs.config.BuildingEnum.SILICON_FOUNDRY;
import static net.cqs.config.BuildingEnum.SPACEPORT;
import static net.cqs.config.BuildingEnum.STEEL_DEPOT;
import static net.cqs.config.BuildingEnum.STEEL_MILL;
import static net.cqs.config.BuildingEnum.TRADE_CENTER;
import static net.cqs.config.BuildingEnum.UNIVERSITY;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.cqs.config.BuildingEnum;
import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyBuilder;
import net.cqs.engine.PlayerStartConfiguration;
import net.cqs.engine.actions.AICreateAction;
import net.cqs.engine.actions.PlayerCreateAction;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.units.UnitSystemImpl;
import net.cqs.main.setup.GameConfiguration.PlayerDescription;
import net.cqs.storage.Storage;
import net.cqs.util.EnumIntMap;

public final class GalaxyCreator
{

public GalaxyCreator()
{/*OK*/}

public Galaxy createGalaxy(Storage galaxyControl, GameConfiguration.GalaxyDescription description)
{
	return createGalaxy(galaxyControl, description, Collections.<PlayerDescription>emptyList());
}

public Galaxy createGalaxy(Storage galaxyControl, GameConfiguration.GalaxyDescription description,
		List<PlayerDescription> players)
{
	Random rand = new Random();
	PlayerStartConfiguration startConfiguration;
	GameConfiguration.PlayerStartConfiguration start = description.getStartConfiguration();
	if (start != null)
	{
		EnumIntMap<ResearchEnum> research = EnumIntMap.of(ResearchEnum.class);
		for (ResearchEnum r : ResearchEnum.values())
			research.set(r, Math.min(start.getResearch(), r.getMax()));
		int population = 0;
		EnumIntMap<BuildingEnum> buildings = EnumIntMap.of(BuildingEnum.class);
		UnitMap units = new UnitMap();
		if (start.getColonies() > 0)
		{
			population = 700000;
			for (BuildingEnum building : new BuildingEnum[] {STEEL_MILL, REFINERY, SILICON_FOUNDRY, PROCESSING_PLANT})
				buildings.set(building, 50);
			buildings.set(TRADE_CENTER, 100);
			buildings.set(INFRASTRUCTURE, 100);
			buildings.set(RESIDENCE, 20);
			for (BuildingEnum building : new BuildingEnum[] {STEEL_DEPOT, OIL_TANKS, SILICON_DEPOT, DEUTERIUM_DEPOT})
				buildings.set(building, 50);
			buildings.set(MILITARY_BASE, 1);
			buildings.set(SHIPYARD, 1);
			buildings.set(SPACEPORT, 1);
			//TRANSMITTER
			//RADIO_TELESCOPE
			buildings.set(UNIVERSITY, 100);
			//LIBRARY
			
			for (Unit unit : UnitSystemImpl.getImplementation().getDefaultUnits())
				units.set(unit, start.getUnits());
			for (Unit unit : UnitSystemImpl.getImplementation().getUniqueUnits())
				units.set(unit, start.getUnits());
		}
		startConfiguration = new PlayerStartConfiguration(research, start.getColonies(), population, buildings, units);
	}
	else
		startConfiguration = PlayerStartConfiguration.NOTHING;
	Galaxy galaxy = GalaxyBuilder.buildGalaxy(galaxyControl, rand, description.getSystemCount(), startConfiguration);
	
	for (PlayerDescription p : players)
	{
		PlayerCreateAction action = new PlayerCreateAction(p.getId(), p.getName(),
				p.getLanguage(), p.isRestricted());
		galaxy.schedex(action);
	}
	
	for (int i = 0; i < description.getAnonymousAiPlayers(); i++)
	{
		String name = "KI-"+i;
		galaxy.schedex(new AICreateAction(name, Locale.GERMANY));
	}
	
	try
	{
		galaxy.check();
	}
	catch (InvalidDatabaseException e)
	{ throw new RuntimeException(e); }
	
	return galaxy;
}

}
