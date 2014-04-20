package net.cqs.web.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.cqs.web.ParsedRequest;

public final class ActionHandler
{

private static final Logger logger = Logger.getLogger(ActionHandler.class.getName());

private final Class<?>[] paramClasses;
private final HashMap<String,GetAction> getTable = new HashMap<String,GetAction>();
private final HashMap<String,PostAction> postTable = new HashMap<String,PostAction>();
private final HashMap<Class<?>,Converter<?>> converterTable = new HashMap<Class<?>,Converter<?>>();

public ActionHandler(Class<?>... paramClasses)
{ this.paramClasses = paramClasses.clone(); }

public void addConverter(Class<?> clazz, Converter<?> converter)
{ converterTable.put(clazz, converter); }

public <T extends Enum<T>> void addConverter(Class<T> clazz)
{ converterTable.put(clazz, new EnumConverter<T>(clazz)); }

public void add(Object o)
{
	ActionParser parser = new ActionParser(paramClasses);
	for (Entry<Class<?>, Converter<?>> entry : converterTable.entrySet())
		parser.addConverter(entry.getKey(), entry.getValue());
	
	PostAction[] postActions = parser.parsePostActions(o);
	for (PostAction a : postActions)
	{
		String s = a.getName();
		if (postTable.get(s) != null)
			logger.warning("Duplicate POST-Handler for "+s);
		if (a.isDeprecated())
			logger.warning("Deprecated POST-Handler for "+s);
		postTable.put(s, a);
	}
	
	GetAction[] getActions = parser.parseGetActions(o);
	for (GetAction a : getActions)
	{
		String s = a.getName();
		if (getTable.get(s) != null)
			logger.warning("Duplicate GET-Handler for "+s);
		if (a.isDeprecated())
			logger.warning("Deprecated GET-Handler for "+s);
		getTable.put(s.toLowerCase(), a);
	}
	
	if ((getActions.length == 0) && (postActions.length == 0))
		logger.warning("No actions found on \""+o+"\" of class \""+o.getClass()+"\"!");
}

private void handlePost(HttpServletRequest req, Object... params)
{
	if (!"POST".equals(req.getMethod())) return;
	String todo = req.getParameter("do");
	if (todo == null) return;
	
	logger.fine("Handling \""+todo+"\"");
	PostAction ph = postTable.get(todo);
	if (ph != null)
	{
		if (ph.isDeprecated())
			logger.warning("Using Deprecated POST-Handler for "+todo);
		try
		{
			try
			{
				ph.activate(req, params);
			}
			catch (NumberFormatException e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
			}
			catch (RuntimeException e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
			}
			catch (Error e)
			{
				logger.log(Level.SEVERE, "Exception caught", e);
			}
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "POST: \""+todo+"\"", e);
		}
	}
	else
		logger.severe("No handler found for: \""+todo+"\"");
}

private void handleGet(ParsedRequest req, Object... params)
{
	Iterator<String> it = req.getQueryKeyIterator();
	while (it.hasNext())
	{
		String key = it.next();
		String value = req.getQueryParameter(key);
		
		try
		{
			GetAction gh = getTable.get(key);
			if (gh != null)
			{
				if (gh.isDeprecated())
					logger.warning("Using Deprecated POST-Handler for "+key);
				try
				{
					gh.activate(value, params);
				}
				catch (RuntimeException e)
				{
					logger.log(Level.SEVERE, "GET failed: "+key+"="+value, e);
				}
				catch (Error e)
				{
					logger.log(Level.SEVERE, "GET failed: "+key+"="+value, e);
				}
			}
			else
				logger.warning("Query nicht erkannt: "+key+" = "+value);
		}
		catch (Exception e)
		{ logger.log(Level.SEVERE, "Exception caught", e); }
	}
}

public void handle(HttpServletRequest req, ParsedRequest parsedRequest, Object... params)
{
	handleGet(parsedRequest, params);
	if ("POST".equals(req.getMethod()))
		handlePost(req, params);
}

}
