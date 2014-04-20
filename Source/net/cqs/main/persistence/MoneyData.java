package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.config.MoneyReason;
import net.cqs.config.Time;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;
import net.cqs.web.charts.LineChart;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class MoneyData implements Serializable
{

private static final long serialVersionUID = 1L;

private final long[] values = new long[7*24];
private long lastHour = 0;
private long value = 0;
private long count = 0;

public MoneyData()
{/*OK*/}

public int size()
{ return values.length; }

public long get(int i)
{ return values[i]; }

@Restricted
public void logMoney(long time, MoneyReason reason, long amount)
{
	long hour = time / Time.hours(1);
	if (hour == lastHour)
	{
		value += amount;
		count++;
	}
	else
	{
		while (lastHour < hour)
		{
			for (int i = 0; i < values.length-1; i++)
				values[i] = values[i+1];
			lastHour++;
		}
		lastHour = hour;
		value = amount;
		count = 1;
	}
	values[values.length-1] = value/count;
}

public String getUrl()
{ return LineChart.getUrl(500, 400, new String[] {"-7d", "-6d", "-5d", "-4d", "-3d", "-2d", "-1d", "now"}, values); }

public static String getBinding(int pid)
{ return pid+"-MONEY"; }

public static MoneyData getMoneyData(int pid)
{
	String binding = getBinding(pid);
	MoneyData result;
	try
	{
		result = Context.getDataManager().getBinding(binding, MoneyData.class);
	}
	catch (NameNotBoundException e)
	{
		result = new MoneyData();
		Context.getDataManager().setBinding(binding, result);
	}
	return result;
}

public static MoneyData getMoneyDataCopy(Storage storage, int pid)
{
	String binding = getBinding(pid);
	try
	{ return storage.getCopy(binding, MoneyData.class); }
	catch (NameNotBoundException e)
	{ return new MoneyData(); }
}

}
