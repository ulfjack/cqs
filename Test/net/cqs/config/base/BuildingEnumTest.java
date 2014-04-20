package net.cqs.config.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.cqs.config.BuildingEnum;
import net.cqs.config.ResourceEnum;
import net.cqs.engine.base.Cost;

import org.junit.Test;

public class BuildingEnumTest {

@Test
public void testGetRemovalCost()
{
	for(BuildingEnum building : BuildingEnum.values())
	{
		Cost cost = building.getRemovalCost();
		assertEquals(0, cost.getAdministrativeJobs());
		assertEquals(0, cost.getConstructionJobs());
		assertEquals(0, cost.getPopulation());
		assertEquals(0, cost.getResource(ResourceEnum.STEEL));
		assertEquals(0, cost.getResource(ResourceEnum.OIL));
		assertEquals(0, cost.getResource(ResourceEnum.SILICON));
		assertEquals(0, cost.getResource(ResourceEnum.DEUTERIUM));
		assertTrue(cost.getTime() > 0);
	}
}
	
	
}
