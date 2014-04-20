package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum BattleStateEnum implements Formattable
{

UNDECIDED(-1, "Undecided"),
ATTACKER(0, "Attacker"),
DEFENDER(1, "Defender"),
ALLDEAD(2, "All Killed"),
ALLKILLED(3, "Internal Error"),
BLOCKING_EMPTY_PLANET(4, "A deserted planet was being blocked");

private final String englishTranslation;
private final int index;

private BattleStateEnum(int index, String englishTranslation)
{
	this.index = index;
	this.englishTranslation = englishTranslation;
}

public int index()
{ return index; }

public boolean isSide(int which)
{
	return ((which == 0) && (this == ATTACKER)) || ((which == 1) && (this == DEFENDER));
}

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.BattleStateEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

}
