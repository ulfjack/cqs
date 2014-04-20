package net.cqs.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import de.ofahrt.catfish.utils.ServletHelper;

public class ParsedRequest
{

private static final Pattern PARAMETER_PATTERN = Pattern.compile("(.*?)\\|(.*)");

private final long time;
private final String filename;
private final String basename;
private final List<String> parameters;
private final String method;
private final String todo;
private final boolean compression;
private final HashMap<String,String> queryParameters;
private final HashMap<String,String> postParameters;

public ParsedRequest(HttpServletRequest request, boolean overrideCompression) throws IOException
{
	this.time = System.currentTimeMillis();
	this.filename = ServletHelper.getFilename(request);
	this.method = request.getMethod();
	this.todo = request.getParameter("do");
	this.compression = ServletHelper.supportCompression(request) || overrideCompression;
	this.queryParameters = ServletHelper.parseQuery(request);
	this.postParameters = ServletHelper.parseFormData(request).data;
	this.parameters = new ArrayList<String>();
	
	Matcher m = PARAMETER_PATTERN.matcher(filename);
	if (m.matches())
	{
		basename = m.group(1);
		parameters.add(m.group(2));
	}
	else
		basename = filename;
}

public ParsedRequest(HttpServletRequest request) throws IOException
{ this(request, false); }

public long getTime()
{ return time; }

public String getFilename()
{ return filename; }

public String getBasename()
{ return basename; }

public int getUrlParameterCount()
{ return parameters.size(); }

public String getUrlParameter(int i)
{ 
	if (parameters.size() > i)
		return parameters.get(i);
	return null;
}

public String getMethod()
{ return method; }

public String getTodo()
{ return todo; }

public boolean isCompressionEnabled()
{ return compression; }

public String getQueryParameter(String s)
{ return queryParameters.get(s); }

public String getQueryParameter(String s, String def)
{
	String result = queryParameters.get(s);
	return result != null ? result : def;
}

public Iterator<String> getQueryKeyIterator()
{ return queryParameters.keySet().iterator(); }

public String getPostParameter(String s)
{ return postParameters.get(s); }

public String getPostParameter(String s, String def)
{
	String result = postParameters.get(s);
	return result != null ? result : def;
}

public Iterator<String> getPostKeyIterator()
{ return postParameters.keySet().iterator(); }

public String getParameter(String s)
{ return queryParameters.get(s); }

public String getParameter(String s, String def)
{
	String result = queryParameters.get(s);
	return result != null ? result : def;
}

}
