package net.cqs.plugins;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.logging.Level;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.config.LogEnum;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.LoginListener;
import net.cqs.main.signals.StartupListener;

@Plugin
public class MultiPlugin implements StartupListener
{

private final FrontEnd frontEnd;

public MultiPlugin(PluginConfig config)
{ frontEnd = config.getFrontEnd(); }

@Override
public void startup()
{
	try
	{
		String filename = frontEnd.getLogPath(LogEnum.MULTI);
		final PrintStream multiOutputStream = new PrintStream(new FileOutputStream(filename, true));
		multiOutputStream.println("CQS Startup: "+(new Date()).toString());
		frontEnd.addLoginListener(new LoginListener()
			{
				@Override
        public void login(String cookie, int pid)
				{
					multiOutputStream.println(new Date()+": "+cookie+" -> "+pid);
					multiOutputStream.flush();
				}
			});
	}
	catch (IOException e)
	{ FrontEnd.logger.log(Level.SEVERE, "Exception caught", e); }
}

}
