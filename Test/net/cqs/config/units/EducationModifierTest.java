package net.cqs.config.units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.cqs.config.EducationEnum;
import net.cqs.config.Resource;
import net.cqs.config.ResourceEnum;
import net.cqs.engine.base.Cost;
import net.cqs.engine.units.CorvetteModulesEnum;
import net.cqs.engine.units.UnitClassEnum;
import net.cqs.engine.units.UnitEnum;
import net.cqs.util.EnumIntMap;

import org.junit.Before;
import org.junit.Test;

public class EducationModifierTest
{

final int CORVETTE_LEVEL = 50;
final int RESTRANSPORT_LEVEL = 25;
final int ARMOR_LEVEL = 200;
final int WEAPONS_LEVEL = 100;

EnumIntMap<EducationEnum> education;
EducationModifier modifier;

@Before
public void setUp()
{
	education = EnumIntMap.of(EducationEnum.class);
	for (int i = 0; i < CORVETTE_LEVEL; i++)
		education.increase(EducationEnum.CORVETTE);
	for (int i = 0; i < RESTRANSPORT_LEVEL; i++)
		education.increase(EducationEnum.RESSOURCENTRANSPORT);
	for (int i = 0; i < WEAPONS_LEVEL; i++)
		education.increase(EducationEnum.WEAPONS);
	for (int i = 0; i < ARMOR_LEVEL; i++)
		education.increase(EducationEnum.ARMOR);
	
	modifier = new EducationModifier();
}

public void setUp2(UnitEnum unit, EducationEnum[] edu, UnitClassEnum uclass,
		UnitModule[] umodules, Cost cost)
{
	if (!unit.isDesign()) // special unit
	{
		for (int i = 0; i < edu.length; i++)
		{
			float[] unitfactors = new float[4];
			for (int j = 0; j < unitfactors.length; j++)
				unitfactors[j] = edu[i].getModifier(j);
			modifier.addEducation(edu[i], unitfactors);
		}
	}
	else // module unit
	{
		float[] classfactors = new float[4];
		Cost ccost = uclass.getCost();
		EducationEnum classedu = uclass.getEducation();
		if (classedu != null)
		{
			for (int i = 0; i < classfactors.length; i++)
			{
				float factor = classedu.getModifier(i);
				classfactors[i] = (factor*ccost.getResource(i)/cost.getResource(i));
			}
			modifier.addEducation(classedu, classfactors);
		}
		
		for (int i = 0; i < umodules.length; i++)
		{
			float[] modulefactors = new float[4];
			Cost mcost = umodules[i].getCost();
			EducationEnum moduleedu = umodules[i].getEducation();
			for (int j = 0; j < modulefactors.length; j++)
			{
				float factor = moduleedu.getModifier(j);
				modulefactors[j] = (factor*mcost.getResource(j)/cost.getResource(j));
			}
			modifier.addEducation(moduleedu, modulefactors);
		}
	}
}

@Test
public void testAddEducationSimple()
{
	UnitEnum unit = UnitEnum.CORVETTE;
	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CORVETTE;
	UnitModule[] umodules = unit.getUnitModules();
	Cost cost = unit.getCost();
	setUp2(unit, edu, uclass, umodules, cost);
	assertSame(EducationEnum.CORVETTE, modifier.education[0]);
}

@Test
public void testAddEducation()
{
	UnitEnum unit = UnitEnum.CORVETTE_ATTACK_ATTACK;
	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CORVETTE;
	UnitModule[] umodules = unit.getUnitModules();
	Cost cost = unit.getCost();
	setUp2(unit, edu, uclass, umodules, cost);
	assertSame(EducationEnum.CORVETTE, modifier.education[0]);
	assertSame(EducationEnum.WEAPONS, modifier.education[1]);
	assertSame(EducationEnum.WEAPONS, modifier.education[2]);
}

@Test
public void testAddEducation2()
{
	UnitEnum unit = UnitEnum.FREIGHTER_WARP;
	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CIVILIAN;
	UnitModule[] umodules = unit.getUnitModules();
	Cost cost = unit.getCost();
	setUp2(unit, edu, uclass, umodules, cost);
	assertSame(EducationEnum.RESSOURCENTRANSPORT, modifier.education[0]);
	assertSame(EducationEnum.WARP_ENGINE, modifier.education[1]);
}

@Test
public void testModifySimple()
{
	Cost cost1 = UnitEnum.CORVETTE.getCost();
	Cost cost2 = UnitEnum.CORVETTE.getCost();
	cost2 = modifier.modify(cost2, education);
	for (ResourceEnum i : ResourceEnum.values())
		if (i != ResourceEnum.MONEY)
			assertEquals(cost1.getResource(i), cost2.getResource(i));
}

@Test
public void testModify()
{
	UnitEnum unit = UnitEnum.CORVETTE;
	Cost cost = unit.getCost();
	
	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CORVETTE;
	UnitModule[] umodules = unit.getUnitModules();
	setUp2(unit, edu, uclass, umodules, unit.getCost());
	
	// calculate expected costs
	float factor = EducationModifier.factor(education.get(EducationEnum.CORVETTE));
	int[] ecost = new int[Resource.MAX+1];
	for (int i = 0; i <= Resource.MAX; i++)
		ecost[i] = (int) (cost.getResource(i)*(1-factor*modifier.modifiers[0][i]));
	
	cost = modifier.modify(cost, education);
	for (ResourceEnum i : ResourceEnum.values())
		if (i != ResourceEnum.MONEY)
			assertEquals(ecost[i.index()], cost.getResource(i));	
}

private boolean aboutEqual(int a, int b, int range)
{
	if (Math.abs(a-b) < range)
		return true;
	return false;
}

@Test
public void testModify2()
{
	UnitEnum unit = UnitEnum.CORVETTE_ATTACK_ATTACK;
	Cost cost1 = unit.getCost();

	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CORVETTE;
	UnitModule[] umodules = unit.getUnitModules();
	Cost cost = unit.getCost();
	setUp2(unit, edu, uclass, umodules, cost);

	Cost cost2class = uclass.getCost();
	Cost[] cost2modules = new Cost[umodules.length];
	for (int i = 0; i < cost2modules.length; i++)
		cost2modules[i] = umodules[i].getCost();
	
	// calculate expected costs
	int[] rcost = new int[Resource.MAX+1];
	
	float classfactor = EducationModifier.factor(education.get(EducationEnum.CORVETTE));
	assertSame(EducationEnum.CORVETTE, modifier.education[0]);
	for (int i = 0; i < 4; i++)
		rcost[i] = (int) (cost2class.getResource(i)*(1-classfactor*EducationEnum.CORVETTE.getModifier(i)));
	for (int i = 0; i < cost2modules.length; i++)
	{
		float modulefactor = EducationModifier.factor(education.get(umodules[i].getEducation()));
		assertSame(CorvetteModulesEnum.ATTACK, umodules[i]);
		assertSame(EducationEnum.WEAPONS, modifier.education[i+1]);
		for (int j = 0; j < 4; j++)
			rcost[j] += cost2modules[i].getResource(j)*(1-modulefactor*EducationEnum.WEAPONS.getModifier(j));		
	}
	
//	Cost cost2 = cost2class.clone();
//	for (int i = 0; i < cost2modules.length; i++)
//		cost2 = Cost.sum(cost2, cost2modules[i]);
	
	cost1 = modifier.modify(cost1, education);
	for (ResourceEnum i : ResourceEnum.values())
		if (i != ResourceEnum.MONEY)
			assertTrue(aboutEqual(rcost[i.index()], cost1.getResource(i), 3));	
}

@Test
public void testModify3()
{
	UnitEnum unit = UnitEnum.CORVETTE_ATTACK_DEFENSE;
	Cost cost1 = unit.getCost();

	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CORVETTE;
	UnitModule[] umodules = unit.getUnitModules();
	Cost cost = unit.getCost();
	setUp2(unit, edu, uclass, umodules, cost);
	
	Cost cost2class = uclass.getCost();
	Cost[] cost2modules = new Cost[umodules.length];
	for (int i = 0; i < cost2modules.length; i++)
		cost2modules[i] = umodules[i].getCost();
	
	// calculate expected costs
	int[] rcost = new int[Resource.MAX+1];
	
	float classfactor = EducationModifier.factor(education.get(EducationEnum.CORVETTE));
	assertSame(EducationEnum.CORVETTE, modifier.education[0]);
	for (int i = 0; i < 4; i++)
		rcost[i] = (int) (cost2class.getResource(i)*(1-classfactor*EducationEnum.CORVETTE.getModifier(i)));
	for (int i = 0; i < cost2modules.length; i++)
	{
		float modulefactor = EducationModifier.factor(education.get(umodules[i].getEducation()));
		for (int j = 0; j < 4; j++)
			rcost[j] += cost2modules[i].getResource(j)*(1-modulefactor*modifier.education[i+1].getModifier(j));		
	}
	
//	Cost cost2 = cost2class.clone();
//	for (int i = 0; i < cost2modules.length; i++)
//		cost2 = Cost.sum(cost2, cost2modules[i]);
	
	cost1 = modifier.modify(cost1, education);
	for (ResourceEnum i : ResourceEnum.values())
		if (i != ResourceEnum.MONEY)
			assertTrue(aboutEqual(rcost[i.index()], cost1.getResource(i), 3));	
}

@Test
public void testModifySpecialUnit()
{
	UnitEnum unit = UnitEnum.FREIGHTER_WARP;
	Cost cost1 = unit.getCost();
	
	EducationEnum[] edu = unit.getEducation();
	UnitClassEnum uclass = UnitClassEnum.CIVILIAN;
	UnitModule[] umodules = unit.getUnitModules();
	Cost cost = unit.getCost();
	setUp2(unit, edu, uclass, umodules, cost);
	
	
	// calculate expected costs
	float mods[] = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
	for (int i = 0; i < edu.length; i++)
	{
		float factor = EducationModifier.factor(education.get(edu[i]));
		for (int j = 0; j < mods.length; j++)
			mods[j] += factor*edu[i].getModifier(j);
	}
	int[] rcost = new int[Resource.MAX+1];
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		rcost[i] = (int) (cost1.getResource(i)*(1-mods[i]));
	
	cost1 = modifier.modify(cost1, education);
	for (ResourceEnum i : ResourceEnum.values())
		if (i != ResourceEnum.MONEY)
			assertTrue(""+rcost[i.index()]+" "+cost1.getResource(i), aboutEqual(rcost[i.index()], cost1.getResource(i), 3));
}

}
