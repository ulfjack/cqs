package net.cqs.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.main.resource.Resource;
import net.cqs.main.resource.ResourceManager;
import de.ofahrt.catfish.utils.MimeType;
import de.ofahrt.catfish.utils.MimeTypeRegistry;
import de.ofahrt.catfish.utils.ServletHelper;

public final class DirectoryServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private final ResourceManager ressourceManager;
private final String prefix;
private final boolean forceCaching;

public DirectoryServlet(ResourceManager ressourceManager, String prefix, boolean forceCaching)
{
	this.ressourceManager = ressourceManager;
	this.prefix = prefix;
	this.forceCaching = forceCaching;
}

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	String filename = ServletHelper.getFilename(req);
	Resource f = ressourceManager.getResource(prefix+filename);
	if (f == null)
	{
		res.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	MimeType mimeType = MimeTypeRegistry.guessFromFilename(filename);
	res.setStatus(HttpServletResponse.SC_OK);
	res.setContentType(mimeType.toString());
	if (mimeType.isText())
		res.setCharacterEncoding("UTF-8");
	
	if (forceCaching)
		res.setDateHeader("Expires", System.currentTimeMillis() + 24L*60*60*1000);
	
	InputStream in = f.getInputStream();
	byte[] buffer = new byte[1024];
	OutputStream out = res.getOutputStream();
	int i;
	while ((i = in.read(buffer)) != -1)
		out.write(buffer, 0, i);
}

@Override
protected long getLastModified(HttpServletRequest req)
{
	String filename = ServletHelper.getFilename(req);
	Resource f = ressourceManager.getResource(prefix+filename);
	if (f != null) return f.lastModified();
	return -1;
}

}
