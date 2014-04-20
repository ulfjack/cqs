package net.cqs.engine.base;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AttributeMapTest.class,
	AttributeTest.class,
	DependencyListTest.class,
	EventHandleTest.class,
	EventQueueTest.class,
	UnitMapTest.class,
	UnitQueueTest.class,
	UnitStatsTest.class
})
public class EngineBaseTestSuite
{
// Ok
}
