package net.cqs.plugins.quota;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public final class QuotaConfiguration implements Iterable<QuotaEntry>, Serializable
{

private static final long serialVersionUID = 1L;

private static final String BINDING = "QUOTA-CONFIGURATION";

private final List<QuotaEntry> data = new ArrayList<QuotaEntry>();

public QuotaConfiguration()
{
	// Ok for now.
}

public int size()
{ return data.size(); }

public void delete(String id)
{
	Iterator<QuotaEntry> it = iterator();
	while (it.hasNext())
	{
		QuotaEntry entry = it.next();
		if (id.equals(entry.id()))
			it.remove();
	}
}

public void deleteOldEntries(Date now)
{
	Iterator<QuotaEntry> it = iterator();
	while (it.hasNext())
	{
		QuotaEntry entry = it.next();
		if (entry.hasExpired(now))
			it.remove();
	}
}

public QuotaEntry get(int i)
{ return data.get(data.size()-1-i); }

public void add(QuotaEntry entry)
{ data.add(entry); }

@Override
public Iterator<QuotaEntry> iterator()
{ return data.iterator(); }

private static XStream getXStream()
{
	XStream xstream = new XStream(new DomDriver());
	xstream.alias("entry", QuotaEntry.class);
	xstream.alias("config", QuotaConfiguration.class);
	xstream.addImplicitCollection(QuotaConfiguration.class, "data");
	xstream.addImmutableType(QuotaEntry.class);
	xstream.useAttributeFor(QuotaEntry.class, "id");
	xstream.useAttributeFor(QuotaEntry.class, "name");
	xstream.useAttributeFor(QuotaEntry.class, "value");
	xstream.useAttributeFor(QuotaEntry.class, "duration");
	xstream.useAttributeFor(QuotaEntry.class, "date");
	xstream.useAttributeFor(QuotaEntry.class, "comment");
	return xstream;
}

public static String toXml(QuotaConfiguration config)
{ return getXStream().toXML(config); }

public static QuotaConfiguration fromXml(String xml)
{ return (QuotaConfiguration) getXStream().fromXML(xml); }

public static QuotaConfiguration get()
{
	QuotaConfiguration result;
	try
	{
		result = Context.getDataManager().getBinding(BINDING, QuotaConfiguration.class);
	}
	catch (NameNotBoundException e)
	{
		result = new QuotaConfiguration();
		Context.getDataManager().setBinding(BINDING, result);
	}
	return result;
}

public static QuotaConfiguration getCopy(Storage storage)
{
	try
	{ return storage.getCopy(BINDING, QuotaConfiguration.class); }
	catch (NameNotBoundException e)
	{ return new QuotaConfiguration(); }
}

}