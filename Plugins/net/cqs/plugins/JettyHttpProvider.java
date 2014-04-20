package net.cqs.plugins;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.ShutdownListener;
import net.cqs.main.signals.StartupListener;
import net.cqs.services.HttpService;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;

import de.ofahrt.catfish.RequestListener;

@Plugin
public class JettyHttpProvider implements HttpService, StartupListener, ShutdownListener
{

	private static class Entry
	{/*OK*/}
	private static class ServletEntry extends Entry
	{
		final String pathSpec, fileSpec;
		final Servlet servlet;
		public ServletEntry(String pathSpec, String fileSpec, Servlet servlet)
		{
			this.pathSpec = pathSpec;
			this.fileSpec = fileSpec;
			this.servlet = servlet;
		}
	}
	private static class FilterEntry extends Entry
	{
		final String pathSpec, fileSpec;
		final Filter filter;
		public FilterEntry(String pathSpec, String fileSpec, Filter filter)
		{
			this.pathSpec = pathSpec;
			this.fileSpec = fileSpec;
			this.filter = filter;
		}
	}

private final Logger logger = Logger.getLogger("net.cqs.plugins.JettyHttpProvider");

private final int port;
private final Server server;

private final ArrayList<Entry> list = new ArrayList<Entry>();

public JettyHttpProvider(PluginConfig config)
{
	port = Integer.parseInt(config.get("port"));
	server = new Server();
	Connector connector = new SelectChannelConnector();
	connector.setPort(port);
	server.setConnectors(new Connector[]{connector});
	config.getServiceRegistry().registerService(HttpService.class, this);
}

@Override
public void startup()
{
	ContextHandlerCollection contexts = new ContextHandlerCollection();
	server.setHandler(contexts);
	
	Context c = new Context(contexts, "/", Context.SESSIONS);
	for (Entry e : list)
	{
		if (e instanceof ServletEntry)
		{
			ServletEntry sentry = (ServletEntry) e;
			String path = "/".equals(sentry.pathSpec) ? sentry.fileSpec : sentry.pathSpec+sentry.fileSpec;
			c.addServlet(new ServletHolder(sentry.servlet), path);
		}
		else
		{
			FilterEntry fentry = (FilterEntry) e;
			String path = "/".equals(fentry.pathSpec) ? fentry.fileSpec : fentry.pathSpec+fentry.fileSpec;
			c.addFilter(new FilterHolder(fentry.filter), path, org.mortbay.jetty.Handler.ALL);
		}
	}
	
	try
	{
		server.start();
	}
	catch (Exception e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
}

@Override
public void shutdown()
{
	try
	{
		server.stop();
	}
	catch (Exception e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
}

@Override
public void registerServlet(String pathSpec, String fileSpec, Servlet servlet)
{ list.add(new ServletEntry(pathSpec, fileSpec, servlet)); }

@Override
public void registerFilter(String pathSpec, String fileSpec, Filter filter)
{ list.add(new FilterEntry(pathSpec, fileSpec, filter)); }


@Override
public void registerRequestListener(RequestListener listener)
{ throw new UnsupportedOperationException(); }

}
