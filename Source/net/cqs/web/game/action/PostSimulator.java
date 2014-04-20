package net.cqs.web.game.action;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ResourceEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.battles.SimulatorBattle;
import net.cqs.main.config.Input;
import net.cqs.main.persistence.PlayerData;
import net.cqs.util.EnumLongMap;
import net.cqs.util.IntIntMap;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostSimulator
{

public static final int MAX_FIXED = 1;

static void chooseUnits(UnitMap units, EnumLongMap<ResourceEnum> res, CqsSession session,
		IntIntMap fixedData, IntIntMap uniqueData, IntIntMap slotData)
{
	UnitSystem us = session.getFrontEnd().getUnitSystem();
	for (int i = 0; i < MAX_FIXED; i++)
	{
		int amount = fixedData.get(i);
		if (amount == 0) continue;
		
		switch (i)
		{
			case 0 :
				Unit militia = us.getMilitia();
				if (militia != null) units.increase(militia, amount);
				break;
		}
	}
	
	Unit[] uniqueUnits = us.getUniqueUnits();
	for (int i = 0; i < uniqueUnits.length; i++)
	{
		int amount = uniqueData.get(i);
		if (amount == 0) continue;
		
		units.increase(uniqueUnits[i], amount);
		for (ResourceEnum resource : ResourceEnum.values())
			res.increase(resource, uniqueUnits[i].getCost().getResource(resource)*(long) amount);
	}
	
	PlayerData pdata = session.getPlayerDataCopy();
	for (int i = 0; i < pdata.unitAmount(); i++)
	{
		int amount = slotData.get(i);
		if (amount == 0) continue;
		Unit slot = pdata.getUnitDesign(i);
		
		units.increase(slot, amount);
		for (ResourceEnum resource : ResourceEnum.values())
			res.increase(resource, slot.getCost().getResource(resource)*(long) amount);
	}
}

static void init()
{
	ActionHandler.add("Simulator.join", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				IntIntMap fixedData = new IntIntMap();
				for (int i = 0; i < SimulatorBattle.MAX_FIXED; i++)
				{
					int count = Input.decode(req.getParameter("fixed"+i), 0);
					fixedData.increase(i, count);
				}
				
				IntIntMap defaultData = new IntIntMap();
				UnitSystem us = session.getFrontEnd().getUnitSystem();
				Unit[] uniqueUnits = us.getUniqueUnits();
				for (int i = 0; i < uniqueUnits.length; i++)
				{
					int count = Input.decode(req.getParameter("default"+i), 0);
					defaultData.increase(i, count);
				}
				
				IntIntMap slotData = new IntIntMap();
				PlayerData pdata = session.getPlayerDataCopy();
				for (int i = 0; i < pdata.unitAmount(); i++)
				{
					int count = Input.decode(req.getParameter("slot"+i), 0);
					slotData.increase(i, count);
				}
				
				boolean space = ("space".equals(req.getParameter("kind")));
				
				int side;
				if ("attacker".equals(req.getParameter("side")))
					side = Battle.ATTACKER_SIDE;
				else
					side = Battle.DEFENDER_SIDE;
				
				if (session.simBattle == null)
				{
					session.simBattle = new SimulatorBattle(session.getGalaxy().getTime(), space, session.getPlayer());
					session.simBattle.init();
				}
				
				UnitMap units = new UnitMap();
				EnumLongMap<ResourceEnum> res = EnumLongMap.of(ResourceEnum.class);
				chooseUnits(units, res, session, fixedData, defaultData, slotData);
				
				if ("add".equals(req.getParameter("todo")))
					session.simBattle.addFleet(units, res, side);
				else
					session.simBattle.removeFleet(units, res, side);
			}
		});
	
	ActionHandler.add("Simulator.exec", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req) 
			{
				session.simBattle.doTick(session.getGalaxy().getTime());
			}
		});
	
	ActionHandler.add("Simulator.setKind", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req) 
			{
				session.simBattle.setKind("space".equals(req.getParameter("kind")));
			}
		});
	
	ActionHandler.add("Simulator.reset", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req) 
			{
				boolean space;
				if (session.simBattle == null) space = false;
				else space = session.simBattle.isSpace();
				session.simBattle = new SimulatorBattle(session.getGalaxy().getTime(), space, session.getPlayer());
				session.simBattle.init();
			}
		});
}

private PostSimulator()
{/*OK*/}

}
