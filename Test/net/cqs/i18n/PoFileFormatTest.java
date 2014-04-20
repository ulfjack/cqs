package net.cqs.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import net.cqs.I18nTestHelper;
import net.cqs.NamedParameterized;
import net.cqs.NamedParameterized.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ofahrt.i18n.PoEntry;
import de.ofahrt.i18n.PoParser;

@RunWith(NamedParameterized.class)
public class PoFileFormatTest
{

@Parameters
public static Collection<Object[]> data()
{
	ArrayList<Object[]> result = new ArrayList<Object[]>();
	for (Locale l : I18nTestHelper.TESTED_LOCALES)
		result.add(new Object[] { l });
	return result;
}

private final Locale locale;

public PoFileFormatTest(Locale locale)
{ this.locale = locale; }

@Test
public void checkFormat() throws IOException
{ // throws an Exception if the file format is broken
	ArrayList<PoEntry> entries = new ArrayList<PoEntry>();
	String resname = StringBundle.toKey("cqs", locale)+".po";
	File f = new File(new File("I18n"), resname);
	InputStream in = new FileInputStream(f);
	PoParser parser = new PoParser(new InputStreamReader(in, "UTF-8"));
	PoEntry entry;
	while ((entry = parser.readEntry()) != null)
		entries.add(entry);
}

}
