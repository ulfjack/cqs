package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.cqs.config.units.TestUnitImpl;
import net.cqs.config.units.Unit;
import net.cqs.util.EnumQueue;

import org.junit.Before;
import org.junit.Test;

public class UnitQueueTest
{

private UnitQueue queue;

@Before
public void setUp()
{
	queue = new UnitQueue();
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testDelete1()
{
	queue.delete(-1);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testDelete2()
{
	queue.delete(0);
}

@Test
public void testGetNull()
{
	assertEquals(null, queue.get());
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeekAmount()
{
	queue.add(TestUnitImpl.INFANTRY, 5);
	queue.peekAmount(-1);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek()
{
	queue.add(TestUnitImpl.INFANTRY, 5);
	queue.peek(-1);
}

@Test(expected=NullPointerException.class)
public void testAddNull()
{
	queue.add(null, 0);
}

@Test
public void testAddNone()
{
	Unit u = TestUnitImpl.INFANTRY;
	queue.add(u, 0);
	assertEquals(0, queue.size());
	assertEquals(null, queue.remove());
}

@Test
public void testAddSingle()
{
	Unit u = TestUnitImpl.INFANTRY;
	queue.add(u, 5);
	assertEquals(1, queue.size());
	assertSame(u, queue.get());
}

@Test
public void testAddMany()
{
	Unit[] data = TestUnitImpl.values();
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	
	for (int i = 0; i < data.length; i++)
		assertTrue(queue.peek(i) == data[i]);
	
	for (int i = 0; i < data.length; i++)
		assertTrue(queue.peekAmount(i) == i+1);
}

@Test
public void testAddTooMany()
{
	Unit[] data = new Unit[] { TestUnitImpl.U1, TestUnitImpl.U2, TestUnitImpl.U3, TestUnitImpl.U4, TestUnitImpl.U5 };
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], 249000);
	
	for (int i = 0; i < data.length; i++)
		assertTrue(queue.peek(i) == data[i]);
	
	for (int i = 0; i < data.length-1; i++)
		assertTrue(queue.peekAmount(i) == 249000);
	assertTrue(queue.peekAmount(data.length-1) == 1000000-4*249000);
	assertEquals(1000000, queue.sum());
	queue.add(TestUnitImpl.U1, 5);
}

@Test(expected=IllegalArgumentException.class)
public void testAddNonpositive()
{
	queue.add(TestUnitImpl.INFANTRY, -5);
}

@Test
public void testSize()
{
	Unit[] data = TestUnitImpl.values();
	for (int i = 0; i < data.length; i++)
	{
		queue.add(data[i], 1);
		assertTrue(queue.size() == i+1);
	}
	for (int i = 0; i < 5; i++)
	{
		queue.deleteInverse(1);
		assertEquals(data.length-1-i, queue.size());
	}
}

@Test
public void testSum()
{
	Unit[] data = TestUnitImpl.values();
	int rightSum = 0;
	for (int i = 0; i < data.length; i++)
	{
		queue.add(data[i], i+1);
		rightSum += i+1;
		assertTrue(queue.sum() == rightSum);
	}
	for (int i = 0; i < 300; i++)
	{
		queue.remove();
		rightSum--;
		assertTrue(queue.sum() == rightSum);
	}
}

@Test
public void testMaxSum1()
{
	queue.add(TestUnitImpl.INFANTRY, EnumQueue.MAX_SUM+5);
	assertEquals(EnumQueue.MAX_SUM, queue.sum());
}

@Test
public void testMaxSum2()
{
	queue.add(TestUnitImpl.INFANTRY, 5);
	queue.add(TestUnitImpl.FIGHTER, EnumQueue.MAX_SUM+5);
	assertEquals(EnumQueue.MAX_SUM, queue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, queue.peekAmount(1));
}

@Test
public void testMaxSum6()
{
	queue.add(TestUnitImpl.INFANTRY, EnumQueue.MAX_SUM);
	queue.add(TestUnitImpl.FIGHTER, Integer.MAX_VALUE);
	assertEquals(EnumQueue.MAX_SUM, queue.sum());
	assertEquals(1, queue.size());
}

@Test
public void testClear()
{
	Unit[] data = TestUnitImpl.values();
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	assertTrue(queue.size() != 0);
	queue.clear();
	assertEquals(0, queue.size());
}

@Test
public void testDeleteSimple()
{
	Unit u = TestUnitImpl.INFANTRY;
	queue.add(u, 1);
	assertEquals(1, queue.size());
	Unit v = queue.remove();
	assertEquals(0, queue.size());
	assertSame(u, v);
}

@Test
public void testRemoveSimple()
{
	Unit u = TestUnitImpl.INFANTRY;
	queue.add(u, 5);
	assertEquals(1, queue.size());
	Unit v = queue.remove();
	assertEquals(1, queue.size());
	assertSame(u, v);
	assertEquals(4, queue.peekAmount(0));
}

@Test
public void testRemoveMany()
{
	Unit[] data = TestUnitImpl.values();
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	
	for (int j = 1; j < data.length+1; j++)
	{
		for (int i = 0; i < j; i++)
		{
			Unit u1 = queue.get();
			Unit u2 = queue.remove();
			assertSame(u1, u2);
		}
	}
}

@Test
public void testDeleteInverse()
{
	Unit[] data = TestUnitImpl.values();
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	
	int size = queue.size();
	queue.deleteInverse(-1);
	queue.deleteInverse(5000);
	for (int i = 0; i < size-3; i++)
	{
		queue.deleteInverse(2);
		
		assertTrue(queue.peek(queue.size()-1) == data[data.length-1]);
		for (int j = 0; j < queue.size(); j++)
		{
			assertTrue(queue.peek(j) != null);
		}
	}
}

@Test
public void testModifyInverse()
{
	Unit[] data = TestUnitImpl.values();
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	
	queue.modifyInverse(-1, 150);
	queue.modifyInverse(5000, 150);
	queue.modifyInverse(0, 150);
	assertEquals(data.length+150, queue.peekAmount(data.length-1));
	queue.modifyInverse(2, -10);
	assertEquals(data.length-2-10, queue.peekAmount(data.length-3));
	queue.modifyInverse(3, -400);
	assertEquals(data[data.length-1], queue.peek(data.length-2));
	int sum = queue.sum();
	int oldAmount = queue.peekAmount(data.length-2);
	queue.modifyInverse(0, 1000000);
	assertEquals(queue.peekAmount(data.length-2), 1000000-sum+oldAmount);
}

@Test
public void testModifyInverse1()
{
	queue.add(TestUnitImpl.INFANTRY, EnumQueue.MAX_SUM-5);
	queue.add(TestUnitImpl.CARRIER, 5);
	assertEquals(2, queue.size());
	queue.modifyInverse(0, Integer.MAX_VALUE);
	assertEquals(2, queue.size());
	assertEquals(EnumQueue.MAX_SUM, queue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, queue.peekAmount(0));
	assertEquals(5, queue.peekAmount(1));
}

@Test
public void testModifyInverse2()
{
	queue.add(TestUnitImpl.INFANTRY, EnumQueue.MAX_SUM-5);
	queue.add(TestUnitImpl.CARRIER, 5);
	queue.modifyInverse(0, Integer.MIN_VALUE);
	assertEquals(EnumQueue.MAX_SUM-5, queue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, queue.peekAmount(0));
}

@Test
public void testModifyInverse3()
{
	queue.add(TestUnitImpl.INFANTRY, EnumQueue.MAX_SUM-5);
	queue.add(TestUnitImpl.CARRIER, 5);
	queue.modifyInverse(0, Integer.MIN_VALUE);
	assertEquals(EnumQueue.MAX_SUM-5, queue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, queue.peekAmount(0));
	assertEquals(1, queue.size());
}

@Test
public void testMoveUpInverse()
{
	Unit[] data = new Unit[] { TestUnitImpl.INFANTRY, TestUnitImpl.CARRIER };
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	
	queue.moveUpInverse(-1, 3);
	queue.moveUpInverse(5000, 2);
	queue.moveUpInverse(0, 5);
	queue.moveUpInverse(0, -5);
	queue.moveUpInverse(0, 1);
	assertTrue(queue.peek(0) == data[1]);
	assertTrue(queue.peek(1) == data[0]);
}

@Test
public void testMoveTopInverse()
{
	Unit[] data = new Unit[] { TestUnitImpl.U1, TestUnitImpl.U2, TestUnitImpl.U3, TestUnitImpl.U4, TestUnitImpl.U5 };
	for (int i = 0; i < data.length; i++)
		queue.add(data[i], i+1);
	
	queue.moveTopInverse(-1);
	queue.moveTopInverse(5000);
	queue.moveTopInverse(0);
	assertEquals(data[4], queue.peek(0));
	for (int i = 0; i < 4; i++)
		assertEquals(data[i], queue.peek(i+1));
}

@Test
public void testClone()
{
	queue.add(TestUnitImpl.INFANTRY, 5);
	UnitQueue qclone = new UnitQueue(queue);
	assertFalse(qclone.equals(queue));
	assertNotSame(qclone, queue);
}

@Test
public void testToString1()
{
	assertEquals("[]", queue.toString());
}

@Test
public void testToString2()
{
	queue.add(TestUnitImpl.INFANTRY, 4);
	queue.add(TestUnitImpl.CARRIER, 5);
	queue.toString();
}

}
