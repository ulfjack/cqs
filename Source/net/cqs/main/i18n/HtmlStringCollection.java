package net.cqs.main.i18n;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.main.config.Input;
import net.cqs.main.resource.ResourceManager;
import net.cqs.main.resource.ResourceNameFilter;
import de.ofahrt.ulfscript.Environment;
import de.ofahrt.ulfscript.LanguageText;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.UlfScript;
import de.ofahrt.ulfscript.utils.ProxyEnvironment;

final class HtmlStringCollection implements Iterable<HtmlStringCollection.Entry>
{

public static HtmlStringCollection getCollection(ResourceManager manager, EnvironmentDescriptor<?> page)
{
	final HtmlStringCollection result = new HtmlStringCollection(page.bundleName());
	
	final String path = page.getPrefix();
	final LanguageText callback = new LanguageText()
		{
			@Override
      public String getText(String filename, String text)
			{
				result.add(path+filename, text);
				return text;
			}
			@Override
      public long lastModified()
			{ return 0; }
		};
	
	SourceProvider provider = manager.getSourceProvider(path);
	Environment env = page.newInstance(provider);
	Environment proxy = new ProxyEnvironment(env)
		{
			@Override
			public LanguageText getLanguageText(Locale arg0)
			{ return callback; }
		};
	
	Pattern pattern = Pattern.compile(Input.convertStringToRegex(page.getFilePattern()));
	final Matcher matcher = pattern.matcher("");
	
	String[] res = manager.listResources(path, new ResourceNameFilter()
		{
			@Override
      public boolean accept(String name)
			{ return matcher.reset(name).matches(); }
		});
	Arrays.sort(res);
	for (String f : res)
	{
		try
		{
			UlfScript.compile(proxy, provider.getSource(f), null);
		}
		catch (FileNotFoundException e)
		{ e.printStackTrace(); }
		catch (UnsupportedEncodingException e)
		{ e.printStackTrace(); }
		catch (IOException e)
		{ e.printStackTrace(); }
	}
	
	return result;
}

	public static class Entry
	{
		private final String key;
		private int count = 0;
		private final LinkedHashSet<String> files = new LinkedHashSet<String>();
		public Entry(String key)
		{ this.key = key; }
		public String getKey()
		{ return key; }
		@Override
		public int hashCode()
		{ return key.hashCode(); }
		@Override
		public String toString()
		{ return key; }
		public int getCount()
		{ return files.size(); }
		public void inc(String filename)
		{
			files.add(filename);
			count++;
		}
		public Iterator<String> fileIterator()
		{ return files.iterator(); }
	}

private final LinkedHashMap<String,Entry> data = new LinkedHashMap<String,Entry>();
private final String bundleName;

private HtmlStringCollection(String bundleName)
{ this.bundleName = bundleName; }

private void add(String filename, String key)
{
	Entry e = data.get(key);
	if (e == null)
	{
		e = new Entry(key);
		data.put(key, e);
	}
	e.inc(filename);
}

@Override
public String toString()
{ return data.toString(); }

@Override
public Iterator<HtmlStringCollection.Entry> iterator()
{ return data.values().iterator(); }

public int size()
{ return data.size(); }

public String bundleName()
{ return bundleName; }

}
