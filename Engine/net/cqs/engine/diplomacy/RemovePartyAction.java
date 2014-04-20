package net.cqs.engine.diplomacy;

import java.util.ArrayList;

public final class RemovePartyAction implements ContractAction
{

private final DiplomaticRelation relation;
private final long time;
private final ContractParty party;
private final Contract contract;

public RemovePartyAction(long time, DiplomaticRelation r, ContractParty party, Contract c)
{
	this.time = time;
	this.relation = r;
	this.contract = c;
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
	ArrayList<ContractParty> parties = new ArrayList<ContractParty>(contract.getParties());
	if (contract.removeMember(time, party))
	{
		parties.remove(party);
		relation.signalChange(party, parties);
	}
}

}
