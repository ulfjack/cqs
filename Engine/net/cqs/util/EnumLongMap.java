package net.cqs.util;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * A type-safe map from an enum type to long.
 * All operations are fast (just like an long array).
 */
public final class EnumLongMap<T extends Enum<T>> implements Serializable
{

private static final long serialVersionUID = 1L;

private final Class<T> elementType;
private final long[] values;
private long sum = 0;

private EnumLongMap(Class<T> elementType)
{
	this.elementType = elementType;
	this.values = new long[elementType.getEnumConstants().length];
}

private EnumLongMap(EnumLongMap<T> original)
{
	this.elementType = original.elementType;
	this.values = original.values.clone();
	this.sum = original.sum;
}

final void typeCheck(T element)
{
	Class<?> elClass = element.getClass();
	if ((elClass != elementType) && (elClass.getSuperclass() != elementType))
		throw new ClassCastException(elClass+" != "+elementType);
}

public long getSum()
{	return sum; }

public void clear()
{
	sum = 0;
	for (int i = 0; i < values.length; i++)
		values[i] = 0;
}

public long get(T key)
{
	if (key == null) throw new NullPointerException();
	typeCheck(key);
	return values[key.ordinal()];
}

private void set(int index, long value)
{
	long oldValue = values[index];
	sum += value-oldValue;
	values[index] = value;
}

public void set(T key, long value)
{
	if (value < 0) throw new IllegalArgumentException("value may not be negative");
	typeCheck(key);
	
	int index = key.ordinal();
	set(index, value);
}

private long safeAdd(long a, long b)
{
	if (a > Long.MAX_VALUE-b)
		throw new IllegalArgumentException();
	return a+b;
}

private long safeSub(long a, long b)
{
	if (a < b)
		throw new IllegalArgumentException();
	return a-b;
}

public void increase(T key, long amount)
{	set(key, safeAdd(get(key), amount)); }

public void increase(T key)
{ increase(key, 1); }

public void decrease(T key, long amount)
{ set(key, safeSub(get(key), amount)); }

public void decrease(T key)
{ decrease(key, 1); }

public void subtract(EnumLongMap<T> other)
{
	for (int i = 0; i < values.length; i++)
		if (other.values[i] > values[i])
			throw new IllegalArgumentException();
	for (int i = 0; i < values.length; i++)
		set(i, values[i]-other.values[i]);
}

public void add(EnumLongMap<T> other)
{
	for (int i = 0; i < values.length; i++)
		if (other.values[i] > Long.MAX_VALUE-values[i])
			throw new IllegalArgumentException();
	for (int i = 0; i < values.length; i++)
		set(i, values[i]+other.values[i]);
}

@Override
public String toString()
{
	StringBuilder result = new StringBuilder();
	T[] temp = elementType.getEnumConstants();
	result.append("EnumIntMap:\n");
	for (int i = 0; i < values.length; i++)
	{
		if (values[i] != 0)
		{
			result.append("  ").append(temp[i]).append(" = ").append(values[i]);
		}
	}
	return result.toString();
}

private Object writeReplace()
{ return new SerializationProxy<T>(this); }

	private static class SerializationProxy<E extends Enum<E>> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private final Class<E> elementType;
		
		private final E[] elements;
		private final long[] values;
		
		private SerializationProxy(EnumLongMap<E> map)
		{
			this.elementType = map.elementType;
			int count = 0;
			for (int i = 0; i < map.values.length; i++)
				if (map.values[i] != 0) count++;
			
			E[] temp = elementType.getEnumConstants();
			
			this.elements = newArray(elementType, count);
			this.values = new long[count];
			
			count = 0;
			for (int i = 0; i < temp.length; i++)
				if (map.values[i] != 0)
				{
					this.elements[count] = temp[i];
					this.values[count] = map.values[i];
					count++;
				}
		}
		
		private Object readResolve()
		{
			EnumLongMap<E> result = EnumLongMap.of(elementType);
			for (int i = 0; i < elements.length; i++)
				result.set(elements[i], values[i]);
			return result;
		}
	}

/**
 * Internal helper function. Creates a new array of type T.
 * The generics warning is suppressed.
 * Otherwise there would be warnings all over the place.
 */
@SuppressWarnings("unchecked")
private static <T extends Enum<T>> T[] newArray(Class<T> elementType, int length)
{ return (T[]) Array.newInstance(elementType, length); }

/**
 * Static constructor convenience method.
 */
public static <T extends Enum<T>> EnumLongMap<T> of(Class<T> elementType)
{ return new EnumLongMap<T>(elementType); }

public static <T extends Enum<T>> EnumLongMap<T> copyOf(EnumLongMap<T> original)
{ return new EnumLongMap<T>(original); }

}
