package net.cqs.web;

import net.cqs.web.admin.XmlRuleConverterTest;
import net.cqs.web.charts.LineChartTest;
import net.cqs.web.game.FrontEndToolsTest;
import net.cqs.web.util.WebUtilTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CssTest.class,
	XmlPageTest.class,
	WebUtilTestSuite.class,
	XmlRuleConverterTest.class,
	LineChartTest.class,
	FrontEndToolsTest.class,
})
public class WebTestSuite
{
// Ok
}
