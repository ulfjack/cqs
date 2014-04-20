package net.cqs.web.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.cqs.storage.GalaxyTask;
import net.cqs.web.ParsedRequest;

public final class ManagedReflectionPostAction implements ManagedPostAction
{

	private static abstract class Param
	{
		public Param()
		{/*OK*/}
		public abstract Object get(ParsedRequest request, ParameterProvider params);
	}
	private static class ValueParameter extends Param
	{
		private final String name;
		private final Converter<?> converter;
		public ValueParameter(String name, Converter<?> converter)
		{
			if (converter == null) throw new NullPointerException();
			this.name = name;
			this.converter = converter;
		}
		@Override
		public Object get(ParsedRequest request, ParameterProvider params)
		{
			String value = request.getPostParameter(name);
			return converter.convert(value);
		}
	}
	private static class StaticParameter extends Param
	{
		private final int index;
		public StaticParameter(int index)
		{ this.index = index; }
		@Override
		public Object get(ParsedRequest request, ParameterProvider params)
		{ return params.get(index); }
	}

static String findParameterName(Annotation[] annotations)
{
	Annotation a = null;
	for (int j = 0; j < annotations.length; j++)
	{
		if (annotations[j].annotationType().equals(Parameter.class))
			a = annotations[j];
	}
	if (a != null)
	{
		Parameter v = (Parameter) a;
		return v.value();
	}
	return null;
}

private final String name;
private final boolean deprecated;
private final Object o;
private final Method m;
private final Param[] items;

public ManagedReflectionPostAction(String name, ActionParser parser, Object o, Method m, Class<?>... clazzes)
{
	this.name = name;
	this.o = o;
	this.m = m;
	this.deprecated = m.isAnnotationPresent(Deprecated.class);
	Class<?>[] ps = m.getParameterTypes();
	Annotation[][] as = m.getParameterAnnotations();
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
			String pname = findParameterName(as[i]);
			if (pname == null) throw new RuntimeException("parameter name could not be determined: "+i+" of "+m);
			Converter<?> converter = parser.getConverter(ps[i]);
			items[i] = new ValueParameter(pname, converter);
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
public GalaxyTask parse(ParsedRequest request, ParameterProvider params)
{
	try
	{
		final Object[] values = new Object[items.length];
		for (int i = 0; i < items.length; i++)
			values[i] = items[i].get(request, params);
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