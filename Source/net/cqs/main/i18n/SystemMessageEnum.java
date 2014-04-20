package net.cqs.main.i18n;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum SystemMessageEnum implements Formattable
{

INVITE_ALLIANCE("Invitation to an alliance"),
DENY_ALLIANCE("Alliance application declined"),
PROPOSE_CONTRACT("Contract proposal"),
CONTRACT_DECLINED("Contract declined"),
CONTRACT_CREATION("Contract created"),
LEAVE_CONTRACT("Contract cancelled"),
WAR_DECLARATION("War declaration"),
CANCEL_WAR_DECLARATION("War declaration cancelled");

private final String englishTranslation;

private SystemMessageEnum(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.i18n.SystemMessageEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

}
