package net.cqs.main.setup;

import java.io.InputStream;
import java.util.Map;

import net.cqs.auth.Identity;
import net.cqs.main.XmlConversion;
import net.cqs.main.plugins.PluginEntry;
import net.cqs.main.setup.GameConfiguration.LoginDescription;
import net.cqs.main.setup.GameConfiguration.PlayerDescription;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GameConfigurationParser implements XmlConversion<GameConfiguration>
{

	private static class PluginEntryConverter implements Converter
	{
		public PluginEntryConverter()
		{/*OK*/}
		@Override
		public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz)
		{ return clazz.equals(PluginEntry.class); }
		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer,
		                    MarshallingContext context)
		{
			PluginEntry entry = (PluginEntry) value;
			writer.addAttribute("name", entry.getName());
			writer.addAttribute("load", entry.shallLoad() ? "yes" : "no");
			for (Map.Entry<String,String> e : entry.getParams().entrySet())
			{
				writer.startNode("param");
				writer.addAttribute("name", e.getKey());
				writer.addAttribute("value", e.getValue());
				writer.endNode();
			}
		}
		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
                        UnmarshallingContext context)
		{
			boolean shallLoad = "yes".equals(reader.getAttribute("load"));
			PluginEntry result = new PluginEntry(reader.getAttribute("name"), shallLoad);
			while (reader.hasMoreChildren())
			{
				reader.moveDown();
				if (!"param".equals(reader.getNodeName()))
					throw new RuntimeException("Argh!");
				result.add(reader.getAttribute("name"), reader.getAttribute("value"));
				reader.moveUp();
			}
			return result;
		}
	}
	private static class IdentityConverter extends AbstractSingleValueConverter
	{
		public IdentityConverter()
		{/*OK*/}
		@Override
		public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz)
		{ return clazz.equals(Identity.class); }
		@Override
		public Object fromString(String s)
		{ return new Identity(s); }
	}

public GameConfigurationParser()
{/*OK*/}

private XStream getXStream()
{
	XStream xstream = new XStream(new Sun14ReflectionProvider(), new DomDriver());
	xstream.aliasType("cqs", GameConfiguration.class);
	xstream.useAttributeFor(GameConfiguration.class, "config");
	xstream.aliasField("supportemail", GameConfiguration.class, "supportEmail");
	xstream.aliasField("startsimulation", GameConfiguration.class, "startSimulation");
	
	xstream.registerConverter(new PluginEntryConverter());
	xstream.aliasType("plugin", PluginEntry.class);
	
	xstream.aliasType("player", PlayerDescription.class);
	xstream.useAttributeFor(PlayerDescription.class, "id");
	xstream.useAttributeFor(PlayerDescription.class, "name");
	xstream.useAttributeFor(PlayerDescription.class, "language");
	xstream.useAttributeFor(PlayerDescription.class, "restricted");
	
	xstream.aliasType("login", LoginDescription.class);
	xstream.useAttributeFor(LoginDescription.class, "name");
	xstream.useAttributeFor(LoginDescription.class, "password");
	xstream.addImplicitCollection(LoginDescription.class, "players");
	
	xstream.registerConverter(new IdentityConverter());
	xstream.addImmutableType(Identity.class);
	return xstream;
}

@Override
public GameConfiguration fromXml(InputStream in)
{
	return (GameConfiguration) getXStream().fromXML(in);
}

@Override
public GameConfiguration fromXml(String xml)
{
	return (GameConfiguration) getXStream().fromXML(xml);
}

@Override
public String toXml(GameConfiguration config)
{
	return getXStream().toXML(config);
}

}
