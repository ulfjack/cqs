package net.cqs.main.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.cqs.engine.battles.Battle;

public final class FakeBattleEvent implements Serializable
{

private static final long serialVersionUID = 1L;

public int type;
public String data;
public ArrayList<FakeFleet>[] unitsLost;
public ArrayList<FakeFleet>[] unitsActive;

public FakeBattleEvent(int type, String data)
{
	this.type = type;
	this.data = data;
}

public FakeBattleEvent(int type)
{ this(type, null); }

public int getType()
{ return type; }

public String getText()
{ return data; }

public List<FakeFleet> getAttackerUnitsLost()
{
	if (unitsLost == null) return Collections.<FakeFleet>emptyList();
	return unitsLost[Battle.ATTACKER_SIDE];
}

public List<FakeFleet> getDefenderUnitsLost()
{
	if (unitsLost == null) return Collections.<FakeFleet>emptyList();
	return unitsLost[Battle.DEFENDER_SIDE];
}

public List<FakeFleet> getAttackerUnitsActive()
{
	if (unitsActive == null) return Collections.<FakeFleet>emptyList();
	return unitsActive[Battle.ATTACKER_SIDE];
}

public List<FakeFleet> getDefenderUnitsActive()
{
	if (unitsActive == null) return Collections.<FakeFleet>emptyList();
	return unitsActive[Battle.DEFENDER_SIDE];
}


@SuppressWarnings("unchecked")
private void initArrays()
{
	unitsLost = new ArrayList[2];
	unitsLost[0] = new ArrayList<FakeFleet>();
	unitsLost[1] = new ArrayList<FakeFleet>();
	
	unitsActive = new ArrayList[2];
	unitsActive[0] = new ArrayList<FakeFleet>();
	unitsActive[1] = new ArrayList<FakeFleet>();
}

public void addUnitsLost(int side, FakeFleet fleet)
{
	if (unitsLost == null)
		initArrays();
	unitsLost[side].add(fleet);
}

public void addUnitsActive(int side, FakeFleet fleet)
{
	if (unitsActive == null)
		initArrays();
	unitsActive[side].add(fleet);
}

}
