package net.cqs.engine.diplomacy;

import java.io.Serializable;
import java.util.List;

public interface Contract extends Serializable
{

// TODO(Add id).

/* returns true iff party is a member of the contract */
public boolean contains(ContractParty party);

/* returns the DiplomaticStatus */
public DiplomaticStatus getStatus();

/* returns the list of members */
public List<ContractParty> getParties();

/* adds party to the contract members
 * returns true iff the member was added
 * in particular, if party was already a member false is returned */
public boolean addMember(ContractParty party);

/* checks the number of members
 * if at most 1, then the contract is removed
 * else nothing is done
 */ 
public void checkSize();

/* removes the party from the contract
 * returns true iff the member was removed
 * in particular, if party was not a member false is returned */
public boolean removeMember(long time, ContractParty party);

/* returns the time from which on you are allowed to attack */
public long getAttackTime();

/* returns whether a given alliance may leave this contract */
public boolean partyMayLeave(ContractParty a);

}
