package net.cqs.web.util;

import net.cqs.util.TextConverter;

public final class PlainTextConverter implements TextConverter
{

public PlainTextConverter()
{/*OK*/}

@Override
public String convert(String what)
{
	StringBuffer result = new StringBuffer();
	HtmlToolkit.htmlConvert(result, what, true, true);
	return result.toString();
}

}
