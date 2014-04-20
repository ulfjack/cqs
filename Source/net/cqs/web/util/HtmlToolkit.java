package net.cqs.web.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

public final class HtmlToolkit
{

public static final String exceptionToString(Throwable e)
{
	StringWriter out = new StringWriter();
	PrintWriter temp = new PrintWriter(out);
	e.printStackTrace(temp);
	temp.flush();
	return out.toString();
}

@HtmlFragment
public static final String formatText(String what)
{
  StringBuilder result = new StringBuilder();
  for (int i = 0; i < what.length(); i++)
  {
    char c = what.charAt(i);
    switch (c)
    {
      case 13 :
        break;
      case 10 :
        result.append("<br/>");
        break;
      case '<' :
        result.append("&lt;");
        break;
      case '>' :
        result.append("&gt;");
        break;
      case '&' :
        result.append("&amp;");
        break;
      case '"' :
        result.append("&quot;");
        break;
      default :
        result.append(c);
        break;
    }
  }
  return result.toString();
}

@HtmlFragment
public static String asTextAreaContent(String what)
{
	StringBuffer result = new StringBuffer();
	htmlConvert(result, what, false, false);
  return result.toString();
}

/**
 * Converts a string into valid XHTML by encoding special characters as entities
 * (double quotes, less than, greater than and ampersand). Optionally, it can
 * convert line breaks to br tags (if the lineBreak argument is true).
 * Optionally, it can insert spaces every 30 characters to allow browsers to
 * break the lines.
 *
 * @param out the result is written to this StringBuffer
 * @param what String to be converted
 * @param lineBreak if true, line breaks are converted to br tags
 * @param addSpace if true, spaces are added every 30 non-space characters
 */
public static void htmlConvert(StringBuffer out, String what, boolean lineBreak, boolean addSpace)
{
	int nonspaceCount = 0;
	for (int i = 0; i < what.length(); i++)
	{
		char c = what.charAt(i);
		nonspaceCount++;
		switch (c)
		{
			case 13 :
				break;
			case 10 :
				if (lineBreak) out.append("<br />");
				out.append(c);
				nonspaceCount = 0;
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
			case ' ' :
				out.append(' ');
				nonspaceCount = 0;
				break;
			default :
				if ((nonspaceCount == 30) && addSpace)
				{
					out.append(' ');
					nonspaceCount = 0;
				}
				out.append(c);
				break;
		}
	}
}

public static void htmlConvert(StringBuffer out, String what)
{ htmlConvert(out, what, true, true); }

private HtmlToolkit()
{/*Not instantiable*/}

}
