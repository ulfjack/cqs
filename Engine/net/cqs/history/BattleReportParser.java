package net.cqs.history;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.config.BattleMessageEnum;
import net.cqs.config.BattleStateEnum;
import net.cqs.config.BattleTypeEnum;
import net.cqs.config.BuildingEnum;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.units.UnitSystemImpl;
import net.cqs.main.persistence.FakeBattleEvent;
import net.cqs.main.persistence.BattleReport;
import net.cqs.main.persistence.FakeFleet;
import net.cqs.main.persistence.FakePlayer;

final class BattleReportParser
{

private static final Logger logger = Logger.getLogger(BattleReportParser.class.getName());

private static final Pattern LINE_PATTERN = Pattern.compile("(\\d+)\\.(\\w+)($|:)");
private static final Pattern PAIR_PATTERN = Pattern.compile("(\\w+)\\=(?:([\\w\\d:]+)|(?:\"([^\"]*)\"))");

private final HashMap<String,BattleLineProcessor> normalProcessing = new HashMap<String,BattleLineProcessor>();

public BattleReportParser()
{
	initNormalProcessing();
}

public void update(BattleReport report, String line)
{
	Matcher lineMatcher = LINE_PATTERN.matcher("");
	Matcher pairMatcher = PAIR_PATTERN.matcher("");
	HashMap<String,String> keyData = new HashMap<String,String>();
	report.rawEvents.add(line);
	
	lineMatcher.reset(line);
	if (!lineMatcher.lookingAt())
		throw new RuntimeException("Invalid line: "+line);
	
	if (report.startTime == 0)
		report.startTime = Long.parseLong(lineMatcher.group(1));
	
	BattleLineProcessor processor = normalProcessing.get(lineMatcher.group(2));
	if (processor != null)
	{
		keyData.clear();
		pairMatcher.reset(line);
		while (pairMatcher.find())
		{
			String key = pairMatcher.group(1);
			String value = pairMatcher.group(2);
			if (value == null) value = pairMatcher.group(3);
			keyData.put(key, value);
		}
		processor.process(report, line, keyData);
	}
	else
		logger.warning("Could not decode line: "+line);
}

private void initNormalProcessing()
{
	normalProcessing.put("info", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{/*OK*/}
		});
	
	normalProcessing.put("start", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				report.type = BattleTypeEnum.valueOf(data.get("type"));
				report.position = data.get("position");
			}
		});
	
	normalProcessing.put("end", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				report.winner = BattleStateEnum.valueOf(data.get("reason"));
				report.ended = true;
			}
		});
	
	normalProcessing.put("joinPlayer", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				String pid = data.get("pid");
				FakePlayer player = report.playerMaps[side].get(pid);
				if (player == null)
				{
					player = new FakePlayer();
					player.name = data.get("pname");
					player.tag = data.get("aname");
					report.playerMaps[side].put(pid, player);
				}
			}
		});
	
	normalProcessing.put("joinFleet", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				FakeFleet fleet = new FakeFleet();
				fleet.id = data.get("fid");
				fleet.name = "unbekannt";
				
				String units = data.get("units");
				fleet.joinUnits = new UnitMap(UnitSystemImpl.getImplementation(), units);
				
				String pid = data.get("pid");
				fleet.owner = report.playerMaps[side].get(pid);
				fleet.owner.numFleets++;
				fleet.owner.numUnits += fleet.joinUnits.sum();
				
				String msg = "Flotte "+fleet.id+" von "+fleet.owner.name;
				if (fleet.owner.tag != null)
					msg += " ["+fleet.owner.tag+"]";
				msg += " tritt dem Kampf bei.";
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
	
	normalProcessing.put("withdrawSuccess", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				report.currentRound = null;
				int side = Integer.parseInt(data.get("side"));
				String fid = data.get("fid");
				
				String pid = data.get("pid");
				FakePlayer owner = report.playerMaps[side].get(pid);
				
				String msg = "Flotte "+fid+" von "+owner.name;
				if (owner.tag != null)
					msg += " ["+owner.tag+"]";
				msg += " flieht.";
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
	
	normalProcessing.put("withdrawFailure", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				report.currentRound = null;
				int side = Integer.parseInt(data.get("side"));
				String fid = data.get("fid");
				
				String pid = data.get("pid");
				FakePlayer owner = report.playerMaps[side].get(pid);
				
				String msg = "Flotte "+fid+" von "+owner.name;
				if (owner.tag != null)
					msg += " ["+owner.tag+"]";
				msg += " wird bei der Flucht vernichtet.";
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
	
	normalProcessing.put("loose", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				String fid = data.get("fid");
				
				String pid = data.get("pid");
				FakePlayer owner = report.playerMaps[side].get(pid);
				
				String msg = "Flotte "+fid+" von "+owner.name;
				if (owner.tag != null)
					msg += " ["+owner.tag+"]";
				msg += " wird vernichtet.";
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
	
	normalProcessing.put("leave", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				String fid = data.get("fid");
				
				String pid = data.get("pid");
				FakePlayer owner = report.playerMaps[side].get(pid);
				
				String msg = "Flotte "+fid+" von "+owner.name;
				if (owner.tag != null)
					msg += " ["+owner.tag+"]";
				msg += " verlässt den Kampf.";
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
	
	normalProcessing.put("round", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int num = Integer.parseInt(data.get("num"));
				report.normalEvents.add(new FakeBattleEvent(0, "Runde "+num));
				report.currentRound = new FakeBattleEvent(1);
				report.normalEvents.add(report.currentRound);
			}
		});
	
	normalProcessing.put("endround", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				report.normalEvents.add(new FakeBattleEvent(0, "Ende der Runde"));
				report.currentRound = null;
			}
		});
	
	normalProcessing.put("unitsactive", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				FakeFleet fleet = new FakeFleet();
				fleet.id = data.get("fid");
				fleet.name = "unbekannt";
				
				String units = data.get("units");
				fleet.joinUnits = new UnitMap(UnitSystemImpl.getImplementation(), units);
				
				String pid = data.get("pid");
				fleet.owner = report.playerMaps[side].get(pid);
				
				if (report.currentRound == null)
				{
					report.currentRound = new FakeBattleEvent(1);
					report.normalEvents.add(report.currentRound);
				}
				report.currentRound.addUnitsActive(side, fleet);
			}
		});
	
	normalProcessing.put("unitslost", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				FakeFleet fleet = new FakeFleet();
				fleet.id = data.get("fid");
				fleet.name = "unbekannt";
				
				String units = data.get("units");
				fleet.joinUnits = new UnitMap(UnitSystemImpl.getImplementation(), units);
				report.totalLost[side].add(fleet.joinUnits);
				
				String pid = data.get("pid");
				fleet.owner = report.playerMaps[side].get(pid);
				
				if (report.currentRound == null)
				{
					report.currentRound = new FakeBattleEvent(1);
					report.normalEvents.add(report.currentRound);
				}
				report.currentRound.addUnitsLost(side, fleet);
			}
		});
	
	normalProcessing.put("unitsinvaded", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				int side = Integer.parseInt(data.get("side"));
				String fid = data.get("fid");
				
				String pid = data.get("pid");
				FakePlayer owner = report.playerMaps[side].get(pid);
				
				int lost = Integer.parseInt(data.get("unitcount"));
				String msg = "Flotte "+fid+" von "+owner.name;
				if (owner.tag != null)
					msg += " ["+owner.tag+"]";
				msg += " verliert durch die Invasion "+lost+" Einheiten.";
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
	
	normalProcessing.put("invade", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				BuildingEnum what = BuildingEnum.valueOf(data.get("result"));
				report.buildingsInvaded.increase(what);
			}
		});
	
	normalProcessing.put("burn", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				BuildingEnum what = BuildingEnum.valueOf(data.get("result"));
				report.buildingsBurned.increase(what);
			}
		});
	
	normalProcessing.put("message", new BattleLineProcessor()
		{
			@Override
			public void process(BattleReport report, String line, HashMap<String,String> data)
			{
				BattleMessageEnum what = BattleMessageEnum.valueOf(data.get("id"));
				String msg = what.getName(report.locale);
				report.normalEvents.add(new FakeBattleEvent(2, msg));
			}
		});
}

}
