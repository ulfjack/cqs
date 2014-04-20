package net.cqs.engine.fleets;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FleetCommandI18nTest.class,
	FleetMoveCommandTest.class,
	RoutePlannerTest.class
})
public class EngineFleetTestSuite
{
// Ok
}
