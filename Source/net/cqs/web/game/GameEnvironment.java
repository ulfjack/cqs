package net.cqs.web.game;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import net.cqs.config.BuildingEnum;
import net.cqs.config.Constants;
import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.ResourceEnum;
import net.cqs.config.RightEnum;
import net.cqs.config.Sex;
import net.cqs.config.units.Unit;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Position;
import net.cqs.engine.base.UnitSelector;
import net.cqs.engine.diplomacy.ContractType;
import net.cqs.engine.diplomacy.DiplomaticStatus;
import net.cqs.engine.fleets.Speed;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.engine.units.CivilianModulesEnum;
import net.cqs.engine.units.UnitClassEnum;
import net.cqs.engine.units.UnitEnum;
import net.cqs.main.i18n.AbstractEnvironmentDescriptor;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.main.i18n.StringBundleText;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.search.SearchItem;
import net.cqs.web.util.HtmlToolkit;
import de.ofahrt.ulfscript.EscapeMode;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.LanguageText;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.utils.AbstractEnvironment;

public class GameEnvironment extends AbstractEnvironment
{

public static final String NAME         = "INGAME";
public static final String PREFIX       = "Html/Design/";
public static final String FILE_PATTERN = "*.html";
public static final String BUNDLE_NAME  = "net.cqs.web.Ingame";

public static final EnvironmentDescriptor<GameEnvironment> DESC =
	new AbstractEnvironmentDescriptor<GameEnvironment>(GameEnvironment.class);

public GameEnvironment(SourceProvider sourceProvider)
{
	super(sourceProvider);
	
	defineVariable("Request", ParsedRequest.class);
	defineVariable("Session", CqsSession.class);
	defineVariable("Galaxy", Galaxy.class);
	defineVariable("Tools", FrontEndTools.class);
	
	defineClass("String", String.class);
	defineClass("HtmlToolkit", HtmlToolkit.class);
	defineClass("Colony", Colony.class);
	defineClass("Building", BuildingEnum.class);
	defineClass("Education", EducationEnum.class);
	defineClass("Research", ResearchEnum.class);
	defineClass("Resource", ResourceEnum.class);
	defineClass("Right", RightEnum.class);
	defineClass("Unit", Unit.class);
	defineClass("UnitEnum", UnitEnum.class);
	defineClass("UnitClass", UnitClassEnum.class);
	defineClass("Speed", Speed.class);
	defineClass("UnitSelector", UnitSelector.class);
	defineClass("CivilianModules", CivilianModulesEnum.class);
	defineClass("DiplomaticStatus", DiplomaticStatus.class);
	defineClass("ContractType", ContractType.class);
	defineClass("MessageType", PlayerMessageType.class);
	defineClass("Constants", Constants.class);
	defineClass("Sex", Sex.class);
	defineClass("Position", Position.class);
	defineClass("SearchItemType", SearchItem.Type.class);
	defineClass("ImageEnum", ImageEnum.class);
}

@Override
public EscapeMode escapeMode()
{ return EscapeMode.HTML; }

@Override
public LanguageText getLanguageText(Locale locale)
{ return StringBundleText.getLanguageText(DESC.bundleName(), locale); }

public CharSequence generate(Generator gen, CqsSession session, ParsedRequest parsedRequest) throws IOException
{
	StringWriter out = new StringWriter(5000);
	gen.generate(out, parsedRequest, session, session.getGalaxy(), new FrontEndTools(session.getFrontEnd()));
	return out.getBuffer();
}

}
