package net.cqs.web.game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
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
import net.cqs.web.util.JsonToolkit;
import de.ofahrt.catfish.utils.HttpFieldName;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.GeneratorException;
import de.ofahrt.ulfscript.ParserException;
import de.ofahrt.ulfscript.utils.Cache;

public final class JsonServlet extends HttpServlet
{

	private final class ErrorReply implements JsonReply
	{
		private final String error;
		private ErrorReply(String error)
		{ this.error = error; }
		@Override
		public void generate(StringWriter out, CqsSession session, ParsedRequest request)
		{
			out.write("{\"error\":");
			out.write(JsonToolkit.quote("<pre>"+HtmlToolkit.formatText(error)+"</pre>"));
			out.write("}");
		}
	}

	public static interface JsonReply
	{
		public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException;
	}

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger(JsonServlet.class.getName());

private final FrontEnd frontEnd;
private final GameEnvironment env;
private final Cache cache;
private final HashMap<String,JsonReply> replies = new HashMap<String,JsonReply>();

private final ActionHandler handler;

public JsonServlet(final FrontEnd frontEnd, ResourceManager resourceManager)
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
	
	replies.put("test.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"response\":");
				out.write(JsonToolkit.quote("blub"));
				out.write("}");
			}
		});

	replies.put("colony-open-build.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"colony_build\":");
				out.write(JsonToolkit.quote(genAjax("colony-build.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-open-education.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"colony_education\":");
				out.write(JsonToolkit.quote(genAjax("colony-education.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-open-finances.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"colony_finances\":");
				out.write(JsonToolkit.quote(genAjax("colony-finances.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-open-unit-0.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"colony_unit\":");
				out.write(JsonToolkit.quote(genAjax("colony-unit-0.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-open-unit-1.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"colony_unit\":");
				out.write(JsonToolkit.quote(genAjax("colony-unit-1.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-open-unit-2.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"colony_unit\":");
				out.write(JsonToolkit.quote(genAjax("colony-unit-2.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-build.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"events\":");
				out.write(JsonToolkit.quote(genAjax("events.ajax", session, request)));
				out.write(", \"buildingqueue\":");
				out.write(JsonToolkit.quote(genAjax("building-queue.ajax", session, request)));
				out.write(", \"construction_building\":");
				out.write(JsonToolkit.quote(genAjax("construction-building.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-education.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"events\":");
				out.write(JsonToolkit.quote(genAjax("events.ajax", session, request)));
				out.write(", \"colony_education\":");
				out.write(JsonToolkit.quote(genAjax("colony-education.ajax", session, request)));
				out.write(", \"colony_education_basic\":");
				out.write(JsonToolkit.quote(genAjax("colony-education-basic.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("colony-units.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"events\":");
				out.write(JsonToolkit.quote(genAjax("events.ajax", session, request)));
				out.write(", \"unitqueue\":");
				out.write(JsonToolkit.quote(genAjax("unit-queue.ajax", session, request)));
				out.write(", \"construction_unit\":");
				out.write(JsonToolkit.quote(genAjax("construction-unit.ajax", session, request)));
				out.write("}");
			}
		});
	
	replies.put("fleet-commands.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"events\":");
				out.write(JsonToolkit.quote(genAjax("events.ajax", session, request)));
				out.write(", \"orders\":");
				out.write(JsonToolkit.quote(genAjax("fleet-orders.ajax", session, request)));
				out.write("}");
			}
		});
	

	replies.put("preview.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"preview\":");
				out.write(JsonToolkit.quote(genAjax("preview.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("research.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"events\":");
				out.write(JsonToolkit.quote(genAjax("events.ajax", session, request)));
				out.write(", \"researchqueue\":");
				out.write(JsonToolkit.quote(genAjax("research-queue.ajax", session, request)));
				out.write("}");
			}
		});

	replies.put("stats-production.json", new JsonReply()
		{
			@Override
			public void generate(StringWriter out, CqsSession session, ParsedRequest request) throws IOException
			{
				out.write("{\"production\":");
				out.write(JsonToolkit.quote(genAjax("stats-production.ajax", session, request)));
				out.write("}");
			}
		});


}

private CharSequence genAjax(String filename, CqsSession session, ParsedRequest request) throws IOException
{
	Generator gen = cache.get(filename, session.getLocale());
	return env.generate(gen, session, request);
}

private void generate(int code, MimeType mimeType, HttpServletResponse response, JsonReply generator, CqsSession session, ParsedRequest request) throws IOException
{
	StringWriter temp = new StringWriter(5000);
	generator.generate(temp, session, request);
	response.setHeader(HttpFieldName.CACHE_CONTROL, "no-cache");
	response.setHeader(HttpFieldName.PRAGMA, "no-cache");
	response.setStatus(code);
	response.setContentType(mimeType.toString());
	response.setCharacterEncoding("UTF-8");
	Writer sout = response.getWriter();
	sout.append(temp.getBuffer());
}

private void rescue(CqsSession session, HttpServletResponse res, Throwable exception) throws IOException
{
	logger.log(Level.SEVERE, "Exception caught", exception);
	JsonReply gen = new ErrorReply(HtmlToolkit.exceptionToString(exception));
	generate(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MimeType.APPLICATION_JSON, res, gen, session, null);
}

public void handleRequest(HttpServletRequest req, HttpServletResponse res, boolean isPost) throws IOException
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
		if (isPost)
			handler.handle(req, parsedRequest, parsedRequest, session);
		try
		{
			JsonReply gen = replies.get(filename);
			if (gen == null)
			{
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			generate(HttpServletResponse.SC_OK, MimeType.APPLICATION_JSON, res, gen, session, parsedRequest);
		}
		catch (FileNotFoundException e)
		{ rescue(session, res, e); }
		catch (ParserException e)
		{ rescue(session, res, e); }
		catch (GeneratorException e)
		{ rescue(session, res, e); }
	}
}

@Override
public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	handleRequest(req, res, false);
}

@Override
public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	handleRequest(req, res, true);
}

}
