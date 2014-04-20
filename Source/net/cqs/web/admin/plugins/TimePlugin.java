package net.cqs.web.admin.plugins;

import java.io.PrintWriter;

import net.cqs.config.Time;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;

public final class TimePlugin
{

public TimePlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("Debug", "Time", "debug-time.html", actions);
}

@WebPostAction("Time.advance")
public void advanceTime(FrontEnd frontEnd, PrintWriter out,
		@Parameter("delta") String delta)
{
	long howmuch = Time.parseTime(delta);
	synchronized (frontEnd.getGalaxy())
	{
		frontEnd.getGalaxy().advanceTime(howmuch);
	}
	long sec = howmuch % 60; howmuch /= 60;
	long min = howmuch % 60; howmuch /= 60;
	long hour = howmuch;
	out.println("Time advanced by "+hour+":"+min+":"+sec+"!");
}

@WebPostAction("Time.setSpeed")
public void advanceTime(FrontEnd frontEnd, PrintWriter out,
		@Parameter("speed") int speed)
{
	synchronized (frontEnd.getGalaxy())
	{
		frontEnd.getGalaxy().setSpeed(speed);
		out.println("Speed is now at "+frontEnd.getGalaxy().getSpeed());
	}
}

}
