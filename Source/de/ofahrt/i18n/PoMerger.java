package de.ofahrt.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PoMerger
{

public PoMerger()
{/*OK*/}

private static class Pair
{
	final String context;
	final String message;
	
	public Pair(String context, String message)
	{
		this.context = context;
		this.message = message;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Pair))
			return false;
		Pair pair = (Pair) other;
		return PoEntry.equalsAllowingNull(context, pair.context)
			&& PoEntry.equalsAllowingNull(message, pair.message);
	}
	
	@Override
	public int hashCode()
	{
		return PoEntry.hashCodeIgnoringNull(context)
			+PoEntry.hashCodeIgnoringNull(message);
	}
	
}

static int editDistance(String a, String b)
{
	String colstring, rowstring;
	if (a.length() < b.length())
	{
		colstring = a;
		rowstring = b;
	}
	else
	{
		colstring = b;
		rowstring = a;
	}
	
	if (colstring.isEmpty())
		return rowstring.length();
	
	int[] distances = new int[colstring.length()+1];
	int[] newDistances = new int[colstring.length()+1];
	for (int i = 0; i < distances.length; i++)
		distances[i] = i;

	for (int i = 0; i < rowstring.length(); i++)
	{
		newDistances[0] = distances[0]+2;
		for (int j = 0; j < colstring.length(); j++)
		{
			int tmp = distances[j];
			int cost = Character.toUpperCase(colstring.charAt(j))
				== Character.toUpperCase(rowstring.charAt(i)) ? 1 : 2;
			if (colstring.charAt(j) != rowstring.charAt(i))
				tmp += cost;
			tmp = Math.min(tmp, distances[j+1]+2);
			tmp = Math.min(tmp, newDistances[j]+2);
			newDistances[j+1] = tmp;
		}
		for (int j = 0; j < distances.length; j++)
			distances[j] = newDistances[j];
	}
	return distances[distances.length-1];
}

static PoEntry fuzzyMatch(List<PoEntry> existing, PoEntry toMatch)
{
	PoEntry result = null;
	boolean matchesContext = false;
	int minDist = 2*toMatch.getMessage().length();
	for (PoEntry entry : existing)
	{
		if (entry.getMessage() == null)
			continue;
		int newDist = editDistance(entry.getMessage(), toMatch.getMessage());
		if ((newDist < minDist)
				|| ((newDist == minDist) && !matchesContext
						&& PoEntry.equalsAllowingNull(entry.getContext(),
								toMatch.getContext())))
		{
			result = entry;
			minDist = newDist;
			matchesContext = PoEntry.equalsAllowingNull(entry.getContext(),
					toMatch.getContext());
		}
	}
	return minDist < 3*toMatch.getMessage().length()/2 ? result : null;
}

public List<PoEntry> merge(List<PoEntry> toTranslate, List<PoEntry> existing)
{
	List<PoEntry> result = new ArrayList<PoEntry>();
	HashMap<Pair, PoEntry> map = new HashMap<Pair, PoEntry>();
	for (PoEntry entry : existing)
		map.put(new Pair(entry.getContext(), entry.getMessage()), entry);

	// Save header information first.
	if (existing.size() > 0)
	{
		PoEntry first = existing.get(0);
		if (first.getMessage() == null)
			result.add(first);
	}
	
	for (PoEntry entry : toTranslate)
	{
		if (entry.getMessage().isEmpty())
			continue;
		Pair key = new Pair(entry.getContext(), entry.getMessage());
		PoEntry existingEntry = map.get(key);
		if (existingEntry != null)
		{
			PoEntry.Builder builder = new PoEntry.Builder().copy(entry);
			builder.setTranslation(existingEntry.getTranslation());
			builder.setTranslatorComment(existingEntry.getTranslatorComment());
			result.add(builder.build());
		}
		else
		{
			// Search for fuzzy match.
			PoEntry fuzzyMatch = fuzzyMatch(existing, entry);
			if (fuzzyMatch == null)
				result.add(entry);
			else
			{
				PoEntry.Builder builder = new PoEntry.Builder().copy(entry);
				builder.setTranslation(fuzzyMatch.getTranslation());
				builder.setTranslatorComment(fuzzyMatch.getTranslatorComment());
				builder.appendFlags("fuzzy");
				result.add(builder.build());
			}
		}
	}
	return result;
}

}
