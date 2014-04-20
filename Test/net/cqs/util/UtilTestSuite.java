package net.cqs.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	EnumIntMapTest.class,
	EnumLongMapTest.class,
	EnumQueueTest.class,
	HashedTreeTest.class,
	IntHashMapTest.class,
	IntIntMapTest.class
})
public class UtilTestSuite extends TestSuite
{
// Ok
}
