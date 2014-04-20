package net.cqs.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import de.ofahrt.i18n.PoEntry;
import de.ofahrt.i18n.PoParser;

public final class ResourceStringBundleFactory extends StringBundleFactory
{

private static final Logger logger = Logger.getLogger("net.cqs.i18n");

private final String groupName;
private final ConcurrentHashMap<String,StringBundle> bundles = new ConcurrentHashMap<String,StringBundle>();
private final ConcurrentHashMap<Locale,Iterable<PoEntry>> lists = new ConcurrentHashMap<Locale,Iterable<PoEntry>>();

public ResourceStringBundleFactory(String groupName)
{
	this.groupName = groupName;
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
			InputStream in = StringBundle.class.getClassLoader().getResourceAsStream(resname);
			if (in != null)
			{
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
		StringBundle other = bundles.putIfAbsent(name, result);
		if (other != null) {
		  result = other;
		}
		Locale parent = StringBundle.getParentLocale(locale);
		if (parent != null) {
		  result.setParent(get(baseName, parent));
		}
	}
	return result;
}
}