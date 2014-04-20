package net.cqs.main.plugins;

import java.util.HashMap;
import java.util.Map;

import net.cqs.main.config.FrontEnd;

public final class PluginEntry
{

private final String name;
private final boolean load;
private final Map<String,String> params = new HashMap<String,String>();

public PluginEntry(String name, boolean load)
{
	this.name = name;
	this.load = load;
}

public PluginEntry(String name)
{ this(name, true); }

public String getName()
{ return name; }

public boolean shallLoad()
{ return load; }

public Map<String,String> getParams()
{ return new HashMap<String, String>(params); }

public void add(String key, String value)
{ params.put(key, value); }

public PluginConfig getConfig(FrontEnd frontEnd)
{ return new PluginConfig(frontEnd, params); }

}