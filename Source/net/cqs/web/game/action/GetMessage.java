package net.cqs.web.game.action;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.config.RightEnum;
import net.cqs.main.persistence.AllianceData;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.GetHandler;

class GetMessage
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

static void init()
{
	ActionHandler.add("Alli.Readtime.set", new GetHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, String param)
			{
				long i = Long.parseLong(param);
				session.getPlayer().setAllianceReadTime(i);
			}
		});
	
	ActionHandler.add("Alli.Message.delete", new GetHandler()
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
					final int pid = session.checkAllianceRight(RightEnum.DELETE_MESSAGES).getId();
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
					logger.warning("GET-Request failed: Alli.Message.delete");
			}
		});
	
/*	ActionHandler.add("Alli.Ext.delete", new GetHandler()
		{
			@Override
			public void activate(CqsSession session, String param)
			{
				int i = Integer.parseInt(param);
				Alliance myAlliance = session.checkAllianceRight(RightEnum.DELETE_MESSAGES);
				CqsPersistence.get(myAlliance).mail().removeMessageInverse(MessageType.GROUP_EXTERNAL, i);
			}
		});
	
	ActionHandler.add("Alli.App.reject", new GetHandler()
		{
			@Override
			public void activate(CqsSession session, String param)
			{
				int i = Integer.parseInt(param);
				Alliance myAlliance = session.checkAllianceRight(RightEnum.ACCEPT_APPLICATIONS);
				CqsPersistence.get(myAlliance).mail().removeMessageInverse(MessageType.GROUP_APPLICATION, i);
			}
		});*/
}

private GetMessage()
{/*OK*/}

}
