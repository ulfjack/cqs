package net.cqs.engine.units;

import static org.junit.Assert.assertEquals;

import net.cqs.config.units.UnitModule;

import org.junit.Test;

public class UnitEnumTest
{

@Test
public void testFightCount()
{
	for (UnitEnum e: UnitEnum.values())
	{
		if (e.isPlanetary())
			assertEquals(3, e.getFightSpec().groupCount());
		else
			assertEquals(4, e.getFightSpec().groupCount());
	}
}

@Test
public void testResCapa()
{
	for (UnitEnum e: UnitEnum.values())
	{
		if (e.equals(UnitEnum.TRUCK))
		{
			assertEquals(15000, e.getResourceCapacity());
		}
		else
		{
			boolean hasResModule = false;
			UnitModule[] modules = e.getUnitModules();
			for (int i = 0; i < modules.length; i++)
			{
				if (modules[i].equals(CivilianModulesEnum.RES_TRANSPORT))
				{
					hasResModule = true;
					break;
				}
			}
			if (!hasResModule)
				assertEquals(0, e.getResourceCapacity());
			else
				assertEquals(1500000-375000*(modules.length-1), e.getResourceCapacity());
		}
	}
}

@Test
public void testTroopCapa()
{
	for (UnitEnum e: UnitEnum.values())
	{
		boolean hasTroopModule = false;
		UnitModule[] modules = e.getUnitModules();
		for (int i = 0; i < modules.length; i++)
		{
			if (modules[i].equals(CivilianModulesEnum.TROOP_TRANSPORT))
			{
				hasTroopModule = true;
				break;
			}
		}
		if (!hasTroopModule)
			assertEquals(0, e.getGroundUnitCapacity());
		else
			assertEquals(750-190*(modules.length-1), e.getGroundUnitCapacity());
	}
}


}
