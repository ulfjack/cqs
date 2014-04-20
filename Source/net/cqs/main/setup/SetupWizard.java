package net.cqs.main.setup;

import java.io.File;
import java.io.IOException;

import net.cqs.main.Application;

public final class SetupWizard implements Application
{

private final File dataPath;

public SetupWizard(File dataPath)
{
	this.dataPath = dataPath;
}

@Override
public void run(String[] args) throws IOException
{
	File f = dataPath;
	if (f.exists())
		throw new IOException("Cannot create new galaxy in existing directory!");
	if (!f.mkdirs())
		throw new IOException("Could not create directory!");
	if (!f.isDirectory()) throw new IOException("Must be a directory!");
	
	DirectoryBuilder b = new DirectoryBuilder(f);
	b.touchResource("config.xml");
	System.out.println("Please edit \""+new File(f, "config.xml")+"\" now and then start the server!");
}

}
