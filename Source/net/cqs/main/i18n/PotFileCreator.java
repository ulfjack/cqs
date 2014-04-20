package net.cqs.main.i18n;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.cqs.config.AgeEnum;
import net.cqs.config.BattleMessageEnum;
import net.cqs.config.BattleStateEnum;
import net.cqs.config.BattleTypeEnum;
import net.cqs.config.BuildingEnum;
import net.cqs.config.CheckResult;
import net.cqs.config.EducationEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.InfoEnum;
import net.cqs.config.PlanetEnum;
import net.cqs.config.QueueEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.ResourceEnum;
import net.cqs.config.RightEnum;
import net.cqs.config.Sex;
import net.cqs.engine.AccessLevel;
import net.cqs.engine.diplomacy.ContractType;
import net.cqs.engine.diplomacy.DiplomaticStatus;
import net.cqs.engine.fleets.FleetCommand;
import net.cqs.engine.fleets.FleetCommandRegistry;
import net.cqs.engine.messages.InfoTranslator;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.engine.rtevents.RealTimeEventType;
import net.cqs.engine.units.ModuleNameEnum;
import net.cqs.engine.units.UnitClassEnum;
import net.cqs.engine.units.UnitEnum;
import net.cqs.main.Application;
import net.cqs.main.resource.ResourceManager;
import net.cqs.web.frontpage.FrontpageEnvironment;
import net.cqs.web.game.AjaxEnvironment;
import net.cqs.web.game.GameEnvironment;
import net.cqs.web.game.ReportEnvironment;
import de.ofahrt.i18n.PoEntry;
import de.ofahrt.i18n.PoWriter;

public final class PotFileCreator implements Application
{

public static final List<Class<? extends Enum<?>>> ENUMS_TO_TRANSLATE = collectEnums();

private static List<Class<? extends Enum<?>>> collectEnums()
{
	List<Class<? extends Enum<?>>> list = new ArrayList<Class<? extends Enum<?>>>();
	list.add(AccessLevel.class);
	list.add(AgeEnum.class);
	list.add(BattleMessageEnum.class);
	list.add(BattleStateEnum.class);
	list.add(CheckResult.class);
	list.add(ContractType.class);
	list.add(DiplomaticStatus.class);
	list.add(BattleTypeEnum.class);
	list.add(BuildingEnum.class);
	list.add(EducationEnum.class);
	list.add(ErrorCode.class);
	list.add(InfoEnum.class);
	list.add(PlanetEnum.class);
	list.add(QueueEnum.class);
	list.add(RealTimeEventType.class);
	list.add(ResearchEnum.class);
	list.add(ResourceEnum.class);
	list.add(RightEnum.class);
	list.add(Sex.class);
	list.add(SystemMessageEnum.class);
	list.add(UnitEnum.class);
	list.add(UnitClassEnum.class);
	list.add(EmailEnum.class);
	list.add(PlayerMessageType.class);
	list.add(ModuleNameEnum.class);
	return list;
}

private final ResourceManager manager;

public PotFileCreator(ResourceManager manager)
{
	this.manager = manager;
}

private String getFleetCommandEnglishTranslation(Class<?> clazz)
{
	try
	{
		Method m = clazz.getMethod("englishTranslation", new Class<?>[0]);
		return (String) m.invoke(null);
	}
	catch (Exception e)
	{ throw new RuntimeException(e); }
}

private Collection<PoEntry> getEntries(Class<? extends Enum<?>> classToken)
{
	return new EnumWrapper().getEntries(classToken);
}

List<PoEntry> collectEntries()
{
	ArrayList<PoEntry> entries = new ArrayList<PoEntry>();
	
	{
		String bundleName = AccessDeniedReasonTranslator.getBundleName();
		for (String e : AccessDeniedReasonTranslator.getEnglishTranslations())
		{
			PoEntry entry = new PoEntry.Builder()
				.setContext(bundleName)
				.setMessage(e)
				.build();
			entries.add(entry);
		}
	}
	
	for (Class<? extends Enum<?>> clazz : ENUMS_TO_TRANSLATE)
		entries.addAll(getEntries(clazz));
	
	entries.addAll(TranslationProvider.getTranslations(InfoTranslator.class));
	
	for (Class<?> fleetCommandClass : FleetCommandRegistry.FLEET_COMMAND_TYPES)
	{
		String s = getFleetCommandEnglishTranslation(fleetCommandClass);
		PoEntry entry = new PoEntry.Builder()
				.setContext(FleetCommand.bundleName())
				.setMessage(s)
				.build();
		entries.add(entry);
	}
	
	for (TippsAndTricks trick : TippsAndTricks.values())
	{
		PoEntry entry = new PoEntry.Builder()
				.setContext(TippsAndTricks.bundleName())
				.setMessage(trick.getText())
				.build();
		entries.add(entry);
	}
	
	
	HashSet<String> duplicates = new HashSet<String>();
	EnvironmentDescriptor<?>[] pages = new EnvironmentDescriptor[] { GameEnvironment.DESC, AjaxEnvironment.DESC };
	for (EnvironmentDescriptor<?> page : pages)
	{
		HtmlStringCollection collection = HtmlStringCollection.getCollection(manager, page);
		for (HtmlStringCollection.Entry e : collection)
		{
			if (duplicates.contains(e.getKey())) continue;
			duplicates.add(e.getKey());
			PoEntry.Builder builder = new PoEntry.Builder();
			builder.setContext(collection.bundleName());
			builder.setMessage(e.getKey());
			for (Iterator<String> it = e.fileIterator(); it.hasNext(); )
			{
				String s = it.next();
				if (s.startsWith("./")) s = s.substring(2);
				builder.appendReference(s);
			}
			entries.add(builder.build());
		}
	}
	
	pages = new EnvironmentDescriptor[] {FrontpageEnvironment.DESC, ReportEnvironment.DESC};
	for (EnvironmentDescriptor<?> page : pages)
	{
		duplicates.clear();
		HtmlStringCollection collection = HtmlStringCollection.getCollection(manager, page);
		for (HtmlStringCollection.Entry e : collection)
		{
			if (duplicates.contains(e.getKey())) continue;
			duplicates.add(e.getKey());
			PoEntry.Builder builder = new PoEntry.Builder();
			builder.setContext(collection.bundleName());
			builder.setMessage(e.getKey());
			for (Iterator<String> it = e.fileIterator(); it.hasNext(); )
			{
				String s = it.next();
				if (s.startsWith("./")) s = s.substring(2);
				builder.appendReference(s);
			}
			entries.add(builder.build());
		}
	}
	
	return entries;
}

@Override
public void run(String[] args) throws IOException
{
	Collection<PoEntry> entries = collectEntries();
	
	PoWriter out = new PoWriter(new OutputStreamWriter(new FileOutputStream("cqs.pot"), Charset.forName("UTF-8")), true);
	out.write(entries);
	out.close();
}

}
