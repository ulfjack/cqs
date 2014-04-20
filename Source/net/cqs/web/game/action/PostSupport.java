package net.cqs.web.game.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.config.LogEnum;
import net.cqs.services.SmtpService;
import net.cqs.services.email.Email;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostSupport
{

static void init()
{
	ActionHandler.add("Support.send", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				if (session.isRestricted())
					throw new ErrorCodeException(ErrorCode.RESTRICTED_ACCESS);
				FrontEnd frontEnd = session.getFrontEnd();
				session.flag = 0;
				Email em = new Email(frontEnd.getSupportEmail());
				em.setSubject("[CqS-Support] "+req.getParameter("subject"));
				em.setBody("Support-Request von "+session.getPlayer().getName()+"\n");
				em.setBody(em.getBody() + ("Runde: "+frontEnd.version()+"\n\n"));
				em.setBody(em.getBody() + (req.getParameter("body0")+req.getParameter("body1")));
				em.setBody(em.getBody() + (req.getParameter("body2")+req.getParameter("body3")));
				em.setBody(em.getBody() + (req.getParameter("body4")+req.getParameter("body5")));
				em.setBody(em.getBody() + (req.getParameter("body6")+req.getParameter("body7")));
				em.setBody(em.getBody() + (req.getParameter("body8")+req.getParameter("body9")));
				em.setBody(em.getBody() + (req.getParameter("bodyA")+req.getParameter("bodyB")));
				em.setBody(em.getBody() + (req.getParameter("bodyC")+req.getParameter("bodyD")));
				em.setBody(em.getBody() + (req.getParameter("bodyE")+req.getParameter("bodyF")));
				em.setBody(em.getBody() + (req.getParameter("bodyG")+req.getParameter("bodyH")));
				em.setBody(em.getBody() + (req.getParameter("bodyI")+req.getParameter("bodyJ")));
				em.setBody(em.getBody() + (req.getParameter("bodyK")+req.getParameter("bodyL")));
				em.setBody(em.getBody() + (req.getParameter("bodyM")+req.getParameter("bodyN")));
				em.setBody(em.getBody() + (req.getParameter("bodyO")+req.getParameter("bodyP")));
				em.setBody(em.getBody() + (req.getParameter("bodyQ")+req.getParameter("bodyR")));
				em.setBody(em.getBody() + "\n\n");
				em.setBody(em.getBody().replaceAll("<br>", "\n"));
				try
				{
					File f = new File(frontEnd.getLogPath(LogEnum.SUPPORT));
					if (!f.exists()) f.createNewFile();
					FileOutputStream fout = new FileOutputStream(f, true);
					PrintStream pout = new PrintStream(fout);
					pout.println("From "+session.getPlayer().getName());
					pout.println("Subject: "+em.getSubject());
					pout.println();
					pout.println(em.getBody());
					pout.flush();
					pout.close();
					
					frontEnd.findService(SmtpService.class).sendEmail(em);
					session.flag = 1;
				}
				catch (Exception e)
				{
					ActionHandler.logger.log(Level.SEVERE, "Exception caught", e);
				}
			}
		});
}

private PostSupport()
{/*OK*/}

}
