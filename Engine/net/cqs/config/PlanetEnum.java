package net.cqs.config;

import java.util.Locale;
import java.util.Random;

import net.cqs.i18n.Localized;
import net.cqs.i18n.ManagedLocale;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum PlanetEnum implements Localized
{

//                       prob, size, popModifier,  stahl,     oel,  silizium, deuterium, maxStart
ASTEROIDS  ("asteroids",    9, "180-250", "40-50", "600-999",   "30-50", "300-500", "5-10", 0),
PLANETOIDS ("planetoids",   9, "120-240", "40-50", "300-800", "100-180", "200-700", "2-10", 0),

WASTELAND  ("wasteland",   11, "400-600", "40-120", "300-520",   "70-90", "80-200",    "2-10", 0),
ICEWORLD   ("iceworld",    13, "400-600", "40-120",  "20-100", "100-280", "20-100", "280-420", 0),
DESERTWORLD("desertworld", 18, "400-600", "40-120",   "20-40", "240-440", "80-400",    "2-10", 0),

WATERWORLD ("waterworld",  18, "240-280", "130-150", "84-100", "70-84", "74-90", "80-94", 2),
JUNGLEWORLD("jungleworld", 18, "240-280", "130-150", "84-100", "70-84", "74-90", "80-94", 2),
TERRAWORLD ("terraworld",  18, "240-280", "130-150", "84-100", "70-84", "74-90", "80-94", 2),

GASPLANET  ("gasplanet",    8,   "600-800", "5-15", "80-160", "40-100", "60-80", "200-300", 0),
GASGIANT   ("gasgiant",     8, "1500-1800",  "2-5",  "20-30",  "10-25", "30-40", "200-400", 0);

public static final int MAX_POPULATION_MODIFIER = 150;
private final String id;
private final String englishTranslation;
private final int probability;
private final RangeCreator size;
private final RangeCreator populationModifier;
private final RangeCreator[] modifiers;
private final int maxStart;
private final String imageName;

private PlanetEnum(String englishTranslation, int probability, String size, String popModifier, String sta, String oel, String sil, String deu, int maxStart)
{
	this.id = name().toLowerCase(Locale.US);
	this.englishTranslation = englishTranslation;
	this.probability = probability;
	this.size = new RangeCreator(size);
	this.populationModifier = new RangeCreator(popModifier);
	this.modifiers = new RangeCreator[Resource.MAX+1];
	this.modifiers[Resource.STEEL]     = new RangeCreator(sta);
	this.modifiers[Resource.OIL]       = new RangeCreator(oel);
	this.modifiers[Resource.SILICON]  = new RangeCreator(sil);
	this.modifiers[Resource.DEUTERIUM] = new RangeCreator(deu);
	this.maxStart = maxStart;
	this.imageName = name().toLowerCase(Locale.US);
}

public String getId()
{ return id; }

public int getProbability()
{ return probability; }

public int getSize(Random rand)
{ return size.create(rand); }

public int getPopulationModifier(Random rand)
{ return populationModifier.create(rand); }

public int getResourceModifier(int i, Random rand)
{ return modifiers[i].create(rand); }

public int getMaxStart()
{ return maxStart; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.base.PlanetEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public String toString(ManagedLocale locale)
{ return locale.getSafeString(bundleName(), englishTranslation()); }

public String getImageName()
{ return imageName; }

private static int maxProbability;
private static PlanetEnum[] types;

public static PlanetEnum selectPlanetEnum(Random rand)
{
	if (types == null)
	{
		types = PlanetEnum.values();
		maxProbability = 0;
		for (PlanetEnum e : types)
			maxProbability += e.getProbability();
	}
	
	int which = rand.nextInt(maxProbability);
	int num = -1;
	while (which >= 0)
	{
		num++;
		which -= types[num].probability;
	}
	return types[num];
}

}
