package net.cqs.services.email;

public final class Email
{

private final SmtpAddress recipient;
private String subject;
private String body;

public Email(String recipient)
{
	this.recipient = new SmtpAddress(recipient);
}

public Email(Email other, String newtarget)
{
	this.recipient = new SmtpAddress(newtarget);
	this.setSubject(other.getSubject());
	this.setBody(other.getBody());
}

public String getRecipient()
{ return recipient.toString(); }

public void setSubject(String subject)
{ this.subject = subject; }

public String getSubject()
{ return subject; }

public void setBody(String body)
{ this.body = body; }

public String getBody()
{ return body; }

}
