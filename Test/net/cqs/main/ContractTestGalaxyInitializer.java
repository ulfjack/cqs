package net.cqs.main;

import java.util.Locale;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.actions.AllianceCreateAction;
import net.cqs.engine.actions.PlayerCreateAction;
import net.cqs.engine.diplomacy.AddPartyAction;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.Contract;
import net.cqs.engine.diplomacy.CreatePeacefulContract;
import net.cqs.engine.diplomacy.CreateWarDeclaration;
import net.cqs.engine.diplomacy.DiplomaticRelation;
import net.cqs.engine.diplomacy.DiplomaticStatus;

public final class ContractTestGalaxyInitializer
{

public void initGalaxy(Galaxy galaxy)
{
	DiplomaticRelation relation = galaxy.getDiplomaticRelation();
	
	for (int i = 0; i < 8; i++)
		galaxy.schedex(new PlayerCreateAction(null, "Testspieler "+(i+1), Locale.GERMANY));
	
	Player p1 = galaxy.findPlayerByName("Testspieler 1");
	Player p2 = galaxy.findPlayerByName("Testspieler 2");
	Player p3 = galaxy.findPlayerByName("Testspieler 3");
	Player p4 = galaxy.findPlayerByName("Testspieler 4");
	Player p5 = galaxy.findPlayerByName("Testspieler 5");
	Player p6 = galaxy.findPlayerByName("Testspieler 6");
	Player p7 = galaxy.findPlayerByName("Testspieler 7");
	
//	Player p8 = galaxy.findPlayerByName("UlfJack");
//	Player p9 = galaxy.findPlayerByName("Sara");
	
	galaxy.schedex(new AllianceCreateAction(p1, "MULTI", "MULTI"));
	galaxy.schedex(new AllianceCreateAction(p2, "Testallianz", "TEST"));
	
//	Alliance a1 = p1.alliance;
	Alliance a2 = p2.getAlliance();
	
	CreatePeacefulContract create = new CreatePeacefulContract(relation, DiplomaticStatus.NAP);
	create.execute();
	Contract contract = create.getContract();
	AddPartyAction addAction = new AddPartyAction(relation, contract, p1);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p2);
	addAction.execute();
	
	create = new CreatePeacefulContract(relation, DiplomaticStatus.TRADE);
	create.execute();
	contract = create.getContract();
	addAction = new AddPartyAction(relation, contract, p1);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p3);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p4);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, a2);
	addAction.execute();
	
	create = new CreatePeacefulContract(relation, DiplomaticStatus.UNITED);
	create.execute();
	contract = create.getContract();
	addAction = new AddPartyAction(relation, contract, p1);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p2);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p4);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p5);
	addAction.execute();
	addAction = new AddPartyAction(relation, contract, p6);
	addAction.execute();
	
	CreateWarDeclaration war = new CreateWarDeclaration(relation, p1, p7, galaxy.getTime());
	war.execute();
}

}
