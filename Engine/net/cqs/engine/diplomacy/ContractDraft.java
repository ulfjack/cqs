package net.cqs.engine.diplomacy;

import java.io.Serializable;
import java.util.ArrayList;

public final class ContractDraft implements Serializable
{
	
private static final long serialVersionUID = 1L;

private ArrayList<ContractParty> parties;
private ContractParty initiator;
private ContractType type;

public ContractDraft(ContractType type, ContractParty initiator)
{
	this.type = type;
	parties = new ArrayList<ContractParty>();
	this.initiator = initiator;
}

public void addParty(ContractParty party)
{ if (!parties.contains(party)) parties.add(party); }
	
public void removeParty(ContractParty party)
{ parties.remove(party); }

public ContractParty getInitiator()
{ return initiator; }

public void setInitiator(ContractParty newInitiator)
{
	if (parties.contains(newInitiator))
		parties.remove(newInitiator);
	initiator = newInitiator;
}

public void setType(ContractType newType)
{ type = newType; }

public ContractType getType()
{ return type; }

public int size()
{ return parties.size(); }

public ContractParty get(int i)
{
	if (i < 0 || i >= size())
		throw new ArrayIndexOutOfBoundsException();
	return parties.get(i);
}

public ContractParty[] getParties()
{
	ContractParty[] result = new ContractParty[parties.size()];
	parties.toArray(result);
	return result;
}


}
