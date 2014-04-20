package net.cqs.plugins;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.PlayerMailListener;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.StartupListener;
import net.cqs.services.AuthService;
import net.cqs.services.SmtpService;
import net.cqs.services.email.Email;
import net.cqs.util.TextConverter;

@Plugin
public class SmtpGate implements StartupListener
{

	static class EmailTextConverter implements TextConverter
 	{
 		@Override
    public String convert(String text)
 		{ return text; }
 	}

private static final Logger logger = Logger.getLogger("net.cqs.plugins.SmtpGate");
private static final EmailTextConverter EMAIL_CONVERTER = new EmailTextConverter();

private final FrontEnd frontEnd;

public SmtpGate(PluginConfig config)
{ frontEnd = config.getFrontEnd(); }

@Override
public void startup()
{
	frontEnd.addMailListener(new PlayerMailListener()
		{
			@Override
      public void notifyMail(Player who, Message m)
			{
				if (who.getAttr(Attribute.FORWARD_EMAIL).booleanValue() && (m.getType() == PlayerMessageType.MESSAGE))
				{
					try
					{
						AuthService authService = frontEnd.findService(AuthService.class);
						String email = authService.getEmail(who.getPrimaryIdentity());
						if (email == null)
						{
							logger.fine("Forwarding to email failed. "+who+" has no email adress.");
							return;
						}
						logger.fine("Forwarding message for "+who+" to email "+email);
						
						Player sender = m.getSender(frontEnd.getGalaxy());
						String subject = m.getSubject(EMAIL_CONVERTER);
						String text = m.getText(EMAIL_CONVERTER);
						Email em = new Email(email);
			      em.setSubject("[Conquer-Space.net] "+subject);
			      em.setBody(sender.getName()+" schreibt an "+who.getName()+":\n\n"+text+"\n");
			      frontEnd.findService(SmtpService.class).sendEmail(em);
    			}
					catch (Exception e)
					{
						logger.log(Level.SEVERE, "Exception caught", e);
					}
				}
			}
		});
}

}
