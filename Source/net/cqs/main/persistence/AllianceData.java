package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.engine.messages.Message;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;

/**
 * Persistente Allianz-Daten.
 * 
 * Das wichtigste ist, dass diese Klasse unter GAR KEINEN UMSTAENDEN Referenzen
 * auf die Datenbank hat.
 */
public final class AllianceData implements Serializable
{

private static final long serialVersionUID = 1L;

private final Mailbox mailbox;

public AllianceData()
{
	this.mailbox = new Mailbox(new GroupMailboxFilter(), 5);
}

public Mailbox getMail()
{ return mailbox; }

public Message getMessage(int box, String id)
{ return mailbox.getFolder(box).get(id); }

public Message removeMessage(int box, String id)
{ return mailbox.getFolder(box).remove(id); }

public void addMessage(Message m)
{ mailbox.add(m); }


public static String getBinding(int pid)
{ return pid+"-ALLIANCE"; }

public static AllianceData getAllianceData(int pid)
{
	String binding = getBinding(pid);
	AllianceData result;
	try
	{
		result = Context.getDataManager().getBinding(binding, AllianceData.class);
	}
	catch (NameNotBoundException e)
	{
		result = new AllianceData();
		Context.getDataManager().setBinding(binding, result);
	}
	return result;
}

public static AllianceData getAllianceDataCopy(Storage storage, int pid)
{
	String binding = getBinding(pid);
	try
	{ return storage.getCopy(binding, AllianceData.class); }
	catch (NameNotBoundException e)
	{ return new AllianceData(); }
}

}
