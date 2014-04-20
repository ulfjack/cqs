package net.cqs.config;

import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum InfoboxEnum
{

DEFAULT("events"),
EXPLORATION("exploration"),
ESPIONAGE("espionage"),
BATTLE("battles");

private final String englishTranslation;

private InfoboxEnum(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

public static String bundleName()
{ return "net.cqs.config.base.InfoboxEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

}
