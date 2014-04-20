package net.cqs.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.cqs.web.Tag.TagType;
import de.ofahrt.ulfscript.analysis.xml.XmlCallback;
import de.ofahrt.ulfscript.analysis.xml.XmlState;
import de.ofahrt.ulfscript.analysis.xml.XmlTesterException;

public class StrictXHtmlTestCallback implements XmlCallback
{

private static final Map<String, Tag> XHTML_TAGS =
		asMap(Tag.listXHtmlStrictTags());
private static final Map<String, Entity> XHTML_ENTITIES =
		entitiesAsMap(Entity.listXHtmlStrictEntities());

private static Map<String, Tag> asMap(Iterable<Tag> tags)
{
	HashMap<String, Tag> result = new HashMap<String, Tag>();
	for (Tag tag : tags)
		result.put(tag.getName(), tag);
	return result;
}

private static Map<String, Entity> entitiesAsMap(Iterable<Entity> entities)
{
	HashMap<String, Entity> result = new HashMap<String, Entity>();
	for (Entity entity : entities)
		result.put(entity.getName(), entity);
	return result;
}

private final boolean fragment;
private final String path;

public StrictXHtmlTestCallback(boolean fragment, String path)
{
	this.fragment = fragment;
	this.path = path;
}

public StrictXHtmlTestCallback(String path)
{ this(false, path); }

public StrictXHtmlTestCallback(boolean fragment)
{ this(fragment, null); }

@Override
public void startTag(XmlState state, String tagName) throws XmlTesterException
{
	Tag tag = XHTML_TAGS.get(tagName);
	if (tag == null)
		throw new XmlTesterException("'"+tagName+"' is not a valid tag!");
	
	if (fragment)
	{
		if (tag.type() == TagType.BLOCK)
  		throw new XmlTesterException("you can't put a block element '"+tag.getName()+"' into a HTML fragment");
	}
	else
	{
		if (state.size() == 0)
		{
			if ("html".equals(tagName))
				return;
			throw new XmlTesterException("html must be the outmost tag!");
		}
		
  	Tag parent = XHTML_TAGS.get(state.getPreviousTag(0));
  	if ((tag.type() == TagType.BLOCK) && (parent.type() == TagType.INLINE))
  		throw new XmlTesterException("you can't put a block element '"+tag.getName()+"' into an inline element '"+parent.getName()+"'");
  	if ("head".equals(tagName))
  	{
  		if (!parent.getName().equals("html"))
  			throw new XmlTesterException("a head tag must be contained in an html tag");
  	}
	}
}

@Override
public void addAttribute(XmlState state, String tagName, String key, String value) throws XmlTesterException
{
	Tag tag = XHTML_TAGS.get(tagName);
	if (tag == null)
		throw new XmlTesterException("'"+tagName+"' is not a valid tag!");
	if (!tag.allowedAttributes().contains(key))
		throw new XmlTesterException("attribute '"+key+"' not allowed on '"+tagName+"'");
	if ("a".equals(tagName) && "href".equals(key))
	{
		int i = value.indexOf('?');
		if (i >= 0) value = value.substring(0, i);
		i = value.indexOf('#');
		if (i >= 0) value = value.substring(0, i);
		i = value.indexOf('|');
		if (i >= 0) value = value.substring(0, i);
		if (value.startsWith("http://")) return;
		if (value.startsWith("irc://")) return;
		if (value.startsWith("mailto:")) return;
		if (value.startsWith("/")) return;
		if (value.length() == 0) return;
		if (value.startsWith("javascript:")) return;
		
		if (path != null)
		{
			if (value.endsWith(".html"))
			{
				File f = new File(path+value);
				if (f.exists()) return;
			}
			File f = new File(path+value+".html");
			if (f.exists()) return;
		}
		throw new XmlTesterException("Illegal href: \""+value+"\"!");
	}
	else if ("form".equals(tagName) && "action".equals(key))
	{
		int i = value.indexOf('?');
		if (i >= 0) value = value.substring(0, i);
		i = value.indexOf('#');
		if (i >= 0) value = value.substring(0, i);
		i = value.indexOf('|');
		if (i >= 0) value = value.substring(0, i);
		if (value.startsWith("/")) return;
		if (value.length() == 0) return;
		if (value.startsWith("javascript:")) return;
		
		if (path != null)
		{
			File f = new File(path+value+".html");
			if (f.exists()) return;
		}
		throw new XmlTesterException("Illegal action: \""+value+"\"!");
	}
	else if ("input".equals(tagName) && "type".equals(key) && "submit".equals(value))
		throw new XmlTesterException("Use button instead of input for submit buttons!");
	else if ("button".equals(tagName) && "value".equals(key))
		throw new XmlTesterException("Don't use the value attribute on buttons, IE doesn't support that!");
}

@Override
public void endTag(XmlState state, String tagName, boolean closed) throws XmlTesterException
{
	if (!XHTML_TAGS.containsKey(tagName))
		throw new XmlTesterException("'"+tagName+"' is not a valid tag!");
	if (closed && "p".equals(tagName))
		throw new XmlTesterException("Empty p is not allowed!");
}

@Override
public void text(XmlState state, String text)
{
	// Ok
}

@Override
public void entity(XmlState state, String entityName) throws XmlTesterException
{
	if ("#160".equals(entityName))
		return;
	if ("nbsp".equals(entityName))
		throw new XmlTesterException("Entity &nbsp; is not allowed, use &#160;!");
	Entity entity = XHTML_ENTITIES.get(entityName);
	if (entity == null)
		throw new XmlTesterException("Entity &" + entityName + "; is not widely supported; use numeric entities instead!");
}

}
