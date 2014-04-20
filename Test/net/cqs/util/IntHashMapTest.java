package net.cqs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IntHashMapTest
{

private IntHashMap<Object> map;

@Before
public void setUp()
{
	map = IntHashMap.of(Object.class);
}

@Test
public void testPutSingle()
{
	Object o = new Object();
	map.put(2, o);
	assertTrue(map.get(2) == o);
	for (int i = 0; i < 100; i++)
		if (i != 2)
			assertTrue(map.get(i) == null);
}

@Test
public void testPutMany()
{
	Object[] data = new Object[100];
	for (int i = 0; i < data.length; i++)
	{
		data[i] = new Object();
		map.put(i, data[i]);
	}
	
	for (int i = 0; i < data.length; i++)
		assertTrue(map.get(i) == data[i]);
	
	for (int i = data.length; i < 1000; i++)
		assertTrue(map.get(i) == null);
}

@Test
public void testPutNegative()
{
	Object[] data = new Object[100];
	for (int i = 0; i < data.length; i++)
	{
		data[i] = new Object();
		map.put(i-50, data[i]);
	}
	
	for (int i = 0; i < data.length; i++)
		assertTrue(map.get(i-50) == data[i]);
	
	for (int i = data.length-50; i < 1000; i++)
		assertTrue(map.get(i) == null);
}

@Test(expected=NullPointerException.class)
public void testPutNull()
{
	map.put(-1, null);
}

@Test(expected=NullPointerException.class)
public void testPutNull2()
{
	map.put(2, null);
}

@Test
public void testContainsKey()
{
	map.put(99, new Object());
	assertTrue(map.containsKey(99));
	for (int i = 0; i < 99; i++)
		assertFalse(map.containsKey(i));
}

@Test
public void testContainsKey2()
{
	map.put(1, new Object());
	map.remove(1);
	assertFalse(map.containsKey(-1));
}

@Test
public void testContainsValue()
{
	Object o1 = new Object();
	Object o2 = new Object();
	map.put(1234, o1);
	assertTrue(map.containsValue(o1));
	assertFalse(map.containsValue(o2));
	assertFalse(map.containsValue(null));
}

@Test
public void testEmpty1()
{
	assertEquals(0, map.size());
}

@Test
public void testEmpty()
{
	map.put(1234, new Object());
	assertEquals(1, map.size());
}

@Test
public void testSize1()
{
	assertEquals(0, map.size());
	map.put(0, new Object());
	assertEquals(1, map.size());
}

@Test
public void testSize2()
{
	assertEquals(0, map.size());
	map.put(1, new Object());
	assertEquals(1, map.size());
}

@Test
public void testSizeMany()
{
	for (int i = 0; i < 4321; i++)
	{
		assertEquals(i, map.size());
		map.put(i, new Object());
	}
	
	for (int i = 0; i < 4321; i++)
	{
		assertTrue(map.size() == 4321);
		map.put(i, new Object());
	}
	
	for (int i = 0; i < 100; i++)
	{
		assertTrue(map.size() == 4321-i);
		map.remove(i);
	}
	
	for (int i = 0; i < 100; i++)
	{
		assertEquals(4321-100, map.size());
		map.remove(i);
	}
}

@Test
public void testConstructor1()
{
	IntHashMap<Object> result = new IntHashMap<Object>(Object.class, 10000, 0.75f);
	assertTrue(result.capacity() >= 10000);
}

@Test
public void testConstructor2()
{
	IntHashMap<Object> result = new IntHashMap<Object>(Object.class, 10000, -1);
	assertTrue(result.getLoadFactor() > 0);
}

@Test
public void testAdd1()
{
	assertEquals(17, map.capacity());
	map.put(1, new Object());
	assertEquals(1, map.size());
	map.put(18, new Object());
	assertEquals(2, map.size());
	map.put(18, new Object());
	assertEquals(2, map.size());
}

@Test
public void testAdd2()
{
	Object o1, o2;
	assertEquals(17, map.capacity());
	map.put(1, o1 = new Object());
	assertEquals(1, map.size());
	map.put(18, o2 = new Object());
	assertEquals(2, map.size());
	assertSame(o1, map.get(1));
	assertSame(o2, map.get(18));
}

@Test
public void testRehash()
{
	assertEquals(17, map.capacity());
	Object[] data = new Object[17];
	for (int i = 0; i < 17; i++)
	{
		data[i] = new Object();
		map.put(i, data[i]);
		if (i % 2 == 0)
			map.remove(i);
	}
	assertEquals(8, map.size());
	assertNull(map.get(0));
}

@Test
public void testClear()
{
	map.put(1, new Object());
	assertEquals(1, map.size());
	map.clear();
	assertEquals(0, map.size());
	assertEquals(null, map.get(1));
}

@Test
public void testClone1()
{
	IntHashMap<Object> result = new IntHashMap<Object>(map);
	assertNotSame(result, map);
}

@Test
public void testClone2()
{
	map.put(7, new Object());
	IntHashMap<Object> result = new IntHashMap<Object>(map);
	assertNotNull(result.get(7));
}

@Test
public void testClone3()
{
	map.put(7, new Object());
	IntHashMap<Object> result = new IntHashMap<Object>(map);
	map.remove(7);
	assertNotNull(result.get(7));
}

}
