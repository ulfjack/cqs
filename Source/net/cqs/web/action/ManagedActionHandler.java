package net.cqs.web.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.config.ErrorCode;
import net.cqs.storage.GalaxyTask;
import net.cqs.web.ParsedRequest;

public final class ManagedActionHandler
{

private static final Logger logger = Logger.getLogger(ManagedActionHandler.class.getName());

private final Class<?>[] paramClasses;
private final HashMap<String,ManagedGetAction> getTable = new HashMap<String,ManagedGetAction>();
private final HashMap<String,ManagedPostAction> postTable = new HashMap<String,ManagedPostAction>();
private final HashMap<Class<?>,Converter<?>> converterTable = new HashMap<Class<?>,Converter<?>>();

public ManagedActionHandler(Class<?>... paramClasses)
{ this.paramClasses = paramClasses.clone(); }

public <T> void addConverter(Class<T> clazz, Converter<T> converter)
{ converterTable.put(clazz, converter); }

public <T extends Enum<T>> void addConverter(Class<T> clazz)
{ converterTable.put(clazz, new EnumConverter<T>(clazz)); }

public void add(ActionPlugin plugin)
{
	ActionParser parser = new ActionParser(paramClasses);
	for (Entry<Class<?>, Converter<?>> entry : converterTable.entrySet())
		parser.addConverter(entry.getKey(), entry.getValue());
	
	ManagedGetAction[] getActions = parser.parseNewGetActions(plugin);
	for (ManagedGetAction a : getActions)
	{
		String s = a.getName();
		if (getTable.get(s) != null)
			logger.warning("Duplicate GET-Handler for "+s);
		if (a.isDeprecated())
			logger.warning("Deprecated GET-Handler for "+s);
		getTable.put(a.getName(), a);
	}
	
	ManagedPostAction[] postActions = parser.parseNewPostActions(plugin);
	for (ManagedPostAction a : postActions)
	{
		String s = a.getName();
		if (postTable.get(s) != null)
			logger.warning("Duplicate POST-Handler for "+s);
		if (a.isDeprecated())
			logger.warning("Deprecated POST-Handler for "+s);
		postTable.put(a.getName(), a);
	}
	
	if ((getActions.length == 0) && (postActions.length == 0))
		logger.warning("No actions found on \""+plugin+"\" of class \""+plugin.getClass()+"\"!");
}

private void handlePost(TaskHandler handler, ParsedRequest request, ParameterProvider params)
{
	String todo = request.getPostParameter("do");
	if (todo == null) return;
	
	ManagedPostAction action = postTable.get(todo);
	if (action != null)
	{
		logger.fine("Handling \""+todo+"\"");
		if (action.isDeprecated())
			logger.warning("Using Deprecated POST-Handler for "+todo);
		GalaxyTask task = null;
		try
		{
			task = action.parse(request, params);
		}
		catch (RuntimeException e)
		{
			logger.log(Level.WARNING, "Parsing failed for \""+todo+"\"", e);
			handler.log(ErrorCode.INVALID_INPUT);
		}
		if (task != null)
			handler.execute(task);
	}
	else
		logger.severe("No handler found for: \""+todo+"\"");
}

private void handleGet(TaskHandler handler, ParsedRequest parsedRequest, ParameterProvider params)
{
	Iterator<String> it = parsedRequest.getQueryKeyIterator();
	while (it.hasNext())
	{
		String key = it.next();
		String value = parsedRequest.getQueryParameter(key);
		ManagedGetAction action = getTable.get(key);
		if (action != null)
		{
			if (action.isDeprecated())
				logger.warning("Using deprecated GET-handler for "+key);
			GalaxyTask task = null;
			try
			{
				task = action.parse(value, params);
			}
			catch (RuntimeException e)
			{
				logger.log(Level.WARNING, "Parsing failed for \""+key+"="+value+"\"", e);
				handler.log(ErrorCode.INVALID_INPUT);
			}
			if (task != null)
				handler.execute(task);
		}
		else
			logger.warning("GET not recognized: \""+key+"="+value+"\"");
	}
}

public void handle(TaskHandler handler, ParsedRequest parsedRequest, ParameterProvider params)
{
	handleGet(handler, parsedRequest, params);
	if ("POST".equals(parsedRequest.getMethod()))
		handlePost(handler, parsedRequest, params);
}

}
