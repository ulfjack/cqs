package net.cqs.i18n;

import java.util.Locale;

public class ManagedLocale
{

private final Locale locale;

public ManagedLocale(Locale locale)
{ this.locale = locale; }

public Locale getLocale()
{ return locale; }

public StringBundle getBundle(String baseName)
{ return StringBundleFactory.getBundle(baseName, locale); }

public String getSafeString(String baseName, String originalText)
{ return getBundle(baseName).getSafeString(originalText); }

@Override
public int hashCode()
{ return locale.hashCode(); }

@Override
public String toString()
{ return locale.toString(); }

}
