package net.cqs.web.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ActionParser
{

private final Class<?>[] paramClasses;
private final Map<Class<?>,Converter<?>> map = new HashMap<Class<?>,Converter<?>>();

public ActionParser(Class<?>... paramClasses)
{
	this.paramClasses = paramClasses.clone();
	map.put(String.class, new StringConverter());
	map.put(Integer.class, new IntegerConverter());
	map.put(int.class, new IntegerConverter());
	map.put(Long.class, new LongConverter());
	map.put(long.class, new LongConverter());
	map.put(Boolean.class, new BooleanConverter());
	map.put(boolean.class, new BooleanConverter());
}

public void addConverter(Class<?> clazz, Converter<?> converter)
{ map.put(clazz, converter); }

public Converter<?> getConverter(Class<?> clazz)
{ return map.get(clazz); }

public PostAction[] parsePostActions(Object o)
{
	ArrayList<PostAction> result = new ArrayList<PostAction>();
	Class<?> clazz = o.getClass();
	for (Method m : clazz.getMethods())
	{
		Annotation a = m.getAnnotation(WebPostAction.class);
		if (a != null)
		{
			WebPostAction w = (WebPostAction) a;
			ReflectionPostAction action = new ReflectionPostAction(w.value(), this, o, m, paramClasses);
			result.add(action);
		}
	}
	return result.toArray(new PostAction[0]);
}

public GetAction[] parseGetActions(Object o)
{
	ArrayList<GetAction> result = new ArrayList<GetAction>();
	Class<?> clazz = o.getClass();
	for (Method m : clazz.getMethods())
	{
		Annotation a = m.getAnnotation(WebGetAction.class);
		if (a != null)
		{
			WebGetAction w = (WebGetAction) a;
			ReflectionGetAction action = new ReflectionGetAction(w.value(), this, o, m, paramClasses);
			result.add(action);
		}
	}
	return result.toArray(new GetAction[0]);
}

public ManagedGetAction[] parseNewGetActions(Object o)
{
	ArrayList<ManagedGetAction> result = new ArrayList<ManagedGetAction>();
	Class<?> clazz = o.getClass();
	for (Method m : clazz.getMethods())
	{
		Annotation a = m.getAnnotation(ManagedGET.class);
		if (a != null)
		{
			ManagedGET w = (ManagedGET) a;
			ManagedReflectionGetAction action = new ManagedReflectionGetAction(w.value(), this, o, m, paramClasses);
			result.add(action);
		}
	}
	return result.toArray(new ManagedGetAction[0]);
}

public ManagedPostAction[] parseNewPostActions(Object o)
{
	ArrayList<ManagedPostAction> result = new ArrayList<ManagedPostAction>();
	Class<?> clazz = o.getClass();
	for (Method m : clazz.getMethods())
	{
		Annotation a = m.getAnnotation(ManagedPOST.class);
		if (a != null)
		{
			ManagedPOST w = (ManagedPOST) a;
			try
			{
				ManagedReflectionPostAction action = new ManagedReflectionPostAction(w.value(), this, o, m, paramClasses);
				result.add(action);
			}
			catch (NullPointerException e)
			{
				throw new RuntimeException("in "+m, e);
			}
		}
	}
	return result.toArray(new ManagedPostAction[0]);
}

}
