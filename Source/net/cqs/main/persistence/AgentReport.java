package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.engine.base.UnitMap;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;

/**
 * Persistente Agenten-Daten.
 * 
 * Das wichtigste ist, dass diese Klasse unter GAR KEINEN UMSTAENDEN Referenzen
 * auf die Datenbank hat.
 */
public final class AgentReport implements Serializable
{

private static final long serialVersionUID = 1L;

public String position;
public int id;
public long time;
public long realtime;

public UnitMap units;

/**
 * creates AgentReportData for spy reports (final report)
 * position, id, realtime and units are set in Agent.requestReport
 */
public AgentReport(String position, int id, long time, long realtime, UnitMap units)
{
	this.position = position;
	this.id = id;
	this.time = time;
	this.realtime = realtime;
	this.units = units;
}

public String getPosition()
{ return position; }

public int getId()
{ return id; }

public long getTime()
{ return time; }

public long getRealtime()
{ return realtime; }

public UnitMap getUnits()
{ return units; }

public long lowerBound(long actual)
{ return (long) (Math.max(1,Math.ceil(actual/1.15-1))); }

public long upperBound(long actual)
{ return (long) (Math.floor(actual/0.85+1)); }


public static String getBinding(String pid)
{
	return "AGENTREPORT-"+pid;
}

public static AgentReport getAgentReportCopy(Storage storage, String filename)
{
	try
	{
		String binding = getBinding(filename);
		return storage.getCopy(binding, AgentReport.class);
	}
	catch (NameNotBoundException e)
	{ return null; }
}

}
