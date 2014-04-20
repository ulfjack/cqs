package net.cqs.config;

/**
 * Hilfsklasse zum Konvertieren von Zeitwerten in das Engine-interne
 * Zeitformat (derzeit Sekunden). Enthaelt auch eine Parser-Funktion.
 */
public final class Time
{

public static long millis(long millis)
{ return millis/1000L; }

public static long seconds(long sec)
{ return sec; }

public static long minutes(long min)
{ return min*60L; }

public static long hours(long hours)
{ return hours*60L*60L; }

public static long days(long days)
{ return days*60L*60L*24L; }

public static long parseTime(String value)
{
	value = value.trim();
	if (value.length() == 0) throw new NumberFormatException("Invalid time value \""+value+"\"!");
	char info = value.charAt(value.length()-1);
	String realdata = value.substring(0, value.length()-1);
	switch (info)
	{
		case 's' : return seconds(Long.parseLong(realdata));
		case 'm' : return minutes(Long.parseLong(realdata));
		case 'h' : return hours(Long.parseLong(realdata));
		case 'd' : return days(Long.parseLong(realdata));
	}
	throw new NumberFormatException("Invalid time value \""+value+"\"!");
}

private static String f(long v)
{
	if (v < 10) return "0".concat(Long.toString(v));
	return Long.toString(v);
}

public static String format(long value)
{
	long s = value % 60; value /= 60;
	long m = value % 60; value /= 60;
	long h = value % 24; value /= 24;
	long d = value % 365; value /= 365;
	
	if (d == 0)
	{
		if (h == 0)
			return f(m)+":"+f(s);
		return f(h)+":"+f(m)+":"+f(s);
	}
	return d+"d "+f(h)+":"+f(m)+":"+f(s);
}


private Time()
{/*OK*/}

}
