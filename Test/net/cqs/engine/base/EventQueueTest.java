package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("boxing")
public class EventQueueTest
{

	private class TestEvent extends Event
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void activate(long time)
		{/*OK*/}
		@Override
		public boolean check(Object o)
		{ return o == EventQueueTest.this; }
	}
	
	private class TestEvent2 extends Event
	{
		private static final long serialVersionUID = 1L;
		@Override
		public void activate(long time)
		{/*OK*/}
	}

private EventQueue queue;

@Before
public void setUp()
{
	queue = new EventQueue();
}

@Test
public void testEmpty1()
{
	assertEquals(null, queue.getEvent(0));
}

@Test
public void testEmpty2()
{
	assertEquals(0, queue.size());
}

@Test
public void testAdd1()
{
	TestEvent event = new TestEvent();
	queue.addEvent(1, event);
	assertEquals(null, queue.getEvent(0));
	assertSame(event, queue.getEvent(1));
}

@Test
public void testDelete1()
{
	TestEvent event = new TestEvent();
	EventHandle handle = queue.addEvent(1, event);
	assertEquals(null, queue.getEvent(0));
	assertEquals(true, queue.deleteEvent(handle));
}

@Test
public void testDelete2()
{
	TestEvent event = new TestEvent();
	EventHandle handle = queue.addEvent(1, event);
	assertSame(event, queue.getEvent(1));
	assertEquals(false, queue.deleteEvent(handle));
}

@Test
public void testRemove1()
{
	TestEvent event = new TestEvent();
	queue.addEvent(1, event);
	queue.removeEvents(this);
	assertEquals(0, queue.size());
	assertEquals(null, queue.getEvent(2));
}

@Test
public void testRemove2()
{
	TestEvent event1 = new TestEvent();
	TestEvent2 event2 = new TestEvent2();
	queue.addEvent(1, event1);
	queue.addEvent(1, event2);
	queue.removeEvents(this);
	assertEquals(1, queue.size());
	assertEquals(event2, queue.getEvent(2));
}

@Test
public void testAddOverflow()
{
	TestEvent event1 = new TestEvent();
	TestEvent event2 = new TestEvent();
	queue.addEvent(1, event1);
	queue.id = 0;
	queue.addEvent(1, event2);
}

}
