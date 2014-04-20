package net.cqs.main.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.main.config.Input;
import net.cqs.main.i18n.EnvironmentDescriptor;
import net.cqs.main.i18n.I18nHelper;
import net.cqs.main.resource.FileResourceManager;
import net.cqs.web.frontpage.FrontpageEnvironment;
import net.cqs.web.game.AjaxEnvironment;
import net.cqs.web.game.GameEnvironment;
import net.cqs.web.game.ReportEnvironment;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

import de.ofahrt.ulfscript.Environment;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.UlfScript;
import de.ofahrt.ulfscript.analysis.xml.XmlCallback;
import de.ofahrt.ulfscript.analysis.xml.XmlState;
import de.ofahrt.ulfscript.analysis.xml.XmlTesterException;
import de.ofahrt.ulfscript.analysis.xml.XmlVisitor;

public class CssSelectorAnalyzer
{

static
{ I18nHelper.init(); }

private static final EnvironmentDescriptor<?>[] PAGES = 
	new EnvironmentDescriptor<?>[]
	{
		FrontpageEnvironment.DESC,
		GameEnvironment.DESC,
		AjaxEnvironment.DESC,
		ReportEnvironment.DESC,
	};

private static final Locale LOCALE = Locale.US;

	private static class TagEntry
	{
		private final HashSet<String> classes = new HashSet<String>();
		private final HashSet<String> ids = new HashSet<String>();
	}

private HashMap<String,TagEntry> foundTags = new HashMap<String,TagEntry>();
private HashSet<String> foundClasses = new HashSet<String>();
private HashSet<String> foundIds = new HashSet<String>();

public CssSelectorAnalyzer()
{
	// Ok
}

private static Pattern CLASS_RULE = Pattern.compile("\\*\\.([-a-zA-Z0-9_]+)");
private static Pattern ID_RULE = Pattern.compile("\\*\\#([-a-zA-Z0-9_]+)");
private static Pattern TAG_CLASS_RULE = Pattern.compile("([a-zA-Z]+)\\.([-a-zA-Z0-9_]+)");

private void checkRule(CSSStyleRule rule)
{
	String selector = rule.getSelectorText();
//	System.out.println(selector);
	String[] parts = selector.split(" *, *");
	for (String s : parts)
	{
//		System.out.println("PART "+s);
		Matcher m = CLASS_RULE.matcher(s);
		if (m.matches())
		{
			String classname = m.group(1);
			if (!foundClasses.contains(classname))
				System.out.println("NEVER MATCHED: "+s);
		}
		m = ID_RULE.matcher(s);
		if (m.matches())
		{
			String idname = m.group(1);
			if (!foundIds.contains(idname))
				System.out.println("NEVER MATCHED: "+s);
		}
		m = TAG_CLASS_RULE.matcher(s);
		if (m.matches())
		{
			String tagname = m.group(1);
			String classname = m.group(2);
			if (!get(tagname).classes.contains(classname))
				System.out.println("NEVER MATCHES: "+s);
		}
	}
}

private void parseFile(String filename) throws IOException
{
	Reader r = new FileReader(filename);
	InputSource is = new InputSource(r);
	CSSOMParser parser = new CSSOMParser();
	CSSStyleSheet styleSheet = parser.parseStyleSheet(is);
	CSSRuleList list = styleSheet.getCssRules();
	for (int i = 0; i < list.getLength(); i++)
	{
		CSSRule rule = list.item(i);
		switch (rule.getType())
		{
			case CSSRule.UNKNOWN_RULE : throw new IOException("Unknown rule in css file");
			case CSSRule.STYLE_RULE :
				CSSStyleRule srule = (CSSStyleRule) rule;
				checkRule(srule);
				break;
			default :
				System.out.println(rule);
				break;
		}
	}
}

private TagEntry get(String tagname)
{
	TagEntry result = foundTags.get(tagname);
	if (result == null)
	{
		result = new TagEntry();
		foundTags.put(tagname, result);
	}
	return result;
}

public void checkId(String tagname, String idname)
{
	foundIds.add(idname);
	get(tagname).ids.add(idname);
}

public void checkClass(String tagname, String classname)
{
	foundClasses.add(classname);
	get(tagname).classes.add(classname);
}

public void visitPage(Environment env, String sourceName, String prefix, boolean fragment) throws IOException
{
	Generator gen = UlfScript.compile(env, sourceName, LOCALE);
	if (!fragment)
		gen.analyze(new XmlVisitor(new XmlCallback()
			{
				@Override
				public void startTag(XmlState state, String tagName) throws XmlTesterException
				{/*OK*/}
				@Override
				public void addAttribute(XmlState state, String tagName, String key, String value) throws XmlTesterException
				{
					if ("id".equals(key))
						checkId(tagName, value);
					else if ("class".equals(key))
						checkClass(tagName, value);
				}
				@Override
				public void endTag(XmlState state, String tagName, boolean closed) throws XmlTesterException
				{/*OK*/}
				@Override
				public void text(XmlState state, String text)
				{/*OK*/}
				@Override
				public void entity(XmlState state, String entity) throws XmlTesterException
				{/*OK*/}
			}));
	else
	{
		System.out.println("Not analyzing: "+sourceName+" "+prefix);
		// Doesn't work yet!
//		gen.analyze(new XmlVisitor(new FragmentXHtmlTestCallback(path)));
	}
}

public void run() throws IOException
{
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
			visitPage(proxy, f.getName(), prefix, fragment);
		}
	}
	
	parseFile("Html/Design/pack/css/style.css");
	parseFile("Html/Design/pack/css/new.css");
	
}

public static void main(String[] args) throws IOException
{
	new CssSelectorAnalyzer().run();
}

}
