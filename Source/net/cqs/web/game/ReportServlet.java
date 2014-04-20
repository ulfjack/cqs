package net.cqs.web.game;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.resource.ResourceManager;
import net.cqs.web.ParsedRequest;
import net.cqs.web.frontpage.IdSession;
import net.cqs.web.util.ServletHelper2;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.utils.Cache;

public class ReportServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger("net.cqs.web.reports.ReportsServlet");

private final FrontEnd frontEnd;
private final String source;
private final ReportEnvironment env;
private final Cache cache;

public ReportServlet(FrontEnd frontEnd, ResourceManager resourceManager, String source)
{
	this.frontEnd = frontEnd;
	this.source = source;
	this.env = ReportEnvironment.DESC.newInstance(resourceManager);
	this.cache = new Cache(env);
}

@Override
public synchronized void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	MimeType mimeType = MimeType.TEXT_HTML;
	IdSession session = frontEnd.getIdSession(req);
	
	ParsedRequest parsedRequest = new ParsedRequest(req);
	String filename = parsedRequest.getFilename();
	logger.fine("New request: "+filename);
	
	Generator generator = cache.get(source, session.getLocale());
	CharSequence data = env.generate(generator, session, frontEnd, parsedRequest);
	ServletHelper2.send(HttpServletResponse.SC_OK, mimeType, res, data);
}

}
