package net.cqs.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;


public final class StringBundle
{

private static final Locale ROOT = new Locale("");

static String toKey(String baseName, Locale locale)
{
	if (locale.equals(ROOT))
		return baseName;
	return baseName+"_"+locale;
}

static Locale getParentLocale(Locale locale)
{
	String language = locale.getLanguage();
	String country = locale.getCountry();
	String variant = locale.getVariant();
	if (variant.length() > 0) return new Locale(language, country);
	if (country.length() > 0) return new Locale(language);
	if (language.length() > 0) return ROOT;
	return null;
}


private StringBundle parent;
private final String baseName;
private final Locale locale;
private final HashMap<String,String> properties = new HashMap<String,String>();

public StringBundle(String baseName, Locale locale)
{
	this.baseName = baseName;
	this.locale = locale;
}

public void setParent(StringBundle parent)
{ this.parent = parent; }

public StringBundle getParent()
{ return parent; }

public String getBaseName()
{ return baseName; }

public Locale getLocale()
{ return locale; }

void setString(String key, String value)
{ properties.put(key, value); }

public String getString(String key)
{
	String result = properties.get(key);
	if (result != null) return result;
	if (parent != null) result = parent.getString(key);
	if (result != null) return result;
	throw new MissingResourceException("resource not found", StringBundle.class.getName(), key);
}

public String getSafeString(String key)
{
	String result = properties.get(key);
	if (result != null) return result;
	if (parent != null) return parent.getSafeString(key);
	return key;
}

}