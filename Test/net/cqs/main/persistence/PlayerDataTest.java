package net.cqs.main.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.cqs.config.units.TestUnitImpl;
import net.cqs.config.units.Unit;

import org.junit.Before;
import org.junit.Test;

public class PlayerDataTest
{

private PlayerData data;

@Before
public void setUp()
{
	data = new PlayerData(new Unit[0]);
}

@Test
public void testAddDesign()
{
	int count = 0;
	for (TestUnitImpl unit: TestUnitImpl.values()) {
		if (count == 50)
			break;
		assertTrue(data.addDesign(unit));
		count++;
	}
	assertFalse(data.addDesign(TestUnitImpl.COLONIZATION));
	count = 0;
	for (TestUnitImpl unit: TestUnitImpl.values()) {
		if (count == 50)
			break;
		assertSame(unit, data.getUnitDesign(count));
		count++;
	}
}

@Test
public void testRemoveDesign()
{
	Unit u0, u1;
	data.addDesign(u0 = TestUnitImpl.FIGHTER);
	data.addDesign(u1 = TestUnitImpl.CARRIER);
	assertEquals(2, data.unitAmount());
	assertSame(u0, data.getUnitDesign(0));
	assertSame(u1, data.getUnitDesign(1));
	data.removeDesign(0);
	assertEquals(1, data.unitAmount());
	assertSame(u1, data.getUnitDesign(0));
}

@Test
public void testAddPlayerName()
{
	assertEquals(0, data.playerAmount());
	for (int i = 0; i < 50; i++)
		assertTrue(data.addPlayerName(i));
	assertFalse(data.addPlayerName(50));
	for (int i = 0; i < 50; i++)
		assertEquals(i, data.getPlayer(i));
}

@Test
public void testRemovePlayerName()
{
	assertEquals(0, data.playerAmount());
	for (int i = 0; i < 50; i++)
		data.addPlayerName(i);
	data.removePlayerName(0);
	data.removePlayerName(5);
	int temp = data.playerAmount();
	for (int i = temp; i > temp-10; i--)
		data.removePlayerName(i);
	data.removePlayerName(50);
	for (int i = 0; i < 5; i++)
		assertEquals(i+1, data.getPlayer(i));
	for (int i = 5; i < data.playerAmount(); i++)
		assertEquals(i+2, data.getPlayer(i));
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPlayerExceptions1()
{
	data.addPlayerName(0);
	data.getPlayer(1);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPlayerExceptions2()
{
	data.addPlayerName(0);
	data.getPlayer(11);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testPlayerExceptions3()
{
	data.addPlayerName(0);
	data.getPlayer(-1);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testUnitExceptions1()
{
	data.addDesign(TestUnitImpl.FIGHTER);
	data.getUnitDesign(1);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testUnitExceptions2()
{
	data.addDesign(TestUnitImpl.FIGHTER);
	data.getUnitDesign(11);
}

@Test(expected=ArrayIndexOutOfBoundsException.class)
public void testUnitExceptions3()
{
	data.addDesign(TestUnitImpl.FIGHTER);
	data.getUnitDesign(-1);
}

}
