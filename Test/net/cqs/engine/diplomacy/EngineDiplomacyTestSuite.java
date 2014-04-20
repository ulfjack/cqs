package net.cqs.engine.diplomacy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	DiplomaticRelationTest.class,
	DiplomaticStatusTest.class,
	ContractProposalListTest.class
})
public class EngineDiplomacyTestSuite extends TestSuite
{
// Ok
}
