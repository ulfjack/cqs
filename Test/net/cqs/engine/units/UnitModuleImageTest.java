package net.cqs.engine.units;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.cqs.config.ResearchEnum;
import net.cqs.config.units.UnitModule;
import net.cqs.i18n.EnumTestHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class UnitModuleImageTest
{

@Parameters
public static Collection<?> data()
{
	Collection<Object[]> result = new ArrayList<Object[]>();
	result.addAll(EnumTestHelper.data(AircraftModulesEnum.class));
	result.addAll(EnumTestHelper.data(CivilianModulesEnum.class));
	result.addAll(EnumTestHelper.data(CorvetteModulesEnum.class));
	result.addAll(EnumTestHelper.data(DestroyerModulesEnum.class));
	result.addAll(EnumTestHelper.data(FighterModulesEnum.class));
	result.addAll(EnumTestHelper.data(InfantryModulesEnum.class));
	result.addAll(EnumTestHelper.data(VehicleModulesEnum.class));
	return result;
}

private static final HashSet<String> RESEARCH_NAMES = new HashSet<String>();
static {
	for (ResearchEnum r : ResearchEnum.values())
		RESEARCH_NAMES.add(r.getImageName());
	RESEARCH_NAMES.add("restransport");
	RESEARCH_NAMES.add("transporter");
}

private final UnitModule o;

public UnitModuleImageTest(UnitModule o)
{
	this.o = o;
}

@Test
public void test()
{
	String s = o.getImageName();
	assertTrue(s, RESEARCH_NAMES.contains(s));
}

}
