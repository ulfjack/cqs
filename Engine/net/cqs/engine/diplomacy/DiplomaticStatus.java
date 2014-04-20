package net.cqs.engine.diplomacy;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum DiplomaticStatus implements Formattable
{

SELF("ownership", "O", 0),
ALLIED("alliance", "A", 1),
UNITED("union", "U", 2),
TRADE("trade agreement", "T", 3),
NAP("non-aggression pact", "N", 4),
NEUTRAL("neutrality", "&ndash;", 5),
WAIT("contract expiration", "E", 6),
HOSTILE("war declaration", "W", 7);

private final String englishTranslation;
private final String englishAbbreviation;
private final int index;

private static final int INDEX_ATTACK = 7;
private static final int INDEX_LAND_FLEETS_TO_DEFEND = 4;
private static final int INDEX_TRADE = 3;
private static final int INDEX_PASS_BLOCKADE = 2;
private static final int INDEX_LAND_FLEETS = 2;
private static final int INDEX_TRANSMIT = 1;

private DiplomaticStatus(String englishTranslation, String englishAbbreviation, int index)
{
	this.englishTranslation = englishTranslation;
	this.englishAbbreviation = englishAbbreviation;
	this.index = index;
}

public boolean isBetter(DiplomaticStatus other)
{
	if (other == null) return true;
	return (index < other.index);
}

public boolean isWorse(DiplomaticStatus other)
{
	if (other == null) return true;
	return (index > other.index);
}

/* returns the worse status of this and other */
public DiplomaticStatus worseOf(DiplomaticStatus other)
{ 
	if (other == null) return this;
	return isWorse(other) ? this : other;
}

/* returns the better status of this and other */
public DiplomaticStatus bestOf(DiplomaticStatus other)
{
	if (other == null) return this;
	return isBetter(other) ? this : other;
}

public boolean canTransmit()
{ return (index <= INDEX_TRANSMIT); }

public boolean canPassBlockade(boolean canAttack)
{
	if (canAttack) return (index <= INDEX_PASS_BLOCKADE);
	return (index <= INDEX_TRADE);
}

public boolean canLandToDefend()
{ return (index <= INDEX_LAND_FLEETS_TO_DEFEND); }

public boolean canLand()
{ return (index <= INDEX_LAND_FLEETS); }

public boolean canTrade()
{ return (index <= INDEX_TRADE); }

public boolean canAttack()
{ return (index == INDEX_ATTACK); }

public boolean canAttack(long time, long attackTime)
{
	if (index != INDEX_ATTACK) return false;
	if (attackTime == 0) return false;
	return (attackTime < time);
}

public boolean showAttackTime()
{ return (this == HOSTILE) || (this == WAIT); }

public String englishTranslation()
{ return englishTranslation; }

public String englishAbbreviation()
{ return englishAbbreviation; }

private static String bundleName()
{ return "net.cqs.engine.diplomacy.DiplomaticStatus"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

}

