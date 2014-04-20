package net.cqs.web.game;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.resource.Resource;
import net.cqs.main.resource.ResourceManager;
import net.cqs.web.util.CssCompressor;
import net.cqs.web.util.JsCompressor;
import de.ofahrt.catfish.utils.HttpFieldName;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.catfish.utils.ServletHelper;

public final class CssCompressorServlet extends HttpServlet implements CssCompressorService, JsCompressorService
{

	private static class Entry
	{
		private final MimeType mimeType;
		private final String charset;
		private final byte[] data;
		public Entry(MimeType mimeType, String charset, byte[] data)
		{
			this.mimeType = mimeType;
			this.charset = charset;
			this.data = data;
		}
	}

private static final long serialVersionUID = 1L;

private static final Charset INPUT_CHARSET = Charset.forName("UTF-8");
private static final Charset OUTPUT_CHARSET = Charset.forName("UTF-8");
private static final long YEAR_IN_MILLIS = 1L*365*24*60*60*1000;
private static final long LAST_MODIFIED_DATE = new GregorianCalendar(2009, 0, 1).getTime().getTime();

private final ResourceManager resourceManager;
private final ConcurrentHashMap<String, Entry> content = new ConcurrentHashMap<String, Entry>();
private final ConcurrentHashMap<String, String> ids = new ConcurrentHashMap<String, String>();

public CssCompressorServlet(final FrontEnd frontEnd, ResourceManager resourceManager)
{
	this.resourceManager = resourceManager;
	frontEnd.registerService(CssCompressorService.class, this);
	frontEnd.registerService(JsCompressorService.class, this);
}

// FIXME: Implement this to get last-modified checking!
//protected long getLastModified(HttpServletRequest request)
//{
//	
//}

@Override
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
{
	String filename = ServletHelper.getFilename(request);
	Entry data = content.get(filename);
	if (data == null)
	{
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
	else
	{
		response.setDateHeader(HttpFieldName.LAST_MODIFIED, LAST_MODIFIED_DATE);
		response.setDateHeader(HttpFieldName.EXPIRES, System.currentTimeMillis()+YEAR_IN_MILLIS);
		response.setHeader(HttpFieldName.CACHE_CONTROL, "public");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(data.mimeType.toString());
		if (data.charset != null)
			response.setCharacterEncoding(data.charset);
		OutputStream out = response.getOutputStream();
		out.write(data.data);
	}
}

private static final String CODE = "0123456789abcdef";

public String hexencode(byte[] data)
{
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < data.length; i++)
	{
		byte b = data[i];
		buffer.append(CODE.charAt((b >> 4) & 0x0f));
		buffer.append(CODE.charAt(b & 0x0f));
	}
	return buffer.toString();
}

private String checksum(byte[] data)
{
	try
	{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(data);
		return hexencode(digest.digest());
	}
	catch (NoSuchAlgorithmException e)
	{ throw new RuntimeException(e); }
}

@Override
public String requestCssId(String... cssFiles)
{
	try
	{
		StringBuffer keyBuffer = new StringBuffer();
		Resource[] resources = new Resource[cssFiles.length];
		for (int i = 0; i < cssFiles.length; i++)
		{
			String cssFile = cssFiles[i];
			resources[i] = resourceManager.getResource("Html/Design/"+cssFile);
			keyBuffer.append(cssFile).append('@').append(resources[i].lastModified()).append('\n');
		}
		String key = keyBuffer.toString();
		String id = ids.get(key);
		if (id == null)
		{
			StringBuffer uncompressed = new StringBuffer();
			for (Resource resource : resources)
			{
				Reader in = new InputStreamReader(resource.getInputStream(), INPUT_CHARSET);
				int c;
				while ((c = in.read()) != -1) {
					uncompressed.append((char) c);
				}
				uncompressed.append('\n');
			}
			StringWriter temp = new StringWriter();
			CssCompressor compressor = new CssCompressor(uncompressed);
			compressor.compress(temp);
			
			byte[] compressed = temp.toString().getBytes(OUTPUT_CHARSET);
			id = checksum(compressed)+".css";
			ids.put(key, id);
			content.put(id, new Entry(MimeType.TEXT_CSS, OUTPUT_CHARSET.name(), compressed));
		}
		return id;
	}
	catch (IOException e)
	{ e.printStackTrace(); }
	return null;
}

@Override
public String requestJsId(String... jsFiles)
{
	try
	{
		StringBuffer keyBuffer = new StringBuffer();
		Resource[] resources = new Resource[jsFiles.length];
		for (int i = 0; i < jsFiles.length; i++)
		{
			String jsFile = jsFiles[i];
			resources[i] = resourceManager.getResource("Html/Design/"+jsFile);
			keyBuffer.append(jsFile).append('@').append(resources[i].lastModified()).append('\n');
		}
		String key = keyBuffer.toString();
		String id = ids.get(key);
		if (id == null)
		{
			StringBuffer uncompressed = new StringBuffer();
			for (Resource resource : resources)
			{
				Reader in = new InputStreamReader(resource.getInputStream(), INPUT_CHARSET);
				int c;
				while ((c = in.read()) != -1) {
					uncompressed.append((char) c);
				}
				uncompressed.append('\n');
			}
			JsCompressor compressor = new JsCompressor(uncompressed.toString());
			byte[] compressed = compressor.compress().getBytes(OUTPUT_CHARSET);
			
			id = checksum(compressed)+".js";
			ids.put(key, id);
			content.put(id, new Entry(MimeType.APPLICATION_JAVASCRIPT, OUTPUT_CHARSET.name(), compressed));
		}
		return id;
	}
	catch (IOException e)
	{ e.printStackTrace(); }
	return null;
}

}
