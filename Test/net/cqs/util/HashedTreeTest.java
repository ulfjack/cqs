package net.cqs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("boxing")
public class HashedTreeTest
{

private HashedTree<String,Object> tree;

@Before
public void setUp()
{ tree = HashedTree.of(String.class, Object.class); }

@Test
public void testSize1()
{ assertEquals(0, tree.size()); }

@Test
public void testSize2()
{
	tree.put("A", new Object());
	assertEquals(1, tree.size());
}

@Test
public void testSize3()
{
	tree.put("A", new Object());
	tree.put("B", new Object());
	assertEquals(2, tree.size());
	tree.remove("B");
	assertEquals(1, tree.size());
}

@Test
public void testSize4()
{
	tree.put("A", new Object());
	tree.put("B", new Object());
	assertEquals(2, tree.size());
	tree.remove("A");
	assertEquals(1, tree.size());
}

@Test
public void testPut1()
{
	tree.put("A", new Object());
	assertEquals(true, tree.containsKey("A"));
}

@Test
public void testPut2()
{
	tree.put("A", new Object());
	tree.put("B", new Object());
	assertEquals(true, tree.containsKey("A"));
	assertEquals(true, tree.containsKey("B"));
}

@Test
public void testPut3()
{
	Object o = new Object();
	tree.put("A", o);
	assertEquals(true, tree.containsKey("A"));
	assertSame(o, tree.get("A"));
}

@Test
public void testClear()
{
	tree.put("A", new Object());
	tree.put("B", new Object());
	tree.clear();
	assertEquals(0, tree.size());
	Object o = new Object();
	tree.put("A", o);
	assertEquals(true, tree.containsKey("A"));
	assertSame(o, tree.get("A"));
}

@Test
public void testClearThenPut()
{
	tree.put("A", new Object());
	tree.put("B", new Object());
	tree.clear();
	assertEquals(0, tree.size());
}

@Test(expected=NoSuchElementException.class)
public void testIterator1()
{
	Object o = new Object();
	tree.put("A", o);
	Iterator<?> it = tree.iterator();
	it.next();
	it.next();
}

}
