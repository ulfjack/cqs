package net.cqs.engine;

import static net.cqs.config.PlanetEnum.ASTEROIDS;
import static net.cqs.config.PlanetEnum.DESERTWORLD;
import static net.cqs.config.PlanetEnum.GASGIANT;
import static net.cqs.config.PlanetEnum.GASPLANET;
import static net.cqs.config.PlanetEnum.JUNGLEWORLD;
import static net.cqs.config.PlanetEnum.PLANETOIDS;
import static net.cqs.config.PlanetEnum.TERRAWORLD;
import static net.cqs.config.PlanetEnum.WASTELAND;
import static net.cqs.config.PlanetEnum.WATERWORLD;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import net.cqs.config.PlanetEnum;
import net.cqs.storage.Storage;
import net.cqs.storage.memory.MemoryStorageManager;

public final class GalaxyBuilder
{

private final Random rand;
private final int systems;

private GalaxyBuilder(Random rand, int systems)
{
	this.rand = rand;
	this.systems = systems;
}

private Planet createPlanet(SolarSystem s, PlanetEnum planet, int index, int radius)
{
	int theta = rand.nextInt((int) Math.round(2*3.14159f*SolarSystem.thetaFactor));
	return new Planet(s, index, planet, rand, radius, theta);
}

private PlanetEnum choosePlanetEnum(PlanetEnum... choices)
{ return choices[rand.nextInt(choices.length)]; }

public Planet[] createPlanets(SolarSystem system)
{
	PlanetEnum[] innerPs = new PlanetEnum[9];
	for (int i = 0; i < 2; i++)
		innerPs[i] = PlanetEnum.WASTELAND;
	for (int i = 2; i < 5; i++)
		innerPs[i] = PlanetEnum.DESERTWORLD;
	for (int i = 5; i < 7; i++)
		innerPs[i] = choosePlanetEnum(JUNGLEWORLD, TERRAWORLD, WATERWORLD);
	for (int i = 7; i < innerPs.length; i++)
		innerPs[i] = choosePlanetEnum(JUNGLEWORLD, TERRAWORLD, WATERWORLD, WASTELAND, DESERTWORLD);
	
	PlanetEnum[] middlePs = new PlanetEnum[4];
	for (int i = 0; i < 2; i++)
		middlePs[i] = PlanetEnum.ASTEROIDS;
	middlePs[2] = PlanetEnum.PLANETOIDS;
	for (int i = 3; i < middlePs.length; i++)
		middlePs[i] = choosePlanetEnum(ASTEROIDS, ASTEROIDS, PLANETOIDS);
	
	PlanetEnum[] outerPs = new PlanetEnum[5];
	for (int i = 0; i < 3; i++)
		outerPs[i] = PlanetEnum.GASGIANT;
	outerPs[3] = PlanetEnum.GASPLANET;
	for (int i = 4; i < outerPs.length; i++)
		outerPs[i] = choosePlanetEnum(GASGIANT, GASPLANET);

	Collections.shuffle(Arrays.asList(innerPs));
	Collections.shuffle(Arrays.asList(middlePs));
	Collections.shuffle(Arrays.asList(outerPs));
	
	int[] innerRadius = {34, 47, 60, 73, 86, 99, 112, 125, 138};
	int[] middleRadius = {151, 151, 151, 151};
	int[] outerRadius = {168, 189, 211, 233, 255, 277};

	Planet[] ps = new Planet[innerPs.length+middlePs.length+outerPs.length];
	for (int i = 0; i < innerPs.length; i++)
		ps[i] = createPlanet(system, innerPs[i], i, innerRadius[i]);
	int c = innerPs.length;
	for (int i = 0; i < middlePs.length; i++)
		ps[c+i] = createPlanet(system, middlePs[i], c+i, middleRadius[i]);
	c += middlePs.length;
	for (int i = 0; i < outerPs.length; i++)
		ps[c+i] = createPlanet(system, outerPs[i], c+i, outerRadius[i]);
		
	return ps;
}

public SolarSystem[] createSystems(Galaxy galaxy)
{
	SolarSystem[] sss = new SolarSystem[systems];
	for (int i = 0; i < sss.length; i++)
		sss[i] = new SolarSystem(this, galaxy, i);
	return sss;
}

private Galaxy createGalaxy(Storage galaxyControl, PlayerStartConfiguration startConfiguration)
{
	return new Galaxy(galaxyControl, this, rand, startConfiguration);
}

public Galaxy build(Storage galaxyControl, PlayerStartConfiguration startConfiguration)
{
	return createGalaxy(galaxyControl, startConfiguration);
}


// Factory methods:
public static Galaxy buildGalaxy(Storage galaxyControl, Random rand, int systems, PlayerStartConfiguration startConfiguration)
{
	GalaxyBuilder builder = new GalaxyBuilder(rand, systems);
	return builder.build(galaxyControl, startConfiguration);
}

public static Galaxy buildTestGalaxy(PlayerStartConfiguration startConfiguration)
{
	Random rand = new Random(1234);
	return buildGalaxy(new MemoryStorageManager(), rand, 2, startConfiguration);
}

public static Galaxy buildTestGalaxy()
{
	Random rand = new Random(1234);
	return buildGalaxy(new MemoryStorageManager(), rand, 2, PlayerStartConfiguration.NOTHING);
}

}
