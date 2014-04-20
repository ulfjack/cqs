package net.cqs.main.i18n;

import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

import de.ofahrt.ulfscript.LanguageText;

public final class StringBundleText implements LanguageText
{

//private static HashMap<String,StringBundleText> languageMap = new HashMap<String,StringBundleText>();

/*private static Locale copyLocale(Locale other, String variant)
{ return new Locale(other.getLanguage(), other.getCountry(), variant); }*/

public static StringBundleText getLanguageText(String bundleName, Locale locale)
{
	if (locale == null) throw new NullPointerException();
	return new StringBundleText(StringBundleFactory.getBundle(bundleName, locale), locale);
}


private final StringBundle bundle;
private final Locale locale;

private StringBundleText(StringBundle bundle, Locale locale)
{
	this.bundle = bundle;
	this.locale = locale;
}

public Locale getLocale()
{ return locale; }

@Override
public String getText(String locdesc, String text)
{ return bundle.getSafeString(text); }

@Override
public long lastModified()
{ return 0; }

}
