package net.cqs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.cqs.config.BuildingEnum;
import net.cqs.config.ResearchEnum;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("boxing")
public class EnumQueueTest
{

private EnumQueue<BuildingEnum> bqueue;
private EnumQueue<ResearchEnum> rqueue;

@Before
public void setUp()
{
	bqueue = EnumQueue.of(BuildingEnum.class, false);
	rqueue = EnumQueue.of(ResearchEnum.class, true);
}

@Test
public void testEmpty1()
{
	assertEquals(0, bqueue.size());
}

@Test
public void testEmpty2()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	assertEquals(1, bqueue.size());
}

@Test
public void testEmpty3()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 0);
	assertEquals(0, bqueue.size());
}

@Test
public void testEmpty4()
{
	assertFalse(bqueue.positiveOnly());
	assertTrue(rqueue.positiveOnly());
}

@Test
public void testPeek1()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	assertEquals(BuildingEnum.PROCESSING_PLANT, bqueue.peek());
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek2()
{
	bqueue.peek(0);
}

@Test
public void testPeek3()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 2);
	assertEquals(2, bqueue.peekAmount());
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek4()
{
	bqueue.peekAmount(0);
}

@Test
public void testPeek5()
{
	assertNull(bqueue.get());
}

@Test
public void testPeek6()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 13);
	assertEquals(BuildingEnum.PROCESSING_PLANT, bqueue.get());
}

@Test(expected=NullPointerException.class)
public void testAdd()
{
	bqueue.add(null, 1);
}

@Test
public void testPutSingleBuilding()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, -5);
	assertEquals(BuildingEnum.PROCESSING_PLANT, bqueue.remove());
}

@Test
public void testPutSingleResearch()
{
	rqueue.add(ResearchEnum.ENGINE, 5);
	assertEquals(ResearchEnum.ENGINE, rqueue.remove());
}

@Test
public void testRemove()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, -5);		// -5
	bqueue.add(BuildingEnum.LIBRARY, 4);							// -5,  5
	for (int i = 0; i < 4; i++)
		bqueue.remove();
	assertEquals(BuildingEnum.PROCESSING_PLANT, bqueue.remove());
	assertEquals(BuildingEnum.LIBRARY, bqueue.remove());
}

@Test
public void testSum()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, -5); // -5
	bqueue.add(BuildingEnum.LIBRARY, 5);           // -5,  5
	bqueue.modifyInverse(0, -6);                      // -5, -1
	bqueue.modifyInverse(0, 2);                       // -5,  1
	bqueue.modifyInverse(1, 3);                       // -2,  1
	bqueue.add(BuildingEnum.DEUTERIUM_DEPOT, 8);    // -2,  1, 8
	bqueue.deleteInverse(1);                          // -2,  8
	bqueue.modifyInverse(1, 2);                       //  8
	assertEquals(8, bqueue.sum());
}

@Test
public void testSum2()
{
	int rightSum = 0;
	for (int i = 0; i < 100; i++)
	{
		bqueue.add(BuildingEnum.PROCESSING_PLANT, i+1);
		rightSum += i+1;
		assertEquals(rightSum, bqueue.sum());
	}
	for (int i = 0; i < 300; i++)
	{
		bqueue.remove();
		rightSum--;
		assertEquals(rightSum, bqueue.sum());
	}
}

@Test(expected=IllegalArgumentException.class)
public void testPositiveOnly()
{
	rqueue.add(ResearchEnum.ENGINE, -5);
}

@Test
public void testMaxSum1()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, EnumQueue.MAX_SUM+5);
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
}

@Test
public void testMaxSum2()
{
	bqueue.add(BuildingEnum.LIBRARY, 5);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, EnumQueue.MAX_SUM+5);
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, bqueue.peekAmount(1));
}

@Test
public void testMaxSum3()
{
	bqueue.add(BuildingEnum.LIBRARY, -5);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, EnumQueue.MAX_SUM+5);
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, bqueue.peekAmount(1));
}

@Test
public void testMaxSum4()
{
	bqueue.add(BuildingEnum.LIBRARY, -5);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, -EnumQueue.MAX_SUM-5);
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
	assertEquals(-EnumQueue.MAX_SUM+5, bqueue.peekAmount(1));
}

@Test
public void testMaxSum5()
{
	bqueue.add(BuildingEnum.LIBRARY, -5);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, -EnumQueue.MAX_SUM-5);
	assertFalse(bqueue.add(BuildingEnum.MILITARY_BASE, 1));
}

@Test
public void testMaxSum6()
{
	bqueue.add(BuildingEnum.LIBRARY, EnumQueue.MAX_SUM);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, Integer.MAX_VALUE);
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
	assertEquals(1, bqueue.size());
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testDelete1()
{
	bqueue.delete(0);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testDelete2()
{
	bqueue.delete(-1);
}

@Test
public void testAddMany1()
{
	int[] data = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	int sum = 0;
	for (int i = 0; i < data.length; i++)
	{
		sum += data[i];
		bqueue.add(BuildingEnum.LIBRARY, data[i]);
		assertEquals(sum, bqueue.sum());
	}
	for (int i = 0; i < sum; i++)
		assertNotNull(bqueue.remove());
	assertNull(bqueue.remove());
	assertEquals(0, bqueue.size());
}

@Test
public void testAddMany2()
{
	int[] data = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	int sum = 0;
	for (int i = 0; i < data.length; i++)
	{
		sum += data[i];
		bqueue.add(BuildingEnum.LIBRARY, data[i]);
		assertEquals(sum, bqueue.sum());
	}
	assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 2; assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 3; assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 4; assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 5; assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 6; assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 7; assertEquals(sum, bqueue.sum());
	bqueue.delete(1); sum -= 8; assertEquals(sum, bqueue.sum());
	assertEquals(sum, bqueue.sum());
}

@Test
public void testClone1()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	EnumQueue<BuildingEnum> result = new EnumQueue<BuildingEnum>(bqueue);
	assertEquals(1, result.peekAmount(0));
}

@Test
public void testClone3()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	EnumQueue<BuildingEnum> result = new EnumQueue<BuildingEnum>(bqueue);
	bqueue.deleteInverse(0);
	assertEquals(1, result.peekAmount(0));
}

@Test
public void testToString1()
{
	assertEquals("[]", bqueue.toString());
}

@Test
public void testToString2()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 4);
	bqueue.add(BuildingEnum.LIBRARY, 3);
	assertEquals("[PROCESSING_PLANT:4, LIBRARY:3]", bqueue.toString());
}

@Test
public void testClear1()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	bqueue.clear();
	assertEquals(0, bqueue.size());
	assertEquals(0, bqueue.sum());
}

@Test
public void testDeleteInverse1()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 3);
	assertEquals(false, bqueue.deleteInverse(-1));
}

@Test
public void testDeleteInverse2()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 3);
	assertEquals(false, bqueue.deleteInverse(1));
}

@Test
public void testModifyInverse1()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, EnumQueue.MAX_SUM-5);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 5);
	assertEquals(2, bqueue.size());
	bqueue.modifyInverse(0, Integer.MAX_VALUE);
	assertEquals(2, bqueue.size());
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, bqueue.peekAmount(0));
	assertEquals(5, bqueue.peekAmount(1));
}

@Test
public void testModifyInverse2()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, EnumQueue.MAX_SUM-5);
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 5);
	bqueue.modifyInverse(0, Integer.MIN_VALUE);
	assertEquals(EnumQueue.MAX_SUM, bqueue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, bqueue.peekAmount(0));
	assertEquals(-5, bqueue.peekAmount(1));
}

@Test
public void testModifyInverse3()
{
	rqueue.add(ResearchEnum.ENGINE, EnumQueue.MAX_SUM-5);
	rqueue.add(ResearchEnum.ENGINE, 5);
	rqueue.modifyInverse(0, Integer.MIN_VALUE);
	assertEquals(EnumQueue.MAX_SUM-5, rqueue.sum());
	assertEquals(EnumQueue.MAX_SUM-5, rqueue.peekAmount(0));
	assertEquals(1, rqueue.size());
}

@Test
public void testModifyInverse4()
{
	assertFalse(bqueue.modifyInverse(-1, 10));
}

@Test
public void testModifyInverse5()
{
	assertFalse(bqueue.modifyInverse(0, 10));
}

@Test
public void testMoveUpInverse()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	bqueue.add(BuildingEnum.LIBRARY, 1);
	bqueue.moveUpInverse(-1, 3);
	bqueue.moveUpInverse(5000, 2);
	bqueue.moveUpInverse(0, 5);
	bqueue.moveUpInverse(0, -5);
	bqueue.moveUpInverse(0, 1);
	assertEquals(BuildingEnum.LIBRARY, bqueue.peek(0));
	assertEquals(BuildingEnum.PROCESSING_PLANT, bqueue.peek(1));
}

@Test
public void testMoveTopInverse()
{
	bqueue.add(BuildingEnum.PROCESSING_PLANT, 1);
	bqueue.add(BuildingEnum.LIBRARY, 1);
	bqueue.add(BuildingEnum.DEUTERIUM_DEPOT, 1);
	assertFalse(bqueue.moveTopInverse(-1));
	assertFalse(bqueue.moveTopInverse(3));
	assertTrue(bqueue.moveTopInverse(0));
	assertEquals(BuildingEnum.DEUTERIUM_DEPOT, bqueue.peek(0));
	assertEquals(BuildingEnum.PROCESSING_PLANT, bqueue.peek(1));
	assertEquals(BuildingEnum.LIBRARY, bqueue.peek(2));
}

@Test
public void testIteratorEmpty()
{
	assertFalse(bqueue.iterator().hasNext());
}

@Test
public void testIteratorNotEmpty()
{
	bqueue.add(BuildingEnum.LIBRARY, 1);
	assertTrue(bqueue.iterator().hasNext());
}

}
