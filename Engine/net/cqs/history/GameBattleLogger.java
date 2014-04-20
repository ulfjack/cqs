package net.cqs.history;

import net.cqs.engine.battles.AbstractBattleLogger;
import net.cqs.engine.battles.Battle;
import net.cqs.main.persistence.BattleReport;
import net.cqs.storage.Storage;
import net.cqs.storage.Task;

public final class GameBattleLogger extends AbstractBattleLogger
{

private static final BattleReportParser parser = new BattleReportParser();

private final Storage storage;

public GameBattleLogger(Storage storage, Battle battle)
{
	super(battle);
	this.storage = storage;
	final String pid = getBattle().getId();
	storage.execute(new Task()
		{
			@Override
			public void run()
			{
				BattleReport.createBattleReport(pid);
			}
		});
}

@Override
protected void log(final String msg)
{
	final String pid = getBattle().getId();
	storage.execute(new Task()
		{
			@Override
			public void run()
			{
				BattleReport report = BattleReport.getBattleReport(pid);
				parser.update(report, msg);
			}
		});
}

}
