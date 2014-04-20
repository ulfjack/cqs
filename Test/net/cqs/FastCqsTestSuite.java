package net.cqs;

import net.cqs.auth.AuthTestSuite;
import net.cqs.config.ConfigTestSuite;
import net.cqs.engine.EngineTestSuite;
import net.cqs.extern.ExternTestSuite;
import net.cqs.i18n.I18nTestSuite;
import net.cqs.main.MainTestSuite;
import net.cqs.plugins.PluginsTestSuite;
import net.cqs.services.ServicesTestSuite;
import net.cqs.util.UtilTestSuite;
import net.cqs.web.admin.XmlRuleConverterTest;
import net.cqs.web.charts.LineChartTest;
import net.cqs.web.util.WebUtilTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.ofahrt.CommonTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	AuthTestSuite.class,
	ConfigTestSuite.class,
	EngineTestSuite.class,
	UtilTestSuite.class,
	I18nTestSuite.class,
	MainTestSuite.class,
	ServicesTestSuite.class,
	PluginsTestSuite.class,
	ExternTestSuite.class,
//	WebTestSuite.class,
	WebUtilTestSuite.class,
	XmlRuleConverterTest.class,
	LineChartTest.class,
	
	// Also include the tests for de.ofahrt.*:
	CommonTestSuite.class
})
public class FastCqsTestSuite
{
// Ok
}
