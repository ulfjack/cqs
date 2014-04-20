package net.cqs.util;

import java.io.Serializable;
import java.lang.reflect.Array;

public final class IntHashMap<T> implements Serializable
{

private static final long serialVersionUID = 1L;

//prime numbers, about doubling in size, largest 2^31 -1 (=MAX_VALUE of Integer)
private static final int[] PRIMES = new int[]
  {7, 17, 29, 59, 127, 257, 521, 1049, 2099, 4201, 8419, 16843, 33703, 67409, 
	 134837, 269683, 539389, 1078787, 2157587, 4315183, 8630387, 17260781, 
	 34521589, 69043189, 138086407, 276172823, 552345671, 1104691373, 
	 2147483647};

private final Class<T> elementType;

private int innerMod;
private float loadFactor;

private int amount;
private T[] data;
private int[] keys;

/** Copy constructor. */
public IntHashMap(IntHashMap<T> other)
{
	this.elementType = other.elementType;
	this.innerMod = other.innerMod;
	this.loadFactor = other.loadFactor;
	this.amount = other.amount;
	this.data = other.data.clone();
	this.keys = other.keys.clone();
}

/**
 * Creates a map with PRIMES[1] space and a load factor of 0.75f.
 */
public IntHashMap(Class<T> elementType)
{
	this(elementType, 1);
}

/**
 * Creates a map with at least minAmount space and a load factor of 0.75f.
 */
public IntHashMap(Class<T> elementType, int minAmount)
{
	this(elementType, minAmount, 0.75f);
}

/**
 * Creates a map with at least minAmount space and the given load factor.
 */
public IntHashMap(Class<T> elementType, int minAmount, float loadFactor)
{
	this.elementType = elementType;
	int pos = 1;
	if (minAmount < PRIMES[0])
		minAmount = PRIMES[0];
	
	while (minAmount > PRIMES[pos])
		pos++;
	
	innerMod = PRIMES[pos-1];
	int outerMod = PRIMES[pos];
	data = newArray(elementType, outerMod);
	keys = new int[outerMod];
	amount = 0;
	if ((loadFactor > 0) && (loadFactor <= 1))
		this.loadFactor = loadFactor;
	else
		this.loadFactor = 0.75f;
}

int capacity()
{ return data.length; }

float getLoadFactor()
{ return loadFactor; }

/**
 * returns true if and only if an object is stored at pos
 */
private boolean isValid(int pos)
{
	return (data[pos] != null);
}

/**
 * returns true if and only if no valid object is at pos, but one was deleted
 */
private boolean isDeleted(int pos)
{
	return ((data[pos] == null) && (keys[pos] == -1));
}

/**
 * returns true if an object is stored at pos or one was deleted
 */
private boolean isUsed(int pos)
{
	return isValid(pos) || isDeleted(pos);
}

/**
 * hash function based on modulo primes
 */
private int hash(int key, int i)
{
	int result = (key + i * (1 + key % innerMod) ) % data.length;
	while (result < 0)
		result = result + data.length;
	return result;
}

/**
 * resizes the map to about double the size
 */
private void resize()
{
	int pos = 1;
	while (data.length != PRIMES[pos])
		pos++;
	
	T[] oldData = data;
	int[] oldKeys = keys;
	
	innerMod = PRIMES[pos];
	int outerMod = PRIMES[pos+1];
	data = newArray(elementType, outerMod);
	keys = new int[outerMod];
	amount = 0;
	for (int i = 0; i < oldData.length; i++)
	{
		if (oldData[i] != null)
			put(oldKeys[i], oldData[i]);
	}
}

/**
 * Rehashes the elements, eliminating all "deleted" entries.
 */
private void rehash()
{
	T[] oldData = data;
	int[] oldKeys = keys;
	
	data = newArray(elementType, oldData.length);
	keys = new int[oldData.length];
	amount = 0;
	for (int i = 0; i < oldData.length; i++)
	{
		if (oldData[i] != null)
			put(oldKeys[i], oldData[i]);
	}
}

/**
 * clears map from all objects
 */
public void clear()
{
	amount = 0;
	for (int i = 0; i < data.length; i++)
	{
		keys[i] = 0;
		data[i] = null;
	}
}

/**
 * returns the amount of valid objects in the map
 */
public int size()
{
	return amount;
}

/* Finds the position corresponding to key, returns -1 if not found.
 * WARNING: May rehash as a side-effect to make access faster */
private int findKey(int key)
{
	for (int i = 0; i < data.length; i++)
	{
		int pos = hash(key, i);
		if (isUsed(pos))
		{
			if (keys[pos] == key)
				return pos;
		}
		else
			return -1;
	}
	rehash();
	return -1;
}

/**
 * returns true if and only if the map contains an object with key
 */
public boolean containsKey(int key)
{ return findKey(key) >= 0; }

/**
 * returns true if and only if the map contains an object equal to o
 * returns false if o == null
 * note: slow!
 */
public boolean containsValue(T o)
{
	if (o == null) return false;
	
	for (int i = 0; i < data.length; i++)
	{
		if (data[i] != null && data[i].equals(o))
			return true;
	}
	return false;
}

/**
 * inserts an object with key into the map
 * returns former object, if key already existed, else returns null
 * does nothing when trying to insert null
 * resizes the map, if at least loadFactor of the map is valid
 * if the key is already contained, the object in the map is updated
 */
public T put(int key, T o)
{
	if (o == null) throw new NullPointerException("Cannot insert null value!");

	if ((amount+1)/(float) data.length >= loadFactor)
		resize();
	
	int pos = -1;
	for (int i = 0; i < data.length; i++)
	{
		pos = hash(key, i);
		if ((data[pos] == null) || (keys[pos] == key))
			break;
	}
	
	T result = data[pos];
	if (!isValid(pos))
	{
		amount++;
		keys[pos] = key;
	}
	data[pos] = o;
	return result;
}

/**
 * gets the object with key
 * returns null if no object with key is contained in the map
 */
public T get(int key)
{
	int pos = findKey(key);
	if (pos >= 0) return data[pos];
	return null;
}

/**
 * removes the object with key, returns Object
 * does nothing if no object with key is contained in the map
 */
public T remove(int key)
{
	int pos = findKey(key);
	if (pos < 0) return null;
	
	T result = null;
	if ((keys[pos] == key) && (isValid(pos)))
	{
		result = data[pos];
		data[pos] = null;
		keys[pos] = -1;
		amount--;
	}
	return result;
}


/**
 * Internal helper function. Creates a new array of type T.
 * The generics warning is suppressed.
 * Otherwise there would be warnings all over the place.
 */
@SuppressWarnings("unchecked")
private static <T> T[] newArray(Class<T> elementType, int length)
{ return (T[]) Array.newInstance(elementType, length); }

public static <T> IntHashMap<T> of(Class<T> elementType)
{ return new IntHashMap<T>(elementType); }

}
