package net.cqs.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.cqs.main.resource.Resource;
import net.cqs.main.resource.ResourceManager;
import de.ofahrt.catfish.utils.HttpFieldName;
import de.ofahrt.catfish.utils.MimeType;

public final class RulesServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final String PREFIX = "Doc/Rules/";
private static final String FILE_NAME = "rules.xml";

private final ResourceManager ressourceManager;
//private String XML_FILE;
private String XSLT_FILE = "/usr/share/xml/docbook/stylesheet/nwalsh/xhtml/docbook.xsl";

private byte[] data;
private long lastModified;

private Transformer transformer;

public RulesServlet(ResourceManager ressourceManager)
{
	this.ressourceManager = ressourceManager;
//	this.XML_FILE = frontEnd.getSharedFile(PREFIX+FILE_NAME);
}

private void update() throws IOException
{
	Resource xmlFile = ressourceManager.getResource(PREFIX+FILE_NAME);
	lastModified = xmlFile.lastModified();
	File xsltFile = new File(XSLT_FILE);
	if (lastModified == 0) throw new IOException("Argh!");
	Source xmlSource = new StreamSource(xmlFile.getInputStream());
	Source xsltSource = new StreamSource(xsltFile);
	try
	{
		if (transformer == null)
		{
			TransformerFactory transFact = TransformerFactory.newInstance();
			transformer = transFact.newTransformer(xsltSource);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		transformer.transform(xmlSource, new StreamResult(out));
		data = out.toByteArray();
	}
	catch (Exception e)
	{
		if (e instanceof IOException)
			throw (IOException) e;
		throw (IOException) new IOException().initCause(e);
	}
}

private boolean mustUpdate()
{
	if (data == null) return true;
	Resource xmlFile = ressourceManager.getResource(PREFIX+FILE_NAME);
	return xmlFile.lastModified() > lastModified;
}

@Override
public synchronized void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (mustUpdate()) update();
	
	MimeType mimeType = MimeType.TEXT_HTML;
	if (req.getHeader(HttpFieldName.ACCEPT).contains("application/xhtml+xml"))
		mimeType = MimeType.APPLICATION_XHTML_AND_XML;
	res.setContentType(mimeType.toString());
	res.setStatus(HttpServletResponse.SC_OK);
	OutputStream out = res.getOutputStream();
	out.write(data);
	out.close();
}

}
