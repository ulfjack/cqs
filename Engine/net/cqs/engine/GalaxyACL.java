package net.cqs.engine;

import java.io.Serializable;
import java.util.HashMap;

import net.cqs.auth.Identity;

public final class GalaxyACL implements Serializable
{

private static final long serialVersionUID = 1L;

	public static class PlayerEntry implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private Player player;
		private AccessLevel accessLevel;
		public PlayerEntry(Player player, AccessLevel accessLevel)
		{
			this.player = player;
			this.accessLevel = accessLevel;
		}
		public Player getPlayer()
		{ return player; }
		public AccessLevel getAccessLevel()
		{ return accessLevel; }
	}
	private static class ACLEntry implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private PlayerEntry[] players = new PlayerEntry[0];
		public ACLEntry()
		{/*OK*/}
		public AccessLevel getAccessLevel(Player player)
		{
			for (int i = 0; i < players.length; i++)
				if (players[i].getPlayer() == player) return players[i].getAccessLevel();
			return AccessLevel.NONE;
		}
		public void add(Player player, AccessLevel level)
		{
			PlayerEntry[] temp = new PlayerEntry[players.length+1];
			System.arraycopy(players, 0, temp, 0, players.length);
			temp[players.length] = new PlayerEntry(player, level);
			players = temp;
		}
		public PlayerEntry[] getAccessiblePlayers()
		{ return players; }
	}

private HashMap<Identity,ACLEntry> map = new HashMap<Identity,ACLEntry>();

public GalaxyACL()
{/*OK*/}

public AccessLevel getAccessLevel(Identity id, Player player)
{
	ACLEntry entry = map.get(id);
	if (entry == null) return AccessLevel.NONE;
	return entry.getAccessLevel(player);
}

public PlayerEntry[] getAccessiblePlayers(Identity id)
{
	ACLEntry entry = map.get(id);
	if (entry == null) return new PlayerEntry[0];
	return entry.getAccessiblePlayers();
}

public void add(Identity id, Player player, AccessLevel level)
{
	if (id == null) throw new NullPointerException("id is null");
	if (player == null) throw new NullPointerException("player is null");
	if (level == null) throw new NullPointerException("level is null");
	ACLEntry entry = map.get(id);
	if (entry == null)
	{
		entry = new ACLEntry();
		map.put(id, entry);
	}
	entry.add(player, level);
}

}
