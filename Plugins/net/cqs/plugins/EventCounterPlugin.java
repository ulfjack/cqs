package net.cqs.plugins;

import java.util.logging.Logger;

import net.cqs.engine.Galaxy;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.StartupListener;
import net.cqs.main.signals.StrobeAdapter;

@Plugin
public class EventCounterPlugin implements StartupListener
{

public static final Logger logger = Logger.getLogger("net.cqs.plugins");

private final FrontEnd frontEnd;

private long totalEventCount = 0;
//private long startTime = System.currentTimeMillis();
private long lastEventCount = 0;

public EventCounterPlugin(PluginConfig config)
{ frontEnd = config.getFrontEnd(); }

@Override
public void startup()
{
	frontEnd.addStrobeListener(new StrobeAdapter(60)
		{
			@Override
      public void strobe()
			{
				Galaxy galaxy = frontEnd.getGalaxy();
				lastEventCount = galaxy.getEventCounterAndReset();
				logger.info(System.currentTimeMillis()+": "+lastEventCount);
				totalEventCount += lastEventCount;
			}
		});
}

}
