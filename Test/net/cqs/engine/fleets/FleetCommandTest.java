package net.cqs.engine.fleets;

import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyBuilder;

import org.junit.Before;

abstract class FleetCommandTest
{

protected Galaxy galaxy;

@Before
public void setUp()
{
	galaxy = GalaxyBuilder.buildTestGalaxy();
}

}
