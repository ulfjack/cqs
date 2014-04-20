package net.cqs.web;

import java.util.ArrayList;
import java.util.List;

public final class Entity
{

private final String name;
private final String value;

public Entity(String name, String value)
{
	this.name = name;
	this.value = value;
}

public String getName()
{ return name; }

public String getValue()
{ return value; }

public static List<Entity> listXHtmlStrictEntities()
{
	List<Entity> result = new ArrayList<Entity>();
	result.add(new Entity("amp", "&"));
	result.add(new Entity("gt", ">"));
	result.add(new Entity("lt", "<"));
	result.add(new Entity("quot", "\""));
	
	// Whitelist existing violations.
	result.add(new Entity("ndash", "en dash"));
	result.add(new Entity("lowast", "asterisk"));
	result.add(new Entity("times", "x"));
	result.add(new Entity("infin", "infinity"));
	return result;
}

}