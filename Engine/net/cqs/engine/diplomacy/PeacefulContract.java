package net.cqs.engine.diplomacy;

import java.util.ArrayList;

import net.cqs.config.Constants;

public final class PeacefulContract implements Contract
{

private static final long serialVersionUID = 1L;
	
private final DiplomaticStatus status;
private ArrayList<ContractParty> parties = new ArrayList<ContractParty>();
		
public PeacefulContract(DiplomaticStatus status)
{
	if (status == DiplomaticStatus.HOSTILE ||
		status == DiplomaticStatus.WAIT ||
		status == DiplomaticStatus.NEUTRAL ||
		status == DiplomaticStatus.SELF)
		throw new IllegalArgumentException();
	this.status = status;
}

public PeacefulContract(DiplomaticStatus status, ArrayList<ContractParty> parties)
{
	this(status);
	if (parties != null)
		for (int i = 0; i < parties.size(); i++)
			this.parties.add(parties.get(i));

	for (int i = 0; i < this.parties.size(); i++)
		this.parties.get(i).addContract(this);
}
	
@Override
public DiplomaticStatus getStatus()
{ return status; }

@Override
public ArrayList<ContractParty> getParties()
{ return parties; }

@Override
public boolean contains(ContractParty party)
{ return parties.contains(party); }

@Override
public boolean addMember(ContractParty party)
{
	if ((party == null) || contains(party))
		return false;
	parties.add(party);
	party.addContract(this);
	return true;
}

public long getAttackWait()
{
	switch (status)
	{
	case ALLIED: return Constants.WAIT_ATTACK_ALLIANCE;
	case UNITED: return Constants.WAIT_ATTACK_UNION;
	case TRADE:  return Constants.WAIT_ATTACK_TRADE;
	case NAP:    return Constants.WAIT_ATTACK_NAP;
	default: throw new IllegalArgumentException();
	}
}

@Override
public void checkSize()
{
	if (parties.size() > 1)
		return;
	for (int i = 0; i < parties.size(); i++)
		parties.get(i).removeContract(this);
	parties.clear();
}

@Override
public boolean removeMember(long time, ContractParty party)
{
	if ((party == null) || !contains(party))
		return false;
	if (parties.size() > 1)
		new WaitContract(parties, time+getAttackWait());
	parties.remove(party);
	party.removeContract(this);
	checkSize();
	return true;
}

@Override
public long getAttackTime()
{ return 0; }

@Override
public boolean partyMayLeave(ContractParty party)
{ return true; }

}
