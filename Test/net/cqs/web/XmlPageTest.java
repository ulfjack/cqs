package net.cqs.web;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.I18nTestHelper;
import net.cqs.NamedParameterized;
import net.cqs.NamedParameterized.Parameters;
import net.cqs.main.config.Input;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.main.resource.FileResourceManager;
import net.cqs.web.admin.AdminEnvironment;
import net.cqs.web.frontpage.FrontpageEnvironment;
import net.cqs.web.game.AjaxEnvironment;
import net.cqs.web.game.GameEnvironment;
import net.cqs.web.game.ReportEnvironment;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.ofahrt.ulfscript.Environment;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.UlfScript;
import de.ofahrt.ulfscript.analysis.xml.XmlVisitor;

@RunWith(NamedParameterized.class)
@SuppressWarnings("boxing")
public class XmlPageTest
{

static
{ I18nTestHelper.init(); }

private static final EnvironmentDescriptor<?>[] PAGES = 
	new EnvironmentDescriptor<?>[]
	{
		FrontpageEnvironment.DESC,
		GameEnvironment.DESC,
		AjaxEnvironment.DESC,
		ReportEnvironment.DESC,
		AdminEnvironment.DESC,
	};

@Parameters
public static Collection<Object[]> data()
{
	ArrayList<Object[]> result = new ArrayList<Object[]>();
	
	FileResourceManager manager = new FileResourceManager(new File("."));
	for (final EnvironmentDescriptor<?> page : PAGES)
	{
		final boolean fragment = page == AjaxEnvironment.DESC;
		
		Pattern pattern = Pattern.compile(Input.convertStringToRegex(page.getFilePattern()));
		final Matcher matcher = pattern.matcher("");
		
		String prefix = page.getPrefix();
		File[] fs = new File(prefix).listFiles(new FilenameFilter()
			{
				@Override
        public boolean accept(File dir, String name)
				{ return matcher.reset(name).matches(); }
			});
		
		SourceProvider provider = manager.getSourceProvider(prefix);
		Environment proxy = page.newInstance(provider);
		
		for (File f : fs)
		{
			for (Locale l : I18nTestHelper.TESTED_LOCALES)
			{
				try
				{
					Object[] value = new Object[] { l+" "+prefix+f.getName(), prefix, proxy, f.getName(), l, fragment };
					result.add(value);
				}
				catch (RuntimeException e)
				{ throw new RuntimeException(e); }
			}
		}
	}
	return result;
}

@SuppressWarnings("unused")
private final String name;
private final String prefix;
private final Environment env;
private final String sourceName;
private final Locale locale;

private final boolean fragment;

public XmlPageTest(String name, String prefix, Environment env, String sourceName, Locale locale, boolean fragment)
{
	this.name = name;
	this.prefix = prefix;
	this.env = env;
	this.sourceName = sourceName;
	this.locale = locale;
	this.fragment = fragment;
}

@Test
public void test() throws Throwable
{
	Generator gen = UlfScript.compile(env, sourceName, locale);
	if (!fragment)
		gen.analyze(new XmlVisitor(new StrictXHtmlTestCallback(prefix)));
	else
	{
		// Doesn't work yet!
//		gen.analyze(new XmlVisitor(new FragmentXHtmlTestCallback(prefix)));
	}
}

}
