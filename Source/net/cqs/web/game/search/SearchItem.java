package net.cqs.web.game.search;

import java.io.Serializable;

import net.cqs.engine.Position;

public final class SearchItem implements Serializable
{

private static final long serialVersionUID = 1L;

	public static enum Type
	{
		PLAYER, ALLIANCE, COLONY;
	}

private final Type type;
private final int id;
private final Position data;

public SearchItem(Type type, int id)
{
	this.type = type;
	this.id = id;
	this.data = null;
}

public SearchItem(Type type, Position pos)
{
	this.type = type;
	this.id = -1;
	this.data = pos;
}

public Type getType()
{ return type; }

public int getId()
{ return id; }

public Position getPosition()
{ return data; }

}
