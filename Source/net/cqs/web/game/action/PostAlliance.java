package net.cqs.web.game.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InputValidation;
import net.cqs.config.RightEnum;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.actions.AllianceCreateAction;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.base.Survey;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.Rank;
import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.main.config.LogEnum;
import net.cqs.main.i18n.SystemMessageEnum;
import net.cqs.main.persistence.AllianceData;
import net.cqs.main.persistence.Mailbox;
import net.cqs.services.AuthService;
import net.cqs.services.SmtpService;
import net.cqs.services.email.Email;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostAlliance
{

static void init()
{
	ActionHandler.add("Alliance.create", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String name = req.getParameter("name");
				String shortName = req.getParameter("shortname");
				Player who = session.getPlayer();
				session.getGalaxy().schedex(new AllianceCreateAction(who, name, shortName));
			}
		});
	
	ActionHandler.add("Alliance.Message.send", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.WRITE_MESSAGES);
				String body = req.getParameter("body");
				
				if (body != null)
				{
					final Message m = new Message(PlayerMessageType.FORUM, session.getPlayer(), (Player) null, "", body);
					final int pid = myAlliance.getId();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								AllianceData.getAllianceData(pid).addMessage(m);
							}
						});
					session.flag = 1;
				}
				else
					session.log(ErrorCode.MESSAGE_NO_BODY);
			}
		});
	
	// FIXME: Implement multiple message deletion.
/*	ActionHandler.add("Alliance.Message.delete", new PostHandler()
		{
			@Override
			public void activate(CqsSession session, final Request req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.DELETE_MESSAGES);
				AllianceData allianceData = CqsPersistence.get(myAlliance);
				int box = Integer.parseInt(req.getParameter("box"));
				int counter = Integer.parseInt(req.getParameter("counter"));
				
				if (allianceData.mail().checkRemoveCounter(box, counter))
				{
					MessageList list = allianceData.mail().data[box];
					list.actReverse(new Actor<Message>()
						{
							public Result act(int index, Message element)
							{
								String shouldDelete = req.getParameter("d"+index);
								if ("on".equals(shouldDelete))
									return Result.REMOVE;
								return Result.NONE;
							}
						});
				}
				else
					session.log(ErrorCode.FAILED_SYNCHRONIZATION);
			}
		});*/
	
	ActionHandler.add("Alliance.External.send", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String body = req.getParameter("body");
				int index = Integer.parseInt(req.getParameter("target"));
				Alliance targetAlliance = session.getGalaxy().findAllianceById(index);
				if ((body != null) && (targetAlliance != null) && (!session.isRestricted()))
				{
					final Message m = new Message(PlayerMessageType.MESSAGE, session.getPlayer(), (Player) null, "", body);
					final int pid = targetAlliance.getId();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								AllianceData.getAllianceData(pid).addMessage(m);
							}
						});
					session.flag = 1;
				}
				else
				{
					if (session.isRestricted())
						session.log(ErrorCode.RESTRICTED_ACCESS);
					if (body == null)
						session.log(ErrorCode.MESSAGE_NO_BODY);
					if (targetAlliance == null)
						session.log(ErrorCode.MESSAGE_NO_TARGET);
				}
			}
		});
	
	ActionHandler.add("Alliance.Application.send", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String body = req.getParameter("body");
				int index = Integer.parseInt(req.getParameter("target"));
				Alliance targetAlliance = session.getGalaxy().findAllianceById(index);
				if ((body != null) && (targetAlliance != null) && 
				    (!session.isMulti()) && (!session.isRestricted()) &&
				    (targetAlliance != session.getPlayer().getAlliance()))
				{
					final Message m = new Message(PlayerMessageType.APPLICATION, session.getPlayer(), (Player) null, "", body);
					final int pid = targetAlliance.getId();
					session.execute(new Task()
						{
							@Override
							public void run()
							{
								AllianceData.getAllianceData(pid).addMessage(m);
							}
						});
					session.flag = 1;
				}
				else
				{
					if (targetAlliance == session.getPlayer().getAlliance())
						session.log(ErrorCode.CANNOT_APPLY_ALREADY_MEMBER);
					if (session.isRestricted())
						session.log(ErrorCode.RESTRICTED_ACCESS);
					if (session.isMulti())
						session.log(ErrorCode.RESTRICTED_ACCESS_MULTI);
					if (body == null)
						session.log(ErrorCode.MESSAGE_NO_BODY);
					if (targetAlliance == null)
						session.log(ErrorCode.MESSAGE_NO_TARGET);
				}
			}
		});
	
	ActionHandler.add("Alliance.Application.accept", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.ACCEPT_APPLICATIONS);
				final int i = Integer.parseInt(req.getParameter("box"));
				final String id = req.getParameter("id");
				
				Mailbox allianceData = session.getAllianceMailboxCopy();
				Message msg = allianceData.getFolder(i).get(id);
				if (msg == null) return;
				
				final int pid = myAlliance.getId();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							AllianceData.getAllianceData(pid).removeMessage(i, id);
						}
					});
				
				String body = req.getParameter("body");
				Player player = msg.getSender(session.getGalaxy());
				String subject = SystemMessageEnum.INVITE_ALLIANCE.getName(player.getLocale());
				Message answer = new Message(PlayerMessageType.INVITATION, myAlliance.getId(),
						session.getPlayer(), player, subject, body);
				session.dropMail(player, answer);
			}
		});
	
	ActionHandler.add("Alliance.Application.deny", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.ACCEPT_APPLICATIONS);
				final int i = Integer.parseInt(req.getParameter("box"));
				final String id = req.getParameter("id");
				
				AllianceData allianceData = session.getAllianceDataCopy();
				Message msg = allianceData.getMessage(i, id);
				if (msg == null) return;
				
				final int pid = myAlliance.getId();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							AllianceData.getAllianceData(pid).removeMessage(i, id);
						}
					});
				
				String body = req.getParameter("body");
				Player player = msg.getSender(session.getGalaxy());
				String subject = SystemMessageEnum.DENY_ALLIANCE.getName(player.getLocale());
				Message answer = new Message(PlayerMessageType.MESSAGE, myAlliance.getId(),
						session.getPlayer(), player, subject, body);
				session.dropMail(player, answer);
			}
		});
	
	ActionHandler.add("Alliance.Mail.send", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				session.checkAllianceRight(RightEnum.SEND_MAIL);
				String method = req.getParameter("method");
				String subject = req.getParameter("subject");
				String body = req.getParameter("body");
				if ((body != null) && (subject != null) && (method != null))
				{
					if (method.equals("email"))
					{
						Email em = new Email("");
						em.setSubject("[CqS] "+subject);
						em.setBody(body+"\n\n"+
							"-----------------------------------------------\n"+
							"Diese email wurde von Ihrem Allianz-Admin\n"+
							session.getPlayer().getName()+" geschrieben. Wenn Sie auf\n"+
							"diese Art keine emails erhalten wollen, dann\n"+
							"sprechen Sie bitte mit Ihrem Allianz-Admin\n"+
							"oder treten Sie aus der Allianz aus.\n\n"+
							"Wenn diese email Spam oder beleidigende oder\n"+
							"rassistische Inhalte enth\u00E4lt, so leiten Sie\n"+
							"diese email bitte an ulfjack@conquer-space.net\n"+
							"weiter.");
						try
						{
							File f = new File(session.getFrontEnd().getLogPath(LogEnum.EMAIL));
							if (!f.exists()) f.createNewFile();
							FileOutputStream fout = new FileOutputStream(f, true);
							PrintStream pout = new PrintStream(fout);
							pout.println("From "+session.getPlayer().getName());
							pout.println("Subject: "+subject);
							pout.println();
							pout.println(em.getBody());
							pout.flush();
							pout.close();
							AuthService authService = session.getFrontEnd().findService(AuthService.class);
							boolean sendAll = true;
							
							Iterator<Player> it = session.getPlayer().getAlliance().iterator();
							while (it.hasNext())
							{
								Player p = it.next();
								String pemail = authService.getEmail(p.getPrimaryIdentity());
								if (pemail == null)
								{
									sendAll = false;
									continue;
								}
								session.getFrontEnd().findService(SmtpService.class).sendEmail(new Email(em, pemail));
							}
							session.flag = 1;
							if (!sendAll) session.flag = 2;
						}
						catch (Exception e)
						{
							ActionHandler.logger.log(Level.SEVERE, "Exception caught", e);
						}
					}
					else
					{
						Iterator<Player> it = session.getPlayer().getAlliance().iterator();
						while (it.hasNext())
						{
							Player targetPlayer = it.next();
							Message m = new Message(PlayerMessageType.MESSAGE, session.getPlayer(), targetPlayer, subject, body);
							session.dropMail(targetPlayer, m);
						}
						session.flag = 1;
					}
				}
				else
				{
					if (subject == null)
						session.log(ErrorCode.MESSAGE_NO_SUBJECT);
					if (body == null)
						session.log(ErrorCode.MESSAGE_NO_BODY);
				}
			}
		});
	
	ActionHandler.add("Alliance.Description.set", new PostHandler()
		{
			@Override			
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.EDIT_DESCRIPTION);
				String text = req.getParameter("text");
				if (text == null) text = "";
			  else text = text.trim();
				myAlliance.setAttr(Attribute.ALLIANCE_PLAINTEXT, text);
			}
		});
	
	ActionHandler.add("Alliance.Name.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.CHANGE_NAME);
				String name = req.getParameter("name");
				String shortName = req.getParameter("short");
				if (name == null) name = myAlliance.getName();
				if (shortName == null) shortName = myAlliance.getShortName();
				
				if (!InputValidation.validAllianceName(name)) return;
				if (!InputValidation.validAllianceShort(shortName)) return;
				
				Alliance a1 = session.getGalaxy().findAllianceByName(name);
				Alliance a2 = session.getGalaxy().findAllianceByName(shortName);
				if (((a1 == null) || (a1 == myAlliance)) && 
				    ((a2 == null) || (a2 == myAlliance)))
				{
					myAlliance.setName(name);
					myAlliance.setShortName(shortName);
				}
			}
		});
	
	ActionHandler.add("Alliance.Logo.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.EDIT_DESCRIPTION);
				String text = req.getParameter("server");
				if (text == null || text.length() == 0)
				{
					myAlliance.setAttr(Attribute.ALLIANCE_LOGO, "");
					return;
				}
				
				try
				{
					new URL(text);
				}
				catch (MalformedURLException e)
				{ return; }
				
				int i = text.lastIndexOf('.');
				String extension = text.substring(i).toLowerCase(Locale.US);
				if (extension.equals(".jpg") || extension.equals(".jpeg") ||
				    extension.equals(".gif") || extension.equals(".png"))
					myAlliance.setAttr(Attribute.ALLIANCE_LOGO, text);
			}
		});
	
	ActionHandler.add("Alliance.Ranks.edit", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.EDIT_RANKS);
				for (int i = 0; i < myAlliance.getRanks().size(); i++)
				{
					String newname = req.getParameter("name"+i);
					if (!InputValidation.validPlayerName(newname)) throw new ErrorCodeException();
					EnumSet<RightEnum> allianceRights = EnumSet.noneOf(RightEnum.class);
					for (RightEnum right : RightEnum.values())
						if ("on".equals(req.getParameter("ar"+i+"-"+right.name())))
							allianceRights.add(right);
					if (i != 0)
						myAlliance.getRanks().get(i).set(newname, allianceRights);
				}
			}
		});
	
	ActionHandler.add("Alliance.Ranks.add", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.EDIT_RANKS);
				String newname = req.getParameter("name");
				if (!InputValidation.validPlayerName(newname)) throw new ErrorCodeException();
				EnumSet<RightEnum> allianceRights = EnumSet.noneOf(RightEnum.class);
				for (RightEnum right : RightEnum.values())
					if ("on".equals(req.getParameter("ar"+right.name())))
						allianceRights.add(right);
				myAlliance.getRanks().add(new Rank(newname, allianceRights));
			}
		});
	
	ActionHandler.add("Alliance.Ranks.change", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.CHANGE_RANKS);
				for (int i = 0; i < myAlliance.size(); i++)
				{
					try
					{
						Player pl = myAlliance.get(i);
						String newRank = req.getParameter("r"+pl.getName());
						if (newRank != null)
						{
							int j = newRank.indexOf('.');
							newRank = newRank.substring(0, j);
							int erg = Integer.parseInt(newRank)-1;
							if ((erg >= 0) && (erg < myAlliance.getRanks().size()))
								pl.setRank(myAlliance.getRanks().get(erg));
							else
								pl.setRank(myAlliance.getStartRank());
						}
					}
					catch (Exception e)
					{ e.printStackTrace(); }
				}
				try
				{
					String newRank = req.getParameter("defaultRank");
					if (newRank != null)
					{
						int j = newRank.indexOf('.');
						newRank = newRank.substring(0, j);
						int erg = Integer.parseInt(newRank)-1;
						if ((erg >= 0) && (erg < myAlliance.getRanks().size()))
							myAlliance.setStartRank(myAlliance.getRanks().get(erg));
					}
				}
				catch (Exception e)
				{ e.printStackTrace(); }
			}
		});
	
	ActionHandler.add("Alliance.kick", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, final HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.KICK_MEMBER);
				Iterator<Player> it = myAlliance.iterator();
				ArrayList<Player> kickPlayers = new ArrayList<Player>();
				while (it.hasNext())
				{
					final Player p = it.next();
					String shouldKick = req.getParameter("k"+p.getPid());
					if ("on".equals(shouldKick))
						kickPlayers.add(p);
				}
				for (Player p: kickPlayers)
					myAlliance.remove(p);
			}
		});
	
	ActionHandler.add("Alliance.vote", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.getAlliance();
				if ((myAlliance != null) && (!session.isMulti()))
				{
					int vote = Integer.parseInt(req.getParameter("vote"));
					int i = myAlliance.findPlayerById(vote)+1;
					myAlliance.changeVote(session.getPlayer(), i);
				}
			}
		});
	
	ActionHandler.add("Alliance.Survey.vote", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.MAY_SURVEY);
				String id = req.getParameter("id");
				String vote = req.getParameter("vote");
				Survey survey = myAlliance.getSurvey();
				if ((survey != null) && (id != null) && (vote != null))
				{
					int i = net.cqs.main.config.Input.decode(id, -1);
					int j = session.getPlayer().getAttr(Attribute.ALLIANCE_SURVEY).intValue();
					if ((i == survey.getId()) && (j != survey.getId()))
					{
						survey.addVote(net.cqs.main.config.Input.decode(vote, -1));
						session.getPlayer().setAttr(Attribute.ALLIANCE_SURVEY, Integer.valueOf(survey.getId()));
					}
				}
			}
		});
	
	ActionHandler.add("Alliance.Survey.start", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.START_SURVEY);
				String title = req.getParameter("title");
				if (title != null)
				{
					Survey newSurvey = new Survey(session.getGalaxy().createSurveyId(), title);
					for (int i = 0; i < 10; i++)
					{
						String text = req.getParameter("c"+i);
						if (text != null)
						{
							text = text.trim();
							if (text.length() != 0)
								newSurvey.addText(Galaxy.defaultTextConverter.convert(text));
						}
					}
					if (newSurvey.length() > 0)
						myAlliance.setSurvey(newSurvey);
				}
			}
		});
	
	ActionHandler.add("Alliance.Survey.stop", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Alliance myAlliance = session.checkAllianceRight(RightEnum.START_SURVEY);
				myAlliance.setSurvey(null);
			}
		});
	
	ActionHandler.add("Alliance.leave", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				if ((session.getPlayer().getAlliance() != null) && (!session.isMulti()))
					session.getPlayer().getAlliance().remove(session.getPlayer());
			}
		});
}

private PostAlliance()
{/*OK*/}

}
