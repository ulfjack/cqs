package net.cqs.web.admin;

import java.io.IOException;
import java.io.StringWriter;

import net.cqs.engine.Galaxy;
import net.cqs.main.i18n.AbstractEnvironmentDescriptor;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.web.ParsedRequest;
import net.cqs.web.admin.plugins.QuotaTools;
import net.cqs.web.game.FrontEndTools;
import de.ofahrt.ulfscript.EscapeMode;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.utils.AbstractEnvironment;

public final class AdminEnvironment extends AbstractEnvironment
{

public static final String NAME         = "ADMIN";
public static final String PREFIX       = "Html/Admin/";
public static final String FILE_PATTERN = "*.html";
public static final String BUNDLE_NAME  = "net.cqs.plugins.admin";

public static final EnvironmentDescriptor<AdminEnvironment> DESC =
	new AbstractEnvironmentDescriptor<AdminEnvironment>(AdminEnvironment.class);

public AdminEnvironment(SourceProvider sourceProvider)
{
	super(sourceProvider);
	
	defineVariable("Galaxy", Galaxy.class);
	defineVariable("Session", AdminSession.class);
	defineVariable("AdminTools", AdminTools.class);
	defineVariable("Request", ParsedRequest.class);
	defineVariable("Tools", FrontEndTools.class);
	defineVariable("QuotaTools", QuotaTools.class);
}

@Override
public EscapeMode escapeMode()
{ return EscapeMode.HTML; }

public CharSequence generate(Generator generator, AdminServlet servlet, ParsedRequest parsedRequest, AdminSession session) throws IOException
{
	StringWriter out = new StringWriter(5000);
	generator.generate(out, servlet.getFrontEnd().getGalaxy(), session, new AdminTools(servlet),
			parsedRequest, new FrontEndTools(servlet.getFrontEnd()), new QuotaTools(servlet.getFrontEnd()));
	return out.getBuffer();
}

}
