package net.cqs.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ofahrt.i18n.PoEntry;
import de.ofahrt.i18n.PoParser;

public final class FileStringBundleFactory extends StringBundleFactory
{

private static final Logger logger = Logger.getLogger("net.cqs.i18n");

private final File directory;
private final String groupName;
private final ConcurrentHashMap<String,StringBundle> bundles = new ConcurrentHashMap<String,StringBundle>();
private final ConcurrentHashMap<Locale,Iterable<PoEntry>> lists = new ConcurrentHashMap<Locale,Iterable<PoEntry>>();

public FileStringBundleFactory(File directory, String groupName)
{
	this.directory = directory;
	this.groupName = groupName;
}

public Locale[] listLocales()
{
	Pattern pattern = Pattern.compile(Pattern.quote(groupName)+"_([a-z][a-z])(?:_([A-Z][A-Z])?(?:_(.*))?)?\\.po");
	final Matcher m = pattern.matcher("");
	String[] fileNames = directory.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return m.reset(name).matches();
			}
		});
	ArrayList<Locale> result = new ArrayList<Locale>();
	for (int i = 0; i < fileNames.length; i++)
	{
		m.reset(fileNames[i]);
		if (m.matches())
		{
			String language = m.group(1);
			String country = m.group(2);
			if (country == null) country = "";
			String variant = m.group(3);
			if (variant == null) variant = "";
			result.add(new Locale(language, country, variant));
		}
	}
	return result.toArray(new Locale[0]);
}

private synchronized Iterable<PoEntry> readList(Locale locale)
{
	Iterable<PoEntry> result = lists.get(locale);
	if (result == null)
	{
		ArrayList<PoEntry> entries = new ArrayList<PoEntry>();
		try
		{
			String resname = StringBundle.toKey(groupName, locale)+".po";
			File f = new File(directory, resname);
			if (f.exists())
			{
				InputStream in = new FileInputStream(f);
				PoParser parser = new PoParser(new InputStreamReader(in, "UTF-8"));
				PoEntry entry;
				while ((entry = parser.readEntry()) != null)
					entries.add(entry);
			}
			else
				logger.warning("Loading \""+groupName+"\", not found: "+resname);
		}
		catch (IOException e)
		{ e.printStackTrace(); }
		result = entries;
		lists.putIfAbsent(locale, result);
	}
	return result;
}

@Override
public StringBundle get(String baseName, Locale locale)
{
	String name = StringBundle.toKey(baseName, locale);
	StringBundle result = bundles.get(name);
	if (result == null)
	{
		result = new StringBundle(baseName, locale);
		for (PoEntry entry : readList(locale))
		{
			if (baseName.equals(entry.getContext()) && (entry.getTranslation() != null))
				result.setString(entry.getMessage(), entry.getTranslation());
		}
		bundles.putIfAbsent(name, result);
	}
	return result;
}
}