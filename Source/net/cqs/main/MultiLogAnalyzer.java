package net.cqs.main;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiLogAnalyzer
{

//Thu Jul 05 23:36:30 CEST 2007: 099285cc3b9a7e5427bb2026ea3eb8dd -> 9
private static final Pattern PATTERN = Pattern.compile("");

private static final Logger logger = Logger.getLogger(MultiLogAnalyzer.class.getName());

	private static class Entry
	{
		String id;
		String time;
		public Entry(String id, String time)
		{
			this.id = id;
			this.time = time;
		}
	}

private final File logFile;
private final HashMap<String,ArrayList<Entry>> map = new HashMap<String,ArrayList<Entry>>();

public MultiLogAnalyzer(File logFile)
{
	this.logFile = logFile;
}

private void closeIgnoreException(Closeable item)
{
	try
	{ item.close(); }
	catch (Exception e)
	{ logger.log(Level.WARNING, "Exception ignored", e); }
}

public void run() throws IOException
{
	String s;
	BufferedReader in = null;
	try
	{
		in = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
		while ((s = in.readLine()) != null)
		{
			if (s.length() == 0) continue;
			if (s.startsWith("CQS")) continue;
			Matcher m = PATTERN.matcher(s);
			if (m.matches())
			{
				String cookie = m.group(2);
				String id = m.group(3);
				String time = m.group(1);
				ArrayList<Entry> temp = map.get(cookie);
				if (temp == null)
				{
					temp = new ArrayList<Entry>();
					map.put(cookie, temp);
				}
				temp.add(new Entry(id, time));
			}
			else
				throw new RuntimeException("\""+s+"\"");
		}
	}
	finally
	{
		if (in != null)
			closeIgnoreException(in);
	}
}

}
