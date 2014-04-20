package net.cqs.web.util;

import net.cqs.util.TextConverter;
import de.ofahrt.ulfscript.analysis.xml.XmlState;

public final class XmlTextConverter implements TextConverter
{

private final TextConverter textConverter;

public XmlTextConverter(TextConverter textConverter)
{
	this.textConverter = textConverter;
}

@Override
public String convert(String in)
{
	String result = textConverter.convert(in);
	XmlState state = new XmlState(true);
	state.parse(null, result);
	if (!state.isFragment()) return "XML ERROR";
	return result;
}

}
