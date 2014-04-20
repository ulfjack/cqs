package de.ofahrt.i18n;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PoHelperTest
{

private void assertIdempotentEscape(String s)
{ assertEquals(s, PoHelper.escape(s)); }

private void assertIdempotentUnEscape(String s)
{ assertEquals(s, PoHelper.unescape(s)); }


@Test public void idempotentEscape()
{ assertIdempotentEscape("abc !@#$%^&*()_+=-':;]}[{?></.,"); }

@Test public void idempotentUnEscape()
{ assertIdempotentUnEscape("abc !@#$%^&*()_+=-':;]}[{?></.,"); }

@Test public void unescapeSlashN()
{ assertEquals("a\n", PoHelper.unescape("a\\n")); }

@Test public void unescapeSlashSlashN()
{ assertEquals("\\n", PoHelper.unescape("\\\\n")); }

@Test public void escapeStringDelim()
{ assertEquals("\\\"", PoHelper.escape("\"")); }

@Test public void unescapeStringDelim()
{ assertEquals("\"", PoHelper.unescape("\\\"")); }

@Test public void splitNotFound()
{ assertArrayEquals(new String[] {"abc"}, PoHelper.split("abc", " ")); }

@Test public void splitMiddle()
{ assertArrayEquals(new String[] {"a", "b"}, PoHelper.split("a b", " ")); }

@Test public void splitStart()
{ assertArrayEquals(new String[] {"", "a"}, PoHelper.split(" a", " ")); }

@Test public void splitEnd()
{ assertArrayEquals(new String[] {"a", ""}, PoHelper.split("a ", " ")); }

}
