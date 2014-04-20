package net.cqs.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EnumQueue<T extends Enum<T>> implements Iterable<EnumQueueEntry<T>>, Serializable
{

private static final long serialVersionUID = 1L;

/**
 * maximum value for sum()
 */
public static final int MAX_SUM = 1000000;

/**
 * minimum length of the queue
 * when enlarging a queue, it is enlarged by MIN
 */
static final int MIN = 5;

/**
 * determines whether entries are allowed to have negative values
 */
private boolean positiveOnly;

/**
 * stores the sum over all amounts of units
 */
private int sum;
private int last;

private Class<T> elementType;
private T[] data;
private int[] amount;

/**
 * creates an empty queue
 */
public EnumQueue(Class<T> elementType, boolean positiveOnly)
{
	this.elementType = elementType;
	this.positiveOnly = positiveOnly;
	this.last = 0;
	this.sum = 0;
	this.data = newArray(this.elementType, MIN);
	this.amount = new int[MIN];
}

/**
 * creates a copy of other.
 */
public EnumQueue(EnumQueue<T> other)
{
	this.elementType = other.elementType;
	this.positiveOnly = other.positiveOnly;
	this.last = other.last;
	this.sum = other.sum;
	this.data = newArray(elementType, last);
	this.amount = new int[last];
	for (int i = 0; i < last; i++)
	{
		this.data[i] = other.data[i];
		this.amount[i] = other.amount[i];
	}
}


@Override
public Iterator<EnumQueueEntry<T>> iterator()
{
	return new Iterator<EnumQueueEntry<T>>()
		{
			private int next = 0;
			@Override
			public boolean hasNext()
			{ return next < size(); }
			@Override
			public EnumQueueEntry<T> next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				int current = next++;
				return new EnumQueueEntry<T>(data[current], amount[current]);
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

/**
 * returns the total amount of units in the queue (sums up all amounts)
 * efficient!
 */
public int sum()
{ return sum; }

public boolean positiveOnly()
{ return positiveOnly; }

/**
 * returns the amount of entries in the whole queue
 * size() is compatible to the naming in the Java Standard Library
 */
public int size()
{ return last; }

/**
 * returns the unit at pos
 * if pos < 0 or pos >= size(),
 * an ArrayIndexOutOfBounds-Exception is thrown
 */
public T peek(int pos)
{
	if ((pos < 0) || (pos >= size()))
		throw new ArrayIndexOutOfBoundsException(pos);
	return data[pos];
}

public T peek()
{ return peek(0); }

/**
 * returns the amount of the unit at pos
 * if pos < 0 or pos >= size(),
 * an ArrayIndexOutOfBounds-Exception is thrown
 */
public int peekAmount(int pos)
{
	if ((pos < 0) || (pos >= size()))
		throw new ArrayIndexOutOfBoundsException(pos);
	return amount[pos];
}

public int peekAmount()
{ return peekAmount(0); }

/**
 * aequivalent to peek(0).
 * no exception is thrown though, instead null is returned
 */
public T get()
{
	if (last == 0)
		return null;
	return data[0];
}

/**
 * deletes the entry at position pos out of the queue
 * moves all the entries behind pos one position forward
 * if the queue has more than MIN free spaces, the queue is reduced
 */
void delete(int pos)
{
	if ((pos < 0) || (pos >= last))
		throw new ArrayIndexOutOfBoundsException(pos);
	
	sum -= Math.abs(amount[pos]);
	
	// resize arrays
	if (last < data.length-MIN)
	{
		last--;
		T[] oldData = data;
		int[] oldAmount = amount;
		data = newArray(elementType, last);
		amount = new int[last];
		for (int i = 0; i < last; i++)
		{
			if (i < pos)
			{
				data[i] = oldData[i];
				amount[i] = oldAmount[i];
			}
			else
			{
				data[i] = oldData[i+1];
				amount[i] = oldAmount[i+1];
			}
		}
	}
	// do not resize arrays
	else
	{
		last--;
		for (int i = pos; i < last; i++)
		{
			data[i] = data[i+1];
			amount[i] = amount[i+1];
		}
		data[last] = null;
		amount[last] = 0;
	}
}

/**
 * Removes the first entry out of the queue and returns it.
 * 
 * As the entries are stored in the shape of (T, amount), amount usually only gets decreased by one.
 * If the amount is decreased to 0, the entry is removed.
 * If the queue is empty, null is returned.
 */
public T remove()
{
	if (last == 0)
		return null;
	T result = data[0];
	if (amount[0] > 0)
		amount[0]--;
	else
		amount[0]++;
	if (amount[0] == 0)
		delete(0);
	sum--;
	return result;
}

/**
 * adds a (unit, amount)-entry into the queue
 * automatically enlarges the queue, if the queue is full
 * If sum() would get larger than MAX_SUM, nothing happens
 */
public boolean add(T key, int number)
{
	if (key == null)
		throw new NullPointerException();
	
	if (number == 0) return false;
	
	if ((number < 0) && positiveOnly)
		throw new IllegalArgumentException();
	
	if (number > MAX_SUM) number = MAX_SUM;
	if (number < -MAX_SUM) number = -MAX_SUM;
	
	if (sum > MAX_SUM-Math.abs(number))
	{
		if (number > 0)
			number = MAX_SUM-sum;
		else
			number = -MAX_SUM+sum;
	}
	
	if (number == 0)
		return false;

	if (last == data.length)
	{
		T[] oldData = data;
		int[] oldAmount = amount;
		data = newArray(elementType, oldData.length+MIN);
		amount = new int[oldData.length+MIN];
		for (int i = 0; i < oldData.length; i++)
		{
			data[i] = oldData[i];
			amount[i] = oldAmount[i];
		}
	}
	data[last] = key;
	amount[last] = number;
	sum += Math.abs(number);
	last++;
	return true;
}

/**
 * Moves the element at last-pos-1 up by howfar elements.
 * 
 * @return true if an element was moved, false if the parameters are out of range
 */
public boolean moveUpInverse(int pos, int howfar)
{
	if (pos < 0) return false;
	int ipos = last - pos - 1;
	if (ipos < 0) return false;
	
	int jpos = ipos-howfar;
	if (jpos < 0) return false;
	if (jpos >= last) return false;
	
	T temp0 = data[ipos];
	data[ipos] = data[jpos];
	data[jpos] = temp0;
	
	int temp1 = amount[ipos];
	amount[ipos] = amount[jpos];
	amount[jpos] = temp1;
	return true;
}

/**
 * Move the element at last-pos-1 up to the first position (index 0).
 * @return true, if an element was moved, false if the index is out of range
 */
public boolean moveTopInverse(int pos)
{
	if (pos < 0) return false;
	int ipos = last - pos - 1;
	if (ipos < 0) return false;
	
	T temp0 = data[ipos];
	for (int i = ipos; i > 0; i--)
		data[i] = data[i-1];
	data[0] = temp0;
	
	int temp1 = amount[ipos];
	for (int i = ipos; i > 0; i--)
		amount[i] = amount[i-1];
	amount[0] = temp1;
	
	return true;
}

/**
 * increases the amount in the pos-last entry by number.
 * 0 is the last entry, 1 the second-last etc
 * if number is negative the amount can be decreased
 * if the amount is decreased to 0, the entry is removed
 * if positiveOnly and the amount is decreased to less than 0, the entry is removed
 */
public boolean modifyInverse(int pos, int number)
{
	if (pos < 0) return false;
	int ipos = last-pos-1;
	if (ipos < 0) return false;
	
	if (number > 2*MAX_SUM) number = 2*MAX_SUM;
	if (number < -2*MAX_SUM) number = -2*MAX_SUM;
	
	int oldAmount = amount[ipos];
	if (oldAmount > MAX_SUM-number) number = MAX_SUM-oldAmount;
	if (oldAmount < -MAX_SUM-number) number = -MAX_SUM-oldAmount;
	
	int target = oldAmount+number;
	if (positiveOnly && (target < 0)) target = 0;
	
	sum -= Math.abs(oldAmount);
	amount[ipos] = 0;
	
	if (sum > MAX_SUM-Math.abs(target))
	{
		if (target > 0)
			target = MAX_SUM-sum;
		else
			target = -MAX_SUM+sum;
	}
	
	sum += Math.abs(target);
	amount[ipos] = target;
	
	if (amount[ipos] == 0)
		delete(ipos);
	
	return true;
}

/**
 * Deletes the (last-pos-1) entry.
 * 
 * @return true, if an entry was deleted; false, otherwise.
 */
public boolean deleteInverse(int pos)
{
	if (pos < 0) return false;
	int ipos = last-pos-1;
	if (ipos < 0) return false;
	delete(ipos);
	return true;
}

/**
 * Clears the queue.
 */
public void clear()
{
	data = newArray(elementType, MIN);
	amount = new int[MIN];
	last = 0;
	sum = 0;
}

@Override
public String toString()
{
	StringBuilder result = new StringBuilder();
	result.append('[');
	for (int i = 0; i < last; i++)
	{
		if (i != 0) result.append(", ");
		result.append(data[i]).append(':').append(amount[i]);
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
private static <T> T[] newArray(Class<T> clazz, int length)
{ return (T[]) Array.newInstance(clazz, length); }

public static <T extends Enum<T>> EnumQueue<T> of(Class<T> clazz, boolean positiveOnly)
{ return new EnumQueue<T>(clazz, positiveOnly); }

}
