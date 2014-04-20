package net.cqs.config;

import java.util.Arrays;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.TreeSet;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.engine.Player;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum ResearchEnum
{

ENGINE           ("engines", "This research area provides basic knowledge on different engines for chassis. You will be able to develop linear accelerator-driven reactor and anti-matter engines for example. Later on you might be able to explore the areas of wormhole generators or sublight engines, enabling interstellary travel.",
                  128),
WARP_ENGINE      ("warp-engine", "You will need a warp-engine to enable interstellary travel. This research area will only be concerned about this important step in development opening the whole galaxy to you.",
                  128, ENGINE, 128),

SPEED            ("high speed equipment", "If you want to launch surprise attacks time is going to be a major factor. In this research area ways of increasing the speed of units will be prepared.",
                  128, ENGINE, 32),

PLANETARY_SPEED  ("sleeky planetary chassis", "In this research field scientists will develop methods of increasing the speed of planetary units.",
                   56, SPEED, 16),
SPACE_SPEED      ("space acceleration", "In this research field scientists will develop methods of increasing the speed of space units.",
                  128, SPEED, 32),

ARMOR            ("armor", "Hard wearing units can only be created when knowing methods of armor. This research field accomplishes exactly that.",
                  128),

PLATING          ("plating", "Plating can be used to make planetary units more robust. Hence they will have more defense, but the plating uses space and adds the the weight.",
                  32, ARMOR, 56),
MAGNETIC_FIELD   ("magnetic fields", "Here a way of generating mangetic fields around an unit are developed. As larger amounts of energy are required, this armor can only be used for space units.",
                  56, ARMOR, 128),

INFANTRY         ("infantry", "The infantry forms the essential part of every force. In particular foot soldiers can occupy adversary colonies, having to be present at all times to assure the taking over of legal order and authority. Then this area can be availed in your own interest.",
                   4, ENGINE,  2),
VEHICLE          ("vehicle", "This chassis enables the construction of vehicles. Armoured they are usually track vehicles, which can overcome areas with an aclivity of up to 60%. Depending on the design of the vehicle, it can be a fast moving jeep or a very resistant tank.",
                   8, ENGINE,  8),
AIRCRAFT         ("aircraft", "This chassis enables the construction of aircrafts. As these units can attack from height, fundamentally different weapons can be introduced and are therefore a very dangerous expansion of every planetary fleet.",
                  16, ENGINE, 14),

FIGHTER          ("fighter", "This chassis enables the consctruction of fighters. These ships do not possess much force, but make up for that by being very cheap and fast moving.",
                  16, ENGINE,  32),
CORVETTE         ("corvette", "Corvettes are very good when deploying them in battles with big space ships. They are already pretty impressive vessels, but still have some agility. Unlike fighters these ships can get dangerous for destroyers.",
                  40, ENGINE,  80),
DESTROYER        ("destroyer", "Destroyers are enormous ships with great force. Of course this has its price and they miss the agility of smaller ships. This seems fairly unimportant, as these giants of space are not likely to start a jink.",
                  56, ENGINE, 112),

ESPIONAGE        ("espionage", "In this research area observation tecniques are developed. Hence units can spy out alien colonies. Especially in times of war this is a very strong weapon, even though it is somewhat indirect.",
                  32, INFANTRY, 4),
TERRAIN_ANALYSIS ("terrain analysis", "Methods will be developed that allow units to collect ground samples, analyze these and deduce how common resources are on a planet. When founding a new colony this is something to recommend, else you might get some bad surprises. If you don't have the capacity to supply a colony with resources, preliminary analysis to colonization is a must.",
                  32,  VEHICLE, 8),
COLONIZATION     ("colonization", "As soon as your first colony is running smoothly it is tacticly advisable to expand. Tecniques that allow colonization are developed here. As a side effect, as soon as colonization is possible, you can also move parts of your population to other (already existing) colonies.",
                  32,  VEHICLE, 8),

WEAPONS("weapons", "Only when knowning some basic concepts of weaponry you can drill battle units. This research field prepares the development of more and more complex - and deadly - weapons.",
        128),

TOMMY_GUN        ("machine gun", "A machine gun is one of the easiest to develop weapons. These guns are small and lightweight, hence  it is a very effective weapon to equip an infantry unit with.",
                   2, WEAPONS, 2),
ROCKET_BATTERY   ("rocket battery", "Rockets are explosive objects that can be shot into the atmosphere. As they have an own drive they can cover long distances and with constraints pursuit goals. This weapon is therefore perfect for enhancing vehicles.",
                  20, WEAPONS, 8),
BOMB             ("bombs", "Bombs are bodies that can cause great damage when exploding. Usually a bomb is an iron hollowware filled with demolition charge. It is expecially suitable for dropping from heights and is therefore often used for enhancing aircrafts.",
                  32, WEAPONS, 14),

LASER            ("lasers", "When developing anti-matter-engines suddenly great amounts of energy are available. This leads to totally new possibilities in weaponry. Lasers bundle and amplify light waves and then shoot photon beams. This can only be done in space, so it is a weapon that can only be built into space units. It is an ideal weapon for fighters, the first space unit that you will probably be able to build.",
                  16, WEAPONS, 32),
DESINTEGRATOR    ("desintegrators", "Desinterators can disintegrate materials by destroying intermoleculary bindings. Especially small units that have little armor are in great danger when encountered with this weapon. This is the weapon of choice for corvettes.",
                  40, WEAPONS, 80),
TRANSFORMER      ("transformers", "The concept of transformers is teleporting highly explosive munition directly into its targets to maximise damage. This is a highly dangerous weapon and therefore appropriate for the queen of space ships - the destroyer.",
                  56, WEAPONS, 112),

SCHOKOPUDDING    ("chocolate pudding", "The compulsory easteregg - this idea was initiated when a player asked the main developer UlfJack what he wanted to do with his chocolate pudding - eat it, put it in his hair or implement it into Conquer-Space.net. See for yourself...",
                   4, WEAPONS, 128);

private final String englishTranslation;
private final String englishDescription;
private final int max;
private final ResearchEnum dep;
private final int depcount;
private final String imageName;

private ResearchEnum(String englishTranslation, String englishDescription, int max, ResearchEnum dep, int depcount)
{
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
	this.max = max;
	this.dep = dep;
	this.depcount = depcount;
	this.imageName = name().toLowerCase(Locale.US).replace('_', '-');
}

private ResearchEnum(String englishTranslation, String englishDescription, int max)
{ this(englishTranslation, englishDescription, max, null, 0); }

public int getMax()
{ return max; }

public ResearchEnum getDep()
{ return dep; }

public int getDepCount()
{ return depcount; }

public boolean validResearchTopic(Player who)
{
	if (dep == null) return true;
	return who.getResearchLevel(dep) >= depcount;
}

public boolean mayResearchTopic(Player who)
{
	if (!validResearchTopic(who)) return false;
	return (who.getResearchLevel(this) < max) && (who.getResearchAmount() >= 1);
}

public long timeNeeded()
{ return Time.hours(8); }

public long timeNeeded(int researchers)
{
	int k = 9;
	return (k+1)*timeNeeded() / (researchers+k);
}

public long timeNeeded(Player who)
{ return timeNeeded(who.getResearchAmount()); }

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

private static String bundleName()
{ return "net.cqs.config.base.ResearchEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@HtmlFragment
public String getDescription(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishDescription());
}

public String getImageName()
{ return imageName; }

private static NavigableSet<ResearchEnum> list;

private static NavigableSet<ResearchEnum> getList()
{
	if (list == null) list = new TreeSet<ResearchEnum>(Arrays.asList(values()));
	return list;
}

public static ResearchEnum getPrevious(ResearchEnum topic)
{ return getList().lower(topic); }

public static ResearchEnum getNext(ResearchEnum topic)
{ return getList().higher(topic); }

public static ResearchEnum valueOf(int index)
{ return values()[index]; }

}
