package net.cqs.web.util;

// This class provides tools to generate JSON according to the specification:
// http://www.ietf.org/rfc/rfc4627.txt
public class JsonToolkit
{

// Converts a string to a quoted string, escaping control characters and special characters.
public static String quote(CharSequence s)
{
	StringBuilder result = new StringBuilder();
	result.append("\"");
	for (int i = 0; i < s.length(); i++)
	{
		char c = s.charAt(i);
		switch (c)
		{
			case '"'  : result.append("\\\""); break;
			case '\\' : result.append("\\\\"); break;
			case '\b' : result.append("\\b"); break;
			case '\f' : result.append("\\f"); break;
			case '\n' : result.append("\\n"); break;
			case '\r' : result.append("\\r"); break;
			case '\t' : result.append("\\t"); break;
			case '/'  : result.append("\\/"); break;
			default :
				if ((c <= '\u001F') || ((c >= '\u007F') && (c <= '\u009F')) || (c >= '\u2000' && c <= '\u20FF'))
				{
					result.append("\\u");
					String hex = Integer.toHexString(c);
					for (int k = 0; k < 4-hex.length(); k++)
						result.append('0');
					result.append(hex.toUpperCase());
				}
				else
				{
					result.append(c);
				}
				break;
		}
	}
	result.append("\"");
	return result.toString();
}

}
