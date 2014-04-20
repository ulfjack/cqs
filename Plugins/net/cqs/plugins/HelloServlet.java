package net.cqs.plugins;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private final String text;

public HelloServlet(String text)
{
	this.text = text;
}

@Override
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
{
	response.setContentType("text/html");
	response.setStatus(HttpServletResponse.SC_OK);
	
	Writer out = response.getWriter();
	out.append("<html><body>");
	out.append(text).append("<br />");
	out.append(request.getRequestURI()).append("<br />");
	out.append(request.getRequestURL()).append("<br />");
	out.append(request.getPathTranslated()).append("<br />");
	out.append("</body></html>");
}


}
