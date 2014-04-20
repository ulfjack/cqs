package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum QueueEnum implements Formattable
{

BUILDING("building queue"),
PLANETAR("planetary unit queue"),
INTERPLANETAR("interplanetary unit queue"),
INTERSTELLAR("interstellar unit queue"),
EDUCATION("education queue"),
RESEARCH("research queue");

private final String englishTranslation;

private QueueEnum(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.base.QueueEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

public static QueueEnum getUnitQueue(int i)
{
	switch (i)
	{
		case 0 : return PLANETAR;
		case 1 : return INTERPLANETAR;
		case 2 : return INTERSTELLAR;
	}
	throw new IllegalArgumentException(""+i);
}

}
