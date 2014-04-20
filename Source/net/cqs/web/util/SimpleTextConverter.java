package net.cqs.web.util;

import net.cqs.util.TextConverter;

public final class SimpleTextConverter implements TextConverter
{

public SimpleTextConverter()
{/*OK*/}

@Override
public String convert(String what)
{ return HtmlToolkit.formatText(what); }

}
