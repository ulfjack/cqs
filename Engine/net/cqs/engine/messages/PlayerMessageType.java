package net.cqs.engine.messages;

import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum PlayerMessageType
{

MESSAGE("player message"),
FORUM("forum message"),
APPLICATION("application"),
INVITATION("invitation"),
SYSTEM("system message"),
EVENT("event"),
CONTRACT_PROPOSAL("contract proposal"),
CONTRACT_CREATION("contract creation"),
CONTRACT_DECLINED("contract declined"),
CONTRACT_TIMEOUT("contract timeout"),
CONTRACT_LEFT("contract left"),
WAR_DECLARATION("war declaration"),
WAR_DECLARATION_CANCELLED("war declaration cancelled");

private final String englishTranslation;

private PlayerMessageType(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.engine.messages.PlayerMessageType"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

}
