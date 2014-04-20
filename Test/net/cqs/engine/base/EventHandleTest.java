package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class EventHandleTest
{

private EventHandle handle;

@Before
public void setUp()
{
	handle = new EventHandle(1234, 0);
}

@Test
public void testHashCode()
{
	assertEquals(12340, handle.hashCode());
}

@Test
public void testEquals0()
{
	assertEquals(handle, handle);
}

@Test
public void testEquals1()
{
	assertEquals(handle, new EventHandle(1234, 0));
}

@Test
public void testEquals2()
{
	assertFalse(handle.equals(new EventHandle(1235, 0)));
}

@Test
public void testEquals3()
{
	assertFalse(handle.equals(new EventHandle(1234, 1)));
}

@Test
public void testEquals4()
{
	assertFalse(handle.equals(new Object()));
}

@Test
public void testCompareTo1()
{
	assertEquals(0, handle.compareTo(handle));
}

@Test
public void testCompareTo2()
{
	assertEquals(-1, handle.compareTo(new EventHandle(1235, 0)));
}

@Test
public void testCompareTo3()
{
	assertEquals(-1, handle.compareTo(new EventHandle(1234, 1)));
}

@Test
public void testCompareTo4()
{
	assertEquals(1, handle.compareTo(new EventHandle(1233, 0)));
}

@Test
public void testCompareTo5()
{
	assertEquals(1, handle.compareTo(new EventHandle(1234, -1)));
}

@Test
public void testToString()
{
	assertEquals("Event[time=1234,id=0]", handle.toString());
}

}
