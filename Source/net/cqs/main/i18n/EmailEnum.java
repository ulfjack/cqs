package net.cqs.main.i18n;

import java.util.Locale;
import java.util.logging.Level;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;
import net.cqs.main.config.FrontEnd;
import net.cqs.services.email.Email;
import net.cqs.services.email.SmtpAddress;

public enum EmailEnum
{

APPLICATION("[Conquer-Space.net] Login Information", "%4$s,\\n\\n welcome to Conquer-Space.net!\\n Here is your account data for Conquer-Space.net %1$s:\\n Login: %2$s \\n Password: %3$s \\n \\n Your player data: \\n Nickname: %4$s \\n\\n -- your Conquer-Space.net team"),
REGISTRATION("[Conquer-Space.net] Registration", "You registered for an account on Conquer-Space.net!\\n To complete your registration please click the following link:\\n %1$s/register?regid=%2$s \\n Or enter the following ID on the registration page:\\n %2$s \\n\\n -- your Conquer-Space.net team"),
INVITATION("[Conquer-Space.net] You were invited to Conquer-Space.net", "Someone invited you to Conquer-Space.net.\\n The url is %s and the player with id %s invited you.\\n\\n-- your Conquer-Space.net team"),
REMINDER("[Conquer-Space.net] Password reminder", "%4$s,\\n\\n this is your account data for Conquer-Space.net %1$s:\\n Login: %2$s \\n Password: %3$s \\n \\n Your player data: \\n Nickname: %4$s \\n\\n -- your Conquer-Space.net team"),
MESSAGE("[Conquer-Space.net] Message", "You receive this message, because you have registered with Conquer-Space.net %1$s!\\n");

private final String englishSubject;
private final String englishBody;

private EmailEnum(String englishSubject, String englishBody)
{
	this.englishSubject = englishSubject;
	this.englishBody = englishBody;
}

public String englishTranslation()
{ return englishSubject; }

public String englishDescription()
{ return englishBody; }

private static String bundleName()
{ return "net.cqs.i18n.EmailEnum"; }

public Email generateEmail(Locale locale, SmtpAddress recipient, Object... params)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	String subject = bundle.getSafeString(englishTranslation());
	String body = bundle.getSafeString(englishDescription());
	
	StringBuffer temp = new StringBuffer();
	temp.append(this).append('(');
	for (int i = 0; i < params.length; i++)
	{
		if (i != 0) temp.append(",");
		temp.append(params[i]);
	}
	temp.append(')');
	String message = temp.toString();
	String i18nmessage = message;
	try
	{
		i18nmessage = String.format(locale, body, params);
		i18nmessage = i18nmessage.replace("\\n", "\n");
	}
	catch (Exception e)
	{ FrontEnd.logger.log(Level.SEVERE, "Exception ignored for \""+message+"\"", e); }
	
	Email email = new Email(recipient.toString());
	email.setSubject(subject);
	email.setBody(i18nmessage);
	
	return email;
}

}
