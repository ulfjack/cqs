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

public enum DestroyerModulesEnum implements UnitModule
{

SPEED(ModuleNameEnum.SPACE_SPEED,
		new Cost(new int[] {    0,     0, 10000, 30000}, 40, 0, 50000, 1214L),
		new FightSpec(0.0f, 0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		ResearchEnum.SPACE_SPEED, ResearchEnum.SPACE_SPEED.getMax(),
		EducationEnum.SPEED),

ATTACK(ModuleNameEnum.SPACE_ATTACK,
		new Cost(new int[] {70000, 30000,  2000,  2000}, 40, 0, 50000, 1214L),
		new FightSpec(0.0f, 0, new float[] {1270.0f, 317.5f, 635.0f, 317.5f}),
		ResearchEnum.TRANSFORMER, ResearchEnum.TRANSFORMER.getMax(),
		EducationEnum.WEAPONS),

DEFENSE(ModuleNameEnum.SPACE_DEFENSE,
		new Cost(new int[] {50000, 50000,  2000,  2000}, 40, 0, 50000, 1214L),
		new FightSpec(2095.5f, 0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		ResearchEnum.MAGNETIC_FIELD, ResearchEnum.MAGNETIC_FIELD.getMax(),
		EducationEnum.ARMOR),

WARP(ModuleNameEnum.SPACE_WARP,
		new Cost(new int[] {    0, 10000, 20000, 20000}, 15, 0, 50000, 1214L),
		new FightSpec(0.0f, 0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}), UnitSpecial.WARP,
		ResearchEnum.WARP_ENGINE, ResearchEnum.WARP_ENGINE.getMax(),
		EducationEnum.WARP_ENGINE);

private final ModuleNameEnum names;
private final Cost cost;
private final FightSpec spec;
private final UnitSpecial[] specials;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum education;
private final String imageName;

private DestroyerModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec, ResearchEnum dep, int depcount,
		EducationEnum education)
{ this(names, cost, spec, null, dep, depcount, education); }

private DestroyerModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec, UnitSpecial special, ResearchEnum dep, int depcount,
		EducationEnum education)
{
	this.names = names;
	this.cost = cost;
	this.spec = spec;
	if (special != null)
		this.specials = new UnitSpecial[] { special };
	else
		this.specials = new UnitSpecial[0];
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
{ return specials; }

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
