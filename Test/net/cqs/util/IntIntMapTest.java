package net.cqs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

public class IntIntMapTest
{

private IntIntMap map;

@Before
public void setUp()
{
	map = new IntIntMap();
}

@Test
public void testEmpty()
{
	assertEquals(0, map.getSum());
}

@Test
public void testSet1()
{
	map.set(0, 3);
	assertEquals(3, map.getSum());
}

@Test(expected=IllegalArgumentException.class)
public void testSet2()
{
	map.set(-1, 1);
}

@Test
public void testSet3()
{
	map.set(0, 3);
	assertEquals(3, map.get(0));
}

@Test
public void testSet4()
{
	assertEquals(0, map.get(-1));
}

@Test
public void testIncrease1()
{
	map.increase(5);
	assertEquals(1, map.get(5));
}

@Test
public void testIncrease2()
{
	map.set(5, 1);
	map.increase(5, 4);
	assertEquals(5, map.get(5));
}

@Test
public void testDecrease()
{
	map.decrease(5);
	assertEquals(-1, map.get(5));
}

@Test
public void testAdd1()
{
	map.set(4, 8);
	IntIntMap other = new IntIntMap();
	other.set(4, -4);
	other.set(5, 5);
	map.add(other);
	assertEquals(4, map.get(4));
	assertEquals(5, map.get(5));
}

@Test
public void testSubtract1()
{
	map.set(4, 8);
	IntIntMap other = new IntIntMap(6);
	other.set(4, -4);
	other.set(5, 5);
	map.subtract(other);
	assertEquals(12, map.get(4));
	assertEquals(-5, map.get(5));
}

@Test
public void testClear1()
{
	map.set(4, 19);
	map.clear();
	assertEquals(0, map.getSum());
	assertEquals(0, map.get(4));
}

@Test
public void testClone1()
{
	IntIntMap result = new IntIntMap(map);
	assertEquals(0, result.getSum());
}

@Test
public void testClone2()
{
	IntIntMap result = new IntIntMap(map);
	assertNotSame(result, map);
}

@Test
public void testClone3()
{
	map.set(4, 5);
	IntIntMap result = new IntIntMap(map);
	assertEquals(5, result.get(4));
}

@Test
public void testClone4()
{
	IntIntMap result = new IntIntMap(map);
	map.set(1, 2);
	assertEquals(0, result.get(1));
}

@Test
public void testToString1()
{ assertEquals("[]", map.toString()); }

@Test
public void testToString2()
{
	map.set(2, 5);
	map.set(0, -8);
	assertEquals("[0:-8, 2:5]", map.toString());
}

}
