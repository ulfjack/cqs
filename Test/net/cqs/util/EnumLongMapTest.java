package net.cqs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.cqs.config.ResearchEnum;
import net.cqs.config.ResourceEnum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class EnumLongMapTest
{

private EnumLongMap<ResourceEnum> map;

@Before
public void setUp()
{
	map = EnumLongMap.of(ResourceEnum.class);
}

@Test
public void testEmpty()
{
	assertEquals(0, map.getSum());
}

@Test(expected=NullPointerException.class)
public void testEmpty2()
{
	map.get(null);
}

@Test(expected=ClassCastException.class)
public void testEmpty3()
{
	@SuppressWarnings("rawtypes")
  EnumLongMap map2 = map;
	map2.set(ResearchEnum.ENGINE, 2);
}

@Test
public void testSet1()
{
	map.set(ResourceEnum.STEEL, 1);
	assertEquals(1, map.getSum());
}

@Test
public void testSet2()
{
	map.set(ResourceEnum.OIL, 1);
	assertEquals(1, map.get(ResourceEnum.OIL));
}

@Test
public void testSet3()
{
	map.set(ResourceEnum.OIL, 1);
	map.set(ResourceEnum.STEEL, 2);
	assertEquals(1, map.get(ResourceEnum.OIL));
	assertEquals(2, map.get(ResourceEnum.STEEL));
}

@Test
public void testSet4()
{
	map.set(ResourceEnum.OIL, 1);
	map.set(ResourceEnum.OIL, 2);
	assertEquals(2, map.get(ResourceEnum.OIL));
}

@Test
public void testSet5()
{
	map.set(ResourceEnum.OIL, 1);
	map.set(ResourceEnum.OIL, 2);
	assertEquals(2, map.getSum());
}

@Test(expected=IllegalArgumentException.class)
public void testSet6()
{
	map.set(ResourceEnum.OIL, -1);
}

@Test(expected=NullPointerException.class)
public void testSet7()
{
	map.set(null, 1);
}

@Test
public void testSet8()
{
	map.set(ResourceEnum.OIL, 2);
	boolean thrown = false;
	try
	{
		map.set(ResourceEnum.OIL, -1);
	}
	catch (IllegalArgumentException e)
	{ thrown = true; }
	if (!thrown) Assert.fail();
	assertEquals(2, map.getSum());
}

@Test
public void testSetIncrease1()
{
	map.increase(ResourceEnum.OIL);
	assertEquals(1, map.get(ResourceEnum.OIL));
}

@Test
public void testSetIncrease2()
{
	map.set(ResourceEnum.OIL, 11);
	map.increase(ResourceEnum.OIL, 13);
	assertEquals(24, map.get(ResourceEnum.OIL));
}

@Test(expected=IllegalArgumentException.class)
public void testSetDecrease1()
{
	map.decrease(ResourceEnum.OIL);
}

@Test
public void testSetDecrease2()
{
	map.set(ResourceEnum.OIL, 33);
	map.decrease(ResourceEnum.OIL);
	assertEquals(32, map.get(ResourceEnum.OIL));
}

@Test
public void testSetDecrease3()
{
	map.set(ResourceEnum.OIL, 33);
	map.decrease(ResourceEnum.OIL, 11);
	assertEquals(22, map.get(ResourceEnum.OIL));
}

@Test
public void testSetClear1()
{
	map.set(ResourceEnum.OIL, 33);
	assertEquals(33, map.get(ResourceEnum.OIL));
	map.clear();
	assertEquals(0, map.getSum());
	assertEquals(0, map.get(ResourceEnum.OIL));
}

@Test
public void testAddMap()
{
	map.set(ResourceEnum.OIL, 33);
	EnumLongMap<ResourceEnum> map2 = EnumLongMap.of(ResourceEnum.class);
	map2.set(ResourceEnum.DEUTERIUM, 100);
	assertEquals(33, map.getSum());
	assertEquals(100, map2.getSum());
	map.add(map2);
	assertEquals(133, map.getSum());
	assertEquals(33, map.get(ResourceEnum.OIL));
	assertEquals(100, map.get(ResourceEnum.DEUTERIUM));
}

@Test
public void testSubtractMap()
{
	map.set(ResourceEnum.OIL, 33);
	EnumLongMap<ResourceEnum> map2 = EnumLongMap.of(ResourceEnum.class);
	map2.set(ResourceEnum.OIL, 20);
	assertEquals(33, map.getSum());
	assertEquals(20, map2.getSum());
	map.subtract(map2);
	assertEquals(13, map.getSum());
	assertEquals(13, map.get(ResourceEnum.OIL));
}

private EnumLongMap<ResourceEnum> serialize() throws IOException, ClassNotFoundException
{
	ByteArrayOutputStream baout = new ByteArrayOutputStream();
	ObjectOutputStream oout = new ObjectOutputStream(baout);
	oout.writeObject(map);
	oout.close();
	
	ByteArrayInputStream bain = new ByteArrayInputStream(baout.toByteArray());
	ObjectInputStream oin = new ObjectInputStream(bain);
	return (EnumLongMap<ResourceEnum>) oin.readObject();
}

@Test
public void testSerialization1() throws IOException, ClassNotFoundException
{
	EnumLongMap<ResourceEnum> result = serialize();
	assertEquals(0, result.getSum());
}

@Test
public void testSerialization2() throws IOException, ClassNotFoundException
{
	map.set(ResourceEnum.OIL, 1);
	EnumLongMap<ResourceEnum> result = serialize();
	assertEquals(1, result.getSum());
}

@Test
public void testToString()
{
	map.set(ResourceEnum.OIL, 17);
	String s = map.toString();
	assertNotNull(s);
	assertTrue(s.contains("17"));
	assertTrue(s.contains(ResourceEnum.OIL.name()));
}

}
