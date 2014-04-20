package net.cqs.main.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import net.cqs.auth.Identity;
import net.cqs.main.plugins.PluginEntry;
import net.cqs.main.setup.GameConfiguration.GalaxyDescription;
import net.cqs.main.setup.GameConfiguration.LoginDescription;
import net.cqs.main.setup.GameConfiguration.PlayerDescription;
import net.cqs.main.setup.GameConfiguration.PlayerStartConfiguration;

import org.junit.Test;

public class GameConfigurationParserTest
{

private String toXml(GameConfiguration config)
{ return new GameConfigurationParser().toXml(config); }

private GameConfiguration fromXml(InputStream in)
{ return new GameConfigurationParser().fromXml(in); }

private GameConfiguration fromXml(String s)
{
	InputStream in = new ByteArrayInputStream(s.getBytes(Charset.forName("UTF-8")));
	return fromXml(in);
}

private InputStream getResourceAsStream(String name)
{
	return GameConfigurationParserTest.class.getClassLoader().getResourceAsStream(name);
}

@Test
public void testSimple()
{
	PluginEntry[] plugins = new PluginEntry[]
		{
			new PluginEntry("net.cqs.plugins.CatfishHttpProvider"),
		};
	plugins[0].add("port", "9292");
	plugins[0].add("threads", "10");
	GalaxyDescription galaxyDescription = new GalaxyDescription(50, 0,
			new PlayerStartConfiguration(),
			new PlayerDescription[] { new PlayerDescription(new Identity("UlfJack@local"), "UlfJack", Locale.GERMANY, false) },
			new LoginDescription[0]);
	GameConfiguration original = new GameConfiguration(galaxyDescription, plugins);
	GameConfiguration result = fromXml(toXml(original));
	assertEquals(original.getConfigVersion(), result.getConfigVersion());
}

@Test
public void supportEmailInDefaultConfiguration()
{
	InputStream in = getResourceAsStream("net/cqs/main/setup/config.xml");
	GameConfiguration config = fromXml(in);
	assertNotNull(config.getGalaxyDescription());
	assertNotNull(config.getGalaxyDescription().getStartConfiguration());
}

@Test
public void testFromXmlWithKnownConstants()
{
	InputStream in = getResourceAsStream("net/cqs/main/setup/test-config.xml");
	GameConfiguration config = fromXml(in);
	assertNotNull(config.getGalaxyDescription());
	assertNotNull(config.getGalaxyDescription().getStartConfiguration());
	PlayerStartConfiguration start = config.getGalaxyDescription().getStartConfiguration();
	assertEquals(1, start.getResearch());
	assertEquals(2, start.getColonies());
	assertEquals(3, start.getUnits());
	List<LoginDescription> logins = config.getGalaxyDescription().getLogins();
	assertNotNull(logins);
	assertEquals(1, logins.size());
	LoginDescription login = logins.get(0);
	assertNotNull(login);
	List<PlayerDescription> players = login.getPlayers();
	assertNotNull(players);
	assertEquals(1, players.size());
	assertNotNull(players.get(0));
}

}
