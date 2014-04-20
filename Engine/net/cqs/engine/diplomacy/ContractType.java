package net.cqs.engine.diplomacy;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.config.Constants;
import net.cqs.config.Time;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum ContractType implements Formattable
{

WAR("war declaration", "Before you can attack others, you need to declare "
		+"war on them. After declaring war, you have to wait "
		+Constants.WAIT_ATTACK_WAR/Time.hours(1)+" hours, so that the enemy "
		+"has time to receive the war declaration."),
NAP("non-aggression pact", "With this contract all parties promise not to "
		+"launch any attacks. When a non-aggression pact is canceled, "
		+Constants.WAIT_ATTACK_NAP/Time.hours(1)+" hours are reserved in "
		+"which you cannot attack each other."),
TRADE("trade agreement", "A trade agreement enables all parties to trade "
		+"resources with each other. This is done by unloading resources "
		+"at other's colonies. So contract members may pass each other's "
		+"blockades, as long as the fleets do not include any attack units. "
		+"When a trade agreement is canceled, "
		+Constants.WAIT_ATTACK_TRADE/3600+" hours are reserved in which you "
		+" cannot attack each other."),
UNION("union", "When joined in a union you can trade resources, as well as "
		+"pass the other's blockades with all your fleets - even when they "
		+"include attack units. When a union is canceled, "
		+Constants.WAIT_ATTACK_UNION/Time.hours(1)+" hours are reserved in "
		+"which you cannot attack each other."),
ALLIANCE("alliance", "An alliance expresses the highest level of trust. You "
		+"may trade resources, pass blockades with all fleets, and even use "
		+"the other's transmitters. When an alliance is canceled, "
		+Constants.WAIT_ATTACK_ALLIANCE/Time.hours(1)+" hours are reserved "
		+"in which you cannot attack each other.");

private final String englishTranslation;
private final String englishDescription;

private ContractType(String englishTranslation, String englishDescription)
{
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
}

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

private static String bundleName()
{ return "net.cqs.engine.diplomacy.ContractType"; }

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

public static ContractType valueOf(int key)
{ return values()[key]; }

}
