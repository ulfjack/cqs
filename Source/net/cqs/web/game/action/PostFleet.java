package net.cqs.web.game.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InputValidation;
import net.cqs.config.ResourceEnum;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.FleetState;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitMap;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.engine.human.HumanFleetController;
import net.cqs.main.config.Input;
import net.cqs.util.EnumLongMap;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostFleet
{

private static String prepareString(String s)
{ return s.replaceAll("[., ]", ""); }

static void init()
{
	ActionHandler.add("Fleet.Command.add", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String what = req.getParameter("action");
				int id = Input.decode(req.getParameter("id"), -1);
				Fleet f = session.getPlayer().findFleetById(id);
				try
				{
					FleetCommand c = ActionHandler.decodeCommand(what, request, "");
					if (c != null)
						((HumanFleetController) f.getController()).append(c);
				}
				catch (InvalidCommandException e)
				{ e.printStackTrace(); }
			}
		});
	
	ActionHandler.add("Fleet.Command.edit", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, final HttpServletRequest req)
			{
				//String what = req.getParameter("action");
				int id = Input.decode(req.getParameter("id"), -1);
				Fleet f = session.getPlayer().findFleetById(id);
				if (f == null) throw new ErrorCodeException(ErrorCode.INVALID_INPUT_NO_FLEET);
				
				int num = Integer.parseInt(prepareString(req.getParameter("num")));
				FleetCommand cmd = ((HumanFleetController) f.getController()).getCommand(num);
				if (cmd == null) throw new ErrorCodeException();
				
				FleetCommandEditor fce = ActionHandler.getCommandEditor(cmd.getClass());
				if (fce != null)
				{
					try
					{
						fce.edit(cmd, new ParameterMap()
							{
								public String get(String key)
								{ return req.getParameter(key); }
							});
					}
					catch (Exception e)
					{ e.printStackTrace(); }
				}
				else
					throw new RuntimeException("Fleet.Command.edit - Kein Editor vorhanden fuer "+cmd.getClass());
			}
		});
	
	ActionHandler.add("Fleet.Commands.delete", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String what = req.getParameter("action");
				int id = Input.decode(req.getParameter("id"), -1);
				Fleet f = session.getPlayer().findFleetById(id);
				if (f == null)
					throw new ErrorCodeException(ErrorCode.INVALID_INPUT_NO_FLEET);
				
				HumanFleetController controller = (HumanFleetController) f.getController();
				if ("beforeCurrent".equals(what))
					controller.removeBefore();
				else if ("afterCurrent".equals(what))
					controller.removeAfter();
				else if ("notCurrent".equals(what))
				{
					controller.removeBefore();
					controller.removeAfter();
				}
				else if ("all".equals(what))
					controller.removeAll();
			}
		});
	
	ActionHandler.add("Fleet.send", new PostHandler()
		{
			Pattern p1 = Pattern.compile("unit(\\w+)");
			Matcher m1 = p1.matcher("");
			
			Pattern p2 = Pattern.compile("command(\\w+)");
			Matcher m2 = p2.matcher("");
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				ActionHandler.logger.fine("Sending fleet!");
				
				Colony colony = session.getColony(Position.decode(request.getPostParameter("c")));
				
				// FIXME: what is this supposed to do?
/*				long rand = Long.parseLong(req.getParameter("requestCounter"));
				if (rand == session.requestCounter)
				{
					ActionHandler.logger.warning("Two requests!");
					return;
				}*/
				
				long secId = Long.parseLong(request.getPostParameter("secid"));
				if (secId != session.getSecurityId())
					throw new ErrorCodeException(ErrorCode.INVALID_SECID);
				
				List<FleetCommand> orders = new ArrayList<FleetCommand>();
				UnitMap units = new UnitMap();
				
				String name = request.getPostParameter("name");
				if ((name != null) && (name.length() > 0))
				{
					if (!InputValidation.validFleetName(name))
						name = null;
				}
				else
					name = null;
				
				TreeMap<String,String> commandMap = new TreeMap<String,String>();
				
				UnitSystem us = session.getFrontEnd().getUnitSystem();
				for (Iterator<String> it = request.getPostKeyIterator(); it.hasNext(); )
				{
					String key = it.next();
					String value = request.getPostParameter(key);
					
					m1.reset(key);
					if (m1.matches())
					{
						Unit l = us.parseUnit(m1.group(1));
						int k = Input.decode(value, -1);
						if (k > 0) units.increase(l, k);
					}
					
					m2.reset(key);
					if (m2.matches())
					{
						if ("on".equals(request.getPostParameter("active"+m2.group(1))))
							commandMap.put(m2.group(1), value);
					}
				}
				
				boolean mayStart = true;
				Iterator<Map.Entry<String,String>> it2 = commandMap.entrySet().iterator();
				while (it2.hasNext())
				{
					Map.Entry<String,String> entry = it2.next();
					String modifier = entry.getKey();
					String what = entry.getValue();
					try
					{
						FleetCommand c = ActionHandler.decodeCommand(what, request, modifier);
						if (c != null)
							orders.add(c);
					}
					catch (InvalidCommandException e)
					{
						ActionHandler.logger.log(Level.WARNING, "Exception caught", e);
						mayStart = false;
					}
				}
				
				if (units.sum() > 0)
				{
					ActionHandler.logger.info("Flotte verschicken!");
					Fleet f = Fleet.createFleet(colony, session.getGalaxy().getTime(), name, units, orders);
					if (f != null)
					{
						int desktop = Integer.parseInt(request.getPostParameter("desktop"));
						f.setDesktop(desktop);
						if (mayStart)
							f.resume();
						session.flag = 1;
					}
					else
						session.flag = 0;
				}
				else
					throw new ErrorCodeException(ErrorCode.FLEET_CANNOT_START_NO_SELECTION);
			}
		});
	
	ActionHandler.add("Fleet.join", new PostHandler()
		{
			Pattern p = Pattern.compile("fleet(-?\\d+)");
			Matcher m = p.matcher("");
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				List<Fleet> fleets = new ArrayList<Fleet>();
				
				Iterator<String> enumeration = request.getPostKeyIterator();
				while (enumeration.hasNext())
				{
					String key = enumeration.next();
					//String value = req.getParameter(key);
					
					m.reset(key);
					if (m.matches())
					{
						int i = Integer.parseInt(m.group(1));
						Fleet f = session.getPlayer().findFleetById(i);
						if (f == null)
							throw new ErrorCodeException(ErrorCode.FLEET_CANNOT_MERGE_NOT_VALID);
						fleets.add(f);
					}
				}
				
				if (fleets.size() < 2)
					throw new ErrorCodeException(ErrorCode.CANNOT_MERGE_FLEET_NO_SELECTION);
				
				int cmdlistid = Integer.parseInt(request.getPostParameter("cmdlist"));
				int cmdlistIndex = -1;
				for (int i = 0; i < fleets.size(); i++)
					if (fleets.get(i).getId() == cmdlistid)
						cmdlistIndex = i;
				if (cmdlistIndex > 0)
				{
					Fleet h = fleets.get(0);
					fleets.set(0, fleets.get(cmdlistIndex));
					fleets.set(cmdlistIndex, h);
				}
				else if (cmdlistIndex == -1)
					throw new ErrorCodeException(ErrorCode.FLEET_CANNOT_MERGE_NO_CMDLIST);
				
				boolean differentLanded = false;
				Fleet f0 = fleets.get(0);
				for (int i = 0; i < fleets.size(); i++)
				{
					Fleet f1 = fleets.get(i);
					if (!f0.getPosition().equals(f1.getPosition()))
						throw new ErrorCodeException(ErrorCode.CANNOT_MERGE_FLEET_NOT_SAME_POSITION);
					
					if (f0.getGalaxy() != f1.getGalaxy())
						throw new ErrorCodeException(ErrorCode.CANNOT_MERGE_FLEET_NOT_SAME_POSITION);
					
					if (f0.isLanded() != f1.isLanded())
						differentLanded = true;
					
					if (f1.getState() != FleetState.WAITING)
						throw new ErrorCodeException(ErrorCode.CANNOT_MERGE_FLEET_MOVING);
				}
				
				if (differentLanded)
				{
					UnitMap um = new UnitMap();
					for (int i = 0; i < fleets.size(); i++)
					{
						Fleet f = fleets.get(i);
						f.addUnitsTo(um);
					}
					
					if (um.getGroundUnitCapacity() >= um.getGroundUnitSize())
					{
						f0.setLanded(false);
						if ((f0.getPosition().specificity() == Position.COLONY) && f0.getPosition().isValid(f0.getGalaxy()))
						{
							if (f0.findColony().getOwner().alliedWith(f0.getOwner()))
								f0.setLanded(true);
						}
					}
					else
						throw new ErrorCodeException(ErrorCode.CANNOT_MERGE_FLEET_DIFFERENT_LANDED);
				}
				
				for (int i = 1; i < fleets.size(); i++)
				{
					Fleet f1 = fleets.get(i);
					f0.join(f1);
					f1.removeFleet();
				}
				int desktop = Integer.parseInt(request.getPostParameter("desktop"));
				f0.setDesktop(desktop);
			}
		});
	
	ActionHandler.add("Fleet.split", new PostHandler()
		{
			Pattern p1 = Pattern.compile("unit(\\w+)");
			Matcher m1 = p1.matcher("");
			
			Pattern p2 = Pattern.compile("res(\\d+)");
			Matcher m2 = p2.matcher("");
			
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				int id = Input.decode(request.getPostParameter("id"), -1);
				Fleet f = session.getPlayer().findFleetById(id);
				
				boolean transferCmds = "on".equalsIgnoreCase(
						request.getPostParameter("transferCmds"));
				
				if (f == null)
					throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_NOT_VALID);
				
				int firstDesktop = Input.decode(request.getPostParameter("desktop-oldfleet"), f.getDesktop());
				int secondDesktop = Input.decode(request.getPostParameter("desktop-newfleet"), f.getDesktop());

				String firstName = request.getPostParameter("name-oldfleet");
				String secondName = request.getPostParameter("name-newfleet");

				if (f.getState() != FleetState.WAITING)
					throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_MOVING);
				
				UnitMap secondUnits = new UnitMap();
				EnumLongMap<ResourceEnum> secondRes = EnumLongMap.of(ResourceEnum.class);
				
				UnitSystem us = session.getFrontEnd().getUnitSystem();
				Iterator<String> enumeration = request.getPostKeyIterator();
				while (enumeration.hasNext())
				{
					String key = enumeration.next();
					String value = request.getPostParameter(key);
					
					m1.reset(key);
					if (m1.matches())
					{
						Unit uid = us.parseUnit(m1.group(1));
						int amount = Integer.parseInt(prepareString(value));
						
						if ((amount < 0) || (amount > f.getUnits(uid)))
							throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_WRONG_INPUT);
						
						secondUnits.increase(uid, amount);
					}
					
					m2.reset(key);
					if (m2.matches())
					{
						int resnum = Integer.parseInt(m2.group(1));
						ResourceEnum res = ResourceEnum.get(resnum);
						long amount = Long.parseLong(prepareString(value));
						
						if ((amount < 0) || (amount > f.getCargo(res)))
							throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_WRONG_INPUT);
						
						secondRes.increase(res, amount);
					}
				}
				
				if (secondUnits.sum() == 0)
					throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_WRONG_INPUT);
				
				UnitMap firstUnits = f.getUnitsCopy();
				EnumLongMap<ResourceEnum> firstRes = f.getCargoCopy();
				try
				{
					firstUnits.subtract(secondUnits);
					firstRes.subtract(secondRes);
				}
				catch (Exception e)
				{
					throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_WRONG_INPUT);
				}
				
				if (firstUnits.sum() == 0)
					throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_WRONG_INPUT);
				
				if ((firstUnits.getResourceSpace() < firstRes.getSum()) ||
				    (secondUnits.getResourceSpace() < secondRes.getSum()))
						throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_RES_TRANS);
				if (!f.isLanded())
				{
					if ((firstUnits.getGroundUnitCapacity() < firstUnits.getGroundUnitSize()) ||
					    (secondUnits.getGroundUnitCapacity() < secondUnits.getGroundUnitSize()))
						throw new ErrorCodeException(ErrorCode.CANNOT_SPLIT_FLEET_GROUND_TRANS);
				}
				
				f.setDesktop(firstDesktop);
				f.setName(firstName);
				Fleet newF = f.splitFleet(secondUnits, secondRes, firstUnits, firstRes, transferCmds);
				if (newF != null)
				{
					newF.setDesktop(secondDesktop);
					newF.setName(secondName);
				}
			}
		});
	
	ActionHandler.add("Fleet.setDesktopsAndResume", new PostHandler()
		{
			Pattern p1 = Pattern.compile("fleetdesktop(-?\\d+)");
			Matcher m1 = p1.matcher("");
			
			Pattern p2 = Pattern.compile("fleetresume(-?\\d+)");
			Matcher m2 = p2.matcher("");
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Enumeration<?> enumeration = req.getParameterNames();
				while (enumeration.hasMoreElements())
				{
					String key = (String) enumeration.nextElement();
					String value = req.getParameter(key);
					
					m1.reset(key);
					if (m1.matches())
					{
						int i = Integer.parseInt(m1.group(1));
						Fleet f = session.getPlayer().findFleetById(i);
						int j = Integer.parseInt(value);
						if ((j >= 0) && (j <= Constants.MAX_DESKTOP))
							f.setDesktop(j);
					}
					
					m2.reset(key);
					if (m2.matches())
					{
						int i = Integer.parseInt(m2.group(1));
						Fleet f = session.getPlayer().findFleetById(i);
						boolean b = (value.toLowerCase().equals("on"));
						if (b && f.isStopped())
							f.resume();
					}
				}
			}
		});
}

private PostFleet()
{/*OK*/}

}
