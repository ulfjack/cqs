package net.cqs.main.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class LocaleProvider
{

private static final Logger logger = Logger.getLogger("net.cqs.i18n");

private static final Pattern LANG_PATTERN = Pattern.compile("(\\w\\w)(?:_(\\w\\w))?(?:_(\\w+))?");

private final Locale defaultLocale = Locale.GERMANY;
private final List<Locale> availableLocales = Arrays.asList(new Locale[] { Locale.GERMANY, Locale.US });

public LocaleProvider()
{/*OK*/}

public Locale getDefaultLocale()
{ return defaultLocale; }

public List<Locale> getAvailableLocales()
{ return availableLocales; }

private Locale internalFilter(Locale loc)
{
	// try complete match
	for (Locale l : availableLocales)
	{
		if (loc.equals(l))
			return l;
	}
	
	// try language and country
	for (Locale l : availableLocales)
	{
		if (loc.getLanguage().equals(l.getLanguage()))
		{
			if (loc.getCountry().equals(l.getCountry()))
				return l;
		}
	}
	
	// try at least language
	for (Locale l : availableLocales)
	{
		if (loc.getLanguage().equals(l.getLanguage()))
			return l;
	}
	
	return null;
}

public Locale filter(String value)
{
	logger.fine(value);
	Locale loc;
	Matcher m = LANG_PATTERN.matcher(value);
	if (m.matches())
	{
		if (m.group(2) == null)
			loc = new Locale(m.group(1));
		else if (m.group(3) == null)
			loc = new Locale(m.group(1), m.group(2));
		else
			loc = new Locale(m.group(1), m.group(2), m.group(3));
	}
	else
		return defaultLocale;
	
	Locale result = internalFilter(loc);
	return result == null ? defaultLocale : result;
}

public Locale filter(HttpServletRequest req)
{ return internalFilter(req.getLocale()); }

}
