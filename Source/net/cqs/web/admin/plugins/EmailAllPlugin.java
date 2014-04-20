package net.cqs.web.admin.plugins;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import net.cqs.engine.Player;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.EmailEnum;
import net.cqs.services.AuthService;
import net.cqs.services.SmtpService;
import net.cqs.services.email.Email;
import net.cqs.services.email.SmtpAddress;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;

public final class EmailAllPlugin
{

public EmailAllPlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("System", "Email All", "email-all.html", actions);
}

@WebPostAction("Email.sendAll")
public void createEvent(FrontEnd frontEnd, PrintWriter out,
		@Parameter("body") String body)
{
	out.println("Sending email to:");
	AuthService authService = frontEnd.findService(AuthService.class);
	SmtpService smtpService = frontEnd.findService(SmtpService.class);
	String version = frontEnd.version();
	HashSet<String> knownEmails = new HashSet<String>();
	List<Player> players = frontEnd.getGalaxy().getPlayers();
	for (int i = 0; i < players.size(); i++)
	{
		Player tempWho = players.get(i);
		String email = authService.getEmail(tempWho.getPrimaryIdentity());
		if (email == null)
			out.println(tempWho.getName()+": no email found");
		else if (!knownEmails.contains(email))
		{
			out.println(tempWho.getName()+": "+email);
			SmtpAddress recipient = new SmtpAddress(email);
			Email em = EmailEnum.MESSAGE.generateEmail(Locale.GERMANY, recipient, version);
			em.setBody(em.getBody()+"\n\n"+body+"\n");
			smtpService.sendEmail(em);
			knownEmails.add(email);
		}
	}
}

}
