package net.cqs.engine.diplomacy;

import java.util.ArrayList;
import java.util.List;

class TestParty implements ContractParty
{

DiplomaticRelation relation;
List<Contract> list;
ContractParty superParty;

TestParty(DiplomaticRelation relation)
{
	this.relation = relation;
	this.list = new ArrayList<Contract>();
}

@Override
public String getName()
{ return "x"; }

@Override
public ContractParty getSuperParty()
{ return superParty; }

@Override
public List<Contract> getContracts()
{ return list; }

@Override
public void addContract(Contract c)
{ list.add(c); }

@Override
public void removeContract(Contract c)
{ list.remove(c); }

public void setSuperParty(ContractParty superParty)
{ this.superParty = superParty; }

@Override
public boolean isPlayer()
{ return false; }
	
}