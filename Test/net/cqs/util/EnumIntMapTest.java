package net.cqs.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import net.cqs.config.BuildingEnum;
import net.cqs.config.ResearchEnum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class EnumIntMapTest
{

private EnumIntMap<BuildingEnum> map;

@Before
public void setUp()
{
	map = EnumIntMap.of(BuildingEnum.class);
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
  EnumIntMap map2 = map;
	map2.set(ResearchEnum.ENGINE, 2);
}

@Test
public void testSet1()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	assertEquals(1, map.getSum());
}

@Test
public void testSet2()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	assertEquals(1, map.get(BuildingEnum.PROCESSING_PLANT));
}

@Test
public void testSet3()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	map.set(BuildingEnum.LIBRARY, 2);
	assertEquals(1, map.get(BuildingEnum.PROCESSING_PLANT));
	assertEquals(2, map.get(BuildingEnum.LIBRARY));
}

@Test
public void testSet4()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	map.set(BuildingEnum.PROCESSING_PLANT, 2);
	assertEquals(2, map.get(BuildingEnum.PROCESSING_PLANT));
}

@Test
public void testSet5()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	map.set(BuildingEnum.PROCESSING_PLANT, 2);
	assertEquals(2, map.getSum());
}

@Test(expected=IllegalArgumentException.class)
public void testSet6()
{
	map.set(BuildingEnum.PROCESSING_PLANT, -1);
}

@Test(expected=NullPointerException.class)
public void testSet7()
{
	map.set(null, 1);
}

@Test
public void testSet8()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 2);
	boolean thrown = false;
	try
	{
		map.set(BuildingEnum.PROCESSING_PLANT, -1);
	}
	catch (IllegalArgumentException e)
	{ thrown = true; }
	if (!thrown) Assert.fail();
	assertEquals(2, map.getSum());
}

@Test
public void testSetIncrease1()
{
	map.increase(BuildingEnum.PROCESSING_PLANT);
	assertEquals(1, map.get(BuildingEnum.PROCESSING_PLANT));
}

@Test
public void testSetIncrease2()
{
	map.set(BuildingEnum.DEUTERIUM_DEPOT, 11);
	map.increase(BuildingEnum.DEUTERIUM_DEPOT, 13);
	assertEquals(24, map.get(BuildingEnum.DEUTERIUM_DEPOT));
}

@Test(expected=IllegalArgumentException.class)
public void testSetDecrease1()
{
	map.decrease(BuildingEnum.INFRASTRUCTURE);
}

@Test
public void testSetDecrease2()
{
	map.set(BuildingEnum.INFRASTRUCTURE, 33);
	map.decrease(BuildingEnum.INFRASTRUCTURE);
	assertEquals(32, map.get(BuildingEnum.INFRASTRUCTURE));
}

@Test
public void testSetDecrease3()
{
	map.set(BuildingEnum.INFRASTRUCTURE, 33);
	map.decrease(BuildingEnum.INFRASTRUCTURE, 11);
	assertEquals(22, map.get(BuildingEnum.INFRASTRUCTURE));
}

@Test
public void testSetClear1()
{
	map.set(BuildingEnum.INFRASTRUCTURE, 33);
	assertEquals(33, map.get(BuildingEnum.INFRASTRUCTURE));
	map.clear();
	assertEquals(0, map.getSum());
}

@Test
public void testSetClear2()
{
	map.set(BuildingEnum.INFRASTRUCTURE, 33);
	assertEquals(33, map.get(BuildingEnum.INFRASTRUCTURE));
	map.clear();
	assertEquals(0, map.get(BuildingEnum.INFRASTRUCTURE));
}

@Test
public void testChooseRandom1()
{
	assertEquals(null, map.chooseRandom(1234));
}

@Test
public void chooseRandomWithRandomGen()
{
	assertEquals(null, map.chooseRandom(new JavaRandom(new Random())));
}

@Test
public void testChooseRandom2()
{
	map.set(BuildingEnum.TRADE_CENTER, 1);
	assertEquals(BuildingEnum.TRADE_CENTER, map.chooseRandom(1234));
}

@Test
public void testChooseRandom3()
{
	map.set(BuildingEnum.TRADE_CENTER, 1);
	assertEquals(BuildingEnum.TRADE_CENTER, map.chooseRandom(-1234));
}

@Test
public void testChooseRandom4()
{
	map.set(BuildingEnum.TRADE_CENTER, 1);
	assertEquals(BuildingEnum.TRADE_CENTER, map.chooseRandom(Integer.MIN_VALUE));
}

@Test
public void testChooseRandom5()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	map.set(BuildingEnum.LIBRARY, 1);
	assertEquals(BuildingEnum.LIBRARY, map.chooseRandom(1));
}

private EnumIntMap<BuildingEnum> serialize() throws IOException, ClassNotFoundException
{
	ByteArrayOutputStream baout = new ByteArrayOutputStream();
	ObjectOutputStream oout = new ObjectOutputStream(baout);
	oout.writeObject(map);
	oout.close();
	
	ByteArrayInputStream bain = new ByteArrayInputStream(baout.toByteArray());
	ObjectInputStream oin = new ObjectInputStream(bain);
	return (EnumIntMap<BuildingEnum>) oin.readObject();
}

@Test
public void testSerialization1() throws IOException, ClassNotFoundException
{
	EnumIntMap<BuildingEnum> result = serialize();
	assertEquals(0, result.getSum());
}

@Test
public void testSerialization2() throws IOException, ClassNotFoundException
{
	map.set(BuildingEnum.PROCESSING_PLANT, 1);
	EnumIntMap<BuildingEnum> result = serialize();
	assertEquals(1, result.getSum());
}

@Test
public void testToString()
{
	map.set(BuildingEnum.PROCESSING_PLANT, 17);
	String s = map.toString();
	assertNotNull(s);
	assertTrue(s.contains("17"));
	assertTrue(s.contains(BuildingEnum.PROCESSING_PLANT.name()));
}

}
