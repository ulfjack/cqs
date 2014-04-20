package de.ofahrt.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
	{
		Base64Test.class,
		HexTest.class
	})
public class IoTestSuite
{
// Ok
}
