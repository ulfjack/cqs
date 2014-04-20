package net.cqs.engine.diplomacy;

import java.util.ArrayList;

public final class WaitContract implements Contract
{

private static final long serialVersionUID = 1L;
	
private final long time;
private ArrayList<ContractParty> parties = new ArrayList<ContractParty>();
		
public WaitContract(ArrayList<ContractParty> parties, long time)
{
	this.time = time;
	if (parties != null)
		for (int i = 0; i < parties.size(); i++)
			this.parties.add(parties.get(i));

	for (int i = 0; i < this.parties.size(); i++)
		this.parties.get(i).addContract(this);
}
	
@Override
public DiplomaticStatus getStatus()
{ return DiplomaticStatus.WAIT; }

@Override
public ArrayList<ContractParty> getParties()
{ return parties; }

@Override
public boolean contains(ContractParty party)
{ return parties.contains(party); }

@Override
public boolean addMember(ContractParty party)
{ return false; }

@Override
public void checkSize()
{ /*ok*/ }

@Override
public boolean removeMember(long t, ContractParty party)
{ return false; }

@Override
public long getAttackTime()
{ return time; }

@Override
public boolean partyMayLeave(ContractParty party)
{ return false; }

}
