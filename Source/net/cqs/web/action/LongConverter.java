package net.cqs.web.action;

class LongConverter implements Converter<Long>
{

@Override
public Long convert(String value)
{ return Long.valueOf(value); }

}