package net.cqs.engine.diplomacy;

import java.util.List;

public interface ContractParty
{

public String getName();	
	
/* get the superordinate contract party, eg. an alliance when this is a player */
public ContractParty getSuperParty();

/* get list of all contracts this party is involved in */
public List<Contract> getContracts();

/* add c to the party's contracts */
public void addContract(Contract c);

/* remove c from the party's contracts */
public void removeContract(Contract c);

/* returns whether this party is a player */
public boolean isPlayer();

}
