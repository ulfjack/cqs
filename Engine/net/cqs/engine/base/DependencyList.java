package net.cqs.engine.base;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.cqs.util.EnumIntMap;

/**
 * Definiert eine Liste von Paaren (key,value).
 * Vorrangiger Verwendungszweck sind Abhaengigkeitslisten fuer Forschungen.
 * Alle Schluessel sind erlaubt, alle Werte sind erlaubt.
 * 
 * @param <T> Enum type
 */
public final class DependencyList<T extends Enum<T>> implements Serializable, Iterable<DependencyListEntry<T>>
{

private static final long serialVersionUID = 1L;

private static final int[] EMPTY_INT = new int[0];

private final Class<T> elementType;
private int amount;
private T[] keys;
private int[] values;

public DependencyList(DependencyList<T> other)
{
	elementType = other.elementType;
	amount = other.amount;
	keys = newArray(elementType, amount);
	values = new int[amount];
	for (int i = 0; i < amount; i++)
	{
		keys[i] = other.keys[i];
		values[i] = other.values[i];
	}
}

public DependencyList(Class<T> elementType, int size)
{
	this.elementType = elementType;
	amount = 0;
	keys = newArray(elementType, size);
	values = new int[size];
}

public DependencyList(Class<T> elementType)
{ this(elementType, 0); }

@Override
public Iterator<DependencyListEntry<T>> iterator()
{
	return new Iterator<DependencyListEntry<T>>()
		{
			private int next = 0;
			@Override
			public boolean hasNext()
			{ return next < size(); }
			@Override
			public DependencyListEntry<T> next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				int current = next++;
				return new DependencyListEntry<T>(keys[current], values[current]);
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

private void ensureCapacity(int capacity)
{
	if (keys.length >= capacity)
		return;
	
	T[] temp0 = newArray(elementType, 2*keys.length+1);
	int[] temp1 = new int[temp0.length];
	System.arraycopy(keys, 0, temp0, 0, keys.length);
	System.arraycopy(values, 0, temp1, 0, keys.length);
	keys = temp0;
	values = temp1;
}

public int size()
{ return amount; }

public T peek(int pos)
{
	if ((pos < 0) || (pos >= amount))
		throw new ArrayIndexOutOfBoundsException();
	return keys[pos];
}

public int peekAmount(int pos)
{
	if ((pos < 0) || (pos >= amount))
		throw new ArrayIndexOutOfBoundsException();
	return values[pos];
}

/**
 * Loescht saemtliche Paare.
 */
public void clear()
{
	amount = 0;
	keys = newArray(elementType, 0);
	values = EMPTY_INT;
}

/**
 * Setzt den Wert fuer einen bestimmten Schluessel auf den gegebenen Wert.
 * Wenn kein Paar existiert, so wird eins mit dem Schluessel angelegt.
 * Existiert bereits eins, so wird der Wert gesetzt.
 */
public void set(T key, int value)
{
	for (int i = 0; i < amount; i++)
	{
		if (keys[i] == key)
		{
			values[i] = value;
			return;
		}
	}
	ensureCapacity(amount+1);
	keys[amount] = key;
	values[amount] = value;
	amount++;
}

/**
 * Setzt den Wert fuer einen bestimmten Schluessel auf das Maximum des 
 * aktuellen und neuen Wertes.
 * Wenn kein Paar existiert, so wird eins mit dem Schluessel angelegt.
 */
public void setMax(T key, int value)
{
	for (int i = 0; i < amount; i++)
	{
		if (keys[i] == key)
		{
			if (value > values[i])
				values[i] = value;
			return;
		}
	}
	ensureCapacity(amount+1);
	keys[amount] = key;
	values[amount] = value;
	amount++;
}

/**
 * Gibt den zum Schluessel gehoerigen Wert zurueck. Ist kein solches Paar
 * vorhanden, wird 0 zurueckgegeben.
 */
public int get(T key)
{
	for (int i = 0; i < amount; i++)
	{
		if (keys[i] == key)
			return values[i];
	}
	return 0;
}

/**
 * Mergt die angegebene Liste in diese.
 */
public void merge(DependencyList<T> other)
{
	if (other.elementType != elementType)
		throw new IllegalArgumentException();
	for (int i = 0; i < other.amount; i++)
		setMax(other.keys[i], other.values[i]);
}

public void add(DependencyList<T> other)
{
	if (other.elementType != elementType)
		throw new IllegalArgumentException();
	for (int i = 0; i < other.amount; i++)
		set(other.keys[i], get(other.keys[i])+other.values[i]);
}

/**
 * Prueft ob die gegebene IntList jeweils Schluessel einen hoeheren Wert
 * zuweist. Sinnvoll vor allem zum pruefen, ob die in dieser Liste
 * vorgegebenen Abhaengigkeiten erfuellt sind.
 * 
 * Enthaelt diese Liste keine Paare, so wird true zurueckgegeben.
 */
public boolean check(EnumIntMap<T> data)
{
	for (int i = 0; i < amount; i++)
		if (data.get(keys[i]) < values[i]) return false;
	return true;
}

/**
 * Gibt eine Zeichenkettenrepraesentation dieser Liste zurueck.
 * Beispiel: "[0:5, 8:10]", "[]"
 * Die Reihenfolge der Schluessel ist nicht festgelegt.
 */
@Override
public String toString()
{
	StringBuffer result = new StringBuffer(5*amount);
	result.append('[');
	for (int i = 0; i < amount; i++)
	{
		result.append(keys[i]).append(':').append(values[i]);
		if (i < amount-1) result.append(", ");
	}
	result.append(']');
	return result.toString();
}


/**
 * Internal helper function. Creates a new array of type T.
 * The generics warning is suppressed.
 * Otherwise there would be warnings all over the place.
 */
@SuppressWarnings("unchecked")
private static <T extends Enum<T>> T[] newArray(Class<T> clazz, int length)
{ return (T[]) Array.newInstance(clazz, length); }

public static <T extends Enum<T>> DependencyList<T> of(Class<T> elementType, int length)
{ return new DependencyList<T>(elementType, length); }

public static <T extends Enum<T>> DependencyList<T> of(Class<T> elementType)
{ return new DependencyList<T>(elementType); }

}
