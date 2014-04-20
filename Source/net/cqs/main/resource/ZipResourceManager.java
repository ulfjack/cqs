package net.cqs.main.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.io.TextSource;

public class ZipResourceManager implements ResourceManager
{

private final ZipFile zipFile;

public ZipResourceManager(File file) throws IOException
{
	this.zipFile = new ZipFile(file);
}

private String cutName(String name)
{
	int i = name.lastIndexOf('/');
	if (i < 0) i = 0; else i++;
	return name.substring(i);
}

@Override
public Resource getResource(String name)
{
	final ZipEntry f = zipFile.getEntry(name);
	if (f == null) return null;
	return new Resource()
		{
			@Override
      public InputStream getInputStream() throws IOException
			{ return zipFile.getInputStream(f); }
			@Override
      public long lastModified()
			{ return f.getTime(); }
		};
}

@Override
public String[] listResources(String path, ResourceNameFilter filter)
{
	List<String> result = new ArrayList<String>();
	for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); )
	{
		ZipEntry f = entries.nextElement();
		if (f.getName().startsWith(path) && (f.getName().indexOf('/', path.length()) == -1))
		{
			String name = cutName(f.getName());
			if (filter.accept(name))
				result.add(name);
		}
	}
	return result.toArray(new String[0]);
}

@Override
public SourceProvider getSourceProvider(final String prefix)
{
	return new SourceProvider()
		{
			@Override
			public TextSource getSource(final String name)
			{
				String entryName = prefix+name;
				entryName = entryName.replaceAll("/[^/]+/../", "/");
				final ZipEntry f = zipFile.getEntry(entryName);
				if (f == null) throw new NullPointerException("No file named \""+entryName+"\" found!");
				return new TextSource()
					{
						@Override
						public Reader getReader() throws IOException
						{ return new InputStreamReader(zipFile.getInputStream(f), "UTF-8"); }
						@Override
						public String getName()
						{ return name; }
						@Override
						public long lastModified()
						{ return f.getTime(); }
					};
			}
		};
}

}
