package net.cqs.extern;

import static org.junit.Assert.assertEquals;
import net.cqs.web.util.LinkTextConverter;
import net.cqs.web.util.XmlTextConverter;

import org.junit.Test;

public class XmlTextConverterTest
{

private String convert(String in)
{
	return new XmlTextConverter(new LinkTextConverter()).convert(in);
}

@Test
public void testSimple1()
{ assertEquals("", convert("")); }

@Test
public void testSimple2()
{ assertEquals("&lt;", convert("<")); }

}
