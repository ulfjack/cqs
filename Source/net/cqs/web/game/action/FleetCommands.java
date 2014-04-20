package net.cqs.web.game.action;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.Resource;
import net.cqs.engine.Position;
import net.cqs.engine.fleets.FleetAttackBlockadeCommand;
import net.cqs.engine.fleets.FleetAttackCommand;
import net.cqs.engine.fleets.FleetBlockPlanetCommand;
import net.cqs.engine.fleets.FleetBreakBlockadeCommand;
import net.cqs.engine.fleets.FleetColonizeCommand;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.engine.fleets.FleetDefendColonyCommand;
import net.cqs.engine.fleets.FleetDisbandCommand;
import net.cqs.engine.fleets.FleetDonateCommand;
import net.cqs.engine.fleets.FleetExploreCommand;
import net.cqs.engine.fleets.FleetGateCommand;
import net.cqs.engine.fleets.FleetInvasionCommand;
import net.cqs.engine.fleets.FleetLandCommand;
import net.cqs.engine.fleets.FleetLoadCommand;
import net.cqs.engine.fleets.FleetMoveCommand;
import net.cqs.engine.fleets.FleetRobCommand;
import net.cqs.engine.fleets.FleetSettleCommand;
import net.cqs.engine.fleets.FleetSpyCommand;
import net.cqs.engine.fleets.FleetStationCommand;
import net.cqs.engine.fleets.FleetStopCommand;
import net.cqs.engine.fleets.FleetTakeoffCommand;
import net.cqs.engine.fleets.FleetUnloadCommand;
import net.cqs.engine.fleets.FleetWaitCommand;
import net.cqs.web.ParsedRequest;

final class FleetCommands
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

private static String prepareString(String s)
{ return s.replaceAll("[., ]", ""); }

private final HashMap<String,FleetCommandFactory> factoryMap = new HashMap<String,FleetCommandFactory>();
private final IdentityHashMap<Class<? extends FleetCommand>,FleetCommandEditor> editorMap = new IdentityHashMap<Class<? extends FleetCommand>,FleetCommandEditor>();

public FleetCommands()
{
	add("none", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return null; }
		});
	
	add("invadeColony", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetInvasionCommand(); }
		});
	
	add("attackColony", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetAttackCommand(); }
		});
	
	add("attackBlockade", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetAttackBlockadeCommand(); }
		});
	
	add("blockPlanet", new FleetCommandFactory()
		{
				public FleetCommand create(ParameterMap params)
				{ return new FleetBlockPlanetCommand(); }
		});
	
	add("defendColony", new FleetCommandFactory()
		{
				public FleetCommand create(ParameterMap params)
				{ return new FleetDefendColonyCommand(); }
		});
	
	add("singleSettle", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetSettleCommand(1); }
		});
	
	add("settle", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				int howmany = Integer.parseInt(params.get("settleramount"));
				if (howmany >= 1)
					return new FleetSettleCommand(howmany);
				return null;
			}
		});
	
	add("singleSpyColony", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetSpyCommand(1); }
		});
	
	add("spyColony", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				int howmany = Integer.parseInt(params.get("spyamount"));
				if (howmany >= 1)
					return new FleetSpyCommand(howmany);
				return null;
			}
		});
	
	add("stop", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetStopCommand(); }
		});
	
	add("gate", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				Position p = Position.decode(params.get("targetposition"));
				return new FleetGateCommand(p);
			}
		});	
	
	add("move", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				Position p = Position.decode(params.get("targetposition"));
				return new FleetMoveCommand(p, false);
			}
		});
	
	add("breakBlockade", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				return new FleetBreakBlockadeCommand();
			}
		});
	
	add("takeoff", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetTakeoffCommand(); }
		});
	
	add("land", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetLandCommand(); }
		});
	
	add("stationHome", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetStationCommand(); }
		});
	
	add("stationAlly", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetDonateCommand(true); }
		});
	
	add("stationAny", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetDonateCommand(false); }
		});
	
	add("explore", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetExploreCommand(); }
		});
	
	add("colonize", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetColonizeCommand(); }
		});
	
	add("disband", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{ return new FleetDisbandCommand(); }
		});
	
	add("waitfor", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				int what = Integer.parseInt(prepareString(params.get("waitparam")));
				if (what >= 1)
					return new FleetWaitCommand(FleetWaitCommand.WAIT_FOR, what);
				return null;
			}
		});
	
	add("waituntil", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				int what = Integer.parseInt(prepareString(params.get("waitparam")));
				if (what >= 1)
					return new FleetWaitCommand(FleetWaitCommand.WAIT_UNTIL, what);
				return null;
			}
		});
	
	add("loadMulti", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				FleetLoadCommand result = new FleetLoadCommand();
				boolean valid = false;
				for (int i = Resource.MIN; i <= Resource.MAX; i++)
				{
					String param = params.get("resource-"+i+"-cmd");
					if ((param != null) && (param.length() > 0))
					{
						int what = Integer.parseInt(prepareString(param));
						valid |= what > 0;
						result.setAmount(i, what);
					}
				}
				if (!valid) return null;
				return result;
			}
		});
	add("unloadMulti", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				FleetUnloadCommand result = new FleetUnloadCommand();
				for (int i = Resource.MIN; i <= Resource.MAX; i++)
				{
					String param = params.get("resource-"+i+"-cmd");
					if ((param != null) && (param.length() > 0))
					{
						int what = Integer.parseInt(prepareString(param));
						result.setAmount(i, what);
					}
				}
				return result;
			}
		});
	add("unloadAllMulti", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				FleetUnloadCommand result = new FleetUnloadCommand();
				for (int i = Resource.MIN; i <= Resource.MAX; i++)
					result.setAmount(i, -1);
				return result;
			}
		});
	
	add("robColonyMulti", new FleetCommandFactory()
		{
			public FleetCommand create(ParameterMap params)
			{
				FleetRobCommand result = new FleetRobCommand();
				for (int i = Resource.MIN; i <= Resource.MAX; i++)
				{
					String param = params.get("resource-"+i+"-cmd");
					if ((param != null) && (param.length() > 0))
					{
						int what = Integer.parseInt(prepareString(param));
						if (what < 0) what = -1;
						result.setAmount(i, what);
					}
				}
				return result;
			}
		});
	
	add(FleetLoadCommand.class, new FleetCommandEditor()
		{
			public void edit(FleetCommand cmd, ParameterMap params)
			{
				FleetLoadCommand command = (FleetLoadCommand) cmd;
				int r0 = Integer.parseInt(prepareString(params.get("r0")));
				int r1 = Integer.parseInt(prepareString(params.get("r1")));
				int r2 = Integer.parseInt(prepareString(params.get("r2")));
				int r3 = Integer.parseInt(prepareString(params.get("r3")));
				command.setAmounts(r0, r1, r2, r3);
				int l0 = Integer.parseInt(prepareString(params.get("l0")));
				int l1 = Integer.parseInt(prepareString(params.get("l1")));
				int l2 = Integer.parseInt(prepareString(params.get("l2")));
				int l3 = Integer.parseInt(prepareString(params.get("l3")));
				command.setRests(l0, l1, l2, l3);
			}
		});
	
	add(FleetUnloadCommand.class, new FleetCommandEditor()
		{
			public void edit(FleetCommand cmd, ParameterMap params)
			{
				FleetUnloadCommand command = (FleetUnloadCommand) cmd;
				int r0 = Integer.parseInt(prepareString(params.get("r0")));
				int r1 = Integer.parseInt(prepareString(params.get("r1")));
				int r2 = Integer.parseInt(prepareString(params.get("r2")));
				int r3 = Integer.parseInt(prepareString(params.get("r3")));
				command.setAmounts(r0, r1, r2, r3);
				int l0 = Integer.parseInt(prepareString(params.get("l0")));
				int l1 = Integer.parseInt(prepareString(params.get("l1")));
				int l2 = Integer.parseInt(prepareString(params.get("l2")));
				int l3 = Integer.parseInt(prepareString(params.get("l3")));
				command.setRests(l0, l1, l2, l3);
			}
		});
	
	
	add(FleetRobCommand.class, new FleetCommandEditor()
		{
			public void edit(FleetCommand cmd, ParameterMap params)
			{
				FleetRobCommand command = (FleetRobCommand) cmd;
				int r0 = Integer.parseInt(prepareString(params.get("r0")));
				int r1 = Integer.parseInt(prepareString(params.get("r1")));
				int r2 = Integer.parseInt(prepareString(params.get("r2")));
				int r3 = Integer.parseInt(prepareString(params.get("r3")));
				command.setAmounts(r0, r1, r2, r3);
			}
		});
	
	add(FleetSpyCommand.class, new FleetCommandEditor()
		{
			public void edit(FleetCommand cmd, ParameterMap params)
			{
				FleetSpyCommand command = (FleetSpyCommand) cmd;
				int amount = Integer.parseInt(params.get("amount"));
				command.setAmount(amount);
			}
		});
	
	add(FleetSettleCommand.class, new FleetCommandEditor()
		{
			public void edit(FleetCommand cmd, ParameterMap params)
			{
				FleetSettleCommand command = (FleetSettleCommand) cmd;
				int amount = Integer.parseInt(params.get("amount"));
				command.setAmount(amount);
			}
		});
}

private void add(String name, FleetCommandFactory factory)
{
	if (factoryMap.get(name) != null)
		logger.warning("Duplicate FleetCommandFactory for "+name);
	factoryMap.put(name, factory);
}

private void add(Class<? extends FleetCommand> clazz, FleetCommandEditor editor)
{
	if (editorMap.get(clazz) != null)
		logger.warning("Duplicate FleetCommandEditor for "+clazz);
	editorMap.put(clazz, editor);
}

public FleetCommand decodeCommand(String what, final ParsedRequest request,
		final String modifier) throws InvalidCommandException
{
	FleetCommandFactory fcf;
	
	fcf = factoryMap.get(what);
	if (fcf != null)
	{
		try
		{
			FleetCommand fc = fcf.create(new ParameterMap()
				{
					public String get(String key)
					{ return request.getPostParameter(key+modifier); }
				});
			return fc;
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "Exception caught", e);
			throw new ErrorCodeException(ErrorCode.INVALID_INPUT);
		}
	}
	else
		throw new InvalidCommandException("Unrecognized order: "+what);
}

public FleetCommandEditor getCommandEditor(Class<? extends FleetCommand> clazz)
{ return editorMap.get(clazz); }

}
