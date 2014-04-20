package de.ofahrt.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PoHelper
{

static String escape(String s)
{
	if (s == null) return null;
	return s.replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("\\\\"))
		.replaceAll(Pattern.quote("\n"), Matcher.quoteReplacement("\\n"))
		.replaceAll(Pattern.quote("\t"), Matcher.quoteReplacement("\\t"))
		.replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("\\\""));
}

static String unescape(String s)
{
	if (s == null) return null;
	if (s.equals("")) return null;
	StringBuilder result = new StringBuilder(s.length());
	for (int i = 0; i < s.length(); i++)
	{
		if (s.charAt(i) == '\\')
		{
			if (i+1 == s.length())
				throw new IllegalArgumentException("The last character of the input may not be a \\!");
			i++;
			char c = s.charAt(i);
			switch (c)
			{
				case 'n' :
					result.append("\n");
					break;
				case 't' :
					result.append("\t");
					break;
				case '\\' :
					result.append("\\");
					break;
				case '"' :
					result.append("\"");
					break;
				default :
					throw new IllegalArgumentException("Unexpected escape sequence \\"+c+"!");
			}
		}
		else
			result.append(s.charAt(i));
	}
	return result.toString();
}

static String[] split(String s, String at)
{
	List<String> result = new ArrayList<String>();
	int i, last = 0;
	while ((i = s.indexOf(at, last)) != -1)
	{
		result.add(s.substring(last, i));
		last = i+at.length();
	}
	result.add(s.substring(last));
	return result.toArray(new String[0]);
}

}
