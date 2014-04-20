package net.cqs.engine.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

public final class MultiI18nMessage implements Serializable
{

private static final long serialVersionUID = 1L;

private final Locale defaultLocale;
private final HashMap<Locale,String> messages = new HashMap<Locale,String>();

public MultiI18nMessage(Locale defaultLocale)
{
	this.defaultLocale = defaultLocale;
}

public String get(Locale locale)
{
	String result = messages.get(locale);
	if (result == null) result = messages.get(defaultLocale);
	return result;
}

public void addI18n(Locale locale, String s)
{ messages.put(locale, s); }

}
