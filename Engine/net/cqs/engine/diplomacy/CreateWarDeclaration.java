package net.cqs.engine.diplomacy;

import java.util.ArrayList;

public final class CreateWarDeclaration implements ContractAction
{

private final DiplomaticRelation relation;
private final long time;
private final ContractParty attacker;
private final ContractParty victim;
private Contract contract;
	
public CreateWarDeclaration(DiplomaticRelation relation, ContractParty attacker, ContractParty victim, long time)
{
	this.relation = relation;
	this.time = time;
	this.attacker = attacker;
	this.victim = victim;
}

@Override
public Contract getContract()
{ return contract; }

@Override
public ArrayList<ContractParty> getParties()
{
	ArrayList<ContractParty> result = new ArrayList<ContractParty>();
	result.add(attacker);
	result.add(victim);
	return result;
}

@Override
public void execute()
{
	contract = new WarDeclaration(attacker, victim, time);
	relation.signalChange(this);
}

}
