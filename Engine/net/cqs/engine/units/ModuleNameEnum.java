package net.cqs.engine.units;

import java.util.Locale;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum ModuleNameEnum
{

SURFACE_SPEED("planetary speed", "Make your planetary units go faster."),
SURFACE_ATTACK("planetary force", "Overpower your enemies with extra strong planetary units."),
SURFACE_DEFENSE("planetary hardiness", "Robust planetary units survive longer in battle."),

SPACE_SPEED("space speed", "Make your space units go faster."),
SPACE_ATTACK("space force", "Overpower your enemies with extra strong space units."),
SPACE_DEFENSE("space hardiness", "Robust space units survive longer in battle."),
SPACE_WARP("warp engine", "Enable interstellar travel (and make it faster)."),

RES_TRANSPORT("transport of resources", "Enable transport of resources."),
TROOP_TRANSPORT("transport of troops", "Enable transport of planetary units.");

private final String englishTranslation;
private final String englishDescription;

private ModuleNameEnum(String englishTranslation, String englishDescription)
{
	this.englishTranslation = englishTranslation;
	this.englishDescription = englishDescription;
}

public String englishTranslation()
{ return englishTranslation; }

public String englishDescription()
{ return englishDescription; }

private static String bundleName()
{ return "net.cqs.engine.units.ModuleNameEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@HtmlFragment
public String getDescription(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishDescription());
}

}
