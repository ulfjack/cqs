package net.cqs.web.admin;

import java.io.InputStream;
import java.util.Map;

import net.cqs.main.XmlConversion;
import net.cqs.web.admin.auth.AllowGroupRule;
import net.cqs.web.admin.auth.AllowPersonRule;
import net.cqs.web.admin.auth.Rule;
import net.cqs.web.admin.auth.RuleList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

final class XmlRuleConverter implements XmlConversion<Object>
{

	private static class RuleConverter implements Converter
	{
		public RuleConverter()
		{/*OK*/}
    @Override
		public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz)
		{
			return clazz.equals(AllowPersonRule.class) ||
				clazz.equals(AllowGroupRule.class) || clazz.equals(Rule.class);
		}
		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer,
		                    MarshallingContext context)
		{
			writer.addAttribute("action", "allow");
			if (value instanceof AllowPersonRule)
			{
				writer.addAttribute("range", "person");
				writer.addAttribute("name", ((AllowPersonRule) value).getName());
			}
			else
			{
				writer.addAttribute("range", "group");
				writer.addAttribute("name", ((AllowGroupRule) value).getName());
			}
		}
		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
                        UnmarshallingContext context)
		{
			if (!"allow".equals(reader.getAttribute("action")))
				throw new RuntimeException("Can only unmarshal action=allow rules!");
			String range = reader.getAttribute("range");
			String name = reader.getAttribute("name");
			if (range.equals("person"))
				return new AllowPersonRule(name);
			else if (range.equals("group"))
				return new AllowGroupRule(name);
			else
				throw new RuntimeException("Can only unmarshal range=person or range=group rules!");
		}
	}
	private static class RuleListConverter implements Converter
	{
		public RuleListConverter()
		{/*OK*/}
		@Override
		public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz)
		{ return clazz.equals(RuleList.class); }
		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer,
		                    MarshallingContext context)
		{
			for (Rule rule : (RuleList) value)
			{
				if (rule instanceof RuleList)
				{
					writer.startNode("ruleset");
					context.convertAnother(rule);
					writer.endNode();
				}
				else
				{
					writer.startNode("rule");
					context.convertAnother(rule);
					writer.endNode();
				}
			}
		}
		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
                        UnmarshallingContext context)
		{
			RuleList result = new RuleList();
			while (reader.hasMoreChildren())
			{
				reader.moveDown();
				Rule rule;
				if ("ruleset".equals(reader.getNodeName()))
					rule = (RuleList) context.convertAnother(result, RuleList.class);
				else
					rule = (Rule) context.convertAnother(result, Rule.class);
				result.add(rule);
				reader.moveUp();
			}
			return result;
		}
	}
	private static class RuleMapConverter implements Converter
	{
		public RuleMapConverter()
		{/*OK*/}
		@Override
		public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz)
		{ return clazz.equals(RuleMap.class); }
		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer,
		                    MarshallingContext context)
		{
			for (Map.Entry<String,Rule> e : (RuleMap) value)
			{
				if (e.getValue() instanceof RuleList)
				{
					writer.startNode("ruleset");
					writer.addAttribute("for", e.getKey());
					context.convertAnother(e.getValue());
					writer.endNode();
				}
				else
				{
					writer.startNode("rule");
					writer.addAttribute("for", e.getKey());
					context.convertAnother(e.getValue());
					writer.endNode();
				}
			}
		}
		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
                        UnmarshallingContext context)
		{
			RuleMap result = new RuleMap();
			while (reader.hasMoreChildren())
			{
				reader.moveDown();
				String target = reader.getAttribute("for");
				Rule rule;
				if ("ruleset".equals(reader.getNodeName()))
					rule = (RuleList) context.convertAnother(result, RuleList.class);
				else
					rule = (Rule) context.convertAnother(result, Rule.class);
				result.add(target, rule);
				reader.moveUp();
			}
			return result;
		}
	}

public XmlRuleConverter()
{/*Ok*/}

private XStream getXStream2()
{
	XStream xstream = new XStream(new DomDriver());
	xstream.registerConverter(new RuleMapConverter());
	xstream.alias("rules", RuleMap.class);
	
	xstream.registerConverter(new RuleConverter());
	xstream.alias("rule", AllowPersonRule.class);
	xstream.alias("rule", AllowGroupRule.class);
	
	xstream.registerConverter(new RuleListConverter());
	xstream.alias("ruleset", RuleList.class);
	xstream.addImplicitCollection(RuleList.class, "list");
	return xstream;
}

@Override
public Object fromXml(String xml)
{ return getXStream2().fromXML(xml); }

@Override
public Object fromXml(InputStream in)
{ return getXStream2().fromXML(in); }

@Override
public String toXml(Object rules)
{ return getXStream2().toXML(rules); }

}
