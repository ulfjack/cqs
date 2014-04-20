package net.cqs.web.admin.plugins;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.base.Survey;
import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.MultiI18nMessage;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;

public final class SystemPlugin
{

public SystemPlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("System", "Display Message", "system-message-direct.html", actions);
	service.addPage("System", "Send Message", "system-message-send.html", actions);
	service.addPage("System", "Survey", "system-survey.html", actions);
}

@WebPostAction("System.clearMessage")
public void clearSystemMessage(FrontEnd frontEnd)
{ frontEnd.getGalaxy().setInstantMessage(null); }

@WebPostAction("System.displayMessage")
public void displaySystemMessage(FrontEnd frontEnd,
		@Parameter("locale") String language,
		@Parameter("translation") String translation)
{
	Locale locale = frontEnd.getLocaleProvider().filter(language);
	MultiI18nMessage message = frontEnd.getGalaxy().getInstantMessage();
	if (message == null)
	{
		message = new MultiI18nMessage(locale);
		frontEnd.getGalaxy().setInstantMessage(message);
	}
	translation = Galaxy.defaultTextConverter.convert(translation);
	message.addI18n(locale, translation);
}

@WebPostAction("System.sendMessage")
public void displaySystemMessage(FrontEnd frontEnd, PrintWriter out,
		@Parameter("igm-all") boolean sendAll,
		@Parameter("nick") String nick,
		@Parameter("subject") String subject,
		@Parameter("body") String body)
{
	final Galaxy galaxy = frontEnd.getGalaxy();
	if (sendAll)
	{
		synchronized (galaxy)
		{
			List<Player> players = galaxy.getPlayers();
			for (int i = 0; i < players.size(); i++)
			{
				Player p = players.get(i);
				Message msg = new Message(PlayerMessageType.SYSTEM, p, frontEnd.getDefaultLocale(), subject, body);
				frontEnd.dropMail(p, msg);
				out.println(p.getName());
			}
		}
	}
	else
	{
		synchronized (galaxy)
		{
			Player p = null;
			if ((nick != null) && (nick.length() != 0))
				p = galaxy.findPlayerByName(nick);
			if (p != null)
			{
				Message msg = new Message(PlayerMessageType.SYSTEM, p, frontEnd.getDefaultLocale(), subject, body);
				frontEnd.dropMail(p, msg);
				out.println(p.getName());
			}
		}
	}
}

private String conv(String text)
{
	if (text != null)
	{
		text = text.trim();
		if (text.length() != 0)
			return Galaxy.defaultTextConverter.convert(text);
	}
	return null;
}

@WebPostAction("System.endSurvey")
public void endSurvey(FrontEnd frontEnd, PrintWriter out)
{
	frontEnd.getGalaxy().setSurvey(null);
}

@WebPostAction("System.startSurvey")
public void startSurvey(FrontEnd frontEnd, PrintWriter out,
		@Parameter("title") String title,
		@Parameter("c0") String c0,
		@Parameter("c1") String c1,
		@Parameter("c2") String c2,
		@Parameter("c3") String c3,
		@Parameter("c4") String c4,
		@Parameter("c5") String c5,
		@Parameter("c6") String c6,
		@Parameter("c7") String c7,
		@Parameter("c8") String c8,
		@Parameter("c9") String c9)
{
	if (title != null)
	{
		Survey newSurvey = new Survey(frontEnd.getGalaxy().createSurveyId(), title);
		String text;
		text = conv(c0); if (text != null) newSurvey.addText(text);
		text = conv(c1); if (text != null) newSurvey.addText(text);
		text = conv(c2); if (text != null) newSurvey.addText(text);
		text = conv(c3); if (text != null) newSurvey.addText(text);
		text = conv(c4); if (text != null) newSurvey.addText(text);
		text = conv(c5); if (text != null) newSurvey.addText(text);
		text = conv(c6); if (text != null) newSurvey.addText(text);
		text = conv(c7); if (text != null) newSurvey.addText(text);
		text = conv(c8); if (text != null) newSurvey.addText(text);
		text = conv(c9); if (text != null) newSurvey.addText(text);
		if (newSurvey.length() > 0)
			frontEnd.getGalaxy().setSurvey(newSurvey);
	}
}

/*public void init()
{
	add(new DynamicFileAdapter("System", "Kommission Survey", this,
"system-survey.html")
		{
			@Override
			protected void startRequest(StringMap postData, StringMap getData)
			{
				actionResult = "";
				
				String action = postData.get("action");
				if (action == null) return;
				
				if (action.equals("Beenden"))
				{
					manager.getFrontEnd().getGalaxy().metaGalaxy.kommission = null;
					return;
				}
				
				String title = postData.get("title");
				if (title != null)
				{
					Survey newSurvey = new Survey(manager.getFrontEnd().getGalaxy().metaGalaxy.getSurveyID(), title);
					for (int i = 0; i < 10; i++)
					{
						String text = postData.get("c"+i);
						if (text != null)
						{
							text = text.trim();
							if (text.length() != 0)
								newSurvey.addText(FrontEnd.defaultTextConverter.convert(text));
						}
					}
					if (newSurvey.length() > 0)
						manager.getFrontEnd().getGalaxy().metaGalaxy.kommission = newSurvey;
				}
			}
		});
}*/

}
