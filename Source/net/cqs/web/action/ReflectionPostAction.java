package net.cqs.web.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

public final class ReflectionPostAction implements PostAction
{

	private static abstract class Param
	{
		public Param()
		{/*OK*/}
		public abstract Object get(HttpServletRequest req, Object... params);
	}
	private static class RequestParameter extends Param
	{
		private final String name;
		private final Converter<?> converter;
		public RequestParameter(String name, Converter<?> converter)
		{
			if (converter == null) throw new NullPointerException("for "+name);
			this.name = name;
			this.converter = converter;
		}
		public Converter<?> converter()
		{ return converter; }
		@Override
		public Object get(HttpServletRequest req, Object... params)
		{
			String value = req.getParameter(name);
			return converter().convert(value);
		}
	}
	private static class StaticParameter extends Param
	{
		private final int index;
		public StaticParameter(int index)
		{ this.index = index; }
		@Override
		public Object get(HttpServletRequest req, Object... params)
		{ return params[index]; }
	}
	private static class ServletRequestParameter extends Param
	{
		public ServletRequestParameter()
		{/*OK*/}
		@Override
		public Object get(HttpServletRequest req, Object... params)
		{ return req; }
	}

private final String name;
private final boolean deprecated;
private final Object o;
private final Method m;
private final Param[] items;

public ReflectionPostAction(String name, ActionParser parser, Object o, Method m, Class<?>... clazzes)
{
	this.name = name;
	this.o = o;
	this.m = m;
	this.deprecated = m.isAnnotationPresent(Deprecated.class);
	Class<?>[] ps = m.getParameterTypes();
	Annotation[][] as = m.getParameterAnnotations();
	items = new Param[ps.length];
	for (int i = 0; i < ps.length; i++)
	{
		Annotation a = null;
		for (int j = 0; j < as[i].length; j++)
		{
			if (as[i][j].annotationType().equals(Parameter.class))
				a = as[i][j];
		}
		if (a != null)
		{
			Parameter v = (Parameter) a;
			String pname = v.value();
			Converter<?> converter = parser.getConverter(ps[i]);
			if (converter == null) throw new NullPointerException("for "+m+" parameter "+i);
			items[i] = new RequestParameter(pname, converter);
		}
		else
		{
			for (int j = 0; j < clazzes.length; j++)
				if (ps[i].equals(clazzes[j]))
					items[i] = new StaticParameter(j);
			if (ps[i].equals(HttpServletRequest.class))
				items[i] = new ServletRequestParameter();
		}
		if (items[i] == null)
			throw new NullPointerException("for "+m+" parameter "+i);
	}
}

@Override
public String getName()
{ return name; }

@Override
public boolean isDeprecated()
{ return deprecated; }

@Override
public void activate(HttpServletRequest req, Object... params)
{
	try
	{
		final Object[] values = new Object[items.length];
		for (int i = 0; i < items.length; i++)
			values[i] = items[i].get(req, params);
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