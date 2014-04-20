package de.ofahrt.i18n;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
	{
		PoHelperTest.class,
		PoEntryTest.class,
		PoParserTest.class,
		PoMergerTest.class
	})
public class I18nTestSuite
{
// Ok
}
