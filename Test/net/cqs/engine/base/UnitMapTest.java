package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.cqs.config.units.TestUnitImpl;
import net.cqs.config.units.TestUnitSystemImpl;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSpecial;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class UnitMapTest
{

private UnitMap map;

@Before
public void setUp()
{
	map = new UnitMap();
}

@Test
public void testEmpty()
{
	assertEquals(0, map.size());
}

@Test
public void testEmpty2()
{ assertFalse(map.iterator().hasNext()); }

@Test
public void testEmpty3()
{ assertFalse(map.unitIterator().hasNext()); }

@Test
public void testSet1()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	assertEquals(4, map.sum());
}

@Test(expected=IllegalArgumentException.class)
public void testSet2()
{
	map.set(TestUnitImpl.INFANTRY, -4);
}

@Test
public void testSet3()
{
	map.set(TestUnitImpl.INFANTRY, 7);
	assertEquals(7, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testSet4()
{
	map.set(TestUnitImpl.FIGHTER, 7);
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.INFANTRY, 17);
	assertEquals(24, map.sum());
	assertEquals(17, map.get(TestUnitImpl.INFANTRY));
}

@Test(expected=IllegalArgumentException.class)
public void testSet5()
{
	map.set(TestUnitImpl.INFANTRY, 1);
	map.set(TestUnitImpl.INFANTRY, -4);
}

@Test
public void testSet6()
{
	map.set(TestUnitImpl.FIGHTER, 4);
	assertEquals(1, map.size());
	map.set(TestUnitImpl.FIGHTER, 0);
	assertEquals(0, map.size());
}

@Test
public void testSet7()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	assertEquals(1, map.size());
	map.set(TestUnitImpl.INFANTRY, 0);
	assertEquals(0, map.size());
}

@Test
public void testSet8()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	assertEquals(1, map.size());
	map.set(TestUnitImpl.INFANTRY, 1);
	assertEquals(1, map.size());
	assertEquals(1, map.sum());
}

@Test
public void testSet9()
{
	Unit[] units = new Unit[] { TestUnitImpl.U1, TestUnitImpl.U2, 	TestUnitImpl.U3, 
			TestUnitImpl.U4, TestUnitImpl.U5, TestUnitImpl.U6, TestUnitImpl.U7,
			TestUnitImpl.U8, TestUnitImpl.U9, TestUnitImpl.U10, TestUnitImpl.U11,
			TestUnitImpl.U12, TestUnitImpl.U13, TestUnitImpl.U14, TestUnitImpl.U15,
			TestUnitImpl.U16, TestUnitImpl.U17, TestUnitImpl.U18, TestUnitImpl.U19,
			TestUnitImpl.U20};
	int sum = 0;
	for (int i = 0; i < 20; i++)
	{
		sum += i+1;
		map.set(units[i], i+1);
	}
	assertEquals(sum, map.sum());
	assertEquals(20, map.size());
}

@Test
public void testSet10()
{
	map.set(TestUnitImpl.INFANTRY, 5);
	map.set(TestUnitImpl.FIGHTER, 6);
	map.set(TestUnitImpl.CARRIER, 8);
	map.set(TestUnitImpl.FIGHTER, 0);
	assertEquals(2, map.size());
	assertEquals(13, map.sum());
}

@Test
public void testSet11()
{
	Unit[] units = new Unit[] { TestUnitImpl.U1, TestUnitImpl.U2, 	TestUnitImpl.U3, 
			TestUnitImpl.U4, TestUnitImpl.U5, TestUnitImpl.U6, TestUnitImpl.U7,
			TestUnitImpl.U8, TestUnitImpl.U9, TestUnitImpl.U10, TestUnitImpl.U11,
			TestUnitImpl.U12, TestUnitImpl.U13, TestUnitImpl.U14, TestUnitImpl.U15,
			TestUnitImpl.U16, TestUnitImpl.U17, TestUnitImpl.U18, TestUnitImpl.U19,
			TestUnitImpl.U20, TestUnitImpl.U21, TestUnitImpl.U22, TestUnitImpl.U23,
			TestUnitImpl.U24, TestUnitImpl.U25, TestUnitImpl.U26, TestUnitImpl.U27,
			TestUnitImpl.U28};
	int[] data = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
	int sum = 0;
	for (int i = 0; i < data.length; i++)
	{
		sum += data[i];
		map.set(units[i], data[i]);
		assertEquals(sum, map.sum());
	}
	
	for (int i = 0; i < 20; i++)
	{
		map.delete(1);
		sum -= data[i+1];
		assertEquals(sum, map.sum());
	}
	assertEquals(data.length-20, map.size());
}

@Test
public void testGet1()
{
	assertEquals(0, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testSum()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 5);
	
	assertEquals(9, map.sum());
	assertEquals(4, map.planetarySum());
	assertEquals(5, map.interplanetarySum());
}

@Test
public void testContains1()
{
	assertFalse(map.contains(TestUnitImpl.FIGHTER));
}

@Test
public void testContains2()
{
	map.set(TestUnitImpl.INFANTRY, 23);
	assertTrue(map.contains(TestUnitImpl.INFANTRY));
	assertFalse(map.contains(TestUnitImpl.FIGHTER));
}

@Test
public void testContains3()
{
	map.set(TestUnitImpl.INFANTRY, 34);
	assertTrue(map.contains(false));
	assertFalse(map.contains(true));
}

@Test
public void testContains4()
{
	map.set(TestUnitImpl.FIGHTER, 34);
	assertFalse(map.contains(false));
	assertTrue(map.contains(true));
}

@Test
public void testContains5()
{
	map.set(TestUnitImpl.INFANTRY, 34);
	assertTrue(map.contains(UnitSelector.GROUND_ONLY));
	assertFalse(map.contains(UnitSelector.SPACE_ONLY));
}

@Test
public void testContains6()
{
	map.set(TestUnitImpl.FIGHTER, 34);
	assertFalse(map.contains(UnitSelector.GROUND_ONLY));
	assertTrue(map.contains(UnitSelector.SPACE_ONLY));
}

@Test
public void testContains7()
{
	assertFalse(map.contains(UnitSelector.GROUND_ONLY));
	assertFalse(map.contains(UnitSelector.SPACE_ONLY));
}

@Test
public void testIncrease1()
{
	map.increase(TestUnitImpl.INFANTRY);
	assertEquals(1, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testIncrease2()
{
	map.set(TestUnitImpl.INFANTRY, 3);
	map.increase(TestUnitImpl.INFANTRY, 9);
	assertEquals(12, map.get(TestUnitImpl.INFANTRY));
}

@Test(expected=IllegalArgumentException.class)
public void testDecrease1()
{
	map.decrease(TestUnitImpl.INFANTRY);
}

@Test
public void testDecrease2()
{
	map.set(TestUnitImpl.INFANTRY, 34);
	map.decrease(TestUnitImpl.INFANTRY);
	assertEquals(33, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testDecrease3()
{
	map.set(TestUnitImpl.INFANTRY, 34);
	map.decrease(TestUnitImpl.INFANTRY, 9);
	assertEquals(25, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testClear1()
{
	map.set(TestUnitImpl.INFANTRY, 13);
	map.clear();
	assertEquals(0, map.sum());
	assertEquals(0, map.planetarySum());
	assertEquals(0, map.interplanetarySum());
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testDelete1()
{
	map.delete(0);
}

@Test
public void testClone1()
{
	UnitMap result = new UnitMap(map);
	assertEquals(0, result.sum());
}

@Test
public void testClone2()
{
	map.set(TestUnitImpl.INFANTRY, 5);
	UnitMap result = new UnitMap(map);
	assertEquals(5, result.get(TestUnitImpl.INFANTRY));
}

@Test
public void testClone3()
{
	UnitMap result = new UnitMap(map);
	map.set(TestUnitImpl.INFANTRY, 2);
	assertEquals(0, result.get(TestUnitImpl.INFANTRY));
}

@Test
public void testPeek1()
{
	map.set(TestUnitImpl.INFANTRY, 1);
	assertEquals(TestUnitImpl.INFANTRY, map.peek(0));
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek2()
{
	map.peek(0);
}

@Test
public void testPeek3()
{
	map.set(TestUnitImpl.INFANTRY, 23);
	assertEquals(23, map.peekAmount(0));
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek4()
{
	map.peekAmount(0);
}

@Test
public void testSerialize1()
{
	map.set(TestUnitImpl.INFANTRY, 56);
	map.set(TestUnitImpl.FIGHTER, 67);
	String s = map.serialize();
	UnitMap result = new UnitMap(new TestUnitSystemImpl(), s);
	assertEquals(56, result.get(TestUnitImpl.INFANTRY));
	assertEquals(67, result.get(TestUnitImpl.FIGHTER));
}

@Test
public void testSerialize2()
{
	map.set(TestUnitImpl.INFANTRY, 56);
	map.set(TestUnitImpl.FIGHTER, 67);
	assertEquals("2|INFANTRY|56|FIGHTER|67", map.serialize());
}

@Test
public void testSerialize3()
{
	String s = "2|INFANTRY|56|FIGHTER|67";
	UnitMap result = new UnitMap(new TestUnitSystemImpl(), s);
	assertEquals(56, result.get(TestUnitImpl.INFANTRY));
	assertEquals(67, result.get(TestUnitImpl.FIGHTER));
	assertEquals(56+67, result.sum());
}

@Test(expected=IllegalArgumentException.class)
public void testSerialize4()
{
	String s = "2|INFANTRY|56|x|67";
	new UnitMap(new TestUnitSystemImpl(), s);
}

@Test(expected=IllegalArgumentException.class)
public void testSerialize5()
{
	String s = "";
	new UnitMap(new TestUnitSystemImpl(), s);
}

@Test(expected=NullPointerException.class)
public void testSerialize6()
{
	String s = null;
	new UnitMap(new TestUnitSystemImpl(), s);
}

@Test(expected=IllegalArgumentException.class)
public void testSerialize7()
{
	String s = "3000|";
	new UnitMap(new TestUnitSystemImpl(), s);
}

@Test(expected=IllegalArgumentException.class)
public void testSerialize8()
{
	String s = "1|INFANTRY|5|4321";
	new UnitMap(new TestUnitSystemImpl(), s);
}

@Test
public void testSerialize9()
{
	String s = "1|FIGHTER|5";
	map.deserialize(new TestUnitSystemImpl(), s);
	assertEquals(5, map.interplanetarySum());
	assertEquals(5, map.sum());
}

@Test
public void testFindSpecialUnit()
{
	Unit type = TestUnitImpl.COLONIZATION;
	map.set(type, 5);
	assertEquals(type, map.findSpecialUnit(UnitSpecial.SETTLEMENT, false));
	assertNull(map.findSpecialUnit(UnitSpecial.SETTLEMENT, true));
}

@Test
public void testAdd1()
{
	map.set(TestUnitImpl.INFANTRY, 8);
	UnitMap other = new UnitMap();
	other.set(TestUnitImpl.INFANTRY, 4);
	other.set(TestUnitImpl.FIGHTER, 5);
	map.add(other);
	assertEquals(12, map.get(TestUnitImpl.INFANTRY));
	assertEquals(5, map.get(TestUnitImpl.FIGHTER));
}

@Test
public void testSubtract1()
{
	map.set(TestUnitImpl.INFANTRY, 8);
	map.set(TestUnitImpl.FIGHTER, 10);
	UnitMap other = new UnitMap();
	other.set(TestUnitImpl.INFANTRY, 4);
	other.set(TestUnitImpl.FIGHTER, 5);
	map.subtract(other);
	assertEquals(4, map.get(TestUnitImpl.INFANTRY));
	assertEquals(5, map.get(TestUnitImpl.FIGHTER));
}

@Test
public void testImportTypes()
{
	map.set(TestUnitImpl.INFANTRY, 8);
	UnitMap other = new UnitMap();
	other.set(TestUnitImpl.INFANTRY, 4);
	other.set(TestUnitImpl.FIGHTER, 5);
	map.importTypes(other);
	assertEquals(8, map.sum());
	assertEquals(8, map.get(TestUnitImpl.INFANTRY));
	assertEquals(0, map.get(TestUnitImpl.FIGHTER));
	assertEquals(2, map.size());
}

@Test
public void testCalculateGroupCount()
{
	map.set(TestUnitImpl.INFANTRY, 2);
	map.set(TestUnitImpl.CARRIER, 3);
	map.set(TestUnitImpl.U2, 5);
	map.set(TestUnitImpl.U3, 4);
	map.set(TestUnitImpl.U4, 7);
	
	int[] count = map.calculateGroupCount(UnitSelector.ALL);
	assertEquals(7, count.length);
	assertEquals(2, count[0]);
	assertEquals(3, count[1]);
	assertEquals(9, count[2]);
	assertEquals(7, count[3]);
	assertEquals(0, count[4]);
	assertEquals(0, count[5]);
}

@Test
public void testCalculateGroupProportion()
{
	map.set(TestUnitImpl.INFANTRY, 2);
	map.set(TestUnitImpl.CARRIER, 3);
	map.set(TestUnitImpl.U2, 5);
	map.set(TestUnitImpl.U3, 4);
	map.set(TestUnitImpl.U4, 7);
	
	float sum = 2*TestUnitImpl.INFANTRY.getSize() + 3*TestUnitImpl.CARRIER.getSize()
		+ 9*TestUnitImpl.U2.getSize() + 7*TestUnitImpl.U4.getSize();
	float[] count = map.calculateGroupProportion(UnitSelector.ALL);
	
	assertEquals(7, count.length);
	assertEquals((2f*TestUnitImpl.INFANTRY.getSize())/sum, count[0], 0.001f);
	assertEquals((3f*TestUnitImpl.CARRIER.getSize())/sum, count[1], 0.001f);
	assertEquals((9f*TestUnitImpl.U2.getSize())/sum, count[2], 0.001f);
	assertEquals((7f*TestUnitImpl.U4.getSize())/sum, count[3], 0.001f);
	assertEquals(0f, count[4], 0f);
	assertEquals(0f, count[5], 0f);
}

@Test
public void testGetGroundUnitCapacity()
{
	Unit type = TestUnitImpl.FIGHTER;
	map.set(type, 17);
	map.set(TestUnitImpl.INFANTRY, 16);
	long expect = 17*TestUnitImpl.FIGHTER.getGroundUnitCapacity();
	long value = map.getGroundUnitCapacity();
	assertEquals(expect, value);
}

@Test
public void testGetGroundUnitSize()
{
	map.set(TestUnitImpl.INFANTRY, 17);
	map.set(TestUnitImpl.COLONIZATION, 13);
	long expect = 30;
	long value = map.getGroundUnitSize();
	assertEquals(expect, value);
}

@Test
public void testGetSpaceUnitSize()
{
	map.set(TestUnitImpl.CARRIER, 17);
	map.set(TestUnitImpl.FIGHTER, 13);
	long expect = 13*TestUnitImpl.FIGHTER.getSize();
	long value = map.getSpaceUnitSize();
	assertEquals(expect, value);
}

@Test
public void testGetSpaceUnitCapacity()
{
	map.set(TestUnitImpl.CARRIER, 17);
	long expect = 17*TestUnitImpl.CARRIER.getSpaceUnitCapacity();
	long value = map.getSpaceUnitCapacity();
	assertEquals(expect, value);
}

@Test
public void testGetPower()
{
	map.set(TestUnitImpl.CARRIER, 17);
	map.set(TestUnitImpl.FIGHTER, 13);
	float expect = 17*TestUnitImpl.CARRIER.getPower()+13*TestUnitImpl.FIGHTER.getPower();
	float value = map.getPower(UnitSelector.ALL);
	assertEquals(expect, value, 0.001f);
}

@Test
public void testGetResourceSpace()
{
	Unit type1 = TestUnitImpl.CARRIER;
	Unit type2 = TestUnitImpl.INFANTRY;
	map.set(type1, 17);
	map.set(type2, 13);
	long expect = 17*TestUnitImpl.CARRIER.getResourceCapacity() + 13*TestUnitImpl.INFANTRY.getResourceCapacity();
	long value = map.getResourceSpace();
	assertEquals(expect, value);
}

@Test
public void testGetResourceSpace2()
{
	map.set(TestUnitImpl.INFANTRY, Integer.MAX_VALUE/10);
	assertEquals(1000L*(Integer.MAX_VALUE/10), map.getResourceSpace());
}

@Test
public void testGetHighscoreValue()
{
	Unit type1 = TestUnitImpl.CARRIER;
	Unit type2 = TestUnitImpl.INFANTRY;
	map.set(type1, 17);
	map.set(type2, 13);
	long expect = 17*TestUnitImpl.CARRIER.getScore() + 13*TestUnitImpl.INFANTRY.getScore();
	long value = map.getScore();
	assertEquals(expect, value);
}

@Test
public void testToString1()
{
	assertEquals("[]", map.toString());
}

@Test
public void testToString2()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 13);
	assertEquals("[INFANTRY:4,FIGHTER:13]", map.toString());
}

@Test
public void testIterator1()
{
	UnitIterator it = map.unitIterator();
	assertFalse(it.hasNext());
}

@Test
public void testIterator2()
{
	map.set(TestUnitImpl.INFANTRY, 8);
	UnitIterator it = map.unitIterator();
	assertTrue(it.hasNext());
	it.next();
	assertEquals(TestUnitImpl.INFANTRY, it.key());
	assertEquals(8, it.value());
}

@Test
public void testIterator3()
{
	map.set(TestUnitImpl.INFANTRY, 8);
	UnitIterator it = map.unitIterator();
	assertTrue(it.hasNext());
	it.next();
	it.setValue(9);
	assertEquals(9, map.get(TestUnitImpl.INFANTRY));
}

@Test(expected=NoSuchElementException.class)
public void testIterator4()
{
	UnitIterator it = map.unitIterator();
	it.next();
}

@Test(expected=IllegalStateException.class)
public void testIterator5()
{
	UnitIterator it = map.unitIterator();
	it.remove();
}

@Test(expected=IllegalStateException.class)
public void testIterator6()
{
	UnitIterator it = map.unitIterator();
	it.setValue(3);
}

@Test(expected=IllegalStateException.class)
public void testIterator7()
{
	UnitIterator it = map.unitIterator();
	it.increase(3);
}

@Test(expected=IllegalStateException.class)
public void testIterator8a()
{
	UnitIterator it = map.unitIterator();
	it.decrease(3);
}

@Test(expected=IllegalStateException.class)
public void testIterator8b()
{
	UnitIterator it = map.unitIterator();
	it.key();
}

@Test(expected=IllegalStateException.class)
public void testIterator8d()
{
	UnitIterator it = map.unitIterator();
	it.value();
}

@Test
public void testIterator9()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 3);
	UnitIterator it = map.unitIterator(UnitSelector.GROUND_ONLY);
	assertTrue(it.hasNext());
	it.next();
	assertEquals(4, it.value());
	assertFalse(it.hasNext());
}

@Test
public void testIterator10()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 3);
	UnitIterator it = map.unitIterator();
	it.next();
	it.decrease(3);
	assertEquals(1, it.value());
	assertEquals(1, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testIterator11()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 3);
	UnitIterator it = map.unitIterator();
	it.next();
	it.decrease(4);
	assertEquals(1, map.size());
	assertTrue(it.hasNext());
}

@Test
public void testIterator12()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.decrease(4);
	assertEquals(0, map.size());
	assertFalse(it.hasNext());
}

@Test(expected=NoSuchElementException.class)
public void testIterator13()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.decrease(4);
	it.next();
}

@Test
public void testIterator14()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 3);
	UnitIterator it = map.unitIterator();
	it.next();
	it.increase(3);
	assertEquals(7, it.value());
	assertEquals(7, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testIterator15()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 3);
	UnitIterator it = map.unitIterator();
	it.next();
	it.increase(-4);
	assertEquals(1, map.size());
	assertTrue(it.hasNext());
}

@Test
public void testIterator16()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.increase(-4);
	assertEquals(0, map.size());
	assertFalse(it.hasNext());
}

@Test(expected=NoSuchElementException.class)
public void testIterator17()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.increase(-4);
	it.next();
}

@Test
public void testIterator18()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.FIGHTER, 3);
	UnitIterator it = map.unitIterator();
	it.next();
	it.remove();
	assertEquals(1, map.size());
	assertEquals(0, map.get(TestUnitImpl.INFANTRY));
}

@Test
public void testIterator19()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.remove();
	assertEquals(0, map.size());
	assertFalse(it.hasNext());
}

@Test(expected=NoSuchElementException.class)
public void testIterator20()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.remove();
	it.next();
}

@Test
public void testIterator21()
{
	map.set(TestUnitImpl.INFANTRY, 4);
	map.set(TestUnitImpl.CARRIER, 4);
	UnitIterator it = map.unitIterator();
	it.next();
	it.setValue(0);
	it.next();
	it.setValue(0);
}

@Test
public void testIterator22()
{
	Iterator<UnitEntry> it = map.iterator();
	assertFalse(it.hasNext());
}

@Test(expected=NoSuchElementException.class)
public void testIterator23()
{
	Iterator<UnitEntry> it = map.iterator();
	it.next();
}

@Test(expected=IllegalArgumentException.class)
public void testOverflowInIncrease()
{
	map.set(TestUnitImpl.INFANTRY, 10000);
	map.increase(TestUnitImpl.INFANTRY, Integer.MAX_VALUE);
}

@Test @Ignore // This is currently broken and should be fixed.
public void testOverflowInGetGroupCount()
{
	map.set(TestUnitImpl.INFANTRY, Integer.MAX_VALUE);
	map.set(TestUnitImpl.FIGHTER, 10);
	int[] groups = map.calculateGroupCount(UnitSelector.ALL);
	assertTrue("groups[0] >= 0", groups[0] >= 0);
	assertTrue("groups[1] >= 0", groups[1] >= 0);
}

}
