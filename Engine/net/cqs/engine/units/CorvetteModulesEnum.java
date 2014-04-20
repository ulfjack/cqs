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

public enum CorvetteModulesEnum implements UnitModule
{

SPEED(ModuleNameEnum.SPACE_SPEED,
		new Cost(new int[] {    0,    0, 10000, 12000}, 15, 0, 10000, 429L),
		new FightSpec(0,     0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		ResearchEnum.SPACE_SPEED, 80,
		EducationEnum.SPEED),

ATTACK(ModuleNameEnum.SPACE_ATTACK,
		new Cost(new int[] {10000, 7000,   500,   500}, 15, 0, 10000, 429L),
		new FightSpec(0,     0, new float[] {106.25f, 212.5f, 425.0f, 212.5f}),
		ResearchEnum.DESINTEGRATOR, ResearchEnum.DESINTEGRATOR.getMax(),
		EducationEnum.WEAPONS),

DEFENSE(ModuleNameEnum.SPACE_DEFENSE,
		new Cost(new int[] { 8000, 8000,   500,   500}, 15, 0, 10000, 429L),
		new FightSpec(701.25f, 0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		ResearchEnum.MAGNETIC_FIELD, 48,
		EducationEnum.ARMOR),

WARP(ModuleNameEnum.SPACE_WARP,
		new Cost(new int[] {    0,    0,  8000, 15000}, 5, 0, 10000, 429L),
		new FightSpec(0,     0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}), UnitSpecial.WARP,
		ResearchEnum.WARP_ENGINE, 80,
		EducationEnum.WARP_ENGINE);

private final ModuleNameEnum names;
private final Cost cost;
private final FightSpec spec;
private final UnitSpecial[] specials;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum education;
private final String imageName;

private CorvetteModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec, ResearchEnum dep, int depcount,
		EducationEnum education)
{ this(names, cost, spec, null, dep, depcount, education); }

private CorvetteModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec, UnitSpecial special,
		ResearchEnum dep, int depcount, EducationEnum education)
{
	if (dep == null) throw new NullPointerException();
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
