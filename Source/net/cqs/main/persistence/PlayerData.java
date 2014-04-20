package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.config.units.Unit;
import net.cqs.engine.messages.Message;
import net.cqs.engine.units.UnitSystemImpl;
import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;

/**
 * Persistente Spieler-Daten.
 * <p>
 * Das wichtigste ist, dass diese Klasse unter GAR KEINEN UMSTAENDEN Referenzen
 * auf die Datenbank hat.
 */
public final class PlayerData implements Serializable
{

private static final long serialVersionUID = 1L;

public static final int MAX_DESIGN = 50;
public static final int MAX_NAMES = 50;

private Mailbox mail;

// Zum Merken von Spielernamen.
private int[] playerNames = new int[5];
private int playerNameCount = 0;

private Unit[] unitDesigns;
private int unitDesignCount;

private boolean ajaxEnabled = true;
private String adminComment;

public PlayerData(Unit[] unitDesigns)
{
	this.mail = new Mailbox(new PlayerMailboxFilter(), 5);
	this.unitDesigns = unitDesigns.clone();
	this.unitDesignCount = unitDesigns.length;
}

public Mailbox getMail()
{ return mail; }

public void addMessage(Message msg)
{ mail.add(msg); }

public int playerAmount()
{ return playerNameCount; }

public int unitAmount()
{ return unitDesignCount; }

public int getPlayer(int which)
{
	if ((which < 0) || (which >= playerNameCount))
		throw new ArrayIndexOutOfBoundsException();
	return playerNames[which];
}

// FIXME: this should return an array with no null elements
public Unit[] getUnitDesigns()
{ return unitDesigns; }

public Unit getUnitDesign(int which)
{
	if ((which < 0) || (which >= unitDesignCount))
		throw new ArrayIndexOutOfBoundsException();
	return unitDesigns[which];
}

public boolean addPlayerName(int who)
{
	boolean found = false;
	for (int i = 0; i < playerNameCount; i++)
	{
		if (playerNames[i] == who)
		{
			found = true;
			break;
		}
	}
	if (!found && (playerNameCount < MAX_NAMES))
	{
		if (playerNameCount == playerNames.length)
		{
			int max = playerNameCount+10;
			if (max > MAX_NAMES) max = MAX_NAMES;
			int[] temp = new int[max];
			System.arraycopy(playerNames, 0, temp, 0, playerNames.length);
			playerNames = temp;
		}
		playerNames[playerNameCount++] = who;
		return true;
	}
	return false;
}

public void removePlayerName(int which)
{
	if ((which >= 0) && (which <= playerNameCount))
	{
		playerNameCount--;
		for (int i = which; i < playerNameCount; i++)
			playerNames[i] = playerNames[i+1];
	}
}

public boolean addDesign(Unit design)
{
	if (design == null) throw new NullPointerException();
	for (int i = 0; i < unitDesigns.length; i++) {
		if (design.equals(unitDesigns[i]))
				return true;
	}
	if (unitDesignCount < MAX_DESIGN)
	{
		if (unitDesignCount == unitDesigns.length)
		{
			int max = unitDesignCount+10;
			if (max > MAX_DESIGN) max = MAX_DESIGN;
			Unit[] temp = new Unit[max];
			System.arraycopy(unitDesigns, 0, temp, 0, unitDesigns.length);
			unitDesigns = temp;
		}
		unitDesigns[unitDesignCount++] = design;
		return true;
	}
	return false;
}

public void removeDesign(int which)
{
	if ((which >= 0) && (which <= unitDesignCount))
	{
		unitDesignCount--;
		for (int i = which; i < unitDesignCount; i++)
			unitDesigns[i] = unitDesigns[i+1];
		unitDesigns[unitDesignCount] = null;
	}
}

public void removeDesign(Unit design)
{
	for (int i = 0; i < unitDesignCount; i++)
		if (unitDesigns[i] == design)
		{
			removeDesign(i);
			return;
		}
}

public void setAjax(boolean enabled)
{ ajaxEnabled = enabled; }

public boolean isAjax()
{ return ajaxEnabled; }

public String getAdminComment()
{ return adminComment; }

public void setAdminComment(String adminComment)
{ this.adminComment = adminComment; }


public static String getBinding(int pid)
{ return pid+"-DATA"; }

public static PlayerData getPlayerData(int pid)
{
	String binding = getBinding(pid);
	PlayerData result;
	try
	{
		result = Context.getDataManager().getBinding(binding, PlayerData.class);
	}
	catch (NameNotBoundException e)
	{
		result = new PlayerData(UnitSystemImpl.getImplementation().getDefaultUnits());
		Context.getDataManager().setBinding(binding, result);
	}
	return result;
}

public static PlayerData getPlayerDataCopy(Storage storage, int pid)
{
	String binding = getBinding(pid);
	try
	{ return storage.getCopy(binding, PlayerData.class); }
	catch (NameNotBoundException e)
	{ return new PlayerData(UnitSystemImpl.getImplementation().getDefaultUnits()); }
}

}
