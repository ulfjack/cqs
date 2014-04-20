package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum BattleMessageEnum implements Formattable
{

NO_DEFENDER_WIN("The attacker wins, because there is no defense."),
INVASION_SUCCESSFUL("The invasion was successful."),
ROB_SUCCESSFUL("Resources were robbed.");

private final String englishTranslation;

private BattleMessageEnum(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.BattleMessageEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

}
