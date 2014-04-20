package net.cqs.web.game;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.ResourceEnum;
import net.cqs.config.RightEnum;
import net.cqs.config.Sex;
import net.cqs.engine.Galaxy;
import net.cqs.engine.diplomacy.ContractType;
import net.cqs.engine.diplomacy.DiplomaticStatus;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.AbstractEnvironmentDescriptor;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.main.i18n.StringBundleText;
import net.cqs.web.ParsedRequest;
import net.cqs.web.frontpage.IdSession;
import de.ofahrt.ulfscript.EscapeMode;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.LanguageText;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.utils.AbstractEnvironment;

public class ReportEnvironment extends AbstractEnvironment
{

public static final String NAME         = "REPORTS";
public static final String PREFIX       = "Html/Design/reports/";
public static final String FILE_PATTERN = "*.html";
public static final String BUNDLE_NAME  = "net.cqs.web.Reports";

public static final EnvironmentDescriptor<ReportEnvironment> DESC =
	new AbstractEnvironmentDescriptor<ReportEnvironment>(ReportEnvironment.class);

public ReportEnvironment(SourceProvider sourceProvider)
{
	super(sourceProvider);
	
	defineVariable("Request", ParsedRequest.class);
	defineVariable("Session", IdSession.class);
	defineVariable("Galaxy", Galaxy.class);
	defineVariable("Tools", FrontEndTools.class);
	
	defineClass("Building", BuildingEnum.class);
	defineClass("Education", EducationEnum.class);
	defineClass("Research", ResearchEnum.class);
	defineClass("Resource", ResourceEnum.class);
	defineClass("Right", RightEnum.class);
	defineClass("DiplomaticStatus", DiplomaticStatus.class);
	defineClass("ContractType", ContractType.class);
	defineClass("MessageType", PlayerMessageType.class);
	defineClass("Constants", net.cqs.config.Constants.class);
	defineClass("Sex", Sex.class);
}

@Override
public EscapeMode escapeMode()
{ return EscapeMode.HTML; }

@Override
public LanguageText getLanguageText(Locale locale)
{ return StringBundleText.getLanguageText(DESC.bundleName(), locale); }

public CharSequence generate(Generator generator, IdSession session, FrontEnd frontEnd, ParsedRequest parsedRequest) throws IOException
{
	StringWriter out = new StringWriter(5000);
	generator.generate(out, parsedRequest, session, frontEnd.getGalaxy(), new FrontEndTools(frontEnd));
	return out.getBuffer();
}

}
