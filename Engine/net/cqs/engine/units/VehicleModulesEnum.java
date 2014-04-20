package net.cqs.engine.units;

import java.util.Locale;

import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.FightSpec;
import net.cqs.config.units.UnitModule;
import net.cqs.config.units.UnitSpecial;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;

public enum VehicleModulesEnum implements UnitModule
{

SPEED(ModuleNameEnum.SURFACE_SPEED,
		new Cost(new int[] {0,   200, 200, 400}, 4, 0, 100, 71L),
		new FightSpec(0.0f,  0.0f,  new float[] {0.0f, 0.0f, 0.0f}),
		ResearchEnum.PLANETARY_SPEED, 32,
		EducationEnum.SPEED),

ATTACK(ModuleNameEnum.SURFACE_ATTACK,
		new Cost(new int[] {700, 400, 0,  0}, 4, 0, 100, 71L),
		new FightSpec(0.0f,  0.15625f, new float[] {175.0f, 87.5f, 43.75f}),
		ResearchEnum.ROCKET_BATTERY, ResearchEnum.ROCKET_BATTERY.getMax(),
		EducationEnum.WEAPONS),

DEFENSE(ModuleNameEnum.SURFACE_DEFENSE,
		new Cost(new int[] {500, 500,  0,  0}, 4, 0, 100, 71L),
		new FightSpec(288.75f, 0.0f,  new float[] {0.0f, 0.0f, 0.0f}),
		ResearchEnum.PLATING, 20,
		EducationEnum.ARMOR);

private final ModuleNameEnum names;
private final Cost cost;
private final FightSpec spec;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EducationEnum education;
private final String imageName;

private VehicleModulesEnum(ModuleNameEnum names, Cost cost, FightSpec spec,
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

@Override
public String getDescription(Locale locale)
{ return names.getDescription(locale); }

@Override
public String getImageName()
{ return imageName; }

}
