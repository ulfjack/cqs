package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum CheckResult implements Formattable
{

OK("Ok", ErrorCode.INTERNAL_ERROR),
RESOURCE_MISSING("not enough resources", ErrorCode.NOT_ENOUGH_RESOURCES),
STEEL_MISSING("not enough steel", ErrorCode.NOT_ENOUGH_RESOURCES, ResourceEnum.STEEL),
OIL_MISSING("not enough oil", ErrorCode.NOT_ENOUGH_RESOURCES, ResourceEnum.OIL),
SILICON_MISSING("not enough silicon", ErrorCode.NOT_ENOUGH_RESOURCES, ResourceEnum.SILICON),
DEUTERIUM_MISSING("not enough deuterium", ErrorCode.NOT_ENOUGH_RESOURCES, ResourceEnum.DEUTERIUM),
PEOPLE_MISSING("not enough inhabitants", ErrorCode.NOT_ENOUGH_POPULATION);

private final String englishTranslation;
private final ErrorCode errorCode;
private final ErrorCode unitErrorCode;
private final ResourceEnum res;

private CheckResult(String englishTranslation, ErrorCode errorCode, ResourceEnum res)
{
	this.englishTranslation = englishTranslation;
	this.errorCode = errorCode;
	if (errorCode == ErrorCode.NOT_ENOUGH_POPULATION)
		unitErrorCode = ErrorCode.NOT_ENOUGH_POPULATION_UNIT;
	else if (errorCode == ErrorCode.NOT_ENOUGH_RESOURCES)
		unitErrorCode = ErrorCode.NOT_ENOUGH_RESOURCES_UNIT;
	else
		unitErrorCode = errorCode;
	this.res = res;
}

private CheckResult(String englishTranslation, ErrorCode error)
{ this(englishTranslation, error, null); }

public ErrorCode getBuildingErrorCode()
{ return errorCode; }

public ErrorCode getUnitErrorCode()
{ return unitErrorCode; }

public ResourceEnum getResource()
{ return res; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.CheckResult"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

public static CheckResult forResource(int id)
{
	switch (id)
	{
		case Resource.STEEL :     return STEEL_MISSING;
		case Resource.OIL :       return OIL_MISSING;
		case Resource.SILICON :   return SILICON_MISSING;
		case Resource.DEUTERIUM : return DEUTERIUM_MISSING;
	}
	return RESOURCE_MISSING;
}

}