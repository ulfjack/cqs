package net.cqs.main.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.io.TextSource;

public class FileResourceManager implements ResourceManager
{

private final File directory;

public FileResourceManager(File directory)
{
	if (!directory.exists()) throw new IllegalArgumentException();
	if (!directory.isDirectory()) throw new IllegalArgumentException();
	this.directory = directory;
}

private Resource getRessourceForFile(final File f)
{
	if (!f.exists()) return null;
	if (!f.isFile()) return null;
	return new Resource()
		{
			@Override
      public InputStream getInputStream() throws IOException
			{ return new FileInputStream(f); }
			@Override
      public long lastModified()
			{ return f.lastModified(); }
		};
}

@Override
public Resource getResource(String name)
{
	File f = new File(directory, name);
	return getRessourceForFile(f);
}

@Override
public String[] listResources(String path, final ResourceNameFilter filter)
{
	File[] files = new File(path).listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{ return filter.accept(name); }
		});
	String[] result = new String[files.length];
	for (int i = 0; i < files.length; i++)
		result[i] = files[i].getName();
	return result;
}

@Override
public SourceProvider getSourceProvider(String prefix)
{
	final File dir = new File(directory, prefix);
	if (!dir.exists()) throw new RuntimeException("\""+dir.getPath()+"\" does not exist!");
	if (!dir.isDirectory()) throw new RuntimeException("\""+dir.getPath()+"\" is not a directory!");
	return new SourceProvider()
		{
			@Override
      public TextSource getSource(final String sourcename)
			{
				final File f = new File(dir, sourcename);
				if (!f.exists()) return null;
				if (!f.isFile()) return null;
				return new TextSource()
					{
						@Override
						public Reader getReader() throws IOException
						{ return new InputStreamReader(new FileInputStream(f), Charset.forName("UTF-8")); }
						@Override
						public String getName()
						{ return sourcename; }
						@Override
						public long lastModified()
						{ return f.lastModified(); }
					};
			}
		};
}

}
