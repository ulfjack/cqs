package net.cqs.web.frontpage;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.WebPostAction;

public final class ReminderPlugin
{

private static final Logger logger = Logger.getLogger("net.cqs.web.frontpage");

public ReminderPlugin()
{/*OK*/}

@Deprecated
private static boolean sendReminder(FrontEnd frontEnd, Player p, String msg)
{
	;
/*	try
	{
		AuthService authService = frontEnd.findService(AuthService.class);
		String email = authService.getEmail(p.getPrimaryIdentity());
		if (email == null)
		{
			logger.fine("No email found for "+p);
			return false;
		}
		logger.fine("Sending email...");
		SmtpAddress sender = new SmtpAddress(frontEnd.FROM_MAIL);
		SmtpAddress recipient = new SmtpAddress(email);
		String pwd = authService.getPassword(p.getPrimaryIdentity());
		Email em = EmailEnum.REMINDER.generateEmail(Locale.GERMANY, sender, recipient, frontEnd.VERSION, p.loginId, pwd, p.name);
		em.setBody(em.getBody()+"\n\n"+msg+"\n");
		frontEnd.findService(SmtpService.class).sendEmail(em);
		return true;
	}
	catch (Exception e)
	{ e.printStackTrace(); }*/
	return false;
}

@WebPostAction("remind")
public void remind(FrontEnd frontEnd, IdSession session, HttpServletRequest req)
{
	String user;
	user = req.getParameter("user");
	if (user != null)
	{
		logger.fine("Attempting to Remind User "+user);
		Galaxy galaxy = frontEnd.getGalaxy();
		synchronized (galaxy)
		{
			Player pl = galaxy.findPlayerByPid(Integer.parseInt(user));
			if (pl == null)
				pl = galaxy.findPlayerByName(user);
			
			if (pl != null)
			{
				if (sendReminder(frontEnd, pl, ""))
					session.getData().reminded = true;
			}
		}
	}
}

}
