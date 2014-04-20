package net.cqs.engine.diplomacy;

import java.util.ArrayList;

public final class CreatePeacefulContract implements ContractAction
{

private final DiplomaticRelation relation;
private final DiplomaticStatus status;
private final ArrayList<ContractParty> parties;
private PeacefulContract contract;
	
public CreatePeacefulContract(DiplomaticRelation relation, DiplomaticStatus status)
{
	this.relation = relation;
	this.status = status;
	this.parties = new ArrayList<ContractParty>();
}

public CreatePeacefulContract(DiplomaticRelation relation, DiplomaticStatus status, ArrayList<ContractParty> parties)
{
	this.relation = relation;
	this.status = status;
	this.parties = parties;
}

// TODO: change to PeacefulContract
@Override
public Contract getContract()
{ return contract; }

@Override
public ArrayList<ContractParty> getParties()
{ return parties; }

@Override
public void execute()
{
	contract = new PeacefulContract(status, parties);
	relation.signalChange(this);
}

}
