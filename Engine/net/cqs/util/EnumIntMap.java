package net.cqs.util;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * A type-safe map from an enum type to int.
 * All operations are fast (just like an int array).
 */
public final class EnumIntMap<T extends Enum<T>> implements Serializable
{

private static final long serialVersionUID = 1L;

private final Class<T> elementType;
private final int[] values;
private int sum = 0;

private EnumIntMap(Class<T> elementType)
{
	this.elementType = elementType;
	this.values = new int[elementType.getEnumConstants().length];
}

private EnumIntMap(EnumIntMap<T> original)
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

public int getSum()
{	return sum; }

/**
 * Returns a random element of this map that has a non-zero entry.
 * Entries with a higher value have a higher probability of being picked.
 * 
 * <p>The results of this method are not guaranteed to be equally distributed,
 * even if the input integer is equally distributed. As long as this list's
 * sum is significantly less than 2^32, they are approximately equally
 * distributed.
 * 
 * <p>The first entries - upto a total of floor(2^31/sum) - have a slightly
 * higher probability of being picked.
 * 
 * <p>If this method is called with a random value between 0 (inclusive)
 * and {@link #getSum()} (exclusive), the results are equally distributed.
 */
//@VisibleForTesting
T chooseRandom(int rand)
{
	if (sum == 0) return null;
	// flips the sign bit, so that rand is guaranteed to be positive
	if (rand < 0) rand = ~rand;
	int value = rand % sum;
	int index = -1;
	T result = null;
	T[] temp = elementType.getEnumConstants();
	while (value >= 0)
	{
		index++;
		while ((index < values.length) && (values[index] == 0))
			index++;
		result = temp[index];
		value -= values[index];
	}
	return result;
}

public T chooseRandom(RandomNumberGenerator rand)
{
	if (sum == 0) return null;
	return chooseRandom(rand.nextInt(sum));
}

public void clear()
{
	sum = 0;
	for (int i = 0; i < values.length; i++)
		values[i] = 0;
}

public int get(T key)
{
	if (key == null) throw new NullPointerException();
	typeCheck(key);
	return values[key.ordinal()];
}

public void set(T key, int value)
{
	if (value < 0) throw new IllegalArgumentException("value may not be negative");
	typeCheck(key);
	
	int index = key.ordinal();
	int oldValue = values[index];
	sum += value-oldValue;
	values[index] = value;
}

public void increase(T key, int amount)
{	set(key, get(key)+amount); }

public void increase(T key)
{ increase(key, 1); }

public void decrease(T key, int amount)
{ set(key, get(key)-amount); }

public void decrease(T key)
{ decrease(key, 1); }

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
		private final int[] values;
		
		private SerializationProxy(EnumIntMap<E> map)
		{
			this.elementType = map.elementType;
			int count = 0;
			for (int i = 0; i < map.values.length; i++)
				if (map.values[i] != 0) count++;
			
			E[] temp = elementType.getEnumConstants();
			
			this.elements = newArray(elementType, count);
			this.values = new int[count];
			
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
			EnumIntMap<E> result = EnumIntMap.of(elementType);
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
public static <T extends Enum<T>> EnumIntMap<T> of(Class<T> elementType)
{ return new EnumIntMap<T>(elementType); }

public static <T extends Enum<T>> EnumIntMap<T> copyOf(EnumIntMap<T> original)
{ return new EnumIntMap<T>(original); }

}
