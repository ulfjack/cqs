package net.cqs.config;

import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum InfoEnum
{

QUEUE_EMPTY("Colony %s: %s is empty.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum
BUILDING_QUEUE_STOPPED_RESOURCES_MISSING_FOR_CONSTRUCTION("Colony %s: %s was stopped, because %s is missing for the construction of a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, ResourceEnum, Building
BUILDING_QUEUE_STOPPED_RESOURCES_MISSING_FOR_REMOVAL("Colony %s: %s was stopped, because %s is missing for pulling down a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, ResourceEnum, Building
BUILDING_QUEUE_STOPPED_POPULATION_MISSING_FOR_CONSTRUCTION("Colony %s: %s was stopped, because there is not enough population for the construction of a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, Building
BUILDING_QUEUE_STOPPED_POPULATION_MISSING_FOR_REMOVAL("Colony %s: %s was stopped, because there is not enough population for pulling down a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, Building
UNIT_QUEUE_STOPPED_RESOURCES_MISSING("Colony %s: %s was stopped, because %s is missing for drilling a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, ResourceEnum, Unit
UNIT_QUEUE_STOPPED_POPULATION_MISSING("Colony %s: %s was stopped, because there is not enough population for drilling a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, Unit
UNIT_QUEUE_STOPPED_UNIT_UNKNOWN("Colony %s: %s was stopped, because you have not satisfied the research dependencies for drilling a(n) %s.", hours(8), InfoboxEnum.DEFAULT), // Position, QueueEnum, Unit

FLEET_COLONIZED ("Position %s: A new colony was founded.",
                 hours(1), InfoboxEnum.DEFAULT), // Position
FLEET_EXPLORED  ("Planet %s of type %s was explored:<br/>size: %d<br/>steel: %d%%, oil: %d%%, silicon: %d%%, deuterium: %d%%<br/>quality of life: %d",
                 days(10), InfoboxEnum.EXPLORATION), // Position, type, int(size), int(steel), int(oil), int(silicon), int(deuterium), long(quality of life) 
FLEET_SETTLED   ("Fleet %s: a new settler was added.",
                 hours(1), InfoboxEnum.DEFAULT), // Fleet
FLEET_LOST_CARGO("Fleet %s: The following resources were lost:<br/>steel: %d, oil: %d, silicon: %d, deuterium: %d",
                 hours(2), InfoboxEnum.DEFAULT), // Fleet, int(steel), int(oil), int(silicon), int(deuterium)
FLEET_LOST_UNITS("Fleet %s: %d units were lost.",
                 hours(2), InfoboxEnum.DEFAULT), // Fleet, nr of lost units

BATTLE_REPORT  ("%3$s at %2$s: <a href=\"/BattleReports/%1$s\">view battle report</a>",
                days(20), InfoboxEnum.BATTLE), // id (for link), position, type
RESOURCES_PLUNDERED("Colony %s: The following resources were plundered:<br />steel:%d, oil:%d, silicon:%d, deuterium:%d)",
                days(4), InfoboxEnum.BATTLE),  // Position, int(steel), int(oil), int(silicion), int(deuterium)
RESOURCES_PLUNDERED_FROM_ENEMY("You plundered the following resources from colony %s:<br />steel:%d, oil:%d, silicon:%d, deuterium:%d)",
                        days(4), InfoboxEnum.BATTLE),  // Position, int(steel), int(oil), int(silicion), int(deuterium)

//FIXME
LOST_IN_SPACE           ("After a lost space-battle, your ground troups were lost.",
                         1, InfoboxEnum.BATTLE),
//FIXME
BATTLE_SUCC_INVASION    ("Successful invasion at %%<br/>Invaded: %d<br/>Burnt: %d",
                         1, InfoboxEnum.BATTLE),
//FIXME
BATTLE_BEEN_INVADED     ("Colony %% was invaded<br/>Invaded: %d<br/>Burnt: %d",
                         1, InfoboxEnum.BATTLE),
//FIXME
INVASION_TROOPS_DEDUCTED("%d infantry units were deducted from fleet %s to invade a colony.",
                         1, InfoboxEnum.BATTLE),

SPY_SET      ("Colony %2$s: Fleet %1$s successfully planted a spy.",
              hours(2), InfoboxEnum.ESPIONAGE), // Fleet, Position
SPY_LOST     ("Colony %2$s: Fleet %1$s lost a spy when trying to plant it.",
              hours(2), InfoboxEnum.ESPIONAGE), // Fleet, Position
SPY_NO_REPORT("Colony %s: Your spy did not create a spy report.",
              hours(2), InfoboxEnum.ESPIONAGE), // Position
SPY_REPORT   ("Colony %s: <a href=\"/AgentReports/%s\">view spy report</a>",
              days(4), InfoboxEnum.ESPIONAGE), // Position, id (for link)
SPY_FOUND    ("Colony %s: You exposed an enemy spy from %s!",
              days(2), InfoboxEnum.ESPIONAGE), // Position, Owner

NEW_EDUCATION_PROFESSOR("Colony %s: A new professor for %s was appointed.",
                        hours(2), InfoboxEnum.DEFAULT); // Position, Topic

// FIXME: the translations are mostly buggy,
// * replace with MessageFormat translations
// * english original should be placed in this file
// * test the translations

public static int hours(int num)
{ return 60*num; }

public static int days(int num)
{ return 12*60*num; }

private final String englishTranslation;
private final int rate;
private final InfoboxEnum target;

private InfoEnum(String englishTranslation, int rate, InfoboxEnum target)
{
	this.englishTranslation = englishTranslation;
	this.rate = rate;
	this.target = target;
}

public int getRate()
{ return rate; }

public InfoboxEnum getTarget()
{ return target; }

public String englishTranslation()
{ return englishTranslation; }

public static String bundleName()
{ return "net.cqs.config.base.InfoEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

}
