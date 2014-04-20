package net.cqs.plugins.quota;

import java.io.Serializable;
import java.util.Date;


import net.cqs.engine.Galaxy;
import net.cqs.util.UIDGenerator;

public final class QuotaEntry implements Serializable
{

private static final long serialVersionUID = 1L;

private static final UIDGenerator generator = new UIDGenerator();

private final String id;
private final String name;
private final int value;
private final int duration;
private final Date date;
private final String comment;

public QuotaEntry(String name, int value, int duration, Date date, String comment)
{
	this.id = generator.generateUniqueID();
	this.name = name;
	this.value = value;
	this.duration = duration;
	this.date = date;
	this.comment = comment;
}

public String id()
{ return id; }

public String name()
{ return name; }

public String comment()
{ return comment; }

public int value()
{ return value; }

public int duration()
{ return duration; }

public Date date()
{ return date; }

public boolean hasStarted()
{ return new Date().after(date); }

public boolean validName(Galaxy galaxy)
{ return galaxy.findPlayerByName(name) != null; }

public boolean hasExpired(Date when)
{
	Date expireDate = new Date(date.getTime()+duration*(60L*60*24*1000));
	return when.after(expireDate);
}

public boolean hasExpired()
{ return hasExpired(new Date()); }

}