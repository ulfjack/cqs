package net.cqs.web.frontpage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.resource.ResourceManager;
import net.cqs.web.ParsedRequest;
import net.cqs.web.action.ActionHandler;
import net.cqs.web.util.HtmlToolkit;
import net.cqs.web.util.ServletHelper2;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.catfish.utils.ServletHelper;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.GeneratorException;
import de.ofahrt.ulfscript.ParserException;
import de.ofahrt.ulfscript.utils.Cache;

public final class FrontpageServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger("net.cqs.web");

private static final MimeType HTML_MIME_TYPE = MimeType.TEXT_HTML;

private final FrontEnd frontEnd;
private final FrontpageEnvironment env;
private final Cache cache;

private final ActionHandler handler;

public FrontpageServlet(FrontEnd frontEnd, ResourceManager resourceManager)
{
	this.frontEnd = frontEnd;
	this.env = FrontpageEnvironment.DESC.newInstance(resourceManager);
	this.cache = new Cache(env);
	this.handler = new ActionHandler(IdSession.class, FrontEnd.class);
	handler.add(new AccountPlugin());
	handler.add(new LoginPlugin());
	handler.add(new RegistrationPlugin());
	handler.add(new ReminderPlugin());
}

private void rescue(IdSession idSession, HttpServletResponse res, ParsedRequest parsedRequest, Throwable exception) throws IOException
{
	logger.log(Level.SEVERE, "Exception caught", exception);
	idSession.getData().errormessage = HtmlToolkit.exceptionToString(exception);
	
	Generator gen = cache.get("error.html", idSession.getLocale());
	CharSequence data = env.generate(gen, idSession, frontEnd, parsedRequest);
	ServletHelper2.send(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HTML_MIME_TYPE, res, data);
}

public void internalHandleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (frontEnd.isShutdownSequence())
		throw new IOException("Shutdown In Progress!");
	
	ParsedRequest parsedRequest = new ParsedRequest(req);
	String filename = parsedRequest.getFilename();
	if (!filename.endsWith(".html") && !filename.endsWith(".svg"))
		filename = filename+".html";
	
	if (env.getSource(filename) == null)
	{
		res.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	
	MimeType mimeType = MimeType.TEXT_HTML;
	if (filename.endsWith(".svg"))
		mimeType = MimeType.IMAGE_SVG;
	
	IdSession idSession = frontEnd.getIdSession(req);
	
	FrontpageSession session = idSession.getData();
	session.start();
	session.locale = idSession.getLocale();
	session.sessionId = req.getSession().getId();
	session.compressionEnabled = ServletHelper.supportCompression(req);
	
	handler.handle(req, parsedRequest, idSession, frontEnd);
	
	try
	{
		Generator gen = cache.get(filename, idSession.getLocale());
		if (gen == null) throw new FileNotFoundException(filename);
		CharSequence data = env.generate(gen, idSession, frontEnd, parsedRequest);
		ServletHelper2.send(HttpServletResponse.SC_OK, mimeType, res, data);
	}
	catch (FileNotFoundException e)
	{ rescue(idSession, res, parsedRequest, e); }
	catch (ParserException e)
	{ rescue(idSession, res, parsedRequest, e); }
	catch (GeneratorException e)
	{ rescue(idSession, res, parsedRequest, e); }
}

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (frontEnd.isShutdownSequence())
		throw new IOException("Shutdown In Progress!");
	
	synchronized (frontEnd.getGalaxy())
	{
		try
		{
			internalHandleRequest(req, res);
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "Exception caught", e);
			throw e;
		}
	}
}

@Override
public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	doGet(req, res);
}

}
