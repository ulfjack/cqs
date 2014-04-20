package net.cqs.extern;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Locale;

import net.cqs.config.ResearchEnum;
import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyBuilder;
import net.cqs.engine.Player;

import org.junit.Before;
import org.junit.Test;

import de.ofahrt.ulfscript.EscapeMode;
import de.ofahrt.ulfscript.Generator;
import de.ofahrt.ulfscript.SourceProvider;
import de.ofahrt.ulfscript.UlfScript;
import de.ofahrt.ulfscript.io.DirectTextSource;
import de.ofahrt.ulfscript.utils.AbstractEnvironment;
import de.ofahrt.ulfscript.utils.FileSourceProvider;

public class UlfScriptTest
{

	private static class TestEnvironment extends AbstractEnvironment
	{
		public TestEnvironment(SourceProvider sourceProvider)
		{
			super(sourceProvider);
			defineVariable("player", Player.class);
			defineClass("Research", ResearchEnum.class);
		}
		@Override
		public EscapeMode escapeMode()
		{ return EscapeMode.NONE; }
	}

private TestEnvironment env;
private Galaxy galaxy;
private Player player;

@Before
public void setUp()
{
	env = new TestEnvironment(new FileSourceProvider("Html/Design/", "UTF-8"));
	galaxy = GalaxyBuilder.buildTestGalaxy();
	player = galaxy.createTestPlayer(Locale.GERMAN);
}

private String generate(String code) throws Exception
{
	Generator gen = UlfScript.compile(env, new DirectTextSource(code), Locale.US);
	StringWriter out = new StringWriter();
	gen.generate(out, player);
	return out.toString();
}

@Test
public void testA() throws Exception
{
	String a;
	a = generate("%loop topic foreach Research.values;%if topic.validResearchTopic(player);%= topic;%endif;%endloop;");
	assertEquals("", "ENGINEARMORWEAPONS", a);
}

@Test
public void testB() throws Exception
{
	String a;
	a = generate("%loop topic foreach Research.values where topic.getDep = null;%= topic;%endloop;");
	assertEquals("", "ENGINEARMORWEAPONS", a);
}

@Test
public void testC() throws Exception
{
	String a;
	a = generate("%loop topic foreach Research.values where topic.validResearchTopic(player);%= topic;%endloop;");
	assertEquals("", "ENGINEARMORWEAPONS", a);
}

}
