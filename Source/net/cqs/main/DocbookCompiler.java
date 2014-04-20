package net.cqs.main;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class DocbookCompiler
{

private static String XML_FILE = "Html/Rules/rules.xml";
private static String XSLT_FILE = "/usr/share/xml/docbook/stylesheet/nwalsh/xhtml/docbook.xsl";

public static void main(String[] args) throws Exception
{
	File xmlFile = new File(XML_FILE);
	File xsltFile = new File(XSLT_FILE);
	Source xmlSource = new StreamSource(xmlFile);
	Source xsltSource = new StreamSource(xsltFile);
	
	TransformerFactory transFact = TransformerFactory.newInstance();
	Transformer trans = transFact.newTransformer(xsltSource);
	FileOutputStream out = new FileOutputStream("test.xhtml");
	trans.transform(xmlSource, new StreamResult(out));
	out.flush();
	out.close();
}

}
