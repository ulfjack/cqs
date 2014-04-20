package net.cqs.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.cqs.I18nTestHelper;
import net.cqs.main.i18n.TippsAndTricks;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.ofahrt.ulfscript.analysis.xml.XmlState;

@RunWith(Parameterized.class)
public class TippsAndTricksTest
{

static {
	I18nTestHelper.init();
}

@Parameters
public static Collection<?> data()
{
	ArrayList<Object[]> result = new ArrayList<Object[]>();
	for (final Locale l : I18nTestHelper.TESTED_LOCALES)
		for (TippsAndTricks tipp : TippsAndTricks.values())
			result.add(new Object[] { tipp, l });
	return result;
}

private final TippsAndTricks tipp;
private final Locale locale;

public TippsAndTricksTest(TippsAndTricks tipp, Locale locale)
{
	this.tipp = tipp;
	this.locale = locale;
}

@Test
public void test()
{
	if (!XmlState.checkFragment(tipp.getText(locale)))
		Assert.fail("Not valid XML ("+locale+"):\n"+tipp.getText());
}

}
