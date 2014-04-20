package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum AgeEnum implements Formattable
{

COLONISATION("Age of Colonisation"),
EXPANSION("Age of Expansion"),
CONFLICT("Age of Conflict"),
WAR("Age of War");

private final String englishTranslation;

private AgeEnum(String englishTranslation)
{
	this.englishTranslation = englishTranslation;
}

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.AgeEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

}
