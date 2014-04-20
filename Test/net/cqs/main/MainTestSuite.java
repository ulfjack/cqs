package net.cqs.main;

import net.cqs.main.config.ConfigTestSuite;
import net.cqs.main.i18n.TranslationProviderTest;
import net.cqs.main.persistence.PersistenceTestSuite;
import net.cqs.main.plugins.PluginConfigTest;
import net.cqs.main.setup.GameConfigurationParserTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ConfigTestSuite.class,
	PersistenceTestSuite.class,
	PluginConfigTest.class,
	GameConfigurationParserTest.class,
	TranslationProviderTest.class
})
public class MainTestSuite
{
// Ok
}
