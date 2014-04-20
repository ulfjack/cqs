package net.cqs.main.i18n;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.cqs.main.Application;
import net.cqs.main.resource.ResourceManager;
import de.ofahrt.i18n.PoEntry;
import de.ofahrt.i18n.PoMerger;
import de.ofahrt.i18n.PoParser;
import de.ofahrt.i18n.PoWriter;

public final class PoFileMerger implements Application
{

private ResourceManager resourceManager;

public PoFileMerger(ResourceManager resourceManager)
{
	this.resourceManager = resourceManager;
}

@Override
public void run(String[] args) throws IOException
{
	if (args.length < 2)
	{
		System.out.println("Please provide the name of the po file. It will be "
				+"overwritten with the new data.");
		return;
	}
	String filename = args[1];
	PoParser in = new PoParser(new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8")));
	List<PoEntry> potEntries = new PotFileCreator(resourceManager).collectEntries();
	List<PoEntry> existingEntries = new ArrayList<PoEntry>();
	PoEntry entry;
	while ((entry = in.readEntry()) != null)
		existingEntries.add(entry);
	List<PoEntry> result = new PoMerger().merge(potEntries, existingEntries);
	PoWriter out = new PoWriter(new OutputStreamWriter(new FileOutputStream(filename), Charset.forName("UTF-8")), false);
	out.write(result);
	out.close();
}

}
