package de.ofahrt.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class PoEntryTest
{

static String write(PoEntry entry) throws IOException
{
	StringWriter out = new StringWriter();
	entry.print(new PrintWriter(out));
	return out.toString();
}

static String write(String message, String translation) throws IOException
{
	PoEntry entry = new PoEntry.Builder()
		.setMessage(message)
		.setTranslation(translation)
		.build();
	return write(entry);
}

static String roundTripMessage(String s) throws IOException
{ return PoParserTest.parse(write(s, null)).getMessage(); }

static void assertRoundTripMessage(String s) throws IOException
{ assertEquals(s, roundTripMessage(s)); }


@Test
public void simpleWrite() throws IOException
{ assertEquals("msgid \"a\"\nmsgstr \"b\"\n", write("a", "b")); }

@Test
public void encodeSlashN() throws IOException
{ assertEquals("msgid \"\"\n\"a\\n\"\nmsgstr \"b\"\n", write("a\n", "b")); }

@Test
public void encodeSlash() throws IOException
{ assertEquals("msgid \"a\\\\\"\nmsgstr \"b\"\n", write("a\\", "b")); }

@Test
public void roundTripSlashN() throws IOException
{ assertRoundTripMessage("a\n"); }

@Test
public void roundTripSlashNSlashN() throws IOException
{ assertRoundTripMessage("a\n\n"); }

@Test
public void roundTripSlash() throws IOException
{ assertRoundTripMessage("a\\"); }

@Test
public void roundTripSlashSlashN() throws IOException
{ assertRoundTripMessage("a\\n"); }

@Test
public void preserveEmptyMsgId() throws IOException
{ assertEquals("msgid \"\"\nmsgstr \"\"\n", write(null, null)); }

@Test
public void noLineBreak() throws IOException
{
	assertEquals(""
			+"msgid \"A a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a aa.\"\n"
			+"msgstr \"\"\n",
			write("A a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a aa.", null));
}

@Test
public void lineBreak() throws IOException
{
	assertEquals(""
			+"msgid \"\"\n"
			+"\"A a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a aaa.\"\n"
			+"msgstr \"\"\n",
			write("A a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a aaa.", null));
}

@Test
public void longLineBreak() throws IOException
{
	assertEquals(""
			+"msgid \"\"\n"
			+"\"Some very very very very very very very very very very very very very very \"\n"
			+"\"very very very very long text.\\n\"\n"
			+"\"Some more very very very very very very very very very very very very very \"\n"
			+"\"very very very very very very very very very very very very very very very \"\n"
			+"\"very very long text.\"\n"
			+"msgstr \"\"\n",
			write("Some very very very very very very very very very very very very "
					+"very very very very very very long text.\nSome more very very "
					+"very very very very very very very very very very very very very "
					+"very very very very very very very very very very very very very "
					+"very very long text.", null));
}

@Test
public void references() throws IOException
{
	PoEntry entry = new PoEntry.Builder()
		.setMessage("x")
		.setReference("12345678901234567890123456789012345678901234567890 12345678901234567890 123456789012345678901234567890")
		.build();
	String s = write(entry);
	assertEquals("#: 12345678901234567890123456789012345678901234567890 12345678901234567890\n"+
			"#: 123456789012345678901234567890\n"+
			"msgid \"x\"\nmsgstr \"\"\n", s);
}

@Test
public void completeExample() throws IOException
{
	PoEntry entry = new PoEntry.Builder()
		.setTranslatorComment("translator")
		.setExtractedComment("extracted")
		.setReference("reference")
		.setFlags("flags")
		.setContext("context")
		.setMessage("message")
		.setTranslation("translation")
		.build();
	String s = write(entry);
	assertEquals(""+
			"#  translator\n"+
			"#. extracted\n"+
			"#: reference\n"+
			"#, flags\n"+
			"msgctxt \"context\"\n"+
			"msgid \"message\"\n"+
			"msgstr \"translation\"\n", s);
}

@Test
public void testEqualsAllowingNull()
{
	assertTrue(PoEntry.equalsAllowingNull(null, null));
	assertTrue(PoEntry.equalsAllowingNull("a", "a"));
	assertFalse(PoEntry.equalsAllowingNull("a", null));
	assertFalse(PoEntry.equalsAllowingNull(null, "a"));
	assertFalse(PoEntry.equalsAllowingNull("a", "b"));
}

@Test
public void testHashcodeIgnoringNull()
{
	assertEquals(0, PoEntry.hashCodeIgnoringNull(null));
	assertEquals("a".hashCode(), PoEntry.hashCodeIgnoringNull("a"));
}

@Test
public void testEquals()
{
	PoEntry e1 = new PoEntry.Builder()
	.setTranslatorComment("translator")
	.setExtractedComment("extracted")
	.setReference("reference")
	.setFlags("flags")
	.setContext("context")
	.setMessage("message")
	.setTranslation("translation")
	.build();
	PoEntry e2 = new PoEntry.Builder()
	.setTranslatorComment("translator")
	.setExtractedComment("extracted")
	.setReference("reference")
	.setFlags("flags")
	.setContext("context")
	.setMessage("message")
	.setTranslation("translation")
	.build();
	assertTrue(e1.equals(e2));
	assertEquals(e1.hashCode(), e2.hashCode());
}

public void testNotEquals()
{
	PoEntry e1 = new PoEntry.Builder()
	.setTranslatorComment("translator")
	.setExtractedComment("extracted")
	.setReference("reference")
	.setFlags("flags")
	.setContext("context")
	.setMessage("message")
	.setTranslation("translation")
	.build();
	PoEntry e2 = new PoEntry.Builder()
	.setTranslatorComment("translator")
	.setExtractedComment("extracted")
	.setMessage("message")
	.setTranslation("translation")
	.build();
	assertFalse(e1.equals(e2));
	assertFalse(e1.hashCode() == e2.hashCode());
}

}
