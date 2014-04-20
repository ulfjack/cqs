package net.cqs.web.game.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import net.cqs.engine.Position;
import net.cqs.web.game.search.SearchItem.Type;

public final class SearchResult implements Serializable, Iterable<SearchItem>
{

private static final long serialVersionUID = 1L;

private final String mask;
private final int start;
private final int count;
private int current;
private ArrayList<SearchItem> data = new ArrayList<SearchItem>();

public SearchResult(String mask, int start, int count)
{
	this.mask = mask;
	this.start = start;
	this.count = count;
}

public int size()
{ return data.size(); }

@Override
public Iterator<SearchItem> iterator()
{ return data.iterator(); }

public SearchItem get(int i)
{ return data.get(i); }

public String getMask()
{ return mask; }

public int getStart()
{ return start; }

public int getCount()
{ return count; }

public int getTotal()
{ return current; }

public void reset()
{ 
	current = 0;
	data.clear();
}

private void add(SearchItem item)
{
	if ((current >= start) && (current <= start+count))
		data.add(item);
	current++;
}

public void addPlayer(int id)
{ add(new SearchItem(Type.PLAYER, id)); }

public void addAlliance(int id)
{ add(new SearchItem(Type.ALLIANCE, id)); }

public void addColony(Position p)
{ add(new SearchItem(Type.COLONY, p)); }

}
