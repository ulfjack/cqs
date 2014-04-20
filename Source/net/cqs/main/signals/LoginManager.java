package net.cqs.main.signals;

import java.util.ArrayList;
import java.util.Iterator;

public final class LoginManager
{

private final ArrayList<LoginListener> listeners = new ArrayList<LoginListener>();

public synchronized void add(LoginListener l)
{ listeners.add(l); }

public synchronized void remove(LoginListener l)
{ listeners.remove(l); }

public synchronized void fireLogin(String cookie, int pid)
{
  Iterator<LoginListener> it = listeners.iterator();
  while (it.hasNext())
  {
  	LoginListener l = it.next();
    l.login(cookie, pid);
  }
}

}
