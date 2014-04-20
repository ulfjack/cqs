package net.cqs.engine.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class AttributeMap implements Serializable
{

private static final long serialVersionUID = 1L;

private HashMap<Attribute<?>,Object> data = new HashMap<Attribute<?>,Object>();

public AttributeMap()
{/*OK*/}

public int size()
{ return data.size(); }

public Iterator<Entry<Attribute<?>,Object>> iterator()
{ return data.entrySet().iterator(); }

public <T extends Serializable> T get(Attribute<T> key)
{
	T result = key.cast(data.get(key));
	if (result == null) result = key.getDefaultValue();
	return result;
}

public <T extends Serializable> void set(Attribute<T> key, T value)
{
	key.cast(value);
	data.put(key, value);
}

public void remove(Attribute<?> key)
{ data.remove(key); }

}
