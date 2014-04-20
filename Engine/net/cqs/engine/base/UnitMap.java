package net.cqs.engine.base;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.units.UnitClassEnum;

/**
 * A list of pairs (unit type, count).
 */
public final class UnitMap implements Iterable<UnitEntry>, Serializable
{

	public static UnitMap copyOf(UnitMap original)
	{ return new UnitMap(original); }

private static final long serialVersionUID = 1L;
private static final int[] EMPTY_INT = new int[0];
private static final Unit[] EMPTY_UNIT = new Unit[0];

private static final int INCREASE = 10;
private static final int DISTANCE = 2*INCREASE;

/*
 * WARNING: The arrays can be longer than the content!
 *          Only iterate up to amount.
 */
private Unit[] ids = new Unit[INCREASE];
private int[] nums = new int[INCREASE];
private int amount = 0;

private int sum = 0;
private int planetarySum = 0;
private int interplanetarySum = 0;
private int interstellarSum = 0;

public UnitMap(UnitSystem unitSystem, String s) 
{
	this();
	deserialize(unitSystem, s);
}

public UnitMap(UnitMap other)
{
	ids = other.ids.clone();
	nums = other.nums.clone();
	
	amount = other.amount;
	sum = other.sum;
	
	countUnits();
}

public UnitMap()
{/*OK*/}


public int sum()
{ return sum; }

public int planetarySum()
{ return planetarySum; }

public int spaceSum()
{ return interplanetarySum()+interstellarSum(); }

public int interplanetarySum()
{ return interplanetarySum; }

public int interstellarSum()
{ return interstellarSum; }

public int size()
{ return amount; }

private void countUnits()
{
	int tempsum = 0;
	planetarySum = 0;
	interplanetarySum = 0;
	interstellarSum = 0;
	for (int i = 0; i < amount; i++)
	{
		tempsum += nums[i];
		if (ids[i].isPlanetary())
			planetarySum += nums[i];
		else if (ids[i].isInterplanetary())
			interplanetarySum += nums[i];
		else
			interstellarSum += nums[i];
	}
	sum = tempsum;
}

void delete(int index)
{
	if ((index < 0) || (index >= amount))
		throw new ArrayIndexOutOfBoundsException(index);
	
	sum -= nums[index];
	if (ids[index].isPlanetary())
		planetarySum -= nums[index];
	else if (ids[index].isInterplanetary())
		interplanetarySum -= nums[index];
	else
		interstellarSum -= nums[index];
	
	amount--;
	for (int i = index; i < amount; i++)
	{
		ids[i] = ids[i+1];
		nums[i] = nums[i+1];
	}
	
	if (amount < ids.length - DISTANCE)
	{
		Unit[] temp0 = new Unit[amount];
		int[]  temp1 = new int[amount];
		
		for (int i = 0; i < amount; i++)
		{
			temp0[i] = ids[i];
			temp1[i] = nums[i];
		}
		
		ids = temp0;
		nums = temp1;
	}
}

private void add(Unit typ, int value)
{
	if (value < 0)
		throw new IllegalArgumentException(value+" < 0");
	
	sum += value;
	if (typ.isPlanetary())
		planetarySum += value;
	else if (typ.isInterplanetary())
		interplanetarySum += value;
	else
		interstellarSum += value;
	
	if (amount == ids.length)
	{
		Unit[] temp0 = new Unit[ids.length+INCREASE];
		int[]  temp1 = new int[ids.length+INCREASE];
		
		for (int i = 0; i < ids.length; i++)
		{
			temp0[i] = ids[i];
			temp1[i] = nums[i];
		}
		
		ids = temp0;
		nums = temp1;
	}
	
	ids[amount] = typ;
	nums[amount] = value;
	
	amount++;
}

public Unit peek(int pos)
{
	if ((pos < 0) || (pos >= size()))
		throw new ArrayIndexOutOfBoundsException(pos);
	return ids[pos];
}

public int peekAmount(int pos)
{
	if ((pos < 0) || (pos >= size()))
		throw new ArrayIndexOutOfBoundsException(pos);
	return nums[pos];
}

	private class UnitMapIterator implements UnitIterator
	{
		UnitSelector selector;
		int lastElement = -1;
		int nextElement = 0;
		
		UnitMapIterator(UnitSelector selector)
		{
			this.selector = selector;
			while (nextElement < amount)
			{
				if (selector.isSelected(ids[nextElement])) 
					return;
				nextElement++;
			}
			nextElement = -1;
		}
		
		@Override
    public boolean hasNext()
		{ return nextElement >= 0; }
		@Override
    public void next()
		{
			if (nextElement < 0)
				throw new NoSuchElementException();
			lastElement = nextElement;
			nextElement++;
			while (nextElement < amount)
			{
				if (selector.isSelected(ids[nextElement])) 
					return;
				nextElement++;
			}
			nextElement = -1;
		}
		@Override
    public Unit key()
		{
			if (lastElement < 0)
				throw new IllegalStateException();
			return ids[lastElement];
		}
		@Override
    public int value()
		{
			if (lastElement < 0)
				throw new IllegalStateException();
			return nums[lastElement];
		}
		@Override
    public void setValue(int count)
		{
			if (lastElement < 0)
				throw new IllegalStateException();
			UnitMap.this.setValue(lastElement, count);
			if (count == 0)
			{
				lastElement = -1;
				nextElement--;
			}
		}
		@Override
    public void decrease(int count)
		{
			if (lastElement < 0)
				throw new IllegalStateException();
			int target = nums[lastElement]-count;
			UnitMap.this.setValue(lastElement, target);
			if (target == 0)
			{
				lastElement = -1;
				nextElement--;
			}
		}
		@Override
    public void increase(int count)
		{
			if (lastElement < 0)
				throw new IllegalStateException();
			int target = nums[lastElement]+count;
			UnitMap.this.setValue(lastElement, target);
			if (target == 0)
			{
				lastElement = -1;
				nextElement--;
			}
		}
		@Override
    public void remove()
		{
			if (lastElement < 0)
				throw new IllegalStateException();
			UnitMap.this.delete(lastElement);
			lastElement = -1;
			nextElement--;
		}
	}

public UnitIterator unitIterator(UnitSelector selector)
{ return new UnitMapIterator(selector); }

public UnitIterator unitIterator()
{ return unitIterator(UnitSelector.ALL); }

@Override
public Iterator<UnitEntry> iterator()
{
	return new Iterator<UnitEntry>()
		{
			private int nextElement = 0;
			@Override
			public boolean hasNext()
			{ return nextElement < amount; }
			@Override
			public UnitEntry next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				int e = nextElement++;
				return new UnitEntry(ids[e], nums[e]);
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

public void clear()
{
	ids = EMPTY_UNIT;
	nums = EMPTY_INT;
	sum = 0;
	planetarySum = 0;
	interplanetarySum = 0;
	amount = 0;
}

public void add(UnitMap other)
{
	UnitIterator it = other.unitIterator();
	while (it.hasNext())
	{
		it.next();
		increase(it.key(), it.value());
	}
}

public void subtract(UnitMap other)
{
	UnitIterator it = other.unitIterator();
	while (it.hasNext())
	{
		it.next();
		decrease(it.key(), it.value());
	}
}


public boolean contains(Unit typ)
{
	for (int i = 0; i < amount; i++)
	{
		if (ids[i] == typ) return true;
	}
	return false;
}

public void importTypes(UnitMap other)
{
	UnitIterator it = other.unitIterator();
	while (it.hasNext())
	{
		it.next();
		if (!contains(it.key())) add(it.key(), 0);
	}
}

void setValue(int index, int amount)
{
	if (amount < 0)
		throw new IllegalArgumentException(amount+" < 0");
	if (amount == 0)
		delete(index);
	else
	{
		sum += amount-nums[index];
		if (ids[index].isPlanetary())
			planetarySum += amount-nums[index];
		else if (ids[index].isInterplanetary())
			interplanetarySum += amount-nums[index];
		else
			interstellarSum += amount-nums[index];
		nums[index] = amount;
	}
}

public void set(Unit typ, int value)
{
	for (int i = 0; i < amount; i++)
		if (ids[i] == typ)
		{
			setValue(i, value);
			return;
		}
	if (value != 0) add(typ, value);
}

public int get(Unit typ)
{
	for (int i = 0; i < amount; i++)
		if (ids[i] == typ) return nums[i];
	return 0;
}

public int get(UnitSelector selector)
{
	int result = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		if (selector.isSelected(it.key()))
			result += it.value();
	}
	return result;
}

public void increase(Unit typ, int byamount)
{ set(typ, get(typ)+byamount); }

public void increase(Unit typ)
{ increase(typ, 1); }

public void decrease(Unit typ, int byamount)
{ set(typ, get(typ)-byamount); }

public void decrease(Unit typ)
{ decrease(typ, 1); }

public boolean contains(boolean space)
{
	if (space) return spaceSum() != 0;
	return planetarySum != 0;
}

public boolean contains(UnitSelector selector)
{
	if (sum == 0) return false;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		if ((it.value() > 0) && selector.isSelected(it.key()))
			return true;
	}
	return false;
}

@Override
public String toString()
{
	StringBuilder sb = new StringBuilder(256);
	sb.append("[");
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		sb.append(it.key());
		sb.append(":");
		sb.append(it.value());
		if (it.hasNext()) sb.append(",");
	}
	sb.append("]");
	return sb.toString();
}

private static final String SEP = "|";

public String serialize()
{
  StringBuffer sb = new StringBuffer();
  sb.append(amount);
  for (int i = 0; i < amount; i++)
  {
  	sb.append(SEP).append(ids[i]).append(SEP).append(nums[i]);
  }
  return sb.toString();
}

public void deserialize(UnitSystem unitSystem, String s)
{
	clear();
	
	String[] tokens = s.split(Pattern.quote(SEP));
	try
	{
		amount = Integer.parseInt(tokens[0]);
		if (tokens.length != 2*amount+1)
			throw new IllegalArgumentException();
		nums = new int[amount];
		ids = new Unit[amount];
		for (int i = 0; i < amount; i++)
		{
			ids[i] = unitSystem.parseUnit(tokens[2*i+1]);
			nums[i] = Integer.parseInt(tokens[2*i+2]);
		}
	}
	catch (NumberFormatException e)
	{ throw new IllegalArgumentException(s); }
	
	countUnits();
}

public Unit findSpecialUnit(UnitSpecial ability, boolean space)
{
	UnitIterator it = unitIterator(
			space ? UnitSelector.SPACE_ONLY : UnitSelector.GROUND_ONLY);
	while (it.hasNext())
	{
		it.next();
		if ((it.value()>0) && (it.key().hasSpecial(ability)))
			return it.key();
	}
	return null;
}

public int[] calculateGroupCount(UnitSelector selector)
{
	int groups = UnitClassEnum.values().length;
	int[] count = new int[groups];
	
	UnitIterator it = unitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		int group = it.key().getGroup();
		count[group] += it.value();
	}
	
	return count;
}

public float[] calculateGroupProportion(UnitSelector selector)
{
	int groups = UnitClassEnum.values().length;
	float[] proportion = new float[groups];
	long[] scaledCount = new long[groups];
	long scaledSum = 0;
	
	UnitIterator it = unitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		int group = it.key().getGroup();
		long value = ((long) it.value()) * it.key().getSize();
		
		scaledCount[group] += value;
		scaledSum += value;
	}
	
	if (scaledSum > 0)
	{
		for (int i = 0; i < groups; i++)
			proportion[i] = (float) (scaledCount[i] / (double) scaledSum);
	}
	
	return proportion;
}


public long getGroundUnitCapacity()
{
	long result = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		result += ((long) it.value()) * it.key().getGroundUnitCapacity();
	}
	return result;
}

public long getSpaceUnitCapacity()
{
	long result = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		result += ((long) it.value()) * it.key().getSpaceUnitCapacity();
	}
	return result;
}

public long getGroundUnitSize()
{
	long result = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		Unit unit = it.key();
		if (unit.isPlanetary())
			result += ((long) it.value()) * unit.getSize();
	}
	return result;
}

public long getSpaceUnitSize()
{
	long result = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		Unit unit = it.key();
		if (!unit.isPlanetary() && !unit.hasSpecial(UnitSpecial.WARP))
			result += ((long) it.value()) * unit.getSize();
	}
	return result;
}

public float getPower(UnitSelector selector)
{
	float result = 0;
	UnitIterator it = unitIterator(selector);
	while (it.hasNext())
	{
		it.next();
		result += it.value() * (float) it.key().getPower();
	}
	return result;
}

public long getResourceSpace()
{
	long result = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		result += ((long) it.value()) * it.key().getResourceCapacity();
	}
	return result;
}

public long getScore()
{
	long value = 0;
	UnitIterator it = unitIterator();
	while (it.hasNext())
	{
		it.next();
		value += ((long) it.value()) * it.key().getScore();
	}
	return value;
}	

}
