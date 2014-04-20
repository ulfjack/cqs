package net.cqs.main.persistence;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.cqs.config.InfoboxEnum;
import net.cqs.engine.messages.Information;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;

public final class Infobox implements Serializable
{

private static final long serialVersionUID = 1L;

private final EnumMap<InfoboxEnum,LinkedList<Information>> data = new EnumMap<InfoboxEnum,LinkedList<Information>>(InfoboxEnum.class);

public Infobox()
{/*OK*/}

public int getSize(int i)
{ return getList(InfoboxEnum.values()[i]).size(); }

public int getSize(InfoboxEnum a)
{ return getList(a).size(); }

public LinkedList<Information> getList(InfoboxEnum which)
{
	LinkedList<Information> result = data.get(which);
	if (result == null)
	{
		result = new LinkedList<Information>();
		data.put(which, result);
	}
	return result;
}

public LinkedList<Information> getList(int i)
{ return getList(InfoboxEnum.values()[i]); }

public void addInfo(Information m)
{ getList(m.getBox()).addFirst(m); }

private void cleanup(LinkedList<Information> list, long time)
{
	Iterator<Information> it = list.iterator();
	while (it.hasNext())
	{
		Information i = it.next();
		if (i.getCertainty(time) <= 0)
			it.remove();
	}
}

public void cleanupInfoBox(InfoboxEnum which, long time)
{
	if (which == null) throw new NullPointerException();
	cleanup(getList(which), time);
}

public void cleanupInfoBoxes(long time)
{
	for (InfoboxEnum box : InfoboxEnum.values())
		cleanup(getList(box), time);
}


public static String getBinding(int pid)
{ return pid+"-INFO"; }

public static Infobox getInfobox(int pid)
{
	String binding = getBinding(pid);
	Infobox result;
	try
	{
		result = Context.getDataManager().getBinding(binding, Infobox.class);
	}
	catch (NameNotBoundException e)
	{
		result = new Infobox();
		Context.getDataManager().setBinding(binding, result);
	}
	return result;
}

public static Infobox getInfoboxCopy(Storage storage, int pid)
{
	String binding = getBinding(pid);
	try
	{ return storage.getCopy(binding, Infobox.class); }
	catch (NameNotBoundException e)
	{ return new Infobox(); }
}

}
