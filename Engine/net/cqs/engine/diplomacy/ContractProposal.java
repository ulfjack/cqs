package net.cqs.engine.diplomacy;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ContractProposal implements Serializable, Iterable<ContractParty>
{

private static final long serialVersionUID = 1L;
	
private final int id;
private final ContractType type;
private final ContractParty[] parties;
private boolean[] partyAnswers;
private final long proposalTime;

public ContractProposal(ContractType type, ContractParty[] parties, int id, long time)
{
	if (type == ContractType.WAR)
		throw new IllegalArgumentException();
	this.type = type;
	this.parties = parties.clone();
	this.id = id;
	partyAnswers = new boolean[parties.length];
	for (int i = 0; i < partyAnswers.length; i++)
		partyAnswers[i] = false;
	proposalTime = time;
}

public int getId()
{ return id; }

public ContractType getType()
{ return type; }

public long getTime()
{ return proposalTime; }

public boolean hasAnswered(int i)
{
	if (i < 0 || i >= parties.length)
		throw new IllegalArgumentException();
	return partyAnswers[i];
}

public void accept(ContractParty p)
{
	if (p == null) return;
	for (int i = 0; i < parties.length; i++)
		if (p.equals(parties[i]))
			partyAnswers[i] = true;
}

public boolean isIn(ContractParty p)
{
	if (p == null) return false;
	for (int i = 0; i< parties.length; i++)
		if (p.equals(parties[i])) return true;
	return false;
}

/** return true iff all ContractParties have accepted **/
public boolean isAccepted()
{
	for (int i = 0; i < partyAnswers.length; i++)
		if (!partyAnswers[i]) return false;
	return true;	
}

public int size()
{ return parties.length; }

public boolean hasAccepted(int i)
{ return partyAnswers[i]; }

public ContractParty getParty(int i)
{ return parties[i]; }

@Override
public Iterator<ContractParty> iterator()
{
	return new Iterator<ContractParty>()
		{
			private int index = 0;
			@Override
			public boolean hasNext()
			{ return index < size(); }
			@Override
			public ContractParty next()
			{
				if (index >= size()) throw new NoSuchElementException();
				ContractParty result = getParty(index);
				index++;
				return result;
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

}
