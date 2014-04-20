package net.cqs.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.util.TextConverter;

public final class LinkTextConverter implements TextConverter
{

static final Pattern LINK_PATTERN = Pattern.compile("(http|ftp)\\://[^\\s\\)]+");

public LinkTextConverter()
{/*OK*/}

private void appendSpecial(StringBuffer out, String what)
{ HtmlToolkit.htmlConvert(out, what); }

private void appendLink(StringBuffer out, String link)
{
	HtmlToolkit.htmlConvert(out, link);
	out.append(" [<a href=\"");
	HtmlToolkit.htmlConvert(out, link, false, false);
	out.append("\">Link</a>]");
}

@Override
public String convert(String what)
{
	StringBuffer result = new StringBuffer();
	int startAt = 0;
	
	Matcher matcher = LINK_PATTERN.matcher(what);
	while (matcher.find())
	{
		appendSpecial(result, what.substring(startAt, matcher.start()));
		appendLink(result, matcher.group());
		startAt = matcher.end();
	}
	appendSpecial(result, what.substring(startAt));
	
	return result.toString();
}

}
