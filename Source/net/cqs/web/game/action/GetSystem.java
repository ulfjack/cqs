package net.cqs.web.game.action;

import net.cqs.config.InfoboxEnum;
import net.cqs.main.persistence.Infobox;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.GetHandler;

class GetSystem
{

static void init()
{
	ActionHandler.add("lang", new GetHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, String param)
			{ session.setLocale(session.getFrontEnd().getLocaleProvider().filter(param)); }
		});
	
	ActionHandler.add("Information.cleanup", new GetHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, String param)
			{
				final int which = Integer.parseInt(param);
				final long time = session.getGalaxy().getTime();
				final int pid = session.getPlayer().getPid();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							Infobox.getInfobox(pid).cleanupInfoBox(InfoboxEnum.values()[which], time);
						}
					});
			}
		});
}

private GetSystem()
{/*OK*/}

}
