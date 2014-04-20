package net.cqs.engine.diplomacy;

import java.util.ArrayList;

import net.cqs.config.Constants;

public final class WarDeclaration implements Contract
{

private static final long serialVersionUID = 1L;
	
private ContractParty attacker;
private ContractParty victim;
private final long time;
	
WarDeclaration(ContractParty attacker, ContractParty victim, long time)
{
	this.attacker = attacker;
	this.victim = victim;
	this.time = time;

	this.attacker.addContract(this);
	this.victim.addContract(this);
}

@Override
public ArrayList<ContractParty> getParties()
{
	ArrayList<ContractParty> result = new ArrayList<ContractParty>();
	if (attacker != null) result.add(attacker);
	if (victim != null) result.add(victim);
	return result;
}

public ContractParty getAttacker()
{ return attacker; }
	
public ContractParty getVictim()
{ return victim; }
	
@Override
public boolean contains(ContractParty c)
{
	if (c == null)
		return false;
	return ( c.equals(attacker) || c.equals(victim) );
}
	
@Override
public DiplomaticStatus getStatus()
{ return DiplomaticStatus.HOSTILE; }
	
@Override
public boolean addMember(ContractParty party)
{ return false; }

@Override
public void checkSize()
{
	if ((attacker == null) || (victim == null))
	{
		ContractParty lastMember = (attacker == null ? victim : attacker);
		if (lastMember != null)
			lastMember.removeContract(this);
	}
	
}

@Override
public boolean removeMember(long t, ContractParty party)
{
	if (!contains(party))
		return false;
	if (party.equals(attacker))
		attacker = null;
	else victim = null;
	party.removeContract(this);
	checkSize();
	return true;
}

public long getTime()
{ return time; }

@Override
public long getAttackTime()
{ return time+Constants.WAIT_ATTACK_WAR; }

@Override
public boolean partyMayLeave(ContractParty party)
{ return party.equals(attacker); }
	
}
