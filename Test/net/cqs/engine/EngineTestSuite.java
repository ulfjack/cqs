package net.cqs.engine;

import net.cqs.engine.actions.EngineActionTestSuite;
import net.cqs.engine.base.EngineBaseTestSuite;
import net.cqs.engine.battles.EngineBattleTestSuite;
import net.cqs.engine.diplomacy.EngineDiplomacyTestSuite;
import net.cqs.engine.fleets.EngineFleetTestSuite;
import net.cqs.engine.messages.EngineMessagesTestSuite;
import net.cqs.engine.rtevents.EngineRteventsTestSuite;
import net.cqs.engine.scores.EngineScoreTestSuite;
import net.cqs.engine.units.EngineUnitsTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	EngineActionTestSuite.class,
	EngineBaseTestSuite.class,
	EngineBattleTestSuite.class,
	EngineDiplomacyTestSuite.class,
	EngineFleetTestSuite.class,
	EngineMessagesTestSuite.class,
	EngineRteventsTestSuite.class,
	EngineScoreTestSuite.class,
	EngineUnitsTestSuite.class,
	
	ColonyTest.class,
	FleetTest.class,
	GalaxyTest.class,
	ResourceListTest.class
})
public class EngineTestSuite
{
// Ok
}
