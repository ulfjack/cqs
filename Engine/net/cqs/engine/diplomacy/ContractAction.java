package net.cqs.engine.diplomacy;

import java.util.ArrayList;

public interface ContractAction
{
public ArrayList<ContractParty> getParties();
public Contract getContract();
public void execute();

}
