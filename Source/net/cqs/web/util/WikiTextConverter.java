package net.cqs.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.util.TextConverter;

public final class WikiTextConverter implements TextConverter
{

static final Pattern GLOBAL_PATTERN =
	Pattern.compile("(</?pre>)|(</?math>)|(http\\://[^\\s\\)>\"]+)|(\n)");

Matcher globalMatcher = GLOBAL_PATTERN.matcher("");

/*private static final String[] GLYPH_SEARCH =
	{
		"([^\\s\\[{<])?\\'([dmst]\\b|ll\\b|ve\\b|\\s|$)",  // single closing
		"\\'",                    //# single opening
		"([^\\s\\[{])?\"(\\s|$)", //# double closing
		"\"",                     //# double opening
		"\\b( )?\\.{3}",          //# ellipsis
		"\\s--\\s",               //# em dash
		"\\s-\\s",                //# en dash
		"(\\d+)-(\\d+)",          //# en dash
		"(\\d+) ?x ?(\\d+)",      //# dimension sign
		"\\b ?(\\((tm|TM)\\))",   //# trademark
		"\\b ?(\\([rR]\\))",      //# registered
		"\\b ?(\\([cC]\\))"       //# copyright
	};

private static final String[] GLYPH_REPLACE =
	{
		"$1&#8217;$2",  //# single closing
		"&#8216;",      //# single opening
		"$1&#8221;$2",  //# double closing
		"&#8220;",      //# double opening
		"$1&#8230;",    //# ellipsis
		" &#8212; ",    //# em dash
		" &#8211; ",    //# en dash
		"$1&#8211;$2",  //# en dash
		"$1&#215;$2",   //# dimension sign
		"&#8482;",      //# trademark
		"&#174;",       //# registered
		"&#169;"        //# copyright
	};

private static final String[] BLOCK_FIND =
	{
		"^\\s?\\*\\s(.*)",            //# bulleted list *
		"^\\s?#\\s(.*)",              //# numeric list #
		"^h(\\d)\\. (.*)",            //# plain header hn.
		"^p\\. (.*)",                 //# plain paragraph
		"^([^\\t ]+.*)"               //# remaining plain paragraph
	};

private static final String[] BLOCK_REPLACE =
	{
		"\t<liu>$1</liu>",
		"\t<lio>$1</lio>",
		"<h$1>$2</h$1>",
		"<p>$1</p>",
		"<p>$1</p>"
	};*/

// We need to keep some state around. No Multithreading.
private boolean pre = false;
private boolean math = false;
//private boolean lineStart = false;
private boolean preEnd = false;

public WikiTextConverter()
{/*OK*/}

private void convertHtmlSpecialChars(StringBuffer out, String text)
{
	HtmlToolkit.htmlConvert(out, text, false, true);
/*
	(^|\\s|>)
	srcTags[i]
	([^ ])(.+?)?([^\\w\\s]*?)([^ ])
	srcTags[i]
	([^\\w\\s]{0,2})(\\s|$)
*/
}

static final String ALLOWED = "[\\s\\_\\-\\~\\+\\>\\<\\*]";

static final Pattern BOLDPATTERN = Pattern.compile(
	"(?<=^|"+ALLOWED+")\\*([^ ](?:.*?)[^ ])\\*(?="+ALLOWED+"|$)"
	);

static final Pattern ITALIC_PATTERN = Pattern.compile(
	"(?<=^|"+ALLOWED+")\\_([^ ](?:.*?)[^ ])\\_(?="+ALLOWED+"|$)"
	);

static final Pattern DELPATTERN = Pattern.compile(
	"(?<=^|"+ALLOWED+")\\-([^ ](?:.*?)[^ ])\\-(?="+ALLOWED+"|$)"
	);

static final Pattern ADDPATTERN = Pattern.compile(
	"(?<=^|"+ALLOWED+")\\+([^ ](?:.*?)[^ ])\\+(?="+ALLOWED+"|$)"
	);

static final Pattern[] ALLPATTERN = new Pattern[]
	{ BOLDPATTERN, ITALIC_PATTERN, DELPATTERN, ADDPATTERN };

static final String[] ALLREPLACEMENTS = new String[]
	{ "<b>$1</b>", "<i>$1</i>", "<del>$1</del>", "<ins>$1</ins>" };

private void convertStuff(StringBuffer out, String text)
{
	StringBuffer[] temp = new StringBuffer[2];
	int current = 0, next = 1;
	temp[0] = new StringBuffer(text.length());
	temp[1] = new StringBuffer(text.length());
	convertHtmlSpecialChars(temp[0], text);
	
	for (int i = 0; i < ALLPATTERN.length; i++)
	{
		Matcher m = ALLPATTERN[i].matcher(temp[current]);
		while (m.find())
			m.appendReplacement(temp[next], ALLREPLACEMENTS[i]);
		m.appendTail(temp[next]);
		
		temp[current].setLength(0);
		current = next;
		next = 1-next;
	}
	out.append(temp[current]);
}

private void convertWiki(StringBuffer out, String text)
{
	preEnd = false;
	convertStuff(out, text);
//	lineStart = false;
}


// Link Handling
private void convertLink(StringBuffer out, String text)
{
	out.append("<a href=\"").append(text).append("\">");
	convertHtmlSpecialChars(out, text);
	out.append("</a>");
}


// Pre state handling
private void startPre(StringBuffer out)
{
	if (pre) stopPre(out);
	if (math) stopMath(out);
	out.append("<pre>");
	pre = true;
}

private void stopPre(StringBuffer out)
{
	out.append("</pre>");
	pre = false;
	preEnd = true;
}

private void convertPreTag(StringBuffer out, String text)
{
	if (text.charAt(1) == '/')
		stopPre(out);
	else
		startPre(out);
}

private void convertPre(StringBuffer out, String text)
{
	for (int i = 0; i < text.length(); i++)
	{
		char c = text.charAt(i);
		switch (c)
		{
			case 13 :
				break;
			case '<' :
				out.append("&lt;");
				break;
			case '>' :
				out.append("&gt;");
				break;
			case '&' :
				out.append("&amp;");
				break;
			case '"' :
				out.append("&quot;");
				break;
			default :
				out.append(c);
				break;
		}
	}
}


// Math State Handling
private void startMath(StringBuffer out)
{
	if (pre) stopPre(out);
	if (math) stopMath(out);
	out.append("<pre>");
	math = true;
}

private void stopMath(StringBuffer out)
{
	out.append("</pre>");
	math = false;
}

private void convertMathTag(StringBuffer out, String text)
{
	if (text.charAt(1) == '/')
		stopMath(out);
	else
		startMath(out);
}

private void convertMath(StringBuffer out, String text)
{
	for (int i = 0; i < text.length(); i++)
	{
		char c = text.charAt(i);
		switch (c)
		{
			case 13 :
				break;
			case '<' :
				out.append("&lt;");
				break;
			case '>' :
				out.append("&gt;");
				break;
			case '&' :
				out.append("&amp;");
				break;
			case '"' :
				out.append("&quot;");
				break;
			default :
				out.append(c);
				break;
		}
	}
}


// Line Break Handling
void convertLineBreak(StringBuffer out)
{
	if (preEnd)
		out.append("\n");
	else
		out.append("<br />");
	preEnd = false;
//	lineStart = true;
}

private void splitAll(StringBuffer out, String text)
{
	int startAt = 0;
	Matcher matcher = GLOBAL_PATTERN.matcher(text);
	while (matcher.find())
	{
		if (startAt != matcher.start())
		{
			if (pre)
				convertPre(out, text.substring(startAt, matcher.start()));
			else if (math)
				convertMath(out, text.substring(startAt, matcher.start()));
			else
				convertWiki(out, text.substring(startAt, matcher.start()));
		}
		
		String temp;
		if ((temp = matcher.group(1)) != null)
			convertPreTag(out, temp);
		else if ((temp = matcher.group(2)) != null)
			convertMathTag(out, temp);
		else
		{
			if (pre)
				convertPre(out, matcher.group());
			else if (math)
				convertMath(out, matcher.group());
			else
			{
				if ((temp = matcher.group(3)) != null)
					convertLink(out, temp);
				else if ((temp = matcher.group(4)) != null)
					convertLineBreak(out);
				else
					throw new RuntimeException("Internal error!");
			}
		}
		startAt = matcher.end();
	}
	
	if (startAt != text.length())
		convertWiki(out, text.substring(startAt));
	if (pre) stopPre(out);
}

@Override
public String convert(String what)
{
	if (what == null) return "";
	StringBuffer out = new StringBuffer();
	splitAll(out, what);
	return out.toString();
}

}
