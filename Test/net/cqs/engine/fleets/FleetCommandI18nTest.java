package net.cqs.engine.fleets;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.cqs.I18nTestHelper;
import net.cqs.NamedParameterized;
import net.cqs.NamedParameterized.Parameters;
import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(NamedParameterized.class)
public class FleetCommandI18nTest
{

static {
	I18nTestHelper.init();
}

@Parameters
public static Collection<Object[]> data()
{
	ArrayList<Object[]> result = new ArrayList<Object[]>();
	for (Class<?> clazz : FleetCommandRegistry.FLEET_COMMAND_TYPES)
		result.add(new Object[] { clazz });
	return result;
}

private final Class<?> clazz;

public FleetCommandI18nTest(Class<?> clazz)
{
	this.clazz = clazz;
}

@Test
public void simpleTest() throws Exception
{
	Method m = clazz.getMethod("englishTranslation");
	String translation = (String) m.invoke(null);
	new MessageFormat(translation, Locale.US);
}

protected final String lookupTranslation(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(FleetCommand.bundleName(), locale);
	return bundle.getSafeString(clazz.getName());
}

@Test
public void testTranslation()
{
	for (Locale l : I18nTestHelper.TESTED_LOCALES)
	{
		String translation = lookupTranslation(l);
		new MessageFormat(translation, l);
	}
}

}
