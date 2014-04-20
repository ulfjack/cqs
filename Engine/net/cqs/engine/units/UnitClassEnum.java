package net.cqs.engine.units;

import java.util.EnumSet;
import java.util.Locale;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.FightSpec;
import net.cqs.config.units.UnitClass;
import net.cqs.config.units.UnitModule;
import net.cqs.config.units.UnitSpecial;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum UnitClassEnum implements UnitClass
{

/**
 * The defense value is always 3.3*attack value to own class.
 * Modules add exactly 25% defense for defense module, 25% of all attack values for attack modules.
 * This ensures that the units are balanced in themselves.
 * The classes complement each other, i.e. they follow the paper scissors stone principle.
 */
	
INFANTRY ("infantry", "The infantry forms the essential part of every force. In particular foot soldiers can occupy adversary colonies, having to be present at all times to assure the taking over of legal order and authority. Then this area can be availed in your own interest.",
		2, InfantryModulesEnum.values(), true, 0,
		new Cost(new int[] {400, 200, 0, 0}, 4, 40, 100, 143L),
		new FightSpec(247.5f, 0.5f, new float[] {75.0f, 37.5f, 150.0f}),
		ResearchEnum.INFANTRY, ResearchEnum.INFANTRY.getMax(),
		EducationEnum.INFANTRY,
		"infanterie-chassis-complete"),

VEHICLE("vehicle", "This chassis enables the construction of vehicles. Armoured they are usually track vehicles, which can overcome areas with an aclivity of up to 60%. Depending on the design of the vehicle, it can be a fast moving jeep or a very resistant tank.",
		2, VehicleModulesEnum.values(),  true, 1,
		new Cost(new int[] {1000, 1500, 200, 100}, 16, 80, 1000, 714L),
		new FightSpec(1155.0f, 0.625f, new float[] {700.0f, 350.0f, 175.0f}),
		ResearchEnum.VEHICLE, ResearchEnum.VEHICLE.getMax(),
		EducationEnum.VEHICLE,
		"jeep-chassis"),

AIRCRAFT("aircraft", "This chassis enables the construction of aircrafts. As these units can attack from height, fundamentally different weapons can be introduced and are therefore a very dangerous expansion of every planetary fleet.",
		2, AircraftModulesEnum.values(), true, 2,
		new Cost(new int[] {900, 900, 500, 500}, 32, 100, 1500, 1429L),
		new FightSpec(1155.0f, 1.25f, new float[] {175.0f, 700.0f, 350.0f}),
		ResearchEnum.AIRCRAFT, ResearchEnum.AIRCRAFT.getMax(),
		EducationEnum.AIRCRAFT,
		"heli-chassis"),

FIGHTER("fighter", "This chassis enables the consctruction of fighters. These ships do not possess much force, but make up for that by being very cheap and fast moving.",
		2, FighterModulesEnum.values(),   false, 0,
		new Cost(new int[] {8000, 2000, 8000, 8000}, 10, 700, 7000, 1071L),
		new FightSpec(412.5f, 0, new float[] {125.0f, 250.0f, 62.5f, 250.0f}),
		ResearchEnum.FIGHTER, ResearchEnum.FIGHTER.getMax(),
		EducationEnum.FIGHTER,
		"jaeger-chassis"),

CORVETTE("corvette", "Corvettes are very good when deploying them in battles with big space ships. They are already pretty impressive vessels, but still have some agility. Unlike fighters these ships can get dangerous for destroyers.",
		2, CorvetteModulesEnum.values(),  false, 1,
		new Cost(new int[] {62000, 46000, 70000, 43000}, 60, 4000, 55000, 7500L),
		new FightSpec(2805.0f, 0, new float[] {425.0f, 850.0f, 1700.0f, 850.0f}),
		ResearchEnum.CORVETTE, ResearchEnum.CORVETTE.getMax(),
		EducationEnum.CORVETTE,
		"corvette-chassis"),

DESTROYER("destroyer", "Destroyers are enormous ships with great force. Of course this has its price and they miss the agility of smaller ships. This seems fairly unimportant, as these giants of space are not likely to start a jink.",
		2, DestroyerModulesEnum.values(), false, 2,
		new Cost(new int[] {220000, 150000, 90000, 200000}, 175, 13000, 240000, 22500L),
		new FightSpec(8382.0f, 0, new float[] {5080.0f, 1270.0f, 2540.0f, 1270.0f}),
		ResearchEnum.DESTROYER, ResearchEnum.DESTROYER.getMax(),
		EducationEnum.DESTROYER,
		"zerstoerer-chassis"),
		
CIVILIAN("civil ship", "This chassis was designed for transporting resources or planetary units.",
		5, CivilianModulesEnum.values(),  false, 3,
		new Cost(new int[] {62000, 46000, 70000, 43000}, 30, 4000, 55000, 2000L),
		new FightSpec(1800.0f, 0, new float[] {35.0f, 70.0f, 140.0f, 70.0f}),
		ResearchEnum.CORVETTE, ResearchEnum.CORVETTE.getMax(),
		UnitSpecial.CIVIL, null,
		"cargo-chassis");

private final String englishTranslation;
private final String englishDescription;
private final int size;
private final UnitModule[] modules;
private final EducationEnum education;
private final boolean ground;
private final int group;
private final Cost cost;
private final FightSpec spec;
private final DependencyList<ResearchEnum> dependencies = DependencyList.of(ResearchEnum.class);
private final EnumSet<UnitSpecial> specials;
private final String imageName;

private UnitClassEnum(String englishTranslation, String englishDescription,
		int size, UnitModule[] modules, boolean ground, int group, 
		Cost cost, FightSpec spec, ResearchEnum dep, int depcount, EducationEnum education, String imageName)
{ this(englishTranslation, englishDescription, size, modules, ground, group, cost, spec, dep, depcount, null, education, imageName); }

private UnitClassEnum(String englishTranslation, String englishDescription,
		int size, UnitModule[] modules, boolean ground, int group, 
		Cost cost, FightSpec spec, ResearchEnum dep, int depcount, UnitSpecial special, EducationEnum education,
		String imageName)
{
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
	this.size = size;
	this.modules = modules;
	this.ground = ground;
	this.group = group;
	this.cost = cost;
	this.spec = spec;
	if (depcount < 0) depcount = dep.getMax();
	if (depcount > dep.getMax()) depcount = dep.getMax();
	this.dependencies.set(dep, depcount);
	UnitHelper.transitiveDependencies(this.dependencies);
	if (special != null)
		this.specials = EnumSet.of(special);
	else
		this.specials = EnumSet.noneOf(UnitSpecial.class);
	this.education = education;
	this.imageName = imageName;
}

@Override
public UnitSystem getUnitSystem()
{ return UnitSystemImpl.getImplementation(); }

@Override
public int size()
{ return size; }

@Override
public UnitModule[] getModules()
{ return modules; }

public EducationEnum getEducation()
{ return education; }

@Override
public boolean isPlanetary()
{ return ground; }

public UnitSpecial[] specials()
{ return specials.toArray(new UnitSpecial[0]); }

@Override
public boolean hasSpecial(UnitSpecial special)
{ return specials.contains(special); }

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
public int getGroup()
{ return group; }

@Override
public UnitModule parseUnitModule(String s)
{
	switch (this)
	{
		case INFANTRY: return InfantryModulesEnum.valueOf(s);
		case VEHICLE: return VehicleModulesEnum.valueOf(s);
		case AIRCRAFT: return AircraftModulesEnum.valueOf(s);
		case FIGHTER: return FighterModulesEnum.valueOf(s);
		case CORVETTE: return CorvetteModulesEnum.valueOf(s);
		case DESTROYER: return DestroyerModulesEnum.valueOf(s);
		case CIVILIAN: return CivilianModulesEnum.valueOf(s);
		default: throw new IllegalArgumentException();
	}
}

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

private static String bundleName()
{ return "net.cqs.engine.units.UnitClassEnum"; }

@Override
public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override @HtmlFragment
public String getDescription(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishDescription());
}

@Override
public String getImageName()
{ return imageName; }

}
