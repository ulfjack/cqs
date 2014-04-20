package net.cqs.main.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.cqs.config.BattleStateEnum;
import net.cqs.config.BattleTypeEnum;
import net.cqs.config.BuildingEnum;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.battles.Battle;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;
import net.cqs.util.EnumIntMap;

public final class BattleReport implements Serializable
{

private static final long serialVersionUID = 1L;

public static final int MODE_NORMAL = 0;
public static final int MODE_RAW    = 1;

// FIXME: battle reports always show german event translations
public Locale locale = Locale.GERMANY;
public long startTime = 0;
public boolean ended = false;
public BattleTypeEnum type = BattleTypeEnum.SIMULATION_BATTLE;
public BattleStateEnum winner = BattleStateEnum.UNDECIDED;
public String position = "unbekannt";
public ArrayList<String> rawEvents = new ArrayList<String>();
public ArrayList<FakeBattleEvent> normalEvents = new ArrayList<FakeBattleEvent>();
public HashMap<String,FakePlayer>[] playerMaps;
public UnitMap[] totalLost = new UnitMap[2];
public EnumIntMap<BuildingEnum> buildingsInvaded = EnumIntMap.of(BuildingEnum.class);
public EnumIntMap<BuildingEnum> buildingsBurned = EnumIntMap.of(BuildingEnum.class);

public FakeBattleEvent currentRound;

@SuppressWarnings("unchecked")
public BattleReport()
{
	playerMaps = new HashMap[2];
	playerMaps[0] = new HashMap<String,FakePlayer>();
	playerMaps[1] = new HashMap<String,FakePlayer>();
	totalLost[0] = new UnitMap();
	totalLost[1] = new UnitMap();
}

public BattleTypeEnum getType()
{ return type; }

public long getStartTime()
{ return startTime; }

public String getPosition()
{ return position; }

public BattleStateEnum getState()
{ return winner; }

public HashMap<String,FakePlayer> getAttackers()
{ return playerMaps[Battle.ATTACKER_SIDE]; }

public HashMap<String,FakePlayer> getDefenders()
{ return playerMaps[Battle.DEFENDER_SIDE]; }

public List<FakeBattleEvent> getNormalEvents()
{ return normalEvents; }

public UnitMap getAttackerTotalLost()
{ return totalLost[Battle.ATTACKER_SIDE]; }

public UnitMap getDefenderTotalLost()
{ return totalLost[Battle.DEFENDER_SIDE]; }

public int getBuildingsInvaded(BuildingEnum building)
{ return buildingsInvaded.get(building); }

public int getBuildingsBurned(BuildingEnum building)
{ return buildingsBurned.get(building); }

public List<String> getRawEvents()
{ return rawEvents; }


public static String getBinding(String id)
{ return "BATTLEREPORT-"+id; }

public static BattleReport getBattleReport(String pid)
{
	String binding = getBinding(pid);
	return Context.getDataManager().getBinding(binding, BattleReport.class);
}

public static void createBattleReport(String pid)
{
	String binding = getBinding(pid);
	BattleReport data = new BattleReport();
	Context.getDataManager().setBinding(binding, data);
}

public static BattleReport getBattleReportCopy(Storage storage, String id)
{
	try
	{
		String binding = getBinding(id);
		return storage.getCopy(binding, BattleReport.class);
	}
	catch (NameNotBoundException e)
	{ return null; }
}

}
