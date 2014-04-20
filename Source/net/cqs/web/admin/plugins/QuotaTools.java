package net.cqs.web.admin.plugins;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.cqs.main.config.FrontEnd;
import net.cqs.plugins.quota.QuotaConfiguration;

public final class QuotaTools
{

private static final Locale LOCALE = Locale.GERMAN;
private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", LOCALE);

public static Date parse(String date)
{
	try
	{ return DATE_FORMAT.parse(date); }
	catch (ParseException e)
	{ throw new RuntimeException(e); }
}

private final FrontEnd frontEnd;

public QuotaTools(FrontEnd frontEnd)
{
	this.frontEnd = frontEnd;
}

public String asDate(long value)
{ return DATE_FORMAT.format(new Date(value)); }

public QuotaConfiguration getQuotaConfiguration()
{ return QuotaConfiguration.getCopy(frontEnd.getStorageManager()); }

}