package net.cqs.web.game.action;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.messages.Message;
import net.cqs.main.persistence.Mailbox;
import net.cqs.main.persistence.PlayerData;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.GetHandler;

class GetPlayer
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

static void init()
{
	ActionHandler.add("Player.Message.delete", new GetHandler()
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
					final int pid = session.getPlayer().getPid();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								PlayerData.getPlayerData(pid).getMail().getFolder(i).remove(id);
							}
						});
				}
				else
					logger.warning("GET-Request failed: Player.Message.delete");
			}
		});
	
	ActionHandler.add("Player.Message.deleteAll", new GetHandler()
		{                            // box
			@Override
			public void activate(ParsedRequest request, CqsSession session, String param)
			{
				final int i = Integer.parseInt(param);
				final int pid = session.getPlayer().getPid();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							PlayerData.getPlayerData(pid).getMail().getFolder(i).removeAll();
						}
					});
			}
		});

	
	ActionHandler.add("Player.Message.read", new GetHandler()
		{
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
					final int pid = session.getPlayer().getPid();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								Mailbox mail = PlayerData.getPlayerData(pid).getMail();
								mail.setRead(i, id);
							}
						});
				}
				else
					logger.warning("GET-Request failed: Player.Message.read");
			}
		});
	
	ActionHandler.add("Player.Message.readAll", new GetHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, String param)
			{
				final int i = Integer.parseInt(param);
				final int pid = session.getPlayer().getPid();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							Mailbox mail = PlayerData.getPlayerData(pid).getMail();
							mail.setAllRead(i);
						}
					});
			}
		});

	ActionHandler.add("Player.Invitation.accept", new GetHandler()
		{
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
					
					Mailbox mail = session.getPlayerMailboxCopy();
					Message msg = mail.getFolder(i).get(id);
					Alliance a = session.getGalaxy().findAllianceById(msg.getTypeInfo());
					a.add(session.getGalaxy().getTime(), session.getPlayer());
					
					final int pid = session.getPlayer().getPid();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								PlayerData.getPlayerData(pid).getMail().getFolder(i).remove(id);
							}
						});
				}
				else
					logger.warning("GET-Request failed: Player.Message.read");
			}
		});
}

private GetPlayer()
{/*OK*/}

}
