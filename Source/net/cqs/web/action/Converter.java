package net.cqs.web.action;

public interface Converter<T>
{

T convert(String value);

}