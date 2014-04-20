package net.cqs.web.action;

class BooleanConverter implements Converter<Boolean>
{

@Override
public Boolean convert(String value)
{
	if ("on".equals(value)) return Boolean.TRUE;
	if ("true".equalsIgnoreCase(value)) return Boolean.TRUE;
	if ("false".equalsIgnoreCase(value)) return Boolean.FALSE;
	return Boolean.FALSE;
}

}