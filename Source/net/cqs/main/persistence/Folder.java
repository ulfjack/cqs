package net.cqs.main.persistence;

import java.io.Serializable;
import java.util.Iterator;

import net.cqs.engine.messages.Message;
import net.cqs.util.HashedTree;

public final class Folder implements Iterable<Message>, Serializable
{

private static final long serialVersionUID = 1L;

private final HashedTree<String,Message> messages = new HashedTree<String,Message>(String.class, Message.class);
private long lastAddRealTime;

public Folder()
{/*OK*/}

public int size()
{ return messages.size(); }

public long getLastAddRealTime()
{ return lastAddRealTime; }

public void add(Message m)
{
	lastAddRealTime = m.getRealtime();
	messages.putFirst(m.getId(), m);
}

public Message remove(String id)
{ return messages.remove(id); }

public void removeAll()
{ messages.clear(); }

public Message get(String id)
{ return messages.get(id); }

@Override
public Iterator<Message> iterator()
{ return messages.iterator(); }

}
