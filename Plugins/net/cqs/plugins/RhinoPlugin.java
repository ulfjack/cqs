package net.cqs.plugins;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.ShutdownListener;
import net.cqs.main.signals.StartupListener;
import net.cqs.plugins.rhino.RhinoServer;

@Plugin
public class RhinoPlugin implements StartupListener, ShutdownListener
{

private final RhinoServer service;

public RhinoPlugin(PluginConfig config) throws UnknownHostException
{
	int port = Integer.parseInt(config.getRequired("port"));
	String host = config.get("host");
	InetAddress addr;
	if (host == null)
		addr = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
	else
		addr = InetAddress.getByName(host);
	service = new RhinoServer(config.getFrontEnd(), addr, port);
}

@Override
public void startup()
{ service.startup(); }

@Override
public void shutdown()
{ service.shutdown(); }

}
