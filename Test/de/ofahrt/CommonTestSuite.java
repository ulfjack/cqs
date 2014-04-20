package de.ofahrt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.ofahrt.crypt.CryptTestSuite;
import de.ofahrt.i18n.I18nTestSuite;
import de.ofahrt.io.IoTestSuite;

@RunWith(Suite.class)
@SuiteClasses(
	{
		CryptTestSuite.class,
		I18nTestSuite.class,
		IoTestSuite.class
	})
public class CommonTestSuite
{

// Ok

}
