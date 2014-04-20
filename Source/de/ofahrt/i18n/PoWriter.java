package de.ofahrt.i18n;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class PoWriter
{

private static final SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");

private final PrintWriter out;

public PoWriter(Writer writer, boolean writeHeader)
{
	this.out = new PrintWriter(writer);
	if (writeHeader)
	{
		out.println("# SOME DESCRIPTIVE TITLE.");
		out.println("# Copyright (C) YEAR THE PACKAGE'S COPYRIGHT HOLDER");
		out.println("# This file is distributed under the same license as the PACKAGE package.");
		out.println("# FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.");
		out.println("#");
		out.println("#, fuzzy");
		out.println("msgid \"\"");
		out.println("msgstr \"\"");
		out.println("\"Project-Id-Version: PACKAGE VERSION\\n\"");
		out.println("\"Report-Msgid-Bugs-To: \\n\"");
		out.println("\"POT-Creation-Date: "+FMT.format(new Date())+"\\n\"");
		out.println("\"PO-Revision-Date: YEAR-MO-DA HO:MI+ZONE\\n\"");
		out.println("\"Last-Translator: FULL NAME <EMAIL@ADDRESS>\\n\"");
		out.println("\"Language-Team: LANGUAGE <LL@li.org>\\n\"");
		out.println("\"MIME-Version: 1.0\\n\"");
		out.println("\"Content-Type: text/plain; charset=UTF-8\\n\"");
		out.println("\"Content-Transfer-Encoding: 8bit\\n\"");
	}
}

public void write(PoEntry entry) throws IOException
{
	out.println();
	entry.print(out);
	out.flush();
	if (out.checkError()) throw new IOException();
}

public void write(Iterable<PoEntry> entries) throws IOException
{
	for (PoEntry entry : entries)
		write(entry);
}

public void close() throws IOException
{
	if (out.checkError()) throw new IOException();
	out.close();
}

}
