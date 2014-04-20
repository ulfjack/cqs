package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum ResourceEnum implements Formattable
{

STEEL    (0, "steel", "A steel works refines iron to steel with a carbon concentration of 0.5% to 1.5%. Steel has become construction material, because this process is cost effective, and steel is easy to process further. It is vital for a economically healty colony to produce steel in sufficient amounts. Steel is also necessary for the development of war machinery. Whether for weapons or for armor - steel is impressive in its strength, its ruggedness, and its malleability. Although it is cheap to produce, the importance of steel should not be underestimated."),
OIL      (1, "oil", "Initially, oil was used in particular as a fuel and as a lubricant. The development of plastics further increased its importance. Starting with oil, plastics is produced through polymerization, polycondensation, and polyaddition. Plastics are more versatile and lighter than steel, but not as strong. As a result, oil is a necessary ingredient in almost every industrial process."),
SILICON  (2, "silicon", "Silicon is a semiconductor and one of the most common elements in the planet crust. Because of its electrical properties, silicon is primarily used as the basic material of computer chips. Due to miniaturization and automation, these are the main constituent of almost every machine. With increasing development status, a colony has to provide increasing amounts of silicon to meet the demands."),
DEUTERIUM(3, "deuterium", "Deuterium is a heavy hydrogen isotope, which is separated during the process of elecroanalysis of water. The fusion of deuterium and antideuterium produces extraordinary amounts of energy. Deuterium is therefore an important fuel- and energy source. Unfortunately, the large-scale extraction of deuterium requires highly complex technology. To produce the amount required by space travel, a colony needs a significant number of deuterium processing plants."),
MONEY    (4, "money", "The more active the trade in a system, the higher its economic power. The construction of trading centers increases the trade, and thus the economic development of a colony. The currency in this universe is CqSol. Money is necessary to support buildings, research, education, and, of course, military developments.");

private static final int[] START_AMOUNT =    new int[] { 75000, 75000, 5000, 0, 5000 };

private static final int[] NULL_RATE =       new int[] { 1000, 1000, 250, 0, 6000 };
private static final int[] RATE_FACTORS =    new int[] { 1200, 1200, 1200, 1200, 60000 };

private static final int[] NULL_STORAGE =    new int[] { 1000000, 1000000, 1000000, 1000000, 1000000000 };
private static final int[] STORAGE_FACTORS = new int[] { 3000000, 3000000, 3000000, 3000000, 0};

private final int index;
private final String englishTranslation;
private final String englishDescription;

private ResourceEnum(int index, String englishTranslation, String englishDescription)
{
	this.index = index;
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
}

public int index()
{ return index; }

public int getStartAmount()
{ return START_AMOUNT[index]; }

public int getNullRate()
{ return NULL_RATE[index]; }

public int getRateFactor()
{ return RATE_FACTORS[index]; }

public int getNullStorage()
{ return NULL_STORAGE[index]; }

public int getStorageFactor()
{ return STORAGE_FACTORS[index]; }

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

public static String bundleName()
{ return "net.cqs.config.base.ResourceEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

@HtmlFragment
public String getDescription(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishDescription());
}

public static ResourceEnum get(int i)
{
	switch (i)
	{
		case Resource.STEEL : return STEEL;
		case Resource.OIL :   return OIL;
		case Resource.SILICON : return SILICON;
		case Resource.DEUTERIUM : return DEUTERIUM;
		case Resource.MONEY : return MONEY;
	}
	throw new IllegalArgumentException(""+i);
}

public static String getName(Locale locale, int i)
{ return get(i).getName(locale); }

public static ResourceEnum[] realResources()
{ return new ResourceEnum[] { STEEL, OIL, SILICON, DEUTERIUM }; }

public static int maxResource()
{ return Resource.MAX; }

}
