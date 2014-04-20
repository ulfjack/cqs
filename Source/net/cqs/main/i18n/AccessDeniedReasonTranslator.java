package net.cqs.main.i18n;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;
import net.cqs.web.util.AccessDeniedReason;

public final class AccessDeniedReasonTranslator
{

private static EnumMap<AccessDeniedReason,String> getTranslations()
{
	EnumMap<AccessDeniedReason,String> result =
		new EnumMap<AccessDeniedReason,String>(AccessDeniedReason.class);
	result.put(AccessDeniedReason.UNKNOWN, "Login has failed due to an unknown reason.");
	result.put(AccessDeniedReason.NEW_COOKIE, "A cookie was newly generated. Did you deactivate cookies?");
	result.put(AccessDeniedReason.INVALID_IP, "It is not possible to log in from this IP with the used session-id.");
	result.put(AccessDeniedReason.NOT_LOGGED_IN, "Login has failed.");
	return result;
}

private static final EnumMap<AccessDeniedReason,String> englishTranslations = getTranslations();

public static final String getBundleName()
{ return "net.cqs.i18n.AccessDeniedReason"; }

public static final Collection<String> getEnglishTranslations()
{ return englishTranslations.values(); }

public static String getName(Locale locale, AccessDeniedReason reason)
{
	StringBundle bundle = StringBundleFactory.getBundle(getBundleName(), locale);
	return bundle.getSafeString(englishTranslations.get(reason));
}

}
