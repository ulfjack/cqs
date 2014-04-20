package net.cqs.engine.scores;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ScoreManagerTest
{

ScoreManager<TestScoreable> sm;
List<TestScoreable> list;
TestScoreable[] data;

@Before
public void setUp()
{
	list = new ArrayList<TestScoreable>();
	for (int i = 0; i < 50; i++)
		list.add(new TestScoreable("Test "+i));
	data = list.toArray(new TestScoreable[0]);
	sm = new ScoreManager<TestScoreable>(list);
}

@Test
public void testSetUp()
{
	for (int i = 0; i < data.length; i++)
		assertSame(data[i].getScore(), sm.getData(i));
}

@Test(expected=IllegalStateException.class)
public void testScoreManagerException()
{
	data[3].newScore(sm);
	new ScoreManager<TestScoreable>(list);
}

@Test
public void testWrap()
{
	List<TestScoreable> list2 = new ArrayList<TestScoreable>();
	for (int i = 0; i < 50; i++)
		list2.add(new TestScoreable("Test "+i));
	ScoreManager<TestScoreable> sm2 = ScoreManager.wrap(list2);
	for (int i = 0; i < 50; i++)
		assertSame(list2.get(i).getScore(), sm2.getData(i));
}

@Test(expected=NullPointerException.class)
public void testRemoveNull()
{
	sm.removeObject(null);
}

@Test
public void testRemove1()
{
	sm.removeObject(data[3]);
	for (int i = 0; i < 3; i++)
	{
		assertSame(data[i].getScore(), sm.getData(i));
		assertEquals(i, sm.getData(i).position);
	}
	
	for (int i = 3; i < data.length-1; i++)
	{
		assertSame(data[i+1].getScore(), sm.getData(i));
		assertEquals(i, sm.getData(i).position);
	}
}

@Test
public void testRemove2()
{
	sm.removeObject(data[data.length-1]);
	for (int i = 0; i < data.length-1; i++)
	{
		assertSame(data[i].getScore(), sm.getData(i));
		assertEquals(i, sm.getData(i).position);
	}
}

@Test(expected=IllegalArgumentException.class)
public void testRemoveAllianceNotIn()
{
	TestScoreable ts = new TestScoreable("Test 99");
	sm.removeObject(ts);
}

@Test
public void testLength()
{
	assertEquals(50, sm.length());
}

@Test
public void testAddObject()
{
	TestScoreable ts = new TestScoreable("Test 99");
	sm.addObject(ts);
	assertSame(ts.getScore(), sm.getData(50));
	assertEquals(50, ts.getScore().position);
	for (int i = 0; i < data.length; i++)
		assertSame(data[i].getScore(), sm.getData(i));
}

@Test(expected=IllegalArgumentException.class)
public void testAddObjectException()
{
	TestScoreable ts = new TestScoreable("Test 99");
	ts.newScore(sm);
	sm.addObject(ts);
}

@Test
public void testIsIn()
{
	TestScoreable ts = new TestScoreable("Test 99");
	assertFalse(sm.isIn(ts.getScore()));
	for (int i = 0; i < data.length; i++)
		assertTrue(sm.isIn(data[i].getScore()));
}

@Test
public void testUpdate()
{
	for (int i = 0; i < data.length; i++)
		data[i].setPoints(i);
	for (int i = 0; i < data.length; i++)
		assertSame(data[i].getScore(), sm.getData(data.length-1-i));
	for (int i = 0; i < data.length; i++)
		data[i].setPoints(data.length-i);
	for (int i = 0; i < data.length; i++)
		assertSame(data[i].getScore(), sm.getData(i));
}

@Test
public void testUpdateAverage()
{
	for (int i = 0; i < data.length; i++)
		data[i].setPoints(i);
	data[3].setPoints(6);
	data[9].setPoints(6);
	data[11].setPoints(6);
	data[3].setAveragePoints(5);
	data[6].setAveragePoints(6);
	data[9].setAveragePoints(6);
	data[11].setAveragePoints(4);
	assertSame(data[6].getScore(), sm.getData(41));
	assertSame(data[9].getScore(), sm.getData(42));
	assertSame(data[3].getScore(), sm.getData(43));
	assertSame(data[11].getScore(), sm.getData(44));
}

@Test
public void testUpdateInvalidPos()
{
	data[3].setPosition(5);
	data[3].setPoints(5);
}

}
