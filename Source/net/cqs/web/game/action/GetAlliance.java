package net.cqs.web.game.action;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.main.persistence.AllianceData;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.GetHandler;

class GetAlliance
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

static void init()
{
	ActionHandler.add("Alliance.Message.delete", new GetHandler()
		{                            // box,   id
			Pattern p = Pattern.compile("(\\d+),(\\w+)");
			Matcher m = p.matcher("");
			@Override
			public void activate(ParsedRequest request, CqsSession session, String param)
			{
				m.reset(param);
				if (m.matches())
				{
					final int i = Integer.parseInt(m.group(1));
					final String id = m.group(2);
					final int pid = session.getAlliance().getId();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								AllianceData.getAllianceData(pid).removeMessage(i, id);
							}
						});
				}
				else
					logger.warning("GET-Request failed: Alliance.Message.delete");
			}
		});	
}

private GetAlliance()
{/*OK*/}

}
