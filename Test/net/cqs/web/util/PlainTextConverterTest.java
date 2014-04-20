package net.cqs.web.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PlainTextConverterTest
{

private String convert(String data)
{ return new PlainTextConverter().convert(data); }

@Test
public void testSimple1()
{ assertEquals("", convert("")); }

@Test
public void testSimple2()
{ assertEquals("abcde", convert("abcde")); }

@Test
public void testSimple3()
{ assertEquals("<br />\n&lt;&amp;&gt;&quot;", convert("\n<&>\"")); }

}
