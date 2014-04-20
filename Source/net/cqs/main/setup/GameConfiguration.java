package net.cqs.main.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.cqs.auth.Identity;
import net.cqs.main.plugins.PluginEntry;

public final class GameConfiguration
{

public static final String CONFIGURATION_VERSION = "3.0";

	public static final class PlayerDescription
	{
		private final Identity id;
		private final String name;
		private final Locale language;
		private final boolean restricted;
		public PlayerDescription(Identity id, String name, Locale language, boolean restricted)
		{
			this.id = id;
			this.name = name;
			this.language = language;
			this.restricted = restricted;
		}
		public Identity getId()
		{ return id; }
		public String getName()
		{ return name; }
		public Locale getLanguage()
		{ return language; }
		public boolean isRestricted()
		{ return restricted; }
	}
	
	public static final class LoginDescription
	{
		private final String name;
		private final String password;
		private final List<PlayerDescription> players = new ArrayList<PlayerDescription>();
		public LoginDescription(String name, String password)
		{
			this.name = name;
			this.password = password;
		}
		public String getName()
		{ return name; }
		public String getPassword()
		{ return password; }
		public List<PlayerDescription> getPlayers()
		{ return players == null ? Collections.<GameConfiguration.PlayerDescription>emptyList() : Collections.unmodifiableList(players); }
	}
	
	public static final class PlayerStartConfiguration
	{
		private final int research;
		private final int colonies;
		private final int units;
		public PlayerStartConfiguration(int research, int colonies, int units)
		{
			this.research = research;
			this.colonies = colonies;
			this.units = units;
		}
		public PlayerStartConfiguration()
		{ this(0, 0, 0); }
		public int getResearch()
		{ return research; }
		public int getColonies()
		{ return colonies; }
		public int getUnits()
		{ return units; }
	}
	
	public static final class GalaxyDescription
	{
		private final int systems;
		private final int anonymousaiplayers;
		private final PlayerStartConfiguration start;
		private final List<GameConfiguration.PlayerDescription> players = new ArrayList<GameConfiguration.PlayerDescription>();
		private final List<GameConfiguration.LoginDescription> logins = new ArrayList<GameConfiguration.LoginDescription>();
		public GalaxyDescription(int systems, int anonymousaiplayers,
				PlayerStartConfiguration start, GameConfiguration.PlayerDescription[] players,
				GameConfiguration.LoginDescription[] logins)
		{
			this.systems = systems;
			this.anonymousaiplayers = anonymousaiplayers;
			this.start = start;
			for (GameConfiguration.PlayerDescription p : players)
				this.players.add(p);
			for (GameConfiguration.LoginDescription p : logins)
				this.logins.add(p);
		}
		public GalaxyDescription(int systems, int anonymousaiplayers,
				PlayerStartConfiguration start)
		{
			this(systems, anonymousaiplayers, start,
					new GameConfiguration.PlayerDescription[0], new GameConfiguration.LoginDescription[0]);
		}
		public GalaxyDescription(int systems, int anonymousaiplayers)
		{
			this(systems, anonymousaiplayers, new PlayerStartConfiguration(),
					new GameConfiguration.PlayerDescription[0], new GameConfiguration.LoginDescription[0]);
		}
		public int getSystemCount()
		{ return systems; }
		public int getAnonymousAiPlayers()
		{ return anonymousaiplayers; }
		public PlayerStartConfiguration getStartConfiguration()
		{ return start; }
		public List<GameConfiguration.PlayerDescription> getPlayers()
		{ return players == null ? Collections.<GameConfiguration.PlayerDescription>emptyList() : Collections.unmodifiableList(players); }
		public List<GameConfiguration.LoginDescription> getLogins()
		{ return Collections.unmodifiableList(logins); }
	}

private final String config;

private GalaxyDescription galaxy;

private PluginEntry[] plugins;

public GameConfiguration(GalaxyDescription galaxy, PluginEntry[] plugins)
{
	this.config = CONFIGURATION_VERSION;
	this.galaxy = galaxy;
	this.plugins = plugins;
}

public String getConfigVersion()
{ return config; }

public GalaxyDescription getGalaxyDescription()
{ return galaxy; }

public PluginEntry[] getPlugins()
{ return plugins.clone(); }

}
