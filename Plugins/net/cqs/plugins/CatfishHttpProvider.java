package net.cqs.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.main.config.LogEnum;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.ShutdownListener;
import net.cqs.main.signals.StartupListener;
import net.cqs.services.HttpService;
import net.cqs.services.ServiceRegistry;
import net.cqs.services.StorageService;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Task;
import de.ofahrt.catfish.Builder;
import de.ofahrt.catfish.CatfishHttpServer;
import de.ofahrt.catfish.RequestListener;
import de.ofahrt.catfish.WebalizerLogger;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.catfish.utils.RedirectAllManager;
import de.ofahrt.catfish.utils.ServletHelper;

@Plugin
public class CatfishHttpProvider implements HttpService, StartupListener, ShutdownListener
{

private static final Logger logger = Logger.getLogger("net.cqs.plugins.CatfishHttpProvider");

private static final String BINDING = "CATFISH-SESSIONS";

	private static class CatfishSessions implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private byte[] data;
		public CatfishSessions(byte[] data)
		{ this.data = data; }
		public byte[] getData()
		{ return data; }
	}

private final ServiceRegistry registry;
private final String logPath;
private final int port;
private final int threadamount;
private CatfishHttpServer server;
private final Builder builder = new Builder();
private final ArrayList<RequestListener> listeners = new ArrayList<RequestListener>();

public CatfishHttpProvider(PluginConfig config)
{
	registry = config.getServiceRegistry();
	logPath = config.getFrontEnd().getLogPath(LogEnum.ACCESS);
	port = Integer.parseInt(config.get("port"));
	threadamount = Integer.parseInt(config.get("threads"));
	registry.registerService(HttpService.class, this);
}

@Override
public void registerServlet(String pathSpec, String fileSpec, Servlet servlet)
{
	if (!pathSpec.startsWith("/")) throw new IllegalArgumentException();
	String[] data = pathSpec.substring(1).split("/");
	if ("/".equals(pathSpec)) data = new String[0];
	for (int i = 0; i < data.length; i++)
		builder.enter(data[i]);
	builder.add(servlet, fileSpec);
	for (int i = 0; i < data.length; i++)
		builder.leave();
}

@Override
public void registerFilter(String pathSpec, String fileSpec, Filter filter)
{
	if (!pathSpec.startsWith("/")) throw new IllegalArgumentException();
	String[] data = pathSpec.substring(1).split("/");
	if ("/".equals(pathSpec)) data = new String[0];
	for (int i = 0; i < data.length; i++)
		builder.enter(data[i]);
	builder.add(filter, fileSpec);
	for (int i = 0; i < data.length; i++)
		builder.leave();
}


@Override
public void registerRequestListener(RequestListener listener)
{ listeners.add(listener); }

@SuppressWarnings({ "unused", "serial" })
private Builder buildEmpty()
{
	Builder b = new Builder();
	b.add(new HttpServlet()
		{
			@Override
			public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
			{
				InputStream in = CatfishHttpProvider.class.getClassLoader().getResourceAsStream("net/cqs/plugins/warning.html");
				ServletHelper.setBodyInputStream(response, MimeType.TEXT_HTML, in);
			}
		}, "index");
	b.add(new RedirectAllManager("/"), "/*");
	return b;
}

@Override
public void startup()
{
	registerServlet("/", "*.jpg", new HelloServlet("Catfish"));
	try
	{
		server = new CatfishHttpServer(System.err);
		server.addHead("localhost", builder);
//		server.addHead("server3.conquer-space.net", buildEmpty());
//		server.addHead("gamma.conquer-space.net", buildEmpty());
		
		try
		{
			CatfishSessions sessions = registry.findService(StorageService.class).getCopy(BINDING, CatfishSessions.class);
			server.loadSessions(new ByteArrayInputStream(sessions.getData()));
		}
		catch (NameNotBoundException e)
		{/*IGNORED*/}
		server.listenHttp(port);
		server.addThreads(threadamount);
		server.setCompressionAllowed(true);
		
		server.addRequestListener(new WebalizerLogger(logPath));
		for (RequestListener listener : listeners)
			server.addRequestListener(listener);
		
		server.start();
	}
	catch (Exception e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
}

@Override
public void shutdown()
{
	server.stop();
	try
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		server.saveSessions(out);
		final CatfishSessions sessions = new CatfishSessions(out.toByteArray());
		registry.findService(StorageService.class).execute(new Task()
			{
				@Override
				public void run()
				{ Context.getDataManager().setBinding(BINDING, sessions); }
			});
	}
	catch (IOException e)
	{ logger.log(Level.WARNING, "Exception caught", e); }
}

}
