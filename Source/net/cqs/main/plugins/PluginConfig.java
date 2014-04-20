package net.cqs.main.plugins;

import java.util.HashMap;
import java.util.Map;

import de.ofahrt.ulfscript.Environment;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.Service;
import net.cqs.services.ServiceRegistry;

public final class PluginConfig
{

private final FrontEnd frontEnd;
private final HashMap<String,String> params = new HashMap<String,String>();

public PluginConfig(FrontEnd frontEnd, Map<String,String> params)
{
	this.frontEnd = frontEnd;
	this.params.putAll(params);
}

public PluginConfig(FrontEnd frontEnd)
{ this.frontEnd = frontEnd; }

public FrontEnd getFrontEnd()
{ return frontEnd; }

public ServiceRegistry getServiceRegistry()
{ return frontEnd; }

public <T extends Service> T findService(Class<T> type)
{ return frontEnd.findService(type); }

public <T extends Environment> T newEnvironment(EnvironmentDescriptor<T> descriptor)
{ return descriptor.newInstance(findService(ResourceManager.class)); }

public void add(String key, String value)
{ params.put(key, value); }

public String get(String key)
{ return params.get(key); }

public String getRequired(String key)
{
	String s = params.get(key);
	if (s == null)
		throw new RuntimeException("plugin configuration did not specify required parameter \""+key+"\"");
	return s;
}

public int getInt(String key, int defaultValue)
{
	String s = params.get(key);
	return s != null ? Integer.parseInt(s) : defaultValue;
}

public boolean getBool(String key, boolean defaultValue)
{
	String s = params.get(key);
	return s != null ? Boolean.parseBoolean(s) : defaultValue;
}

public String getString(String key, String defaultValue)
{
	String s = params.get(key);
	return s != null ? s : defaultValue;
}

}
