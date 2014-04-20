package net.cqs.extern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.cqs.web.util.LinkTextConverter;

import org.junit.Test;

import de.ofahrt.ulfscript.analysis.xml.XmlState;

public class LinkTextConverterTest
{

private String convert(String s)
{
	String result = new LinkTextConverter().convert(s);
	XmlState state = new XmlState(true);
	state.parse(null, result);
	if (!state.isFragment()) fail("Not valid XML!");
	return result;
}

@Test
public void testSimple1()
{ assertEquals("abc", convert("abc")); }

@Test
public void testSimple2()
{ assertEquals("&lt;", convert("<")); }

@Test
public void testUrl1()
{ assertEquals("http://&lt;&gt;/ [<a href=\"http://&lt;&gt;/\">Link</a>]", convert("http://<>/")); }

@Test
public void testUrl2()
{ assertEquals("ftp://&lt;&gt;/ [<a href=\"ftp://&lt;&gt;/\">Link</a>]", convert("ftp://<>/")); }

}
