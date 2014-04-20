package net.cqs.web.frontpage;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import net.cqs.engine.Galaxy;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.AbstractEnvironmentDescriptor;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.main.i18n.StringBundleText;
import net.cqs.web.ParsedRequest;
import de.ofahrt.ulfscript.EscapeMode;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.LanguageText;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.utils.AbstractEnvironment;

public class FrontpageEnvironment extends AbstractEnvironment
{

public static final String NAME         = "FRONTPAGE";
public static final String PREFIX       = "Html/Frontpage/";
public static final String FILE_PATTERN = "*.html";
public static final String BUNDLE_NAME  = "net.cqs.web.Frontpage";

public static final EnvironmentDescriptor<FrontpageEnvironment> DESC = 
	new AbstractEnvironmentDescriptor<FrontpageEnvironment>(FrontpageEnvironment.class);

public FrontpageEnvironment(SourceProvider sourceProvider)
{
	super(sourceProvider);
	
	// add variables
	defineVariable("Game", FrontpageTools.class);
	defineVariable("Identity", IdSession.class);
	defineVariable("Session", FrontpageSession.class);
	defineVariable("Galaxy", Galaxy.class);
	defineVariable("Request", ParsedRequest.class);
}

@Override
public EscapeMode escapeMode()
{ return EscapeMode.HTML; }

@Override
public LanguageText getLanguageText(Locale locale)
{ return StringBundleText.getLanguageText(DESC.bundleName(), locale); }

public CharSequence generate(Generator gen, IdSession idSession, FrontEnd frontEnd, ParsedRequest parsedRequest) throws IOException
{
	StringWriter out = new StringWriter(5000);
	gen.generate(out, new FrontpageTools(frontEnd), idSession, idSession.getData(), frontEnd.getGalaxy(), parsedRequest);
	return out.getBuffer();
}

}
