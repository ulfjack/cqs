package net.cqs.web.game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.Position;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.resource.ResourceManager;
import net.cqs.web.ParsedRequest;
import net.cqs.web.action.ActionHandler;
import net.cqs.web.action.Converter;
import net.cqs.web.game.ajax.AjaxColonyPlugin;
import net.cqs.web.game.ajax.AjaxFleetPlugin;
import net.cqs.web.game.ajax.AjaxPlayerPlugin;
import net.cqs.web.util.HtmlToolkit;
import net.cqs.web.util.ServletHelper2;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.GeneratorException;
import de.ofahrt.ulfscript.ParserException;
import de.ofahrt.ulfscript.utils.Cache;

public final class AjaxServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger(AjaxServlet.class.getName());

private final FrontEnd frontEnd;
private final MimeType mimeType = MimeType.TEXT_PLAIN;
private final String errorFile = "error.ajax";
private final GameEnvironment env;
private final Cache cache;

private final ActionHandler handler;

public AjaxServlet(final FrontEnd frontEnd, ResourceManager resourceManager)
{
	this.frontEnd = frontEnd;
	this.env = AjaxEnvironment.DESC.newInstance(resourceManager);
	this.cache = new Cache(env);
	
	this.handler = new ActionHandler(ParsedRequest.class, CqsSession.class);
	handler.addConverter(Position.class, new PositionConverter());
	handler.addConverter(Unit.class, new Converter<Unit>()
		{
			@Override
			public Unit convert(String value)
			{ return frontEnd.getUnitSystem().parseUnit(value); }
		});
	handler.addConverter(BuildingEnum.class);
	handler.addConverter(EducationEnum.class);
	handler.addConverter(ResearchEnum.class);
	
	handler.add(new AjaxColonyPlugin());
	handler.add(new AjaxFleetPlugin());
	handler.add(new AjaxPlayerPlugin());
}

private void rescue(CqsSession session, HttpServletResponse res, ParsedRequest parsedRequest, Throwable exception) throws IOException
{
	logger.log(Level.SEVERE, "Exception caught", exception);
	String error = HtmlToolkit.exceptionToString(exception);
	session.setErrorMessage(error);
	
	try
	{
		Generator gen = cache.get(errorFile, session.getLocale());
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

@Override
public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (frontEnd.isShutdownSequence())
		throw new IOException("Shutdown In Progress!");
	
	CqsSession session = frontEnd.getCqsSession(req.getSession());
	if (session == null) throw new IOException("No session?");
	if (!session.isLoggedIn()) throw new IOException("Not logged in?");
	
	ParsedRequest parsedRequest = new ParsedRequest(req);
	String filename = parsedRequest.getFilename();
	synchronized (frontEnd.getGalaxy())
	{
		session.start();
		handler.handle(req, parsedRequest, parsedRequest, session);
		try
		{
			Generator gen = cache.get(filename, session.getLocale());
			if (gen == null)
			{
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			CharSequence data = env.generate(gen, session, parsedRequest);
			ServletHelper2.send(HttpServletResponse.SC_OK, mimeType, res, data);
		}
		catch (FileNotFoundException e)
		{ rescue(session, res, parsedRequest, e); }
		catch (ParserException e)
		{ rescue(session, res, parsedRequest, e); }
		catch (GeneratorException e)
		{ rescue(session, res, parsedRequest, e); }
	}
}

}
