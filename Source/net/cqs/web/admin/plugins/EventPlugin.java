package net.cqs.web.admin.plugins;

import java.text.DateFormat;
import java.text.ParseException;

import net.cqs.engine.rtevents.RealTimeEvent;
import net.cqs.engine.rtevents.RealTimeEventType;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;
import net.cqs.web.admin.AdminSession;

public final class EventPlugin
{

//private static final SimpleDateFormat dateFormat =
//	new SimpleDateFormat("d.M.y H:m");

public EventPlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("Core", "Events", "system-events.html", actions);
}

@WebPostAction("Event.create")
public void createEvent(FrontEnd frontEnd, AdminSession session,
		@Parameter("type") String type, @Parameter("date") String date) throws ParseException
{
	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, session.getLocale());
	dateFormat.setLenient(true);
	RealTimeEventType rttype = RealTimeEventType.valueOf(type);
	long time = dateFormat.parse(date).getTime();
	
	RealTimeEvent event = new RealTimeEvent(time, rttype);
	frontEnd.getGalaxy().addRealTimeEvent(event);
}

@WebPostAction("Event.execute")
public void executeEvent(FrontEnd frontEnd,
		@Parameter("type") String type)
{
	RealTimeEventType rttype = RealTimeEventType.valueOf(type);
	frontEnd.getGalaxy().execute(rttype);
}

}
