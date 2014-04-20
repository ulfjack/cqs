package net.cqs.i18n;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.cqs.I18nTestHelper;
import net.cqs.NamedParameterized;
import net.cqs.NamedParameterized.Parameters;
import net.cqs.main.i18n.PotFileCreator;
import net.cqs.web.StrictXHtmlTestCallback;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ofahrt.ulfscript.analysis.xml.XmlState;

@RunWith(NamedParameterized.class)
public class XmlDescriptionTest
{

static {
	I18nTestHelper.init();
}

@Parameters
public static Collection<Object[]> data()
{
	ArrayList<Object[]> result = new ArrayList<Object[]>();
	for (Class<? extends Enum<?>> clazz : PotFileCreator.ENUMS_TO_TRANSLATE)
	{
		try
		{
			clazz.getMethod("getDescription", Locale.class);
			result.add(new Object[] { clazz });
		}
		catch (NoSuchMethodException e)
		{/*OK*/}
	}
	return result;
}

private final Class<? extends Enum<?>> clazz;
private final Method descriptionMethod;

public XmlDescriptionTest(Class<? extends Enum<?>> clazz) throws Exception
{
	this.clazz = clazz;
	// If getDescription exists, englishDescription must exist as well!
	Method result = null;
	try
	{
		result = clazz.getMethod("getDescription", Locale.class);
	}
	catch (NoSuchMethodException e)
	{/*OK*/}
	descriptionMethod = result;
}

@Test
public void checkHtmlFragment() throws Exception
{
	for (Enum<?> e : clazz.getEnumConstants())
	{
		for (Locale l : I18nTestHelper.TESTED_LOCALES)
		{
  		String s = (String) descriptionMethod.invoke(e, l);
  		XmlState.validateFragment(new StrictXHtmlTestCallback(true), s);
		}
	}
}

}
