package net.cqs.config;

import java.util.Arrays;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.TreeSet;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum EducationEnum implements Formattable
{

CONSTRUCTION          ("construction techniques", "The more a settlement grows, the bigger the need is for larger buildings. But more complex projects take more time to complete, and the more crowded a planet is, the harder it will be to clear new building lots. By researching and enhancing construction techniques these problems can tackled.<br /><br />This education decreases the construction time of buildings."),

WARP_ENGINE           ("warp-engine", "When launching space ships huge amounts of deuterium are needed. By facilitating the energy more effectively the needed amount of deuterium can be decreased. These advanced techniques can only be applied by specialists, so special schooling is needed.<br /><br />This education decreases the costs of warp modules.",
                       ResearchEnum.WARP_ENGINE, new float[] {0.0f, 0.0f, 0.1f, 0.2f}),

INFANTRY              ("infantry", "The core of every army is its infantry. They can invade and occupy enemy territory. When a colony is seized, the legal order and the military authority are taken over. This allows the attacker to use buildings in their own interest.<br /><br />This education decreases the costs of infantry units.",
                       ResearchEnum.INFANTRY, new float[] {0.3f, 0.3f, 0.3f, 0.3f}),
VEHICLE               ("vehicle", "Vehicles have the advantage of offering considerably more space for weapons in comparison to infantry units. Armor-cased vehicles are often fully tracked, and are designed to easily conquer inclines of up to 60%. They can even surpass smaller vertical barriers. As needed, vehicles can therefore provide a fast moving jeep with lots of fire power, or a very resistant tank.<br /><br />This education decreases the costs for vehicles.",
                       ResearchEnum.VEHICLE, new float[] {0.3f, 0.3f, 0.3f, 0.3f}),
AIRCRAFT              ("aircraft", "Aircraft usually attack from heights, so they can use totally different weapons compared to land-bound units. This means they are usually a formidable expansion for every planetary fleet.<br /><br />This education decreases the costs for aircraft.",
                       ResearchEnum.AIRCRAFT, new float[] {0.3f, 0.3f, 0.3f, 0.3f}),

FIGHTER               ("fighter", "This education decreases the costs for fighters.",
                       ResearchEnum.FIGHTER, new float[] {0.3f, 0.3f, 0.3f, 0.3f}),
CORVETTE              ("corvette", "This education decreases the costs for corvettes.",
                       ResearchEnum.CORVETTE, new float[] {0.3f, 0.3f, 0.3f, 0.3f}),
DESTROYER             ("destroyer", "This education decreases the costs for destroyers.",
                       ResearchEnum.DESTROYER, new float[] {0.3f, 0.3f, 0.3f, 0.3f}),

RESSOURCENTRANSPORT   ("resource transport", "By enhancing storage techniques, including optimal use of space, the costs for freighters can be reduced. Trained workers can apply these optimizations.<br /><br />This education decreases the costs for units that can transport resources.",
                       ResearchEnum.ENGINE, 32, new float[] {0.3f, 0.3f, 0.3f, 0.3f}, "restransport"),
BODENTRUPPENTRANSPORT ("troop transport", "By enhancing storage techniques, including optimal use of space, the costs for transportation units can be reduced. Trained workers can apply these optimizations.<br /><br />This education decreases the costs for units that can transport units.",
                       ResearchEnum.ENGINE, 40, new float[] {0.3f, 0.3f, 0.3f, 0.3f}, "transporter"),

SPEED                 ("speed", "This education decreases the costs for speed modules.",
                       ResearchEnum.SPEED, new float[] {0.2f, 0.2f, 0.2f, 0.2f}),
WEAPONS               ("attack", "This education decreases the costs for weapon modules.",
                       ResearchEnum.WEAPONS, new float[] {0.2f, 0.2f, 0.2f, 0.2f}),
ARMOR                 ("defense", "Enhanced plate armor structures can reduce the iron costs without influencing the effectiveness of the armor. Building armor in this fashion is certainly more complex, and therefore needs specially trained workers. Although only small amounts of iron can be saved for each armor, the numbers quickly add up.<br /><br />This education decreases the costs for defense modules.",
                       ResearchEnum.ARMOR, new float[] {0.2f, 0.2f, 0.2f, 0.2f});

private final String englishTranslation;
private final String englishDescription;
private final ResearchEnum dep;
private final int depcount;
private final float[] modifiers;
private final String imageName;

private EducationEnum(String englishTranslation, String englishDescription, ResearchEnum dep, int depcount, float[] modifiers, String imageName)
{
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
	this.dep = dep;
	this.depcount = depcount;
	this.modifiers = modifiers;
	if (imageName == null)
		this.imageName = name().toLowerCase(Locale.US).replace('_', '-');
	else
		this.imageName = imageName;
}

private EducationEnum(String englishTranslation, String englishDescription, ResearchEnum dep, int depcount, float[] modifiers)
{ this(englishTranslation, englishDescription, dep, depcount, modifiers, null); }

private EducationEnum(String englishTranslation, String englishDescription, ResearchEnum dep, float[] modifiers)
{ this(englishTranslation, englishDescription, dep, dep.getMax(), modifiers); }

private EducationEnum(String englishTranslation, String englishDescription)
{ this(englishTranslation, englishDescription, null, 0, null); }


public ResearchEnum getDep()
{ return dep; }

public float getModifier(int which)
{ return modifiers[which]; }

public long getModifierPercent(int which)
{ return (long) (100.0*getModifier(which)); }

public int getDepCount()
{ return depcount; }

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

private static String bundleName()
{ return "net.cqs.config.base.EducationEnum"; }

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

public String getImageName()
{ return imageName; }

private static NavigableSet<EducationEnum> list;

private static NavigableSet<EducationEnum> getList()
{
	if (list == null) list = new TreeSet<EducationEnum>(Arrays.asList(values()));
	return list;
}

public static EducationEnum getPrevious(EducationEnum topic)
{ return getList().lower(topic); }

public static EducationEnum getNext(EducationEnum topic)
{ return getList().higher(topic); }


public static EducationEnum valueOf(int key)
{ return values()[key]; }

}
