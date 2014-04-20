package net.cqs.plugins;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.engine.Player;
import net.cqs.engine.messages.InfoListener;
import net.cqs.engine.messages.Information;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.ShutdownListener;
import net.cqs.main.signals.StartupListener;
import net.cqs.services.IrcService;
import de.ofahrt.irc.Channel;
import de.ofahrt.irc.LocalUserInfo;
import de.ofahrt.irc.Message;
import de.ofahrt.irc.Server;
import de.ofahrt.irc.ServerEvent;
import de.ofahrt.irc.ServerListener;
import de.ofahrt.irc.ServerStateEvent;
import de.ofahrt.irc.impl.StoredMessageDispatcher;

@Plugin
public final class IrcGate implements IrcService, /*MailListener,*/ InfoListener, 
	ServerListener, StartupListener, ShutdownListener, Runnable
{

	private static class IrcMessage
	{
		final String recipient;
		final String text;
		
/*		public Message(PlayerMessage m)
		{
			this.recipient = m.recipient;
			this.text = "Von "+m.sender+": ("+m.subject+") "+
			    de.ofahrt.http2.Helper.unformatText(m.text);
		}*/
		
		public IrcMessage(Player who, Information m)
		{
			this.recipient = who.getName();
			this.text = m.getI18nMessage();
		}
	}

private static final Logger logger = Logger.getLogger("net.cqs.services.IrcService");

private final FrontEnd frontEnd;
private final String channelName;
private final Server myServer;
private Channel myChannel;

private final BlockingQueue<IrcMessage> data = new LinkedBlockingQueue<IrcMessage>();

public IrcGate(PluginConfig config)
{
	this.frontEnd = config.getFrontEnd();
//  Mailbox.addMailListener(this);
	
	channelName = config.getRequired("channel");
	String serverName = config.getRequired("server");
	int serverPort = config.getInt("port", 6667);
	InetSocketAddress address = new InetSocketAddress(serverName, serverPort);
	myServer = new Server(new LocalUserInfo("CqSBot", "cqsbot", "cqsbot"),
		new StoredMessageDispatcher(), address);
	myServer.addServerListener(this);
}

@Override
public void startup()
{
	frontEnd.getGalaxy().addInfoListener(this);
	new Thread(this).start();
}

@Override
public void shutdown()
{ data.offer(null); }

/*public void notifyMail(Player who, PlayerMessage m)
{
  if (m == null) return;
  
  try
  {
    synchronized (data)
    {
      data.add(new Message(m));
      data.notify();
    }
  }
  catch (Exception e)
  { e.printStackTrace(); }
}*/

@Override
public void notifyInfo(Player who, Information m)
{
	if (who == null) throw new NullPointerException();
	if (m == null) throw new NullPointerException();
	
	try
	{ data.add(new IrcMessage(who, m)); }
	catch (IllegalStateException e)
	{ logger.log(Level.SEVERE, "caught exception", e); }
}

@Override
public void run()
{
	myServer.connect();
	
	while (true)
	{
		try
		{
			IrcMessage m = data.take();
			if (m == null) break;
			
	    String name = m.recipient;
	    String text = m.text;
    	
	    logger.info("forwarding to: "+name);
	    myChannel.sendMessage(name+": "+text);
  	}
		catch (InterruptedException e)
		{ logger.log(Level.SEVERE, "caught exception", e); }
  }
}

// ServerListener implementation
@Override
public void channelJoined(Server server, Channel channel)
{
	logger.info("channel joined "+channel);
	myChannel = channel;
}

@Override
public void channelParted(Server server, Channel channel)
{/*OK*/}

@Override
public void process(Server server, ServerEvent event)
{/*OK*/}

@Override
public void processProtocolMessage(Server server, Message msg)
{
	logger.info(msg.toString());
}

@Override
public void stateChange(Server server, ServerStateEvent arg1)
{
	if (arg1 == ServerStateEvent.CONNECT)
		myServer.join(channelName);
}

}
