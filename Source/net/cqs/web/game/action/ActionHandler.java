package net.cqs.web.game.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InvalidPositionException;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;

public final class ActionHandler
{

	public static abstract class GetHandler
	{
		public boolean isDeprecated()
		{ return false; }
		public abstract void activate(ParsedRequest request, CqsSession session, String param);
	}
	public static abstract class PostHandler
	{
		public boolean isDeprecated()
		{ return false; }
		public abstract void activate(ParsedRequest request, CqsSession session, HttpServletRequest req);
	}

static HashMap<String,GetHandler> getTable = new HashMap<String,GetHandler>();
static HashMap<String,PostHandler> postTable = new HashMap<String,PostHandler>();
static FleetCommands fleetCommands = new FleetCommands();

public static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

public static FleetCommand decodeCommand(String what, ParsedRequest request, String modifier) throws InvalidCommandException
{ return fleetCommands.decodeCommand(what, request, modifier); }

public static FleetCommandEditor getCommandEditor(Class<? extends FleetCommand> clazz)
{ return fleetCommands.getCommandEditor(clazz); }

//public static void add(ActionPlugin o)
//{
//	ActionParser parser = new ActionParser(ParsedRequest.class, CqsSession.class);
//	parser.addConverter(Position.class, new PositionConverter());
//	PostAction[] postActions = parser.parsePostActions(o);
//	GetAction[] getActions = parser.parseGetActions(o);
//	if (getActions.length+postActions.length == 0)
//		logger.warning("No actions defined on \""+o+"\"");
//	for (final PostAction p : postActions)
//	{
//		add(p.getName(), new PostHandler()
//			{
//				@Override
//				public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
//				{ p.activate(req, request, session); }
//			});
//	}
//	for (final GetAction g : getActions)
//	{
//		add(g.getName(), new GetHandler()
//			{
//				@Override
//				public void activate(ParsedRequest request, CqsSession session, String param)
//				{
//					g.activate(param, request, session);
//				}
//			});
//	}
//}

static void add(String s, GetHandler f)
{
	if (getTable.get(s) != null)
		logger.warning("Duplicate GET-Handler for "+s);
	if (f.isDeprecated())
		logger.warning("Deprecated GET-Handler for "+s);
	getTable.put(s, f);
}

static void add(String s, PostHandler f)
{
	if (postTable.get(s) != null)
		logger.warning("Duplicate POST-Handler for "+s);
	if (f.isDeprecated())
		logger.warning("Deprecated POST-Handler for "+s);
	postTable.put(s, f);
}

public static void postHandler(ParsedRequest request, CqsSession session, HttpServletRequest req)
{
	if (!"POST".equals(req.getMethod())) return;
	String todo = req.getParameter("do");
	if (todo == null) return;
	
	PostHandler ph = postTable.get(todo);
	if (ph != null)
	{
		logger.fine("Handling \""+todo+"\"");
		if (ph.isDeprecated())
			logger.warning("Using Deprecated POST-Handler for "+todo);
		try
		{
			try
			{
				ph.activate(request, session, req);
			}
			catch (ErrorCodeException e)
			{
				ErrorCode code = e.getErrorCode();
				if (code == ErrorCode.NONE) code = ErrorCode.INVALID_INPUT;
				session.log(code);
			}
			catch (InvalidPositionException e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
				session.log(ErrorCode.INVALID_POSITION_ERROR);
			}
			catch (NumberFormatException e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
				session.log(ErrorCode.INVALID_INPUT);
			}
			catch (RuntimeException e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
				session.log(ErrorCode.INTERNAL_ERROR);
			}
			catch (Error e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
				session.log(ErrorCode.INTERNAL_ERROR);
			}
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "POST: \""+todo+"\"", e);
			session.log(ErrorCode.INTERNAL_ERROR);
		}
	}
	else
		logger.severe("POST not recognized: \""+todo+"\"");
}

public static void getHandler(ParsedRequest req, CqsSession session)
{
	Iterator<String> it = req.getQueryKeyIterator();
	while (it.hasNext())
	{
		String key = it.next();
		String value = req.getQueryParameter(key);
		
		try
		{
			GetHandler gh = getTable.get(key);
			if (gh != null)
			{
				if (gh.isDeprecated())
					logger.warning("Using deprecated GET-handler for "+key);
				try
				{
					gh.activate(req, session, value);
				}
				catch (ErrorCodeException e)
				{
					ErrorCode code = e.getErrorCode();
					if (code == ErrorCode.NONE) code = ErrorCode.INVALID_INPUT;
					session.log(code);
				}
				catch (InvalidPositionException e)
				{
					logger.log(Level.SEVERE, "Exception caught", e);
					session.log(ErrorCode.INVALID_INPUT);
				}
				catch (NumberFormatException e)
				{
					logger.log(Level.SEVERE, "Exception caught", e);
					session.log(ErrorCode.INVALID_INPUT);
				}
				catch (RuntimeException e)
				{
					logger.log(Level.SEVERE, "Exception caught", e);
					session.log(ErrorCode.INTERNAL_ERROR);
				}
				catch (Error e)
				{
					logger.log(Level.SEVERE, "Exception caught", e);
					session.log(ErrorCode.INTERNAL_ERROR);
				}
			}
			else
				logger.warning("GET not recognized: "+key+" = "+value);
		}
		catch (Exception e)
		{ logger.log(Level.SEVERE, "Exception caught", e); }
	}
}

public static void init()
{/*OK*/}

static
{
	GetAlliance.init();
	GetMessage.init();
	GetPlayer.init();
	GetSystem.init();
	
	PostAlliance.init();
	PostDesign.init();
	PostFleet.init();
	PostPlayer.init();
	PostSearch.init();
	PostSimulator.init();
	PostSupport.init();
	PostSystem.init();
	PostDiplomacy.init();
}

private ActionHandler()
{/*OK*/}

}
