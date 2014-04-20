package net.cqs.web.game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.auth.Identity;
import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.Position;
import net.cqs.engine.base.Attribute;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.config.LogEnum;
import net.cqs.main.resource.ResourceManager;
import net.cqs.web.ParsedRequest;
import net.cqs.web.action.Converter;
import net.cqs.web.action.ManagedActionHandler;
import net.cqs.web.action.ParameterProvider;
import net.cqs.web.game.action.ActionHandler;
import net.cqs.web.game.action.ColonyPlugin;
import net.cqs.web.game.action.FleetPlugin;
import net.cqs.web.util.HtmlToolkit;
import net.cqs.web.util.ServletHelper2;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.catfish.utils.ServletHelper;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.GeneratorException;
import de.ofahrt.ulfscript.ParserException;
import de.ofahrt.ulfscript.utils.Cache;

public final class GameServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger("net.cqs.web");
private static final Logger timeLogger = Logger.getLogger(LogEnum.TIME.getPackageName());

static {
	ActionHandler.init();
//	ActionHandler.add(new ColonyPlugin());
}


private final FrontEnd frontEnd;
private final GameEnvironment env;
private final Cache cache;
private final ManagedActionHandler actionHandler;

public GameServlet(final FrontEnd frontEnd, ResourceManager resourceManager)
{
	this.frontEnd = frontEnd;
	this.env = GameEnvironment.DESC.newInstance(resourceManager);
	this.cache = new Cache(env);
	
	actionHandler = new ManagedActionHandler(Identity.class, int.class);
	actionHandler.addConverter(Position.class, new PositionConverter());
	actionHandler.addConverter(Unit.class, new Converter<Unit>()
		{
			@Override
			public Unit convert(String value)
			{ return frontEnd.getUnitSystem().parseUnit(value); }
		});
	actionHandler.addConverter(BuildingEnum.class);
	actionHandler.addConverter(EducationEnum.class);
	actionHandler.addConverter(ResearchEnum.class);
	
	actionHandler.add(new ColonyPlugin());
	actionHandler.add(new FleetPlugin());
}

private void rescue(CqsSession session, HttpServletResponse res, MimeType mimeType, ParsedRequest parsedRequest, Throwable exception) throws IOException
{
	logger.log(Level.SEVERE, "Exception caught", exception);
	String error = HtmlToolkit.exceptionToString(exception);
	session.setErrorMessage(error);
	
	try
	{
		Generator gen = cache.get("error.html", session.getLocale());
		CharSequence data = env.generate(gen, session, parsedRequest);
		ServletHelper2.send(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mimeType, res, data);
	}
	catch (FileNotFoundException e)
	{ ServletHelper2.rescue(res, error, e); }
	catch (ParserException e)
	{ ServletHelper2.rescue(res, error, e); }
	catch (GeneratorException e)
	{ ServletHelper2.rescue(res, error, e); }
}

public void internalHandleRequest(final CqsSession session, HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (frontEnd.isShutdownSequence())
		throw new IOException("Shutdown In Progress!");
	
//	boolean overrideCompression = session.getPlayer().isOverrideCompression();
//	if (overrideCompression && (res instanceof ResponseImpl))
//		((ResponseImpl) res).enableCompression();
	
	ParsedRequest parsedRequest = new ParsedRequest(req, false);
	String fname = parsedRequest.getBasename();
	String filename = fname+".html";
	
	if (frontEnd.getAttributes().get(Attribute.CHECK_QUOTA).booleanValue())
	{
		int quota = 1024*session.getPlayer().getAttr(Attribute.QUOTA).intValue();
		int used = session.getPlayer().getAttr(Attribute.QUOTA_USED).intValue();
		used += session.getPlayer().getAttr(Attribute.GP_QUOTA_USED).intValue();
		if (used > quota)
		{
			res.sendRedirect("/quota-exceeded.html");
			return;
		}
	}
	
	MimeType mimeType = MimeType.TEXT_HTML;
	// FIXME: Deliver as application/xhtml+xml - looks good so far
//	if (req.getHeader(HeaderEnum.ACCEPT).contains("application/xhtml+xml"))
//		mimeType = MimeType.APPLICATION_XHTML_AND_XML;
	
	session.start();
	
	if (env.getSource(filename) == null)
	{
		res.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	
	actionHandler.handle(session, parsedRequest, new ParameterProvider()
		{
			@Override
			public Object get(int i)
			{
				if (i == 0) return session.getIdentity();
				if (i == 1) return Integer.valueOf(session.getPlayerId());
				throw new IllegalArgumentException(""+i);
			}
		});
	
	ActionHandler.getHandler(parsedRequest, session);
	if ("POST".equals(parsedRequest.getMethod()))
		ActionHandler.postHandler(parsedRequest, session, req);
	
	try
	{
		Generator gen = cache.get(filename, session.getLocale());
		if (gen == null) throw new FileNotFoundException(filename);
		CharSequence data = env.generate(gen, session, parsedRequest);
		ServletHelper2.send(HttpServletResponse.SC_OK, mimeType, res, data);
	}
	catch (FileNotFoundException e)
	{ rescue(session, res, mimeType, parsedRequest, e); }
	catch (ParserException e)
	{ rescue(session, res, mimeType, parsedRequest, e); }
	catch (GeneratorException e)
	{ rescue(session, res, mimeType, parsedRequest, e); }
}

@Override
public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (frontEnd.isShutdownSequence())
		throw new IOException("Shutdown In Progress!");
	
	CqsSession session = frontEnd.getCqsSession(req.getSession());
	if (session == null) throw new IOException("No session?");
	if (!session.isLoggedIn()) throw new IOException("Not logged in?");
	
	String filename = ServletHelper.getFilename(req);
	long wait = System.currentTimeMillis();
	synchronized (frontEnd.getGalaxy())
	{
		long start = System.currentTimeMillis();
		
		try
		{
			internalHandleRequest(session, req, res);
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "Exception caught", e);
			throw e;
		}
		
		long stop = System.currentTimeMillis();
		timeLogger.info((start-wait)+" + "+(stop-start)+" - "+session.getPlayer()+" "+filename);
	}
	long stop = System.currentTimeMillis();
	frontEnd.logTime(stop-wait);
}

@Override
public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	doGet(req, res);
}

}
