package net.cqs.engine.diplomacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class DiplomaticRelationTest
{

//private Galaxy galaxy;
private DiplomaticRelation relation;

@Before
public void setUp()
{
//	galaxy = GalaxyBuilder.buildTestGalaxy();
	relation = new DiplomaticRelation(); //galaxy.getDiplomaticRelation();
}

@Test
public void testPartyPair()
{
	TestParty party1 = new TestParty(relation);
	TestParty party2 = new TestParty(relation);
	TestParty party3 = new TestParty(relation);
	TestParty party4 = new TestParty(relation);
	DiplomaticRelation.PartyPair pair1 = new DiplomaticRelation.PartyPair(party1, party2);
	DiplomaticRelation.PartyPair pair2 = new DiplomaticRelation.PartyPair(party1, party2);
	DiplomaticRelation.PartyPair pair3 = new DiplomaticRelation.PartyPair(party2, party1);
	DiplomaticRelation.PartyPair pair4 = new DiplomaticRelation.PartyPair(party1, party1);
	DiplomaticRelation.PartyPair pair5 = new DiplomaticRelation.PartyPair(party1, party3);
	DiplomaticRelation.PartyPair pair6 = new DiplomaticRelation.PartyPair(party3, party4);

	assertTrue(pair1.equals(pair2));
	assertTrue(pair1.equals(pair3));
	assertTrue(pair2.equals(pair3));

	assertFalse(pair1.equals(pair4));
	assertFalse(pair1.equals(pair5));
	assertFalse(pair1.equals(pair6));
	
	assertEquals(pair1.hashCode(), pair2.hashCode());
	assertEquals(pair1.hashCode(), pair3.hashCode());
	assertEquals(pair2.hashCode(), pair3.hashCode());
	assertFalse(pair1.hashCode() == pair4.hashCode());
	assertFalse(pair1.hashCode() == pair5.hashCode());
	assertFalse(pair1.hashCode() == pair6.hashCode());
}

@Test
public void testContract()
{
	TestParty a1 = new TestParty(relation);
	TestParty a2 = new TestParty(relation);
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(a1, a2).getStatus());
	CreatePeacefulContract createUnion = new CreatePeacefulContract(relation, DiplomaticStatus.UNITED);
	createUnion.execute();
	PeacefulContract union = (PeacefulContract) createUnion.getContract();
	AddPartyAction action1 = new AddPartyAction(relation, union, a1);
	AddPartyAction action2 = new AddPartyAction(relation, union, a2);
	action1.execute();
	action2.execute();

	assertEquals(1, a1.getContracts().size());
	assertEquals(1, a2.getContracts().size());
	assertEquals(union, a1.getContracts().get(0));
	assertEquals(DiplomaticStatus.SELF, relation.getEntry(a1, a1).getStatus());
	assertEquals(union.getStatus(), relation.getEntry(a1, a2).getStatus());
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(a1, a2).getStatus());
}

@Test
public void testContract2()
{
	TestParty[] party = new TestParty[4];
	for (int i = 0; i < party.length; i++)
		party[i] = new TestParty(relation);
	
	party[0].setSuperParty(party[1]);
	party[2].setSuperParty(party[3]);
	
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(party[0], party[2]).getStatus());

	CreateWarDeclaration createWar = new CreateWarDeclaration(relation, party[0], party[2], 0);
	createWar.execute();
	
	CreatePeacefulContract createTrade = new CreatePeacefulContract(relation, DiplomaticStatus.TRADE);
	createTrade.execute();
	PeacefulContract t = (PeacefulContract) createTrade.getContract();
	AddPartyAction action1 = new AddPartyAction(relation, t, party[1]);
	action1.execute();
	AddPartyAction action2 = new AddPartyAction(relation, t, party[2]);
	action2.execute();
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(party[1], party[3]).getStatus());
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(party[0], party[3]).getStatus());
	assertEquals(DiplomaticStatus.TRADE, relation.getEntry(party[1], party[2]).getStatus());

	CreatePeacefulContract createUnion = new CreatePeacefulContract(relation, DiplomaticStatus.UNITED);
	createUnion.execute();
	PeacefulContract u = (PeacefulContract) createUnion.getContract();
	AddPartyAction action3 = new AddPartyAction(relation, u, party[1]);
	action3.execute();
	AddPartyAction action4 = new AddPartyAction(relation, u, party[3]);
	action4.execute();
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(party[1], party[3]).getStatus());
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(party[0], party[3]).getStatus());
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(party[1], party[2]).getStatus());
}

@Test
public void testContract3()
{
	TestParty p0 = new TestParty(relation);
	TestParty p1 = new TestParty(relation);
	
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(p0, p1).getStatus());
	
	CreatePeacefulContract createUnion = new CreatePeacefulContract(relation, DiplomaticStatus.UNITED);
	createUnion.execute();
	PeacefulContract u = (PeacefulContract) createUnion.getContract();
	new AddPartyAction(relation, u, p0).execute();
	new AddPartyAction(relation, u, p1).execute();
	
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(p0, p1).getStatus());
	
	new RemovePartyAction(1, relation, p1, u).execute();
	
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(p0, p1).getStatus());
}

@Test
public void testContract4()
{
	TestParty p0 = new TestParty(relation);
	TestParty p1 = new TestParty(relation);
	
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(p0, p1).getStatus());
	
	CreatePeacefulContract createUnion = new CreatePeacefulContract(relation, DiplomaticStatus.UNITED);
	createUnion.execute();
	PeacefulContract u = (PeacefulContract) createUnion.getContract();
	new AddPartyAction(relation, u, p0).execute();
	new AddPartyAction(relation, u, p1).execute();
	
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(p0, p1).getStatus());
	
	new RemovePartyAction(1, relation, p1, u).execute();
	
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(p0, p1).getStatus());
	
	CreateWarDeclaration createWar = new CreateWarDeclaration(relation, p0, p1, 2);
	createWar.execute();
	
	assertEquals(DiplomaticStatus.HOSTILE, relation.getEntry(p0, p1).getStatus());
}

@Test
public void testContract5()
{
	TestParty p0 = new TestParty(relation);
	TestParty p1 = new TestParty(relation);
	
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(p0, p1).getStatus());
	
	ArrayList<ContractParty> parties = new ArrayList<ContractParty>();
	parties.add(p0);
	parties.add(p1);
	CreatePeacefulContract createUnion = new CreatePeacefulContract(relation, DiplomaticStatus.UNITED,
			parties);
	createUnion.execute();
	
	assertEquals(DiplomaticStatus.UNITED, relation.getEntry(p0, p1).getStatus());
}

}
