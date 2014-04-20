package net.cqs.config;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.TreeSet;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.engine.base.Cost;
import net.cqs.i18n.Localized;
import net.cqs.i18n.ManagedLocale;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum BuildingEnum implements Formattable, Localized
{

//             english name, english description,
//             points,upkeep,  steel oil sil deut,  construct,regular,time
STEEL_MILL      ("steel mill", "Steel mills have two functions. The first includes mining iron ore and extracting iron from it. The second deals with processing iron to steel by mixing it with carbon. This construction should be expanded as soon as possible.<br />Each steel mill provides an extra {0,number,integer} tons of steel per hour, given a planet suitability of 100%.",
                 5, 0, new int[] { 3500,  3000,     0}, 100,  250, Time.minutes(42)),
REFINERY        ("refinery", "Refineries are in low levels simple derricks, that supply oil. Its expansions focus on processing oil. Though more expensive, processed oil is more efficient and can be used for making plastics.<br />Each refinery provides an extra {0,number,integer} tons of oil per hour, given a planet suitability of 100%.",
                 5, 0, new int[] { 4500,  2000,     0}, 100,  250, Time.minutes(48)),
SILICON_FOUNDRY ("silicon foundry", "In a silicon foundry, silicon is separated from highly heated sands and quartz brass. Using the produced silicon, microchips are also built here. This is a very expensive process because of the necessity of precise and sterile work. With increasing technological standards, microtechnology raises in importance and with it the need for silicon foundries.<br />Each silicon foundry provides an extra {0,number,integer} tons of silicon per hour, given a planet suitability of 100%.",
                 5, 0, new int[] { 9500,  8500,     0}, 200,  500, Time.minutes(58)),
PROCESSING_PLANT("processing plants", "A very complicated process is necessary for producing deuterium. Water has to be split up into hydrogen and oxygen by electrolysis. Then the low percentage of deuterium has to be filtered out. As this process is both expensive and inefficient producing larger amounts of deuterium can be time consuming.<br />Each processing plant provides an extra {0,number,integer} tons of deuterium per hour, given a planet suitability of 100%.",
                 5, 0, new int[] {24000, 24000, 11000}, 400, 1000, Time.minutes(78)),

TRADE_CENTER  ("trade center", "A trade center gives merchants the possibility of offering their goods in a normed forum, which could be compared with the stock market. By providing a standardized exchange, the competition is assisted and the economy is backed up. An inconvenient side effect of increased trade is a decrease of population growth.<br />Each trade centre provides an extra income of {0,number,integer} CqSol per hour.",
               15,   0, new int[] { 8000, 7000, 4000}, 300, 0, Time.minutes(53)),
INFRASTRUCTURE("infrastructure", "The growth rate of the population mainly depends on the living conditions of your citizens. Therefore traffic routes, public buildings (i.e. hospitals, police stations, firefighters) and recreational facilities have to be expanded to guarantee a minimum of quality of life.",
               15, 100, new int[] { 4000, 3000, 1000}, 100, 0, Time.minutes(53)),
RESIDENCE     ("residence", "For creating sufficient housing space, residential buildings have to be constructed. As the population grows when enlarging a colony, this building has to be upgraded continuously.<br />Each residence provides living space for {0,number,integer} people.",
               15,   0, new int[] {20000,  300, 4000}, 100, 0, Time.minutes(86)),

STEEL_DEPOT    ("steel depot", "In these buildings steel can be collected and therefore the maximum cargo capacity is enhanced. Basically there is a need for extra storage when large amounts of steel are needed for bigger projects.<br />Each steel depot provides space for {0,number,integer} tons of steel.",
                10, 70, new int[] {25000,    0,     0,    0}, 100, 0, Time.minutes(90)),
OIL_TANKS      ("oil tanks", "Tanks are needed to increase oil capacity.<br />Each oil tank provides space for {0,number,integer} tons of oil.",
                10, 70, new int[] {20000, 5000,     0,    0}, 100, 0, Time.minutes(123)),
SILICON_DEPOT  ("silicon depot", "Silicon has to be preserved in special halls, as the need of purity demands a sterile storage. Consequently it can not be saved in steel depots, which are much cheaper. Increasing the cargo capacity of silicon is of great importance, though.<br />Each silicon depot provides space for {0,number,integer} tons of silicon.",
                10, 70, new int[] {20000,    0,  5000,    0}, 100, 0, Time.minutes(168)),
DEUTERIUM_DEPOT("deuterium depot", "Deuterium has to be stored under extreme refrigeration, so it becomes a liquid. In addition to that the risk of explosions, which could occur because of arc-overs, has to be reduced to a minimum. Consequently, storing deuterium is very expensive. Unfortunately, enhancing the cargo capacity can not be avoided when expanding your fleet.<br />Each deuterium depot provides space for {0,number,integer} tons of deuterium.",
                10, 70, new int[] {10000,    0, 10000, 5000}, 200, 0, Time.minutes(202)),

MILITARY_BASE("military base", "A military basis is divided into two main blocks. Firstly, planet-bound vehicles and weapons are produced. Secondly, troops and crew-members are drilled here.",
              25, 2000, new int[] { 25000, 20000,  5000,     0},  500, 0, Time.minutes(292)),
SHIPYARD     ("shipyard", "Units, which are constructed here, are not planet-bound anymore. They have the possiblity of moving in the interplanetary neighborhood, expanding trading routes and giving you advantages when fighting enemies.",
              30, 3000, new int[] { 50000, 25000, 50000, 15000}, 1000, 0, Time.minutes(373)),
SPACEPORT    ("spaceports", "Spaceports are orbital shipyards that were created to build large spaceships. They are extremely important for interstellar travel, opening the whole universe to you.",
              35, 4000, new int[] {100000, 25000, 80000, 20000}, 3100, 0, Time.minutes(1474)),

TRANSMITTER    ("transmitter station", "A transmitter station mainly consists of a star gate, with which you can transfer units to other colonies. By using wormholes the flight duration is reduced notedly. Unfortunately you have to have a transmitter station on the destination colony, too. Note that with transmitter stations you can transfer fleets very quickly between different systems.<br />Transmitter stations allow you to move fleets from one system to another within {0}.",
                100, 60000, new int[] {300000, 300000, 700000, 500000}, 200000, 0, Time.minutes(78959)),
RADIO_TELESCOPE("radio telescope", "A radio telescope is a traversable antenna that can receive electromagnetic radiation. This information can shed light on the position of other systems. When upgrading whole fields of telescopes can be constructed which leads to more detailed information. You can especially investigate larger areas of the universe. As it gets much harder to interpret the radiation with growing distance, the system view only grows slowly when building more radio telescopes.",
                 30,  2000, new int[] {150000, 100000, 200000,  50000},  50000, 0, Time.minutes(292)),

UNIVERSITY("university", "A university is an academy for all sciences. Teaching succeeds in different faculties, where research and education can take place. For university work it is essential how many professors can be employed. The more chairs are available, the faster you can research and the better you can educate your specialists. When firing a professor, he leaves the university and consequently the level of education decreases.",
            15,    0, new int[] { 10000,   6000,  12000,     0}, 20000, 5000, Time.minutes(56)),
LIBRARY   ("library", "In a library books are stored and cataloged. Consequently the population can look up different things in great works or lend books. At home they then can keep themselves busy with their field of interest or relax a bit by reading a good book. No city should lack this building, as it is an indicator of civilized life.",
           250, 1500, new int[] {150000, 100000, 200000, 50000},  5000,   50, Time.minutes(292));

private final int points;
private final int upkeep;
private final Cost cost;

private final String imageName;
private final String englishTranslation;
private final String englishDescription;

private BuildingEnum(String englishTranslation, String englishDescription,
		int points, int upkeep, int[] resources, int constructionJobs, int regularJobs, long time)
{
	this.points = points;
	this.upkeep = upkeep;
	this.cost = new Cost(resources, 0, 0, regularJobs, 0, constructionJobs, time);
	this.imageName = name().toLowerCase(Locale.US);
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
}

private BuildingEnum(int points, int upkeep, int[] resources, int constructionJobs, int regularJobs, long time)
{ this(null, null, points, upkeep, resources, constructionJobs, regularJobs, time); }

public boolean isSpecial()
{ return false; }

public int upkeepNeeded()
{ return upkeep; }

public int regularJobs()
{ return cost.getRegularJobs(); }

public int constructionJobs()
{ return cost.getConstructionJobs(); }

public int removeJobs()
{ return 0; }

public long timeNeeded(int planetSize, int planetUsed, int amount)
{	
	// When planet is crowded, use timeNeeded for each building.
	if (planetUsed >= planetSize)
		return timeNeeded()*amount;
	int amountBeforeFull = Math.min(amount, planetSize - planetUsed);
	int amountAfterFull = amount - amountBeforeFull;
	// When planet is empty, use (0.25+0.75(currentlyUsed/planetSize))*timeNeeded.
	// Calculate sum (i from 0 to amountBeforeFull-1) 0.25 + 0.75(planetUsed+i)/planetSize.
	double factor = 0.25*amountBeforeFull + 0.75*(amountBeforeFull*amountBeforeFull/2.0 + amountBeforeFull*(planetUsed - 1) + 1)/planetSize;
	return (long) Math.ceil((amountAfterFull + factor)*timeNeeded());
}

public long timeNeeded()
{ return cost.getTime(); }

public int resNeeded(int what)
{ return cost.getResource(what); }

public int getPoints()
{ return points; }

public Cost getCost(int planetSize, int planetUsed)
{
	return new Cost(cost, timeNeeded(planetSize, planetUsed, 1));
}

public Cost getCost()
{ return cost; }

public Cost getRemovalCost()
{ return new Cost(removeJobs(), Time.minutes(100)); }

private Object getBenefitAmount()
{
	switch (this)
	{
		case STEEL_MILL: return Integer.valueOf(ResourceEnum.STEEL.getRateFactor());
		case REFINERY: return Integer.valueOf(ResourceEnum.OIL.getRateFactor());
		case SILICON_FOUNDRY: return Integer.valueOf(ResourceEnum.SILICON.getRateFactor());
		case PROCESSING_PLANT: return Integer.valueOf(ResourceEnum.DEUTERIUM.getRateFactor());
		case STEEL_DEPOT: return Integer.valueOf(ResourceEnum.STEEL.getStorageFactor());
		case OIL_TANKS: return Integer.valueOf(ResourceEnum.OIL.getStorageFactor());
		case SILICON_DEPOT: return Integer.valueOf(ResourceEnum.SILICON.getStorageFactor());
		case DEUTERIUM_DEPOT: return Integer.valueOf(ResourceEnum.DEUTERIUM.getStorageFactor());
		case RESIDENCE: return Integer.valueOf(Constants.PEOPLE_PER_RESIDENCE);
		case TRADE_CENTER: return Integer.valueOf(ResourceEnum.MONEY.getRateFactor());
		case TRANSMITTER: return Time.format(Constants.TRANSMIT_TIME);
		default: return Integer.valueOf(-1);
	}
}

public String getImageName()
{ return imageName; }

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

private static String bundleName()
{ return "net.cqs.config.base.BuildingEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public String toString(ManagedLocale locale)
{ return locale.getSafeString(bundleName(), englishTranslation()); }

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

@HtmlFragment
public String getDescription(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	String pattern = bundle.getSafeString(englishDescription());
	MessageFormat mf = new MessageFormat(pattern, locale);
	return mf.format(new Object[] {getBenefitAmount()});
}


private static NavigableSet<BuildingEnum> list;

private static NavigableSet<BuildingEnum> getList()
{
	if (list == null) list = new TreeSet<BuildingEnum>(Arrays.asList(values()));
	return list;
}

public static BuildingEnum getPrevious(BuildingEnum building)
{ return getList().lower(building); }

public static BuildingEnum getNext(BuildingEnum building)
{ return getList().higher(building); }

public static BuildingEnum valueOf(int key)
{ return values()[key]; }

}
