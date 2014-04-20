package net.cqs.main.setup;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

final class DirectoryBuilder
{

private static final Logger logger = Logger.getLogger(DirectoryBuilder.class.getName());

private File current;
private Stack<File> stack;

public DirectoryBuilder(File base)
{
	current = base;
	stack = new Stack<File>();
	current.mkdirs();
}

private void closeIgnoreException(Closeable item)
{
	try
	{ item.close(); }
	catch (Exception e)
	{ logger.log(Level.WARNING, "Exception ignored", e); }
}

public void touchResource(String name) throws IOException
{
	File f = new File(current, name);
	if (!f.exists())
	{
		InputStream in = null;
		OutputStream out = null;
		try
		{
			in = getClass().getClassLoader().getResourceAsStream("net/cqs/main/setup/"+name);
			out = new FileOutputStream(f, false);
			byte[] data = new byte[1024];
			int count = 0;
			while ((count = in.read(data)) >= 0)
				out.write(data, 0, count);
			out.close();
		}
		finally
		{
			if (in != null)
				closeIgnoreException(in);
			if (out != null)
				closeIgnoreException(out);
		}
	}
}

public void add(String name) throws IOException
{
	File f = new File(current, name);
	if (f.exists())
	{
		if (!f.isDirectory())
			throw new IOException("Argh!");
	}
	else
	{
		if (!f.mkdir())
			throw new IOException("Argh!");
	}
}

public void enter(String name) throws IOException
{
	add(name);
	stack.push(current);
	current = new File(current, name);
}

public void leave()
{
	current = stack.pop();
}

}
