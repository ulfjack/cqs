package net.cqs.web.action;

import java.util.HashMap;

public final class EnumConverter<T extends Enum<T>> implements Converter<T>
{

private final HashMap<String,T> map = new HashMap<String,T>();

public EnumConverter(Class<T> clazz)
{
	for (T t : clazz.getEnumConstants())
		map.put(t.name(), t);
}

@Override
public T convert(String value)
{ return map.get(value); }

}
