package net.cqs.web.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.cqs.storage.GalaxyTask;

public final class ManagedReflectionGetAction implements ManagedGetAction
{

	private static abstract class Param
	{
		public Param()
		{/*OK*/}
		public abstract Object get(String[] value, ParameterProvider params);
	}
	private static class ValueParameter extends Param
	{
		private final int index;
		private final Converter<?> converter;
		public ValueParameter(int index, Converter<?> converter)
		{
			this.index = index;
			this.converter = converter;
		}
		@Override
		public Object get(String[] value, ParameterProvider params)
		{ return converter.convert(value[index]); }
	}
	private static class StaticParameter extends Param
	{
		private final int index;
		public StaticParameter(int index)
		{ this.index = index; }
		@Override
		public Object get(String[] value, ParameterProvider params)
		{ return params.get(index); }
	}

private final String name;
private final boolean deprecated;
private final Object o;
private final Method m;
private final Param[] items;

public ManagedReflectionGetAction(String name, ActionParser parser, Object o, Method m, Class<?>... clazzes)
{
	this.name = name;
	this.o = o;
	this.m = m;
	this.deprecated = m.isAnnotationPresent(Deprecated.class);
	Class<?>[] ps = m.getParameterTypes();
	items = new Param[ps.length];
	int cindex = 0;
	int pindex = 0;
	for (int i = 0; i < ps.length; i++)
	{
		if (cindex < clazzes.length)
		{
			do
			{
				if (ps[i].equals(clazzes[cindex]))
					items[i] = new StaticParameter(cindex);
				cindex++;
			}
			while ((items[i] == null) && (cindex < clazzes.length));
		}
		if (items[i] == null)
		{
			Converter<?> converter = parser.getConverter(ps[i]);
			items[i] = new ValueParameter(pindex, converter);
			pindex++;
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
public GalaxyTask parse(String value, ParameterProvider params)
{
	try
	{
		final String[] parts = value.split(",");
		final Object[] values = new Object[items.length];
		for (int i = 0; i < items.length; i++)
			values[i] = items[i].get(parts, params);
		return new GalaxyTask()
			{
				@Override
				public void run()
				{
					try
					{ m.invoke(o, values); }
					catch (InvocationTargetException e)
					{
						Throwable c = e;
						if (e.getCause() != null) c = e.getCause();
						if (c instanceof RuntimeException)
							throw (RuntimeException) c;
						if (c instanceof Error)
							throw (Error) c;
						throw new RuntimeException(c);
					}
					catch (IllegalAccessException e)
					{ throw new RuntimeException(e); }
				}
			};
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