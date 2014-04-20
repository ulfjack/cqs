package net.cqs.config;

import net.cqs.config.base.ConfigBaseTestSuite;
import net.cqs.config.units.ConfigUnitTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ConfigBaseTestSuite.class,
	ConfigUnitTestSuite.class,
	
	InputValidationTest.class,
	InputTest.class
})
public class ConfigTestSuite
{
// Ok
}
