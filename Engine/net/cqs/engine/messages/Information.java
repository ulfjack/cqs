package net.cqs.engine.messages;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.config.InfoEnum;
import net.cqs.config.InfoboxEnum;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.i18n.Localized;
import net.cqs.i18n.ManagedLocale;

public final class Information implements Serializable
{

private static final long serialVersionUID = 1L;

private final long realtime;
private final long time;
private final InfoEnum key;
private final String message;
private final String i18nmessage;

public Information(long realtime, long time, InfoEnum key, String message, String i18nmessage)
{
	if (key == null) throw new NullPointerException();
	if (message == null) throw new NullPointerException();
	if (i18nmessage == null) throw new NullPointerException();
	this.realtime = realtime;
	this.time = time;
	this.key = key;
	this.message = message;
	this.i18nmessage = i18nmessage;
}

public Information(long time, InfoEnum key, String message, String i18nmessage)
{ this(System.currentTimeMillis(), time, key, message, i18nmessage); }

public long getRealtime()
{ return realtime; }

public long getTime()
{ return time; }

public InfoEnum getType()
{ return key; }

public int getCertainty(long attime)
{
	int certainty = 1000;
	int rate = key.getRate();
	float minutesElapsed = (attime-this.time)/60.0f;
	if (minutesElapsed <= 0) minutesElapsed = 0;
	if (minutesElapsed > rate) return 0;
	return (int) (certainty*(1-minutesElapsed/rate));
}

public InfoboxEnum getBox()
{ return key.getTarget(); }

@Override
public String toString()
{ return key+" at "+new Date(realtime)+" "+message+" ("+i18nmessage+")"; }

public String getMessage()
{ return message; }

@HtmlFragment
public String getI18nMessage()
{ return i18nmessage; }


public static Information createInstance(Locale locale, long time, InfoEnum key, Object... objects)
{
	StringBuffer temp = new StringBuffer();
	temp.append(key).append('(');
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
		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Colony)
				objects[i] = ((Colony) objects[i]).getPosition().toString();
			if (objects[i] instanceof Localized)
				objects[i] = ((Localized) objects[i]).toString(new ManagedLocale(locale));
		}
		i18nmessage = String.format(locale, key.getName(locale), objects);
	}
	catch (Exception e)
	{ Galaxy.logger.log(Level.SEVERE, "Exception ignored for \""+message+"\"", e); }
	return new Information(time, key, message, i18nmessage);
}

}
