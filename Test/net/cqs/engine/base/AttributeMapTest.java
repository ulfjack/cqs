package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({"boxing", "unchecked"})
public class AttributeMapTest
{

static final Attribute<Integer> TEST_ATTR1 = Attribute.of(Integer.class, "test1", new Integer(1234));
static final Attribute<Boolean> TEST_ATTR2 = Attribute.of(Boolean.class, "test2", new Boolean(true));
static final Attribute<Long>    TEST_ATTR3 = Attribute.of(Long.class,    "test3", new Long(4321L));

private AttributeMap map;

@Before
public void setUp()
{
	map = new AttributeMap();
}

@Test
public void testEmpty()
{ assertEquals(0, map.size()); }

@Test
public void testDefault1()
{ assertEquals(1234, map.get(TEST_ATTR1).intValue()); }

@Test
public void testDefault2()
{ assertEquals(true, map.get(TEST_ATTR2).booleanValue()); }

@Test
public void testDefault3()
{ assertEquals(4321L, map.get(TEST_ATTR3).longValue()); }

@Test
public void testDefault4()
{
	String o = new String();
	Attribute<String> a = Attribute.of(String.class, "test-4", o);
	assertSame(o, map.get(a));
}

@Test
public void testSet1()
{
	map.set(TEST_ATTR1, Integer.valueOf(5));
	assertEquals(5, map.get(TEST_ATTR1).intValue());
}

@Test
public void testSet2()
{
	map.set(TEST_ATTR2, Boolean.FALSE);
	assertEquals(false, map.get(TEST_ATTR2).booleanValue());
}

@Test
public void testSet3()
{
	map.set(TEST_ATTR3, Long.valueOf(726L));
	assertEquals(726L, map.get(TEST_ATTR3).longValue());
}

@Test
public void testSet4()
{
	String o = new String();
	Attribute<String> a = Attribute.of(String.class, "test-4", new String());
	map.set(a, o);
	assertSame(o, map.get(a));
}

@Test(expected=ClassCastException.class)
public void testSet5()
{
	@SuppressWarnings("rawtypes")
  Attribute a = TEST_ATTR3;
	map.set(a, "1234");
}

@Test(expected=ClassCastException.class)
public void testSet6()
{
	@SuppressWarnings("rawtypes")
  Attribute a = TEST_ATTR3;
	map.set(a, Boolean.FALSE);
}

@Test(expected=ClassCastException.class)
public void testSet7()
{
	@SuppressWarnings("rawtypes")
  Attribute a = TEST_ATTR1;
	map.set(a, Long.valueOf(4321));
}

@Test(expected=ClassCastException.class)
public void testSet8()
{
	@SuppressWarnings("rawtypes")
  Attribute a = TEST_ATTR1;
	map.set(a, new Boolean(false));
}

@Test
public void testSet9()
{
	map.set(TEST_ATTR1, Integer.valueOf(5));
	map.set(TEST_ATTR1, Integer.valueOf(13));
	assertEquals(13, map.get(TEST_ATTR1).intValue());
}

@Test
public void testRemove1()
{
	map.set(TEST_ATTR1, Integer.valueOf(5));
	map.remove(TEST_ATTR1);
	assertEquals(1234, map.get(TEST_ATTR1).intValue());
}

@Test
public void testIterator1()
{
	Iterator<?> it = map.iterator();
	assertEquals(false, it.hasNext());
}

@Test
public void testIterator2()
{
	map.set(TEST_ATTR1, Integer.valueOf(5));
	map.set(TEST_ATTR2, Boolean.FALSE);
	Iterator<?> it = map.iterator();
	assertEquals(true, it.hasNext());
	it.next();
	assertEquals(true, it.hasNext());
	it.next();
	assertEquals(false, it.hasNext());
}

}
