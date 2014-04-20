package net.cqs.engine.diplomacy;

import java.io.Serializable;
import java.util.ArrayList;

import net.cqs.util.IntHashMap;

public final class ContractProposalList implements Serializable
{
	
private static final long serialVersionUID = 1L;

private IntHashMap<ContractProposal> map = new IntHashMap<ContractProposal>(ContractProposal.class);
private int ids = 0;
	
public ContractProposalList()
{/*OK*/}
	
public ContractProposal proposeContract(ContractType type, ContractParty[] parties, long time)
{
	if ((parties == null) || (parties.length == 1))
		return null;
	ContractProposal cp = new ContractProposal(type, parties, ids, time);
	map.put(ids, cp);
	ids++;
	return cp;
}
	
public ContractProposal get(int id)
{ return map.get(id); }
	
public Contract acceptProposal(DiplomaticRelation relation, int id, ContractParty p, long time)
{
	ContractProposal cp = get(id);
	cp.accept(p);
	if (cp.isAccepted())
		return createContract(relation, cp, time);
	return null;
}
	
public boolean declineProposal(int id, ContractParty p, long time)
{
	ContractProposal cp = get(id);
	if (!cp.isIn(p)) return false;
	else
	{
		map.remove(id);
		return true;
	}
}
	
private Contract createContract(DiplomaticRelation relation, ContractProposal cp, long time)
{
	if (!cp.isAccepted()) return null;
	DiplomaticStatus status;
	{
		switch (cp.getType())
		{
			case ALLIANCE: status = DiplomaticStatus.ALLIED; break;
			case UNION:    status = DiplomaticStatus.UNITED; break;
			case TRADE:    status = DiplomaticStatus.TRADE; break;
			case NAP:      status = DiplomaticStatus.NAP; break;
			default:       throw new IllegalArgumentException();
		}
		ArrayList<ContractParty> partylist = new ArrayList<ContractParty>();
		for (ContractParty p : cp)
			partylist.add(p);
		CreatePeacefulContract c = new CreatePeacefulContract(relation, status, partylist);
		c.execute();
		map.remove(cp.getId());
		return c.getContract();
	}
}
	
}
