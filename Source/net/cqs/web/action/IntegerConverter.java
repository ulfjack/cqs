package net.cqs.web.action;

class IntegerConverter implements Converter<Integer>
{

@Override
public Integer convert(String value)
{ return Integer.valueOf(value); }

}