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

public enum InfantryModulesEnum implements UnitModule
{

SPEED(ModuleNameEnum.SURFACE_SPEED,
		new Cost(new int[] {0,   50,  50, 100}, 1, 0, 100, 29L),
		new FightSpec(0.0f,  0.0f,  new float[] {0.0f, 0.0f, 0.0f}),
		ResearchEnum.PLANETARY_SPEED, 1,
		EducationEnum.SPEED),

ATTACK(ModuleNameEnum.SURFACE_ATTACK,
		new Cost(new int[] {100, 150, 0,  0}, 1,   0, 100, 29L),
		new FightSpec(0.0f, 0.125f, new float[] {18.75f, 9.375f, 37.5f}),
		ResearchEnum.TOMMY_GUN, ResearchEnum.TOMMY_GUN.getMax(),
		EducationEnum.WEAPONS),

DEFENSE(ModuleNameEnum.SURFACE_DEFENSE,
		new Cost(new int[] {200, 50,  0,  0}, 1,   0, 100, 29L),
		new FightSpec(61.875f, 0.0f,  new float[] {0.0f, 0.0f, 0.0f}),
		ResearchEnum.PLATING, 1,
		EducationEnum.ARMOR);

private final ModuleNameEnum names;
private final Cost cost;
private final FightSpec spec;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum education;
private final String imageName;

private InfantryModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec,
		ResearchEnum dep, int depcount, EducationEnum education)
{
	this.names = names;
	this.cost = cost;
	this.spec = spec;
	if (depcount < 0) depcount = dep.getMax();
	this.dependencies.set(dep, depcount);
	UnitHelper.transitiveDependencies(this.dependencies);
	this.education = education;
	this.imageName = dep.getImageName();
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
