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

public enum CivilianModulesEnum implements UnitModule
{
	
/*
 * The order of modules has to be as follows:
 * 1st: speed
 * 2nd: attack (does not exist, so use transport module)
 * 3rd: defense
 * 4th: warp
 * 5th etc: all others (other transport module here)	
 */

SPEED(ModuleNameEnum.SPACE_SPEED,
		new Cost(new int[] {    0,    0, 10000, 12000}, 15, 0, 10000, 3000L),
		new FightSpec(0,     0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		ResearchEnum.SPACE_SPEED, 80,
		EducationEnum.SPEED),

RES_TRANSPORT(ModuleNameEnum.RES_TRANSPORT,
		new Cost(new int[] {    0,    0,    0,    0},  0, 0,      0,     0L),
		new FightSpec(0,     0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		EducationEnum.RESSOURCENTRANSPORT,
		"restransport"),

DEFENSE(ModuleNameEnum.SPACE_DEFENSE,
		new Cost(new int[] { 8000, 8000,   500,   500}, 15, 0, 10000, 3000L),
		new FightSpec(450.0f, 0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		ResearchEnum.MAGNETIC_FIELD, 48,
		EducationEnum.ARMOR),

WARP(ModuleNameEnum.SPACE_WARP,
		new Cost(new int[] {    0,    0,  8000, 15000}, 5, 0, 10000, 3000L),
		new FightSpec(0,     0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		UnitSpecial.WARP,
		ResearchEnum.WARP_ENGINE, 80,
		EducationEnum.WARP_ENGINE,
		ResearchEnum.WARP_ENGINE.getImageName()),

TROOP_TRANSPORT(ModuleNameEnum.TROOP_TRANSPORT,
		new Cost(new int[] {    0,    0,    0,    0},  0, 0,      0,     0L),
		new FightSpec(0,     0, new float[] {0.0f,  0.0f,  0.0f, 0.0f}),
		EducationEnum.BODENTRUPPENTRANSPORT,
		"transporter");


private final ModuleNameEnum names;
private final Cost cost;
private final FightSpec spec;
private final UnitSpecial[] specials;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum education;
private final String imageName;

private CivilianModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec,
		EducationEnum education, String imageName)
{ this(names, cost, spec, null, null, 0, education, imageName); }

private CivilianModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec,
		ResearchEnum dep, int depcount, EducationEnum education)
{ this(names, cost, spec, null, dep, depcount, education, dep.getImageName()); }

private CivilianModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec, UnitSpecial special, ResearchEnum dep, int depcount,
		EducationEnum education, String imageName)
{
	this.names = names;
	if (imageName == null) throw new NullPointerException();
	this.cost = cost;
	this.spec = spec;
	if (special != null)
		this.specials = new UnitSpecial[] { UnitSpecial.CIVIL, special };
	else
		this.specials = new UnitSpecial[] { UnitSpecial.CIVIL };
	if (dep != null)
	{
		if (depcount < 0) depcount = dep.getMax();
	 	this.dependencies.set(dep, depcount);
	}
	UnitHelper.transitiveDependencies(this.dependencies);
	this.education = education;
	this.imageName = imageName;
}

@Override
public int size()
{
	if ((this == RES_TRANSPORT) || (this == TROOP_TRANSPORT))
		return 3;
	return 1;
}

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
