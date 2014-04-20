package net.cqs.main.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PluginConfigTest
{

@Test
public void testGet()
{
	PluginConfig cfg = new PluginConfig(null);
	assertNull(cfg.get("xyz"));
}

@Test
public void testSet()
{
	PluginConfig cfg = new PluginConfig(null);
	cfg.add("xyz", "abc");
	assertEquals("abc", cfg.get("xyz"));
}

}
