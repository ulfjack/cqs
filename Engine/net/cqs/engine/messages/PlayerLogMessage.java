package net.cqs.engine.messages;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorState;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.i18n.Localized;
import net.cqs.i18n.ManagedLocale;
import de.ofahrt.ulfscript.analysis.xml.XmlState;

public final class PlayerLogMessage implements Serializable
{

private static final long serialVersionUID = 1L;

private final long realtime = System.currentTimeMillis();
private final String message;
private final String i18nmessage;
private ErrorState state = ErrorState.SEEN_NEW;

public PlayerLogMessage(String message, String i18nmessage)
{
	this.message = message;
	this.i18nmessage = i18nmessage;
}

public long getRealtime()
{ return realtime; }

@Override
public String toString()
{ return "@"+new Date(realtime)+": "+message+" ("+i18nmessage+")"; }

public String getI18nMessage()
{ return i18nmessage; }

public ErrorState getState()
{ return state; }

public void mark()
{
	if (state == ErrorState.SEEN_MARK)
		state = ErrorState.SEEN_SHOW;
	else
		if (state == ErrorState.SEEN_NEW)
			state = ErrorState.SEEN_MARK;
}

// FIXME: rewrite to use MessageFormat instead!
public static PlayerLogMessage createInstance(Player who, ErrorCode code, Object... objects)
{
	StringBuilder temp = new StringBuilder();
	temp.append(code).append('(');
	for (int i = 0; i < objects.length; i++)
	{
		if (i != 0) temp.append(",");
		temp.append(objects[i]);
	}
	temp.append(')');
	String message = temp.toString();
	String i18nmessage = message;
	try
	{
		Locale locale = who.getLocale();
		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Colony)
				objects[i] = ((Colony) objects[i]).getPosition().toString();
			if (objects[i] instanceof Localized)
				objects[i] = ((Localized) objects[i]).toString(new ManagedLocale(locale));
		}
		String result = String.format(locale, code.getName(locale), objects);
		if (!XmlState.checkFragment(result)) throw new Exception("Not valid XML: "+result);
		i18nmessage = result;
	}
	catch (Exception e)
	{ Galaxy.logger.log(Level.SEVERE, "Exception ignored for \""+message+"\"", e); }
	return new PlayerLogMessage(message, i18nmessage);
}

}
