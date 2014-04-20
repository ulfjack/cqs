package net.cqs.web.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WikiTextConverterTest
{

private String convert(String data)
{ return new WikiTextConverter().convert(data); }

@Test
public void testSimple1()
{ assertEquals("", convert("")); }

@Test
public void testSimple2()
{ assertEquals("abcde", convert("abcde")); }

@Test
public void testSimple3()
{ assertEquals("<br />&lt;&amp;&gt;&quot;", convert("\n<&>\"")); }

@Test
public void testLink1()
{
	assertEquals("<a href=\"http://www.ofahrt.de/\">http://www.ofahrt.de/</a>", convert("http://www.ofahrt.de/"));
}

@Test
public void testPre1()
{
	assertEquals("<pre>\nBla\n</pre>", convert("<pre>\nBla\n</pre>"));
}

@Test
public void testPre2()
{
	assertEquals("Huh<br /><pre>\nBla\n</pre>\nDuh", convert("Huh\n<pre>\nBla\n</pre>\nDuh"));
}

@Test
public void testPre3()
{
	assertEquals("<pre>\n*Bla*\n</pre>", convert("<pre>\n*Bla*\n</pre>"));
}

@Test
public void testMath1()
{
	assertEquals("<pre>\nBla\n</pre>", convert("<math>\nBla\n</math>"));
}

@Test
public void testBold1()
{
	assertEquals("<b>Fett</b>", convert("*Fett*"));
}

@Test
public void testItalic1()
{
	assertEquals("<i>Kursiv</i>", convert("_Kursiv_"));
}

}
