package net.cqs.web.game.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.config.InputValidation;
import net.cqs.engine.Colony;
import net.cqs.engine.Player;
import net.cqs.engine.actions.PlayerColonyStartAction;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.MessageState;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.main.persistence.PlayerData;
import net.cqs.services.AuthService;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostPlayer
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

static void init()
{
	ActionHandler.add("Player.Settings.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				logger.fine("Setting Settings for Player "+who.getName()+":");
				
				boolean autoReturn = "on".equals(req.getParameter("autoReturn"));
				who.setAttr(Attribute.AUTO_RETURN, Boolean.valueOf(autoReturn));
				logger.fine("Auto-Return: "+autoReturn);
				
				
				boolean autoStation = "on".equals(req.getParameter("autoStation"));
				who.setAttr(Attribute.AUTO_STATION, Boolean.valueOf(autoStation));
				logger.fine("Auto-Station: "+autoStation);
				
				
				boolean cssInGP = "on".equals(req.getParameter("cssInGP"));
				who.setAttr(Attribute.CSS_IN_GP, Boolean.valueOf(cssInGP));
				logger.fine("CSS in GP: "+cssInGP);
				
				
				String sgp = req.getParameter("graphicpath");
				if ((sgp == null) || (sgp.length() == 0))
					who.removeAttr(Attribute.GRAPHIC_PATH);
				else
					who.setAttr(Attribute.GRAPHIC_PATH, sgp);
				
				
				session.resetGraphicPath();
				logger.fine("Graphic-Path: "+sgp);
				
				session.flag = 1;
			}
		});
	
	ActionHandler.add("Player.Settings.Account.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				boolean forwardEmail = "on".equals(req.getParameter("forwardEmail"));
				session.getPlayer().setAttr(Attribute.FORWARD_EMAIL, Boolean.valueOf(forwardEmail));
			}
		});
	
	ActionHandler.add("Player.Settings.Game.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				boolean autoStopUnits = "on".equals(req.getParameter("autoStopUnits"));
				session.getPlayer().setAutoStopUnits(autoStopUnits);
				logger.fine("AutoStopUnits: "+autoStopUnits);
			}
		});
	
	ActionHandler.add("Player.Settings.Ui.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				logger.fine("Setting Settings for Player "+who.getName()+":");
				
				boolean autoReturn = "on".equals(req.getParameter("autoReturn"));
				who.setAttr(Attribute.AUTO_RETURN, Boolean.valueOf(autoReturn));
				logger.fine("Auto-Return: "+autoReturn);
				
				boolean autoStation = "on".equals(req.getParameter("autoStation"));
				who.setAttr(Attribute.AUTO_STATION, Boolean.valueOf(autoStation));
				logger.fine("Auto-Station: "+autoStation);
				
				final boolean autoAjax = "on".equals(req.getParameter("autoAjax"));
				final int pid = who.getPid();
				session.execute(new Task()
					{
						@Override
						public void run()
						{ PlayerData.getPlayerData(pid).setAjax(autoAjax); }
					});
				logger.fine("Auto-Ajax: "+autoAjax);
				
				boolean advancedCSS = "on".equals(req.getParameter("advancedCSS"));
				who.setAttr(Attribute.ADVANCED_CSS, Boolean.valueOf(advancedCSS));
				logger.fine("Advanced CSS: "+advancedCSS);
				
				String buildingPreset = req.getParameter("buildingPreset");
				if ((buildingPreset == null) || (buildingPreset.length() == 0))
					buildingPreset = "";
				else
					buildingPreset = Integer.toString(Integer.parseInt(buildingPreset));
				who.setAttr(Attribute.BUILDING_PRESET, buildingPreset);
				
				String unitPreset = req.getParameter("unitPreset");
				if ((unitPreset == null) || (unitPreset.length() == 0))
					unitPreset = "";
				else
					unitPreset = Integer.toString(Integer.parseInt(unitPreset));
				who.setAttr(Attribute.UNIT_PRESET, unitPreset);
			}
		});
	
	ActionHandler.add("Player.Settings.Gp.set", new PostHandler()
			{
				@Override
				public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
				{
					Player who = session.getPlayer();
					logger.fine("Setting Graphic-Pack for Player "+who.getName()+":");
					
					boolean cssInGP = "on".equals(req.getParameter("cssInGP"));
					who.setAttr(Attribute.CSS_IN_GP, Boolean.valueOf(cssInGP));
					logger.fine("CSS in GP: "+cssInGP);
					
					String sgp = req.getParameter("graphicpath");
					if ((sgp == null) || (sgp.length() == 0))
						who.removeAttr(Attribute.GRAPHIC_PATH);
					else
						who.setAttr(Attribute.GRAPHIC_PATH, sgp);
					
					session.resetGraphicPath();
					logger.fine("Graphic-Path: "+sgp);
				}
			});
	
	ActionHandler.add("Player.Settings.Password.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				String pass0, pass1, pass2;
				pass0 = req.getParameter("setpass0");
				pass1 = req.getParameter("setpass1");
				pass2 = req.getParameter("setpass2");
				session.flag = 0;
				if ((pass0 != null) && (pass1 != null) && (pass2 != null))
				{
					logger.fine("Attempting to Change Password for "+who.getName());
					if (!InputValidation.validPassword(pass1))
					{
						logger.fine("Nix is!");
						return;
					}
					if (who.getPid() == 0)
					{
						logger.fine("Gast-Passwort kann nicht geaendert werden!");
						return;
					}
					if (pass1.equals(pass2))
					{
						AuthService authService = session.getFrontEnd().findService(AuthService.class);
						boolean validated = authService.authenticate(who.getPrimaryIdentity().toString(), pass0) != null;
						if (validated)
						{
							boolean changed = authService.changePassword(who.getPrimaryIdentity(), pass1);
							session.flag = changed ? 1 : 0;
							logger.fine("Password Changed Successfully!");
						}
					}
					else
					{
						logger.fine("Falsch!");
						return;
					}
				}
			}
		});
	
	ActionHandler.add("Player.Settings.Language.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				logger.fine("Setting Language for Player "+who.getName()+":");
				
				String language = req.getParameter("language");
				Locale locale = session.getFrontEnd().getLocaleProvider().filter(language);
				session.setLocale(locale);
//				who.setLocale(locale);
				session.check();
				logger.fine("Language: "+language);
			}
		});
	
/*	Temporary.add("Player.Settings.IRC.set", new PostHandler()
		{
			public void activate(Temporary temp, Request req)
			{
				final int max_length = 12;
				String nickname = req.getParameter("nickname");
				String password = req.getParameter("password");
				boolean detach = (null != req.getParameter("detach"));
				String userId = IRCInterface.userId(temp.who.name); 
				if ((nickname != null) && (password != null) && IRCInterface.ready(userId))
				{
					if (!((nickname.length() > max_length) || (password.length() > max_length)))
					{
						temp.who.attributes.setStringValue("IRC_nickname", nickname);
						temp.who.attributes.setStringValue("IRC_password", password);
						temp.who.attributes.setBooleanValue("IRC_detach", detach);
						
						IRCInterface.resetUser(userId, password);
						IRCInterface.grantAccess(userId, "#beta", IRCInterface.operatorAccess);
					}
					else
					{
						if (nickname.length() > max_length)
							temp.who.log(Error.irc_nickname_too_long, nickname.length(), max_length);
						else
							temp.who.log(Error.irc_password_too_long, password.length(), max_length);
					}
				}
			}
		});*/
	
	ActionHandler.add("Player.Message.send", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String target = req.getParameter("target");
				String subject = req.getParameter("subject");
				if (subject == null) subject = "none";
				String body = req.getParameter("body");
				if ((target != null) && (body != null) && (!session.isRestricted()))
				{
					logger.fine("Sending Message to "+target);
					Player targetPlayer = session.getGalaxy().findPlayerByPid(Integer.parseInt(target));
					if (targetPlayer != null)
					{
						Message m = new Message(PlayerMessageType.MESSAGE, session.getPlayer(), targetPlayer, subject, body);
						
						session.dropMail(targetPlayer, m);
						m = new Message(m);
						m.setState(MessageState.SENT);
						session.dropMail(session.getPlayer(), m);
						
						session.flag = 1;
					}
				}
				else
				{
					if (session.isRestricted())
						session.log(ErrorCode.RESTRICTED_ACCESS);
					if (body == null)
						session.log(ErrorCode.MESSAGE_NO_BODY);
					if (target == null)
						session.log(ErrorCode.MESSAGE_NO_TARGET);
				}
			}
		});
	
	ActionHandler.add("Player.singleplayer", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player player = session.getPlayer();
				if (player.getColonies().size() == 0)
				{
					session.getGalaxy().schedex(new PlayerColonyStartAction(player));
					if (player.getColonies().size() == 0)
						session.log(ErrorCode.NO_MORE_COLONIES);
				}
			}
		});
	
	ActionHandler.add("Player.Account.delete", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String name = req.getParameter("name");
				String pass0 = req.getParameter("pass0");
				String pass1 = req.getParameter("pass1");
				if ((name != null) && (pass0 != null) && (pass1 != null))
				{
					if ((name.equals(session.getPlayer().getName())) && (pass1.equals(pass0)))
					{
						AuthService authService = session.getFrontEnd().findService(AuthService.class);
						boolean validated = authService.authenticate(session.getPlayer().getPrimaryIdentity().toString(), pass1) != null;
						if (validated)
						{
							logger.fine("Player deleted!");
							session.flag = 1;
							session.getPlayer().setAttr(Attribute.LOGIN_ALLOWED, Boolean.FALSE);
							session.getPlayer().setInvisible(true);
						}
					}
				}
			}
		});

	ActionHandler.add("Player.memorizedPlayers.add", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			final int toadd = Integer.parseInt(req.getParameter("who"));
			final int pid = session.getPlayer().getPid();
			session.execute(new Task()
			{
				@Override
				public void run()
				{
					PlayerData.getPlayerData(pid).addPlayerName(toadd);
				}
			});
		}
	});

	ActionHandler.add("Player.memorizedPlayers.remove", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			final int toremove = Integer.parseInt(req.getParameter("index"));
			final int pid = session.getPlayer().getPid();
			session.execute(new Task()
			{
				@Override
				public void run()
				{
					PlayerData.getPlayerData(pid).removePlayerName(toremove);
				}
			});
		}
	});
	
	ActionHandler.add("Player.Colonies.sort", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String waytosort = req.getParameter("sort");
				String inverse = req.getParameter("inverse");
				logger.fine("Sorting Colonies for Player "+session.getPlayer().getName()+" ("+waytosort+", "+inverse+")");
				
				Comparator<Colony> cc = null;
				
				if ("points".equals(waytosort))
					cc = new Comparator<Colony>()
						{
							@Override
              public int compare(Colony c1, Colony c2)
							{
								if (c1.getPoints() < c2.getPoints()) return -1;
								if (c1.getPoints() > c2.getPoints()) return 1;
								return 0;
							}
						};
				
				if ("age".equals(waytosort))
					cc = new Comparator<Colony>()
						{
							@Override
              public int compare(Colony c1, Colony c2)
							{
								if (c1.getStartTime() < c2.getStartTime()) return -1;
								if (c1.getStartTime() > c2.getStartTime()) return 1;
								return 0;
							}
						};
				
				if ("name".equals(waytosort))
					cc = new Comparator<Colony>()
						{
							@Override
              public int compare(Colony c1, Colony c2)
							{ return c1.getName().compareToIgnoreCase(c2.getName()); }
						};
				
				if ("coords".equals(waytosort))
					cc = new Comparator<Colony>()
						{
							@Override
              public int compare(Colony c1, Colony c2)
							{ return c1.getPosition().compareTo(c2.getPosition()); }
						};
				
				if (cc != null)
				{
					Collections.sort(session.getPlayer().getColonies(), cc);
					if ("on".equals(inverse))
						Collections.reverse(session.getPlayer().getColonies());
				}
				session.flag = 1;
			}
		});
	
	ActionHandler.add("Player.Language.set", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				String language = req.getParameter("language");
				Locale locale = session.getFrontEnd().getLocaleProvider().filter(language);
				who.setLocale(locale);
				session.setLocale(locale);
			}
		});
	
	ActionHandler.add("Player.Profile.set", new PostHandler()
		{
			@Override			
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				
				String text = req.getParameter("text");
				if (text == null) text = "";
				text = text.trim();
				who.setAttr(Attribute.PLAYER_PLAINTEXT, text);
				
				text = req.getParameter("server");
				if (text == null || text.length() == 0)
				{
					who.setAttr(Attribute.PLAYER_LOGO, "");
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
					who.setAttr(Attribute.PLAYER_LOGO, text);
			}
		});

	ActionHandler.add("Player.Desktop.rename", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				Player who = session.getPlayer();
				
				int id = Integer.parseInt(req.getParameter("id"));
				String name = req.getParameter("name");
				who.setDesktopName(id, name);
			}
		});

	ActionHandler.add("Player.Desktop.setDefault", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			Player who = session.getPlayer();
			
			int id = Integer.parseInt(req.getParameter("id"));
			who.setDefaultDesktop(id);
		}
	});

	ActionHandler.add("Player.Colony.resumeAll", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			Player who = session.getPlayer();
			for (int i = 0; i < who.getColonies().size(); i++)
			{
				Colony c = who.getColonies().get(i);
				c.resumeBuilding();
				for (int queue = 0; queue < Colony.UNIT_QUEUES; queue++)
					c.resumeUnit(queue);
			}
		}
	});

}

private PostPlayer()
{/*OK*/}

}
