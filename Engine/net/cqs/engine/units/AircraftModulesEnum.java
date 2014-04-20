package net.cqs.engine.units;

import java.util.Locale;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.FightSpec;
import net.cqs.config.units.UnitModule;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;

public enum AircraftModulesEnum implements UnitModule
{

SPEED(ModuleNameEnum.SURFACE_SPEED,
		new Cost(new int[] {0,   100,  300,  500}, 8, 0, 100, 143L),
		new FightSpec(0.0f,  0.0f,  new float[] {0.0f, 0.0f, 0.0f}),
		ResearchEnum.PLANETARY_SPEED, ResearchEnum.PLANETARY_SPEED.getMax(),
		EducationEnum.SPEED,
		"speed"),

ATTACK(ModuleNameEnum.SURFACE_ATTACK,
		new Cost(new int[] {300, 900,  0,  0}, 8, 0, 100, 143L),
		new FightSpec(0.0f,  0.3125f, new float[] {43.75f, 175.0f, 87.5f}),
		ResearchEnum.BOMB, ResearchEnum.BOMB.getMax(),
		EducationEnum.WEAPONS,
		"bomb"),

DEFENSE(ModuleNameEnum.SURFACE_DEFENSE,
		new Cost(new int[] {600, 600,  0,  0}, 8, 0, 100, 143L),
		new FightSpec(288.75f, 0.0f,  new float[] {0.0f, 0.0f, 0.0f}),
		ResearchEnum.PLATING, ResearchEnum.PLATING.getMax(),
		EducationEnum.ARMOR,
		"plating");

private final ModuleNameEnum names;
private final Cost cost;
private final FightSpec spec;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum education;
private final String imageName;

private AircraftModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec,
		ResearchEnum dep, int depcount, EducationEnum education, String imageName)
{
	this.names = names;
	this.imageName = imageName;
	this.cost = cost;
	this.spec = spec;
	if (depcount < 0) depcount = dep.getMax();
	this.dependencies.set(dep, depcount);
	UnitHelper.transitiveDependencies(this.dependencies);
	this.education = education;
}

@Override
public int size()
{ return 1; }

@Override
public UnitSpecial[] specials()
{ return new UnitSpecial[0]; }

@Override
public Cost getCost()
{ return cost; }

@Override
public FightSpec getFightSpec()
{ return spec; }

@Override
public DependencyList<ResearchEnum> getResearchDependencies()
{ return dependencies; }

@Override
public EducationEnum getEducation()
{ return education; }

@Override
public String getName(Locale locale)
{ return names.getName(locale); }

@Override @HtmlFragment
public String getDescription(Locale locale)
{ return names.getDescription(locale); }

@Override
public String getImageName()
{ return imageName; }

}
