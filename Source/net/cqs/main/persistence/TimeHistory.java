package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.util.DataSeries;
import net.cqs.util.HistorySeries;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class TimeHistory implements Serializable, DataSeries
{

private static final long serialVersionUID = 1L;

private final HistorySeries data = new HistorySeries(HistorySeries.UpdateType.MAX);

public TimeHistory()
{/*OK*/}

@Override
public int size()
{ return data.size(); }

@Override
public long get(int i)
{ return data.get(i); }

@Override
public long max()
{ return data.max(); }

@Override
public long min()
{ return data.min(); }

@Override
public String toCSV()
{ return data.toCSV(); }

@Restricted
public synchronized void logTime(long amount)
{ data.update(amount); }

}
