package net.cqs.web.game.ajax;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InputValidation;
import net.cqs.engine.Fleet;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.engine.human.HumanFleetController;
import net.cqs.web.ParsedRequest;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler;
import net.cqs.web.game.action.FleetCommandEditor;
import net.cqs.web.game.action.InvalidCommandException;
import net.cqs.web.game.action.ParameterMap;

public final class AjaxFleetPlugin
{

private static String prepareString(String s)
{ return s.replaceAll("[., ]", ""); }

@WebPostAction("Command.remove")
public void removeCommand(CqsSession session, @Parameter("id") int id, @Parameter("index") int index)
{
	Fleet f = session.getPlayer().findFleetById(id);
	((HumanFleetController) f.getController()).removeCommand(index);
}

@WebPostAction("Command.moveUp")
public void moveUpCommand(CqsSession session, @Parameter("id") int id, @Parameter("index") int index)
{
	Fleet f = session.getPlayer().findFleetById(id);
	((HumanFleetController) f.getController()).moveUpCommand(index, 1);
}

@WebPostAction("Command.moveDown")
public void moveDownCommand(CqsSession session, @Parameter("id") int id, @Parameter("index") int index)
{
	Fleet f = session.getPlayer().findFleetById(id);
	((HumanFleetController) f.getController()).moveUpCommand(index, -1);
}

@WebPostAction("Command.switch")
public void switchCommand(CqsSession session, @Parameter("id") int id, @Parameter("index") int index)
{
	Fleet f = session.getPlayer().findFleetById(id);
	((HumanFleetController) f.getController()).setNextCommand(index);
}

@WebPostAction("Fleet.Command.add")
public void addCommand(ParsedRequest request, CqsSession session, @Parameter("id") int id)
{
	Fleet f = session.getPlayer().findFleetById(id);
	String what = request.getPostParameter("action");
	try
	{
		FleetCommand c = ActionHandler.decodeCommand(what, request, "");
		if (c != null)
			((HumanFleetController) f.getController()).append(c);
	}
	catch (InvalidCommandException e)
	{ e.printStackTrace(); }
}

@WebPostAction("Fleet.unloop")
public void disableLoopFleet(CqsSession session, @Parameter("id") int id)
{ session.getPlayer().findFleetById(id).getController().disableLoop(); }

@WebPostAction("Fleet.loop")
public void enableLoopFleet(CqsSession session, @Parameter("id") int id)
{ session.getPlayer().findFleetById(id).getController().enableLoop(); }

@WebPostAction("Fleet.Name.change")
public void renameFleet(CqsSession session, @Parameter("id") int id, @Parameter("name") String name)
{
	Fleet f = session.getPlayer().findFleetById(id);
	name = name.trim();
	if (name.length() < 1)
		throw new ErrorCodeException(ErrorCode.INPUT_NAME_TOO_SHORT);
	if (name.length() > 40)
		throw new ErrorCodeException(ErrorCode.INPUT_NAME_TOO_LONG);
	if (!InputValidation.validFleetName(name))
		throw new ErrorCodeException(ErrorCode.INPUT_NAME_INVALID);
	f.setName(name.intern());
}


@WebPostAction("Fleet.Command.edit")
public void editCommand(final ParsedRequest request, CqsSession session, @Parameter("id") int id)
{
	Fleet f = session.getPlayer().findFleetById(id);
	int num = Integer.parseInt(prepareString(request.getPostParameter("num")));
	FleetCommand cmd = ((HumanFleetController) f.getController()).getCommand(num);
	if (cmd == null) throw new ErrorCodeException();
	
	FleetCommandEditor fce = ActionHandler.getCommandEditor(cmd.getClass());
	if (fce != null)
	{
		try
		{
			fce.edit(cmd, new ParameterMap()
				{
					@Override
          public String get(String key)
					{ return request.getPostParameter(key); }
				});
		}
		catch (Exception e)
		{ e.printStackTrace(); }
	}
	else
		throw new RuntimeException("Fleet.Command.edit - Kein Editor vorhanden fuer "+cmd.getClass());
}

@WebPostAction("Fleet.Commands.delete")
public void removeCommands(ParsedRequest request, CqsSession session, @Parameter("id") int id)
{
	String what = request.getPostParameter("action");
	Fleet f = session.getPlayer().findFleetById(id);
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


}
