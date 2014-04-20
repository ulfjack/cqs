package net.cqs.web.admin.plugins;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;
import net.cqs.main.config.FrontEnd;
import net.cqs.plugins.quota.QuotaConfiguration;
import net.cqs.plugins.quota.QuotaEntry;
import net.cqs.storage.Task;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;

public final class QuotaPlugin
{

public QuotaPlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("Quota", "Quota-Editor", "quota.html", actions);
}

private Date parse(String date)
{ return QuotaTools.parse(date); }

@WebPostAction("Quota.resetAll")
public void addEntry(FrontEnd frontEnd)
{
	Iterator<Player> it = frontEnd.getGalaxy().playerIterator();
	while (it.hasNext())
	{
		Player p = it.next();
		p.setAttr(Attribute.QUOTA_USED, Integer.valueOf(0));
		p.setAttr(Attribute.GP_QUOTA_USED, Integer.valueOf(0));
	}
}

@WebPostAction("Entry.add")
public void addEntry(
		FrontEnd frontEnd,
		@Parameter("name") String name,
		@Parameter("value") int value,
		@Parameter("duration") int duration,
		@Parameter("date") String date,
		@Parameter("comment") String comment)
{
	final QuotaEntry entry = new QuotaEntry(name, value, duration, parse(date), comment);
	frontEnd.getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				QuotaConfiguration quotaConfig = QuotaConfiguration.get();
				quotaConfig.add(entry);
			}
		});
}

@WebPostAction("Entry.deleteOld")
public void deleteOldEntries(FrontEnd frontEnd, PrintWriter out)
{
	final Date now = new Date();
	frontEnd.getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				QuotaConfiguration quotaConfig = QuotaConfiguration.get();
				quotaConfig.deleteOldEntries(now);
			}
		});
}

@WebPostAction("Entry.delete")
public void deleteEntry(FrontEnd frontEnd, PrintWriter out,
		@Parameter("id") final String id)
{
	frontEnd.getStorageManager().execute(new Task()
		{
			@Override
			public void run()
			{
				QuotaConfiguration quotaConfig = QuotaConfiguration.get();
				quotaConfig.delete(id);
			}
		});
}

}
