package net.cqs.web.util;

import org.dojotoolkit.shrinksafe.Compressor;
import org.mozilla.javascript.Context;

public class JsCompressor
{

private final String data;

public JsCompressor(String data)
{
	this.data = data;
}

public String compress()
{
	Context.enter();
	try
	{
		return Compressor.compressScript(data, 0, 1, null);
	}
	finally
	{ Context.exit(); }
}

}
