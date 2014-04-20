package net.cqs.i18n;

import java.util.Locale;

public abstract class StringBundleFactory
{

public abstract StringBundle get(String baseName, Locale locale);


private static StringBundleFactory factory;

public static void init(StringBundleFactory nfactory)
{ factory = nfactory; }

public static StringBundleFactory getStringBundleFactory()
{ return factory; }

public static StringBundle getBundle(String baseName, Locale locale)
{ return factory.get(baseName, locale); }

}
