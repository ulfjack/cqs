package net.cqs.web.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import net.cqs.web.ParsedRequest;
import net.cqs.web.action.GetAction;
import net.cqs.web.action.PostAction;
import net.cqs.web.util.ServletHelper2;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.UlfScript;

final class DynamicPage
{

private final AdminEnvironment env;
private final String source;
private final HashMap<String,GetAction> getTable = new HashMap<String,GetAction>();
private final HashMap<String,PostAction> postTable = new HashMap<String,PostAction>();
private final HashMap<Locale,Generator> cache = new HashMap<Locale,Generator>();

public DynamicPage(AdminEnvironment env, String source, PostAction... actions)
{
	this.env = env;
	this.source = source;
	for (PostAction a : actions)
		postTable.put(a.getName(), a);
}

public String getName()
{
	String link = source.substring(0, source.length()-5);
	return link;
}

public void add(PostAction a)
{ postTable.put(a.getName(), a); }

public void add(GetAction a)
{ getTable.put(a.getName(), a); }

public PostAction getPostAction(String name)
{ return postTable.get(name); }

public GetAction getGetAction(String name)
{ return getTable.get(name); }

private Generator get(Locale locale) throws IOException
{
	Generator result = null;
	result = cache.get(locale);
	
	if (result != null)
		if (!result.isUptodate())
			result = null;
	
	if (result == null)
	{
		result = UlfScript.compile(env, source, locale);
		cache.put(locale, result);
	}
	return result;
}

public void handle(HttpServletResponse response, ParsedRequest parsedRequest, AdminServlet servlet, AdminSession session) throws IOException
{
	Generator generator = get(session.getLocale());
	CharSequence data = env.generate(generator, servlet, parsedRequest, session);
	ServletHelper2.send(HttpServletResponse.SC_OK, MimeType.TEXT_HTML, response, data);
}

}