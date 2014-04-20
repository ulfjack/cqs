package net.cqs.engine.rtevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RealTimeEventTest
{

@Test
public void testEquals()
{
	RealTimeEvent e1 = new RealTimeEvent(100, RealTimeEventType.CREATE);
	RealTimeEvent e2 = new RealTimeEvent(100, RealTimeEventType.CREATE);
	assertTrue(e1.equals(e2));
}

@Test
public void testCompareTo()
{
	RealTimeEvent e1 = new RealTimeEvent(100, RealTimeEventType.CREATE);
	RealTimeEvent e2 = new RealTimeEvent(100, RealTimeEventType.CREATE);
	assertEquals(0, e1.compareTo(e2));
}

@Test
public void testHashCode()
{
	RealTimeEvent e1 = new RealTimeEvent(100, RealTimeEventType.CREATE);
	RealTimeEvent e2 = new RealTimeEvent(100, RealTimeEventType.CREATE);
	assertEquals(e1.hashCode(), e2.hashCode());
}

}
