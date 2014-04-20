package net.cqs.engine.diplomacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.cqs.engine.Player;

public final class DiplomaticRelation
{

	/* a pair of ContractParties */
	static final class PartyPair
	{
	public ContractParty firstParty;
	public ContractParty secondParty;
	
	public PartyPair(ContractParty a, ContractParty b)
	{
		firstParty = a;
		secondParty = b;
	}
	
	/* sum of individual hashCodes
	 * PartyPairs that are equal hence have the same hashCode
	 */
	@Override
	public int hashCode()
	{ return firstParty.hashCode() + secondParty.hashCode(); }
	
	/* returns true iff both pairs contain the same parties */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof PartyPair)) return false;
		PartyPair p = (PartyPair) o;
		return ( ((firstParty.equals(p.firstParty)) && (secondParty.equals(p.secondParty)))
		|| ((firstParty.equals(p.secondParty)) && (secondParty.equals(p.firstParty))) );
	}
	}
	
/* given two ContractPartners store the relevant diplomatic status */
private HashMap<PartyPair,DiplomacyEntry> relation;

public DiplomaticRelation()
{
	relation = new HashMap<PartyPair,DiplomacyEntry>();
}

/* returns the (best) diplomatic status between a and b */
/*public DiplomaticStatus getStatus(ContractParty a, ContractParty b)
{ return getEntry(a, b).getStatus(); }*/

private DiplomacyEntry bestOf(DiplomacyEntry e1, DiplomacyEntry e2)
{
	if (e1 == null) return e2;
	if (e2 == null) return e1;
	DiplomaticStatus status = e1.getStatus().bestOf(e2.getStatus());
	long attackTime = Math.max(e1.getAttackTime(), e2.getAttackTime());
	return new DiplomacyEntry(status, attackTime);	
}

private DiplomacyEntry worseOf(DiplomacyEntry e1, DiplomacyEntry e2, boolean maxAttackTime)
{
	if (e1 == null) return e2;
	if (e2 == null) return e1;
	DiplomaticStatus status = e1.getStatus().worseOf(e2.getStatus());
	long attackTime;
	if (maxAttackTime)
		attackTime = Math.max(e1.getAttackTime(), e2.getAttackTime());
	else 
		attackTime = Math.min(e1.getAttackTime(), e2.getAttackTime());
	return new DiplomacyEntry(status, attackTime);	
}

private DiplomacyEntry getWar(DiplomacyEntry war, DiplomacyEntry wait)
{
	if ((war == null) || (war.getStatus() != DiplomaticStatus.HOSTILE))
		throw new IllegalArgumentException();
	if (wait == null) return war;
	return new DiplomacyEntry(war.getStatus(),
			Math.max(war.getAttackTime(), wait.getAttackTime()));
}

/* returns the (best) DiplomacyEntry between a and b */
public DiplomacyEntry getEntry(ContractParty a, ContractParty b)
{	
	if ((a == null) || (b == null))
		throw new IllegalArgumentException();
	
	// check if same player
	if (a.equals(b))
		return new DiplomacyEntry(DiplomaticStatus.SELF, 0);

	DiplomacyEntry friendly = new DiplomacyEntry(DiplomaticStatus.NEUTRAL, 0);
	DiplomacyEntry wait = new DiplomacyEntry(DiplomaticStatus.WAIT, 0);
	DiplomacyEntry war = null;
	
	DiplomacyEntry tempEntry = relation.get(new PartyPair(a,b));
	if (tempEntry != null)
	{
		friendly = bestOf(tempEntry, friendly);
		if (tempEntry.getStatus() == DiplomaticStatus.HOSTILE)
			war = worseOf(tempEntry, war, false);
		if (tempEntry.getStatus() == DiplomaticStatus.WAIT)
			wait = worseOf(tempEntry, wait, true);
	}
	
	ContractParty superA;
	ContractParty superB;
	while ((a != null)  && (b != null))
	{
		superA = a.getSuperParty();
		superB = b.getSuperParty();
		if (superA != null)
		{
			tempEntry = relation.get(new PartyPair(superA, b));
			if (tempEntry != null)
			{
				friendly = bestOf(tempEntry, friendly);
				if (tempEntry.getStatus() == DiplomaticStatus.HOSTILE)
					war = worseOf(tempEntry, war, false);
				if (tempEntry.getStatus() == DiplomaticStatus.WAIT)
					wait = worseOf(tempEntry, wait, true);
			}
		}
		if (superB != null)
		{
			tempEntry = relation.get(new PartyPair(a, superB));
			if (tempEntry != null)
			{
				friendly = bestOf(tempEntry, friendly);
				if (tempEntry.getStatus() == DiplomaticStatus.HOSTILE)
					war = worseOf(tempEntry, war, false);
				if (tempEntry.getStatus() == DiplomaticStatus.WAIT)
					wait = worseOf(tempEntry, wait, true);
			}
		}
		if ((superA != null) && (superB != null))
		{
			tempEntry = relation.get(new PartyPair(superA, superB));
			if (tempEntry != null)
			{
				friendly = bestOf(tempEntry, friendly);
				if (tempEntry.getStatus() == DiplomaticStatus.HOSTILE)
					war = worseOf(tempEntry, war, false);
				if (tempEntry.getStatus() == DiplomaticStatus.WAIT)
					wait = worseOf(tempEntry, wait, true);
			}
		}
		// TODO
		// we don't really want to check all possibilies, but is this good?
		a = superA;
		b = superB; 
	}
	if ((friendly.getStatus() == DiplomaticStatus.NEUTRAL) && (war != null))
		return getWar(war, wait);
	return friendly;
}

public DiplomaticStatus getStatus(ContractParty a, ContractParty b)
{ return getEntry(a, b).getStatus(); }

/* update DiplomaticRelation when p joins or leaves a */
public void signalAllianceChange(Player p, Alliance a)
{
	if (p == null || a == null) return;
	signalChange(p, a);
	for (int i = 0; i < a.size(); i++)
		signalChange(p, a.get(i));
}

/* update the relationship between party and all elements of parties */
void signalChange(ContractParty party, ArrayList<ContractParty> parties)
{
	for (int i = 0; i < parties.size(); i++)
		signalChange(party, parties.get(i));
}

/* update DiplomaticRelation when changes to a Contract are made */
void signalChange(ContractAction action)
{
	Contract c = action.getContract();
	List<ContractParty> parties = action.getParties();
	List<ContractParty> plist = c.getParties();

	for (int i = 0; i < plist.size(); i++)
		for (int j = 0; j < parties.size(); j++)
			signalChange(plist.get(i), parties.get(j));
}

/* checks whether a and b are at war
 * sets entry in hashtable if so, if not entry is dropped
 * returns the relevant DiplomaticStatus
 */
private DiplomaticStatus checkAndPutWar(ContractParty a, ContractParty b)
{
	List<Contract> contracts = a.getContracts();
	long attackTime = 0;
	long attackTimeWar = 0;
	Contract contract = null;
	for (int i = 0; i < contracts.size(); i++)
	{
		Contract c = contracts.get(i);
		if (c.contains(b))
		{
			if (c.getStatus() == DiplomaticStatus.HOSTILE)
				attackTimeWar = attackTimeWar <= 0 ? c.getAttackTime() : Math.min(attackTimeWar, c.getAttackTime());
			else
				attackTime = Math.max(attackTime, c.getAttackTime());
				
			if ((contract == null) || c.getStatus().isWorse(contract.getStatus()))
				contract = c;
		}
	}
	if ((contract == null) || (contract.getStatus() == DiplomaticStatus.NEUTRAL)
			|| (contract.getStatus() == DiplomaticStatus.WAIT))
	{
		relation.remove(new PartyPair(a,b));
		return DiplomaticStatus.NEUTRAL;
	}
	else
	{
		relation.put(new PartyPair(a,b), new DiplomacyEntry(contract.getStatus(), Math.max(attackTime, attackTimeWar)));
		return contract.getStatus();
	}
}

/* updates the hashtable to best contract status between a and b
 * returns the DiplomaticStatus between a and b */
private DiplomaticStatus signalChange(ContractParty a, ContractParty b)
{
	if (a == null || b == null)
		throw new IllegalArgumentException();
	
	// same party
	if (a.equals(b)) return DiplomaticStatus.SELF;

	DiplomaticStatus newStatus = DiplomaticStatus.NEUTRAL;

	// same alliance
	if (a instanceof Player)
	{
		if (b instanceof Player)
		{
			if ((((Player) a).getAlliance() != null) && (((Player) a).getAlliance() == ((Player) b).getAlliance()))
				newStatus = DiplomaticStatus.ALLIED;			
		}
		else if (b instanceof Alliance)
		{
			if (((Player) a).getAlliance() == (Alliance) b)
				newStatus =  DiplomaticStatus.ALLIED;
		}
	}
	else if ((a instanceof Alliance) && (b instanceof Player))
	{	
		if ((Alliance) a == ((Player) b).getAlliance())
			newStatus =  DiplomaticStatus.ALLIED;
	}
	
	// diplomatic contracts
	List<Contract> contracts = a.getContracts();
	for (int i = 0; i < contracts.size(); i++)
	{
		Contract c = contracts.get(i);
		if ( c.contains(b) )
			newStatus = newStatus.bestOf(c.getStatus());
	}

	if (newStatus == DiplomaticStatus.NEUTRAL)
		return checkAndPutWar(a,b);
	else
	{
		relation.put(new PartyPair(a,b), new DiplomacyEntry(newStatus, 0));
		return newStatus;
	}
}

public void setData(Alliance[] alliances, Player[] players)
{
	for (int i = 0; i < alliances.length; i++)
		for (int j = i+1; j < alliances.length; j++)
			signalChange(alliances[i], alliances[j]);
	
	for (int i = 0; i < players.length; i++)
		for (int j = i+1; j < players.length; j++)
			signalChange(players[i], players[j]);
	
	for (int i = 0; i < players.length; i++)
		for (int j = 0; j < alliances.length; j++)
			signalChange(players[i], alliances[j]);
}
	
}
