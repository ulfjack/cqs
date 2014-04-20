package net.cqs.main.persistence;

import java.io.Serializable;
import java.util.Iterator;

import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.MessageState;

public final class Mailbox implements Serializable
{

private static final long serialVersionUID = 1L;

private MailboxFilter filter;
private Folder[] folders;

public Mailbox(MailboxFilter filter, int count)
{
	this.filter = filter;
	folders = new Folder[count];
	for (int i = 0; i < folders.length; i++)
		folders[i] = new Folder();
}

public int size()
{ return folders.length; }

public int folderSize(int i)
{ return folders[i].size(); }

public Folder getFolder(int i)
{ return folders[i]; }

public void add(Message m)
{
	int box = filter.getBox(m.getType(), m.getState());
	folders[box].add(m);
}

public void setRead(int box, String id)
{
	Message msg = getFolder(box).remove(id);
	msg.setState(MessageState.READ);
	add(msg);
}

public void setAllRead(int box)
{
	Folder folder = getFolder(box);
	Iterator<Message> it = folder.iterator();
	while (it.hasNext())
	{
		Message msg = it.next();
		msg.setState(MessageState.READ);
		add(msg);
	}
	folder.removeAll();
}

}
