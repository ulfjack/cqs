package net.cqs.web.game;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class FrontEndToolsTest
{

FrontEndTools tools;

@Before
public void setUp()
{
	tools = new FrontEndTools(null);
}

@Test
public void testAbbreviatedLong()
{
	assertEquals(tools.getAbbreviatedLong(Locale.GERMANY, 1000000), "1.000k");
	assertEquals(tools.getAbbreviatedLong(Locale.GERMANY, ((long) Integer.MAX_VALUE)+1), "2.147m");
	assertEquals(tools.getAbbreviatedLong(Locale.GERMANY, Long.MAX_VALUE), "9.223.372t");
}

}
