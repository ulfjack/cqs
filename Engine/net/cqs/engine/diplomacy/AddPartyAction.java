package net.cqs.engine.diplomacy;

import java.util.ArrayList;

public final class AddPartyAction implements ContractAction
{

private final DiplomaticRelation relation;
private final ContractParty party;
private final Contract contract;

public AddPartyAction(DiplomaticRelation relation, Contract contract, ContractParty party)
{
	this.relation = relation;
	this.contract = contract;
	this.party = party;
	
}

@Override
public Contract getContract()
{ return contract; }

@Override
public ArrayList<ContractParty> getParties()
{
	ArrayList<ContractParty> result = new ArrayList<ContractParty>();
	result.add(party);
	return result;
}

@Override
public void execute()
{
	if (contract.addMember(party))
		relation.signalChange(this);
}

}
