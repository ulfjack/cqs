package net.cqs.plugins.rhino;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedByInterruptException;
import java.util.Vector;

import net.cqs.main.config.FrontEnd;

public final class RhinoServer implements Runnable
{

final FrontEnd frontEnd;
private final InetSocketAddress address;
private Vector<RhinoThread> threads = new Vector<RhinoThread>();
private ServerSocket server;
private Socket client;

public RhinoServer(FrontEnd frontEnd, InetAddress addr, int port)
{
	this.frontEnd = frontEnd;
	this.address = new InetSocketAddress(addr, port);
}

public void startup()
{
	try
	{
		server = new ServerSocket(address.getPort(), 10, address.getAddress());
		new Thread(this).start();
	}
	catch (Exception e)
	{
		e.printStackTrace();
		return;
	}
}

public void shutdown()
{
	try
	{ server.close(); }
	catch (IOException e)
	{ e.printStackTrace(); }
}

public void setStop()
{
	try { server.close(); }
	catch (Exception ignored)
	{/*OK*/}
	for (int i = threads.size()-1; i > -1; i--)
		threads.get(i).shutdown();
}

@Override
public void run() 
{
	while (true)
	{
		try
		{
			client = server.accept();	
			threads.add(new RhinoThread(client, System.err, threads.size(), this));	
		}
		catch (SocketException e)
		{ setStop(); return; }
		catch (ClosedByInterruptException e)
		{ setStop(); return; }
		catch (Exception e)
		{ e.printStackTrace(); }
	}
}

public void removeThread(RhinoThread rt)
{
	threads.remove(rt);
}

}
