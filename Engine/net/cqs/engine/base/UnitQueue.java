package net.cqs.engine.base;

import java.io.Serializable;

import net.cqs.config.units.Unit;

/**
 * represents an unit-queue
 * entries are of the shape (unit, amount)
 */
public final class UnitQueue implements Serializable
{

private static final long serialVersionUID = 1L;

/**
 * maximum value for sum()
 */
static final int MAX_SUM = 1000000;

/**
 * minimum length of the queue
 * when enlarging a queue, it is enlarged by MIN
 */
private static final int MIN = 5;

/**
 * stores the sum over all amounts of units
 */
private int sum;
private int last;

private Unit[] data;
private int[] amount;

/**
 * creates an empty queue
 */
public UnitQueue()
{
	last = 0;
	sum = 0;
	data = new Unit[MIN];
	amount = new int[MIN];
}

/**
 * creates a copy of other.
 */
public UnitQueue(UnitQueue other)
{
	last = other.last;
	sum = other.sum;
	data = new Unit[last];
	amount = new int[last];
	for (int i = 0; i < last; i++)
	{
		data[i] = other.data[i];
		amount[i] = other.amount[i];
	}
}

/**
 * returns the total amount of units in the queue (sums up all amounts)
 * efficient!
 */
public int sum()
{ return sum; }

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
public Unit peek(int pos)
{
	if ((pos < 0) || (pos >= size()))
		throw new ArrayIndexOutOfBoundsException(pos);
	return data[pos];
}

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

/**
 * aequivalent to peek(0).
 * no exception is thrown though, instead null is returned
 */
public Unit get()
{
	if (last == 0) return null;
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
		Unit[] oldData = data;
		int[] oldAmount = amount;
		data = new Unit[last];
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
 * removes the first entry out of the queue and returns it;
 * as the entries are stored in the shape of (unit, amount),
 * amount usually only gets decreased by one
 * if the amount is decreased to 0, the entry is removed
 * if the queue is empty, null is returned
 */
public Unit remove()
{
	if (last == 0) return null;
	Unit result = data[0];
	if (amount[0] > 1)
	{
		amount[0]--;
		sum--;
	}
	else
		delete(0);
	return result;
}

/**
 * adds a (unit, amount)-entry into the queue
 * automatically enlarges the queue, if the queue is full
 * If sum() would get larger than MAX_SUM, nothing happens
 */
public boolean add(Unit slot, int number)
{
	if (slot == null)
		throw new NullPointerException();
	
	if (number == 0) return false;
	
	if (number < 0)
		throw new IllegalArgumentException();
	
	if (number > MAX_SUM) number = MAX_SUM;
	
	if (sum > MAX_SUM-number)
		number = MAX_SUM-sum;
	
	if (number == 0)
		return false;

	if (last == data.length)
	{
		Unit[] oldData = data;
		int[] oldAmount = amount;
		data = new Unit[oldData.length+MIN];
		amount = new int[oldData.length+MIN];
		for (int i = 0; i < oldData.length; i++)
		{
			data[i] = oldData[i];
			amount[i] = oldAmount[i];
		}
	}
	data[last] = slot;
	amount[last] = number;
	sum += Math.abs(number);
	last++;
	return true;
}

/**
 * Moves the element at index last-pos-1 up by howfar elements.
 * howfar may be zero or negative (in which case the element is moved down)
 * @return true, if an element was moved; false, otherwise
 */
public boolean moveUpInverse(int pos, int howfar)
{
	if (pos < 0) return false;
	int ipos = last - pos - 1;
	if (ipos < 0) return false;
	
	int jpos = ipos-howfar;
	if (jpos < 0) return false;
	if (jpos >= last) return false;
	
	Unit temp0 = data[ipos];
	data[ipos] = data[jpos];
	data[jpos] = temp0;
	
	int temp1 = amount[ipos];
	amount[ipos] = amount[jpos];
	amount[jpos] = temp1;
	return true;
}

/**
 * Moves the element at index last-pos-1 to the first position (index 0).
 * @return true, if an element was moved; false, otherwise
 */
public boolean moveTopInverse(int pos)
{
	if (pos < 0) return false;
	int ipos = last - pos - 1;
	if (ipos < 0) return false;
	
	Unit temp0 = data[ipos];
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
 * if the amount is decreased to 0 or less, the entry is removed
 */
public boolean modifyInverse(int pos, int number)
{
	if (pos < 0) return false;
	int ipos = last-pos-1;
	if (ipos < 0) return false;
	
	if (number > MAX_SUM) number = MAX_SUM;
	if (number < -2*MAX_SUM) number = -2*MAX_SUM;
	
	int oldAmount = amount[ipos];
	if (oldAmount > MAX_SUM-number) number = MAX_SUM-oldAmount;
	if (oldAmount < -number) number = -oldAmount;
	
	int target = oldAmount+number;
	
	sum -= oldAmount;
	amount[ipos] = 0;
	
	if (sum > MAX_SUM-target)
		target = MAX_SUM-sum;
	
	sum += target;
	amount[ipos] = target;
	
	if (amount[ipos] == 0)
		delete(ipos);
	
	return true;
}

/**
 * deletes the pos-last entry
 * aequivalent to modifyReverse(pos, -MAX_SUM).
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
 * clears the queue
 */
public void clear()
{
	data = new Unit[MIN];
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

}
