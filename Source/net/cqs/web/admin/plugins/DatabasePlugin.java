package net.cqs.web.admin.plugins;

import java.io.PrintWriter;

import net.cqs.config.InvalidDatabaseException;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;

public final class DatabasePlugin
{

public DatabasePlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("Debug", "Database", "debug-database.html", actions);
}

@WebPostAction("Database.writeGalaxy")
public void writeGalaxy(FrontEnd frontEnd, PrintWriter out)
{
	frontEnd.writeGalaxy();
}

@WebPostAction("Database.check")
public void checkDatabase(FrontEnd frontEnd, PrintWriter out)
{
	try
	{ frontEnd.getGalaxy().check(); }
	catch (InvalidDatabaseException e)
	{ e.printStackTrace(out); }
	out.println("Done.");
}

}
