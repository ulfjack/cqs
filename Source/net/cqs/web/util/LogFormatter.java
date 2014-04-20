package net.cqs.web.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{

private static final SimpleDateFormat TIME_FORMATER = new SimpleDateFormat("HH:mm:ss(MM-dd)");

public LogFormatter()
{/*OK*/}

@Override
public String format(LogRecord record)
{
	StringBuffer result = new StringBuffer();
	result.append('[');
	result.append(record.getLevel());
	result.append(':');
	result.append(record.getSourceClassName());
	result.append('.');
	result.append(record.getSourceMethodName());
	result.append('@');
	result.append(TIME_FORMATER.format(new Date(record.getMillis())));
	result.append("] ");
	result.append(record.getMessage());
	result.append('\n');
	Throwable t = record.getThrown();
	if (t != null)
	{
		StringWriter out = new StringWriter();
		t.printStackTrace(new PrintWriter(out));
		result.append(out.toString());
	}
	return result.toString();
}

}