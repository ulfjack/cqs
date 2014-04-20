package net.cqs.web.admin;

import java.io.PrintWriter;

import net.cqs.auth.Identity;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;

public final class AdminInternalPlugin
{

private final AdminServlet manager;

public AdminInternalPlugin(AdminServlet manager)
{
	this.manager = manager;
	ActionParser parser = manager.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	manager.addPage("Core", "Rights", "rights.html", actions);
}

@WebPostAction("Admin.add")
public void addAdmin(Identity id, PrintWriter out,
		@Parameter("name") String name)
{
	manager.addAdmin(id, out, name);
}

}
