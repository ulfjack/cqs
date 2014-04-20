package net.cqs.web.action;

import java.lang.reflect.Method;

public final class ReflectionGetAction implements GetAction
{

	private static abstract class Param
	{
		public Param()
		{/*OK*/}
		public abstract Object get(String value, Object... params);
	}
	private static class ValueParameter extends Param
	{
		public ValueParameter()
		{/*OK*/}
		@Override
		public Object get(String value, Object... params)
		{ return value; }
	}
	private static class StaticParameter extends Param
	{
		private final int index;
		public StaticParameter(int index)
		{ this.index = index; }
		@Override
		public Object get(String value, Object... params)
		{ return params[index]; }
	}

private final String name;
private final boolean deprecated;
private final Object o;
private final Method m;
private final Param[] items;

public ReflectionGetAction(String name, ActionParser parser, Object o, Method m, Class<?>... clazzes)
{
	this.name = name;
	this.o = o;
	this.m = m;
	this.deprecated = m.isAnnotationPresent(Deprecated.class);
	Class<?>[] ps = m.getParameterTypes();
	items = new Param[ps.length];
	for (int i = 0; i < ps.length; i++)
	{
		if (ps[i].equals(String.class))
		{
			items[i] = new ValueParameter();
		}
		else
		{
			for (int j = 0; j < clazzes.length; j++)
				if (ps[i].equals(clazzes[j]))
					items[i] = new StaticParameter(j);
		}
		if (items[i] == null)
			throw new NullPointerException("for "+m+": "+i);
	}
}

@Override
public String getName()
{ return name; }

@Override
public boolean isDeprecated()
{ return deprecated; }

@Override
public void activate(String value, Object... params)
{
	try
	{
		final Object[] values = new Object[items.length];
		for (int i = 0; i < items.length; i++)
			values[i] = items[i].get(value, params);
		m.invoke(o, values);
	}
	catch (Exception e)
	{ // undo exception chaining and rethrow as unchecked exception
		// either by casting or by chaining in a RuntimeException
		Throwable c = e;
		if (e.getCause() != null) c = e.getCause();
		if (c instanceof RuntimeException)
			throw (RuntimeException) c;
		if (c instanceof Error)
			throw (Error) c;
		throw new RuntimeException(e);
	}
}

}