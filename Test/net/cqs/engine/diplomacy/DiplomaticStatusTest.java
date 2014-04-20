package net.cqs.engine.diplomacy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DiplomaticStatusTest
{

@Before
public void setUp()
{/*ok*/}

@Test
public void testBestOf()
{
	DiplomaticStatus result = DiplomaticStatus.ALLIED.bestOf(DiplomaticStatus.UNITED);
	assertEquals(result, DiplomaticStatus.ALLIED);
	result = DiplomaticStatus.ALLIED.bestOf(DiplomaticStatus.SELF);
	assertEquals(result, DiplomaticStatus.SELF);
}

@Test
public void testWorstOf()
{
	DiplomaticStatus result = DiplomaticStatus.HOSTILE.worseOf(DiplomaticStatus.NEUTRAL);
	assertEquals(result, DiplomaticStatus.HOSTILE);
	result = DiplomaticStatus.SELF.worseOf(DiplomaticStatus.NEUTRAL);
	assertEquals(result, DiplomaticStatus.NEUTRAL);
}

@Test
public void testNull()
{
	DiplomaticStatus result = DiplomaticStatus.UNITED.bestOf(null);
	assertEquals(result, DiplomaticStatus.UNITED);
	
	result = DiplomaticStatus.UNITED.worseOf(null);
	assertEquals(result, DiplomaticStatus.UNITED);
}

@Test
public void testPassBlockade()
{
	assertTrue(DiplomaticStatus.ALLIED.canPassBlockade(true));
	assertTrue(DiplomaticStatus.TRADE.canPassBlockade(false));
}
	
}
