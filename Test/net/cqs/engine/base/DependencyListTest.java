package net.cqs.engine.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.cqs.config.BuildingEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.util.EnumIntMap;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class DependencyListTest
{

private DependencyList<ResearchEnum> list;

@Before
public void setUp()
{
	list = DependencyList.of(ResearchEnum.class);
}

@Test
public void testEmpty1()
{
	assertEquals(0, list.get(ResearchEnum.LASER));
}

@Test
public void testSet1()
{
	list.set(ResearchEnum.ENGINE, 8);
	assertEquals(8, list.get(ResearchEnum.ENGINE));
}

@Test
public void testSet2()
{
	list.set(ResearchEnum.ENGINE, 8);
	list.set(ResearchEnum.BOMB, -2);
	assertEquals(8, list.get(ResearchEnum.ENGINE));
	assertEquals(-2, list.get(ResearchEnum.BOMB));
	assertEquals(0, list.get(ResearchEnum.CORVETTE));
}

@Test
public void testSetMax1()
{
	list.set(ResearchEnum.ENGINE, 8);
	list.setMax(ResearchEnum.ENGINE, 4);
	assertEquals(8, list.get(ResearchEnum.ENGINE));
}

@Test
public void testSetMax2()
{
	list.set(ResearchEnum.ENGINE, 8);
	list.setMax(ResearchEnum.ENGINE, 11);
	assertEquals(11, list.get(ResearchEnum.ENGINE));
}


@Test
public void testSize()
{
	assertEquals(0, list.size());
	list.set(ResearchEnum.ENGINE, 8);
	assertEquals(1, list.size());
	list.set(ResearchEnum.ENGINE, 10);
	assertEquals(1, list.size());
}


@Test
public void testMerge1()
{
	list.set(ResearchEnum.ENGINE, 8);
	list.merge(new DependencyList<ResearchEnum>(ResearchEnum.class, 30));
	assertEquals(8, list.get(ResearchEnum.ENGINE));
	assertEquals(0, list.get(ResearchEnum.BOMB));
}

@Test
public void testMerge2()
{
	list.set(ResearchEnum.ENGINE, 8);
	DependencyList<ResearchEnum> other = DependencyList.of(ResearchEnum.class, 30);
	other.set(ResearchEnum.ENGINE, 14);
	other.set(ResearchEnum.LASER, 5);
	list.merge(other);
	assertEquals(14, list.get(ResearchEnum.ENGINE));
	assertEquals(5, list.get(ResearchEnum.LASER));
}

@Test
public void testMerge3()
{
	list.set(ResearchEnum.ENGINE, 8);
	DependencyList<ResearchEnum> other = DependencyList.of(ResearchEnum.class, 30);
	other.set(ResearchEnum.ENGINE, -4);
	other.set(ResearchEnum.LASER, 5);
	list.merge(other);
	assertEquals(8, list.get(ResearchEnum.ENGINE));
	assertEquals(5, list.get(ResearchEnum.LASER));
}

@Test(expected=IllegalArgumentException.class)
public void testMerge4()
{
	@SuppressWarnings("rawtypes")
  DependencyList other = DependencyList.of(BuildingEnum.class);
	list.merge(other);
}

@Test
public void testAdd1()
{
	list.set(ResearchEnum.ENGINE, 8);
	DependencyList<ResearchEnum> other = DependencyList.of(ResearchEnum.class, 30);
	other.set(ResearchEnum.ENGINE, -4);
	other.set(ResearchEnum.LASER, 5);
	list.add(other);
	assertEquals(4, list.get(ResearchEnum.ENGINE));
	assertEquals(5, list.get(ResearchEnum.LASER));
}

@Test(expected=IllegalArgumentException.class)
public void testAdd2()
{
	@SuppressWarnings("rawtypes")
  DependencyList other = DependencyList.of(BuildingEnum.class);
	list.add(other);
}

@Test
public void testToString1()
{ assertEquals("[]", list.toString()); }

@Test
public void testToString2()
{
	list.set(ResearchEnum.ENGINE, 5);
	list.set(ResearchEnum.LASER, -8);
	assertEquals("[ENGINE:5, LASER:-8]", list.toString());
}

@Test
public void testCheck1()
{
	EnumIntMap<ResearchEnum> other = EnumIntMap.of(ResearchEnum.class);
	assertTrue(list.check(other));
}

@Test
public void testCheck2()
{
	EnumIntMap<ResearchEnum> other = EnumIntMap.of(ResearchEnum.class);
	other.set(ResearchEnum.ENGINE, 5);
	assertTrue(list.check(other));
}

@Test
public void testCheck3()
{
	EnumIntMap<ResearchEnum> other = EnumIntMap.of(ResearchEnum.class);
	other.set(ResearchEnum.LASER, 5);
	list.set(ResearchEnum.LASER, 5);
	assertTrue(list.check(other));
}

@Test
public void testCheck4()
{
	EnumIntMap<ResearchEnum> other = EnumIntMap.of(ResearchEnum.class);
	other.set(ResearchEnum.LASER, 5);
	list.set(ResearchEnum.LASER, 6);
	assertFalse(list.check(other));
}

@Test
public void testPeek1()
{
	list.set(ResearchEnum.ENGINE, 1);
	assertEquals(ResearchEnum.ENGINE, list.peek(0));
}

@Test
public void testPeek2()
{
	list.set(ResearchEnum.ENGINE, 1);
	assertEquals(1, list.peekAmount(0));
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek3()
{
	list.peek(-1);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPeek4()
{
	list.peekAmount(0);
}

@Test
public void testClear1()
{
	list.set(ResearchEnum.ENGINE, 1);
	list.clear();
	assertEquals(0, list.size());
}

}
