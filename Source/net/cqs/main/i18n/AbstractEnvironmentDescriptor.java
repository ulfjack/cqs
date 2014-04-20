package net.cqs.main.i18n;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.cqs.main.resource.ResourceManager;

import de.ofahrt.ulfscript.Environment;
import de.ofahrt.ulfscript.SourceProvider;

public final class AbstractEnvironmentDescriptor<T extends Environment> implements EnvironmentDescriptor<T>
{

private static String get(Class<?> classToken, String fieldName)
{
	try
	{
		Field f = classToken.getField(fieldName);
		if (!Modifier.isStatic(f.getModifiers()))
			throw new IllegalArgumentException();
		if (!Modifier.isPublic(f.getModifiers()))
			throw new IllegalArgumentException();
		return (String) f.get(null);
	}
	catch (Exception e)
	{ throw new RuntimeException(e); }
}

private final Class<T> classToken;
private final String name;
private final String prefix;
private final String filePattern;
private final String bundleName;

public AbstractEnvironmentDescriptor(Class<T> classToken)
{
	this.classToken = classToken;
	this.name = get(classToken, "NAME");
	this.prefix = get(classToken, "PREFIX");
	this.filePattern = get(classToken, "FILE_PATTERN");
	this.bundleName = get(classToken, "BUNDLE_NAME");
}

public AbstractEnvironmentDescriptor(Class<T> classToken, String name, String prefix, String filePattern, String bundleName)
{
	this.classToken = classToken;
	this.name = name;
	this.prefix = prefix;
	this.filePattern = filePattern;
	this.bundleName = bundleName;
}

@Override
public String getName()
{ return name; }

@Override
public String getPrefix()
{ return prefix; }

@Override
public String getFilePattern()
{ return filePattern; }

@Override
public String bundleName()
{ return bundleName; }

@Override
public T newInstance(SourceProvider provider)
{
	try
	{
		Constructor<T> c = classToken.getConstructor(SourceProvider.class);
		return c.newInstance(provider);
	}
	catch (Exception e)
	{ throw new RuntimeException(e); }
}

@Override
public T newInstance(ResourceManager resourceManager)
{ return newInstance(resourceManager.getSourceProvider(getPrefix())); }

}
