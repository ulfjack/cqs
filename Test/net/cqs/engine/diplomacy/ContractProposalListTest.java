package net.cqs.engine.diplomacy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyBuilder;
import net.cqs.engine.Player;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class ContractProposalListTest
{

private ContractProposalList list;
private DiplomaticRelation relation;
private Galaxy galaxy;
private Player player;

@Before
public void setUp()
{
	list = new ContractProposalList();
	galaxy = GalaxyBuilder.buildTestGalaxy();
	relation = galaxy.getDiplomaticRelation();
	player = galaxy.createTestPlayer(Locale.GERMANY);
}

@Test
public void testProposeContractSimple()
{
	ContractParty[] parties = new ContractParty[3];
	for (int i = 0; i < parties.length; i++)
		parties[i] = new TestParty(relation);
	ContractProposal cp = list.proposeContract(ContractType.ALLIANCE, parties, 0);
	assertEquals(cp, list.get(0));
}
	
@Test
public void testProposeContractAccept()
{
	ContractParty[] parties = new ContractParty[3];
	for (int i = 0; i < parties.length; i++)
		parties[i] = new TestParty(relation);
	ContractProposal cp = list.proposeContract(ContractType.ALLIANCE, parties, 0);
	assertEquals(cp, list.get(0));
	list.acceptProposal(relation, cp.getId(), parties[0], 0);
	assertEquals(cp, list.get(0));
	list.acceptProposal(relation, cp.getId(), parties[1], 0);
	assertEquals(cp, list.get(0));
	list.acceptProposal(relation, cp.getId(), parties[2], 0);
	assertEquals(null, list.get(0));
	assertEquals(DiplomaticStatus.ALLIED, relation.getEntry(parties[0], parties[1]).getStatus());
	assertEquals(DiplomaticStatus.ALLIED, relation.getEntry(parties[0], parties[2]).getStatus());
	assertEquals(DiplomaticStatus.ALLIED, relation.getEntry(parties[1], parties[2]).getStatus());
}

@Test
public void testProposeContractDecline()
{
	ContractParty[] parties = new ContractParty[3];
	for (int i = 0; i < parties.length; i++)
		parties[i] = new TestParty(relation);
	ContractProposal cp = list.proposeContract(ContractType.ALLIANCE, parties, 0);
	list.acceptProposal(relation, cp.getId(), parties[0], 0);
	assertEquals(cp, list.get(0));
	list.declineProposal(cp.getId(), parties[1], 0);
	assertEquals(null, list.get(0));
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(parties[0], parties[1]).getStatus());
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(parties[0], parties[2]).getStatus());
	assertEquals(DiplomaticStatus.NEUTRAL, relation.getEntry(parties[1], parties[2]).getStatus());
}

@Test
public void testProposeContractMulti()
{
	ContractParty[] parties0 = new ContractParty[3];
	ContractParty[] parties1 = new ContractParty[4];
	ContractParty[] parties2 = new ContractParty[2];
	for (int i = 0; i < parties0.length; i++)
		parties0[i] = new TestParty(relation);
	for (int i = 0; i < parties1.length; i++)
		parties1[i] = new TestParty(relation);
	for (int i = 0; i < parties2.length; i++)
		parties2[i] = new TestParty(relation);

	ContractProposal[] cp = new ContractProposal[3];
	cp[0] = list.proposeContract(ContractType.ALLIANCE, parties0, 0);
	cp[1] = list.proposeContract(ContractType.ALLIANCE, parties1, 0);
	cp[2] = list.proposeContract(ContractType.ALLIANCE, parties2, 0);

	for (int i = 0; i < cp.length; i++)
		assertEquals(cp[i], list.get(i));

	for (int i = 0; i < parties0.length; i++)
		list.acceptProposal(relation, cp[0].getId(), parties0[i], 0);
	assertEquals(null, list.get(0));
	assertEquals(cp[1], list.get(1));
	assertEquals(cp[2], list.get(2));
	
	for (int i = 0; i < parties1.length; i++)
		list.acceptProposal(relation, cp[1].getId(), parties1[i], 0);
	assertEquals(null, list.get(1));
	assertEquals(cp[2], list.get(2));
	
	list.declineProposal(2, parties2[1], 0);
	assertEquals(null, list.get(2));
}

/*
@Test
public void testAllianceChange()
{
	Alliance a = new Alliance(galaxy, "Testalliance", "TEST");
	ContractParty[] parties = new ContractParty[4];
	for (int i = 0; i < parties.length-1; i++)
		parties[i] = new TestParty(relation);
	parties[parties.length-1] = a;
	ContractProposal cp = list.proposeContract(ContractType.ALLIANCE, parties, 0);
	for (int i = 0; i < parties.length; i++)
		list.acceptProposal(relation, cp.id, parties[i], 0);
	for (int i = 0; i < parties.length; i++)
		assertEquals(DiplomaticStatus.NEUTRAL, relation.getStatus(player, parties[i]));

	a.add(0, player);
	for (int i = 0; i < parties.length; i++)
		assertEquals(DiplomaticStatus.ALLIED, relation.getStatus(player, parties[i]));
}*/

@Test
public void testWarDeclaration()
{
	ContractParty attacker = new TestParty(relation);
	ContractParty victim = new TestParty(relation);
	CreateWarDeclaration war = new CreateWarDeclaration(relation, attacker, victim, galaxy.getTime());
	war.execute();
	assertEquals(DiplomaticStatus.HOSTILE, relation.getEntry(attacker, victim).getStatus());
}

}
