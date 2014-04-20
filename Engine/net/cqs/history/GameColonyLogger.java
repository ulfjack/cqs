package net.cqs.history;

import net.cqs.engine.Agent;
import net.cqs.engine.Colony;
import net.cqs.engine.colony.ColonyEvent;
import net.cqs.engine.colony.ColonyEventLogger;
import net.cqs.main.persistence.ObserverReport;
import net.cqs.storage.Storage;
import net.cqs.storage.Task;

public final class GameColonyLogger implements ColonyEventLogger
{

private final Storage storage;
private final Colony colony;

public GameColonyLogger(Storage storage, Colony colony)
{
	this.storage = storage;
	this.colony = colony;
}

@Override
public void eventHappened(final ColonyEvent event)
{
	// FIXME: support bulk updates in API
//	Galaxy.logger.fine("FIXME: support bulk updates in API");
	for (Agent a : colony.getAgents())
	{
		final String pid = a.getId();
		storage.execute(new Task()
			{
				@Override
				public void run()
				{
					ObserverReport data = ObserverReport.getAgentData(pid);
					if (data.logEvent(event.getTime()))
						event.execute(data);
				}
			});
	}
}

}
