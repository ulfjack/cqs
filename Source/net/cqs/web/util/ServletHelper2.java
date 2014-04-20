package net.cqs.web.util;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import de.ofahrt.catfish.utils.HttpFieldName;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.catfish.utils.ServletHelper;

public class ServletHelper2
{

private static final Logger logger = Logger.getLogger("net.cqs.web");

public static void send(int code, MimeType mimeType, HttpServletResponse response, CharSequence data) throws IOException
{
	response.setHeader(HttpFieldName.CACHE_CONTROL, "no-cache");
	response.setHeader(HttpFieldName.PRAGMA, "no-cache");
	response.setStatus(code);
	response.setContentType(mimeType.toString());
	response.setCharacterEncoding("UTF-8");
	Writer sout = response.getWriter();
	sout.append(data);
}

public static void rescue(HttpServletResponse res, String previousError, Throwable exception) throws IOException
{
	logger.log(Level.SEVERE, "Exception caught", exception);
	String error = "<pre>"+HtmlToolkit.formatText(previousError)+"</pre>"
			+ "<br /><br />"
			+ "<pre>"+HtmlToolkit.formatText(HtmlToolkit.exceptionToString(exception))+"</pre>";
	
	res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	res.setCharacterEncoding("UTF-8");
	ServletHelper.setBodyString(res, MimeType.TEXT_HTML, error);
}

}
