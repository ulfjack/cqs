package de.ofahrt.i18n;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class PoParserTest
{

static PoEntry parse(String s) throws IOException
{ return new PoParser(new StringReader(s)).readEntry(); }

static PoEntry[] parse2(String s) throws IOException
{
	List<PoEntry> list = new ArrayList<PoEntry>();
	PoParser parser = new PoParser(new StringReader(s));
	PoEntry entry;
	while ((entry = parser.readEntry()) != null)
		list.add(entry);
	return list.toArray(new PoEntry[0]);
}

static String toString(PoEntry entry) throws IOException
{
	StringWriter out = new StringWriter();
	PoWriter writer = new PoWriter(out, true);
	writer.write(entry);
	writer.close();
	return out.toString();
}

@Test
public void testSimpleParse() throws IOException
{
	PoEntry entry = parse("msgid \"a\"\nmsgstr \"b\"");
	assertEquals("a", entry.getMessage());
	assertEquals("b", entry.getTranslation());
}

@Test
public void testMultilineParse() throws IOException
{
	PoEntry entry = parse("msgid \"a\"\nmsgstr \"\"\n\"b\"");
	assertEquals("a", entry.getMessage());
	assertEquals("b", entry.getTranslation());
}

@Test
public void testRoundTrip() throws IOException
{
	PoEntry orig = new PoEntry.Builder()
		.setMessage("a")
		.setTranslation("b")
		.build();
	PoEntry[] entries = parse2(toString(orig));
	assertEquals(2, entries.length);
	assertEquals("a", entries[1].getMessage());
	assertEquals("b", entries[1].getTranslation());
}

@Test
public void referenceIsPreserved() throws IOException
{
	PoEntry entry = parse("#: Perspective:1\nmsgid \"a\"\nmsgstr \"\"\n\"b\"");
	assertEquals("Perspective:1", entry.getReference());
	assertEquals("a", entry.getMessage());
	assertEquals("b", entry.getTranslation());
}

@Test
public void multilineReferenceIsPreserved() throws IOException
{
	PoEntry entry = parse("#: Perspective:1\n#: More:2\nmsgid \"a\"\nmsgstr \"\"\n\"b\"");
	assertEquals("Perspective:1 More:2", entry.getReference());
	assertEquals("a", entry.getMessage());
	assertEquals("b", entry.getTranslation());
}

@Test
public void completeExample() throws IOException
{
	PoEntry entry = parse(""+
			"#  translator\n"+
			"#. extracted\n"+
			"#: reference\n"+
			"#, flags\n"+
			"msgctxt \"context\"\n"+
			"msgid \"message\"\n"+
			"msgstr \"translation\"\n");
	assertEquals("translator", entry.getTranslatorComment());
	assertEquals("extracted", entry.getExtractedComment());
	assertEquals("reference", entry.getReference());
	assertEquals("flags", entry.getFlags());
	assertEquals("context", entry.getContext());
	assertEquals("message", entry.getMessage());
	assertEquals("translation", entry.getTranslation());
}

@Test // The round trip is not perfect, but it's close.
public void roundTripLong() throws IOException
{
	String s = ""+
		"# SOME DESCRIPTIVE TITLE.\n"+
		"# Copyright (C) YEAR THE PACKAGE'S COPYRIGHT HOLDER\n"+
		"# This file is distributed under the same license as the PACKAGE package.\n"+
		"# FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.\n"+
		"#\n"+
		"#, fuzzy\n"+
		"msgid \"\"\n"+
		"msgstr \"\"\n"+
		"\"Project-Id-Version: PACKAGE VERSION\\n\"\n"+
		"\"Report-Msgid-Bugs-To: \\n\"\n"+
		"\"POT-Creation-Date: 2009-09-04 23:19+0200\\n\"\n"+
		"\"PO-Revision-Date: YEAR-MO-DA HO:MI+ZONE\\n\"\n"+
		"\"Last-Translator: FULL NAME <EMAIL@ADDRESS>\\n\"\n"+
		"\"Language-Team: LANGUAGE <LL@li.org>\\n\"\n"+
		"\"MIME-Version: 1.0\\n\"\n"+
		"\"Content-Type: text/plain; charset=UTF-8\\n\"\n"+
		"\"Content-Transfer-Encoding: 8bit\\n\"\n"+
		"\n"+
		"msgid \"a\"\n"+
		"msgstr \"b\"\n";
	PoEntry[] entries = parse2(s);
	assertEquals(2, entries.length);
	StringWriter out = new StringWriter();
	PrintWriter printer = new PrintWriter(out);
	entries[0].print(printer);
	printer.println();
	entries[1].print(printer);
	printer.flush();
//	System.out.println(out.toString());
	String expected = ""+
		"#  SOME DESCRIPTIVE TITLE.\n"+
		"#  Copyright (C) YEAR THE PACKAGE'S COPYRIGHT HOLDER\n"+
		"#  This file is distributed under the same license as the PACKAGE package.\n"+
		"#  FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.\n"+
		"#, fuzzy\n"+
		"msgid \"\"\n"+
		"msgstr \"\"\n"+
		"\"Project-Id-Version: PACKAGE VERSION\\n\"\n"+
		"\"Report-Msgid-Bugs-To: \\n\"\n"+
		"\"POT-Creation-Date: 2009-09-04 23:19+0200\\n\"\n"+
		"\"PO-Revision-Date: YEAR-MO-DA HO:MI+ZONE\\n\"\n"+
		"\"Last-Translator: FULL NAME <EMAIL@ADDRESS>\\n\"\n"+
		"\"Language-Team: LANGUAGE <LL@li.org>\\n\"\n"+
		"\"MIME-Version: 1.0\\n\"\n"+
		"\"Content-Type: text/plain; charset=UTF-8\\n\"\n"+
		"\"Content-Transfer-Encoding: 8bit\\n\"\n"+
		"\n"+
		"msgid \"a\"\n"+
		"msgstr \"b\"\n";
	assertEquals(expected, out.toString());
}

}
