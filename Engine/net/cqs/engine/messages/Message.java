package net.cqs.engine.messages;

import java.io.Serializable;
import java.util.Locale;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.util.TextConverter;
import net.cqs.util.UIDGenerator;
import de.ofahrt.ulfscript.annotations.HtmlFragment;

public final class Message implements Serializable
{

private static final long serialVersionUID = 1L;

private static UIDGenerator uidGenerator = new UIDGenerator();

private static String generateUniqueID()
{ return uidGenerator.generateUniqueID(); }


private final String id;
private final long realtime;
private final int sender;
private final int recipient;
private final Locale locale;
private MessageState state = MessageState.UNREAD;
private final PlayerMessageType type;
private final int typeInfo;
private final String subject;
private final String text;

public Message(PlayerMessageType type, int typeInfo, Player sender, Player recipient,
		Locale locale, String subject, String text)
{
	if (type == null) throw new NullPointerException();
	if ((text == null) && (subject == null)) throw new IllegalArgumentException();
	if (locale == null) throw new NullPointerException();
	this.id = generateUniqueID();
	this.type = type;
	this.typeInfo = typeInfo;
	this.realtime = System.currentTimeMillis();
	this.sender = sender == null ? -1 : sender.getPid();
	this.recipient = recipient == null ? -1 : recipient.getPid();
	this.locale = locale;
	this.subject = subject == null ? "" : subject;
	this.text = text == null ? "" : text;
}

public Message(PlayerMessageType type, int typeInfo, Player sender, Player recipient, String subject, String text)
{ this(type, typeInfo, sender, recipient, sender.getLocale(), subject, text); }

public Message(PlayerMessageType type, Player sender, Player recipient, String subject, String text)
{ this(type, -1, sender, recipient, sender.getLocale(), subject, text); }

public Message(PlayerMessageType type, Player recipient, Locale locale, String subject, String text)
{ this(type, -1, null, recipient, locale, subject, text); }

// for testing only
Message(PlayerMessageType type, int sender, int recipient, long realtime, Locale locale, String subject, String text)
{
	this.id = generateUniqueID();
	this.type = type;
	this.typeInfo = -1;
	this.realtime = realtime;
	this.sender = sender;
	this.recipient = recipient;
	this.locale = locale;
	this.subject = subject;
	this.text = text;
}

// copy constructor
public Message(Message other)
{
	this.id = generateUniqueID();
	this.realtime = other.realtime;
	this.sender = other.sender;
	this.recipient = other.recipient;
	this.locale = other.locale;
	this.state = other.state;
	this.type = other.type;
	this.typeInfo = other.typeInfo;
	this.subject = other.subject;
	this.text = other.text;
}

public String getId()
{ return id; }

public long getRealtime()
{ return realtime; }

public Player getSender(Galaxy galaxy)
{ return galaxy.findPlayerByPid(sender); }

int getSenderId()
{ return sender; }

public Player getRecipient(Galaxy galaxy)
{ return galaxy.findPlayerByPid(recipient); }

int getRecipientId()
{ return recipient; }

public Locale getLocale()
{ return locale; }

public MessageState getState()
{ return state; }

public void setState(MessageState state)
{
	if (state == null) throw new NullPointerException();
	this.state = state;
}

public PlayerMessageType getType()
{ return type; }

public int getTypeInfo()
{ return typeInfo; }

public String getSubject(TextConverter converter)
{ return converter.convert(subject); }

@HtmlFragment
public String getSubject()
{ return Galaxy.defaultLineConverter.convert(subject); }

public String getText(TextConverter converter)
{ return converter.convert(text); }

@HtmlFragment
public String getText()
{ return Galaxy.defaultTextConverter.convert(text); }

}
