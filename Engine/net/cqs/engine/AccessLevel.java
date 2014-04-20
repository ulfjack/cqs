package net.cqs.engine;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum AccessLevel implements Formattable
{

FULL("full"),
GUEST("guest"),
NONE("none");

private final String englishTranslation;

private AccessLevel(String englishTranslation)
{
	this.englishTranslation = englishTranslation;
}

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.engine.AccessLevel"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

public boolean loginAllowed()
{ return this != NONE; }

public boolean maySeeMap()
{ return this == FULL; }

public boolean mayAttack()
{ return this == FULL; }

}
