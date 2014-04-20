package net.cqs.main;

import java.io.InputStream;

public interface XmlConversion<T>
{

T fromXml(InputStream in);
T fromXml(String xml);
String toXml(T o);

}
