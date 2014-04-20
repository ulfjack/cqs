package net.cqs.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Tag
{

	public enum TagType
	{
		BLOCK,
		INLINE,
		MIXED,
		SPECIAL;
	}

private final String name;
private final TagType type;
private final Set<String> allowedAttributes = new HashSet<String>();
private final Set<String> requiredAttributes = new HashSet<String>();

private Tag(Tag.Builder builder)
{
	this.name = builder.name;
	this.type = builder.type;
	this.allowedAttributes.addAll(builder.optionalAttributes);
	this.allowedAttributes.addAll(builder.requiredAttributes);
	this.requiredAttributes.addAll(builder.requiredAttributes);
}

public String getName()
{ return name; }

public TagType type()
{ return type; }

public Set<String> allowedAttributes()
{ return allowedAttributes; }

private static Tag.Builder builder(String name, TagType type)
{ return new Builder(name, type); }

private static Tag common(String name, TagType type)
{ return builder(name, type).common().build(); }

  private static final class Builder
  {
  	private final String name;
  	private final TagType type;
  	private final Set<String> requiredAttributes = new HashSet<String>();
  	private final Set<String> optionalAttributes = new HashSet<String>();
  	public Builder(String name, TagType type)
  	{
  		this.name = name;
  		this.type = type;
  	}
  	public Tag build()
  	{ return new Tag(this); }
  	public Tag.Builder required(String... attributes)
  	{
  		for (String attribute : attributes)
  			requiredAttributes.add(attribute);
  		return this;
  	}
  	public Tag.Builder optional(String... attributes)
  	{
  		for (String attribute : attributes)
  			optionalAttributes.add(attribute);
  		return this;
  	}
  	public Tag.Builder javascript(String... attributes)
  	{
  		for (String attribute : attributes)
  			optionalAttributes.add(attribute);
  		return this;
  	}
  	public Tag.Builder common()
  	{ return optional("class", "dir", "id", "lang", "style", "title", "xml:lang"); }
  	public Tag.Builder standard(String... attributes)
  	{ return optional(attributes); }
  }

public static List<Tag> listXHtmlStrictTags()
{
	List<Tag> result = new ArrayList<Tag>();
	result.add(Tag.builder("a", TagType.INLINE).common()
			.optional("charset", "coords", "href", "hreflang", "name", "rel", "rev", "shape")
			.javascript("onclick").build());
	result.add(Tag.common("abbr", TagType.INLINE));
	result.add(Tag.common("acronym", TagType.INLINE));
	result.add(Tag.common("address", TagType.BLOCK));
	result.add(Tag.builder("area", TagType.SPECIAL)
			.required("alt").common().standard("accesskey", "tabindex")
			.optional("coords", "href", "nohref", "shape").build());
	result.add(Tag.common("b", TagType.INLINE));
	result.add(Tag.builder("base", TagType.SPECIAL).optional("href").build());
	result.add(Tag.builder("bdo", TagType.INLINE)
			.required("dir").standard("class", "id", "lang", "style", "title", "xml:lang").build());
	result.add(Tag.common("big", TagType.INLINE));
	result.add(Tag.builder("blockquote", TagType.BLOCK)
			.common()
			.optional("cite").build());
	result.add(Tag.common("body", TagType.SPECIAL));
	result.add(Tag.builder("br", TagType.INLINE).standard("class", "id", "style", "title").build());
	result.add(Tag.builder("button", TagType.MIXED)
			.common().standard("accesskey", "tabindex")
			.optional("disabled", "name", "type", "value")
			.javascript("onclick").build());
	result.add(Tag.common("caption", TagType.SPECIAL));
	result.add(Tag.common("cite", TagType.INLINE));
	result.add(Tag.common("code", TagType.INLINE));
	result.add(Tag.builder("col", TagType.SPECIAL)
			.common()
			.optional("align", "char", "charoff", "span", "valign", "width").build());
	result.add(Tag.builder("colgroup", TagType.SPECIAL)
			.common()
			.optional("align", "char", "charoff", "span", "valign", "width").build());
	result.add(Tag.common("dd", TagType.BLOCK));
	result.add(Tag.builder("del", TagType.MIXED)
			.common()
			.optional("cite", "datetime").build());
	result.add(Tag.common("dfn", TagType.INLINE));
	result.add(Tag.common("div", TagType.BLOCK));
	result.add(Tag.common("dl", TagType.BLOCK));
	result.add(Tag.common("dt", TagType.BLOCK));
	result.add(Tag.common("em", TagType.INLINE));
	result.add(Tag.common("fieldset", TagType.BLOCK));
	result.add(Tag.builder("form", TagType.BLOCK)
			.required("action").common()
			.optional("accept", "accept-charset", "enctype", "method").build());
	result.add(Tag.common("h1", TagType.BLOCK));
	result.add(Tag.common("h2", TagType.BLOCK));
	result.add(Tag.common("h3", TagType.BLOCK));
	result.add(Tag.common("h4", TagType.BLOCK));
	result.add(Tag.common("h5", TagType.BLOCK));
	result.add(Tag.common("h6", TagType.BLOCK));
	result.add(Tag.builder("head", TagType.SPECIAL)
			.standard("dir", "lang", "xml:lang")
			.optional("profile").build());
	result.add(Tag.common("hr", TagType.BLOCK));
	result.add(Tag.builder("html", TagType.SPECIAL)
			.standard("dir", "lang", "xml:lang")
			.optional("xmlns").build());
	result.add(Tag.common("i", TagType.INLINE));
	result.add(Tag.builder("img", TagType.INLINE)
			.required("alt", "src").common()
			.optional("height", "ismap", "longdesc", "usemap", "width").build());
	result.add(Tag.builder("input", TagType.INLINE)
			.common().standard("accesskey", "tabindex")
			.optional("accept", "alt", "checked", "disabled", "maxlength", "name", "readonly", "size", "src", "type", "value")
			.javascript("onclick").build());
	result.add(Tag.builder("ins", TagType.MIXED)
			.common()
			.optional("cite", "datetime").build());
	result.add(Tag.common("kbd", TagType.INLINE));
	result.add(Tag.builder("label", TagType.INLINE)
			.common().standard("accesskey")
			.optional("for").build());
	result.add(Tag.builder("legend", TagType.SPECIAL)
			.common().standard("accesskey").build());
	result.add(Tag.common("li", TagType.BLOCK));
	result.add(Tag.builder("link", TagType.SPECIAL)
			.common()
			.optional("charset", "href", "hreflang", "media", "rel", "rev", "type").build());
	result.add(Tag.builder("map", TagType.MIXED)
			.required("name").common().build());
	result.add(Tag.builder("meta", TagType.SPECIAL)
			.required("content").standard("id", /*not allowed*/ "dir", "lang", "xml:lang")
			.optional("name", "http-equiv", "scheme").build());
	result.add(Tag.common("noscript", TagType.BLOCK));
	result.add(Tag.builder("object", TagType.MIXED)
			.common().standard("tabindex")
			.optional("archive", "classid", "codebase", "codetype", "data", "declare", "height", "name", "standby", "type", "usemap", "width").build());
	result.add(Tag.common("ol", TagType.BLOCK));
	result.add(Tag.builder("optgroup", TagType.SPECIAL)
			.required("label").common()
			.optional("disabled").build());
	result.add(Tag.builder("option", TagType.SPECIAL)
			.common()
			.optional("disabled", "label", "selected", "value").build());
	result.add(Tag.common("p", TagType.BLOCK));
	result.add(Tag.builder("param", TagType.SPECIAL)
			.required("name").standard("id")
			.optional("type", "value", "valuetype").build());
	result.add(Tag.common("pre", TagType.BLOCK));
	result.add(Tag.builder("q", TagType.INLINE)
			.common()
			.optional("cite").build());
	result.add(Tag.common("samp", TagType.INLINE));
	result.add(Tag.builder("script", TagType.MIXED)
			.required("type")
			.optional("charset", "defer", "src").build());
	result.add(Tag.builder("select", TagType.INLINE)
			.common().standard("tabindex")
			.optional("disabled", "multiple", "name", "size")
			.javascript("onchange").build());
	result.add(Tag.common("small", TagType.INLINE));
	result.add(Tag.common("span", TagType.INLINE));
	result.add(Tag.common("strong", TagType.INLINE));
	result.add(Tag.builder("style", TagType.SPECIAL)
			.required("type").standard("dir", "lang", "title", "xml:lang")
			.optional("media").build());
	result.add(Tag.common("sub", TagType.INLINE));
	result.add(Tag.common("sup", TagType.INLINE));
	result.add(Tag.builder("table", TagType.BLOCK)
			.common()
			.optional("border", "cellpadding", "cellspacing", "frame", "rules", "summary", "width").build());
	result.add(Tag.builder("tbody", TagType.BLOCK)
			.common()
			.optional("align", "char", "charoff", "valign").build());
	result.add(Tag.builder("td", TagType.BLOCK)
			.common()
			.optional("abbr", "align", "axis", "char", "charoff", "colspan", "headers", "rowspan", "scope", "valign").build());
	result.add(Tag.builder("textarea", TagType.INLINE)
			.required("cols", "rows").common().standard("accesskey", "tabindex")
			.optional("disabled", "name", "readonly").build());
	result.add(Tag.builder("tfoot", TagType.BLOCK)
			.common()
			.optional("align", "char", "charoff", "valign").build());
	result.add(Tag.builder("th", TagType.BLOCK)
			.common()
			.optional("abbr", "align", "axis", "char", "charoff", "colspan", "headers", "rowspan", "scope", "valign").build());
	result.add(Tag.builder("thead", TagType.BLOCK)
			.common()
			.optional("align", "char", "charoff", "valign").build());
	result.add(Tag.builder("title", TagType.SPECIAL).standard("dir", "lang", "xml:lang").build());
	result.add(Tag.builder("tr", TagType.BLOCK)
			.common()
			.optional("align", "char", "charoff", "valign").build());
	result.add(Tag.common("tt", TagType.INLINE));
	result.add(Tag.common("ul", TagType.BLOCK));
	result.add(Tag.common("var", TagType.INLINE));
	return result;
}

}