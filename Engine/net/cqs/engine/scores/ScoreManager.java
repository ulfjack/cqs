package net.cqs.engine.scores;

import java.util.Comparator;
import java.util.List;

public final class ScoreManager<T extends Scoreable<T>>
{

private static final Comparator<Score<?>> COMP = new Comparator<Score<?>>()
{
	@Override
  public int compare(Score<?> hd1, Score<?> hd2)
	{
		// sort by points, then by secPoints (in case of ties)
		if (hd1.points < hd2.points) return 1;
		if (hd1.points > hd2.points) return -1;
		if (hd1.secPoints < hd2.secPoints) return 1;
		if (hd1.secPoints > hd2.secPoints) return -1;
		return 0;
	}
};

Score<T>[] list;

public ScoreManager(List<T> data)
{
	Score<T>[] scores = newArray(data.size());
	for (int i = 0; i < data.size(); i++)
	{
		T s = data.get(i);
		if ((s == null) || (s.getScore() != null))
			throw new IllegalStateException();
		scores[i] = s.newScore(this);
	}
	list = scores;
	sortList();
}

/**
 * Sorts list descending by points (for players) or by averagePoints (for alliances)
 */
private void sortList()
{
	java.util.Arrays.sort(list, COMP);
	for (int i = 0; i < list.length; i++)
		list[i].position = i;
}

public int size()
{ return list.length; }

/**
 * Returns length of list
 */
public int length()
{	return list.length; }

public T get(int pos)
{ return list[pos].getScoreable(); }

/**
 * Returns HighscoreData at position pos, where 0 <= pos < length
 * if pos is not in this range, an exception is thrown
 */
public Score<T> getData(int pos)
{ return list[pos]; }

/**
 * Adds a Scoreable to list (insertion at end)
 * a new score for s is created
 * if s already had a score, an IllegalArgumentException is thrown
 */
public void addObject(Scoreable<T> s)
{
	Score<T>[] newList = newArray(list.length + 1);
	System.arraycopy(list, 0, newList, 0, list.length);
	if (s.getScore() != null)
		throw new IllegalArgumentException();
	newList[list.length] = s.newScore(this);
	newList[list.length].position = list.length;
	list = newList;
}

/**
 * Checks if an alliance is in the list
 */
public boolean isIn(Score<T> s)
{
	for (int i = 0; i < list.length; i++)
	{
		if (list[i] == s) return true;
	}
	return false;
}

/**
 * Returns true, if the alliance is removed from the list
 * if a == null, a NullPointerException is thrown
 * if a is not in the list, an IllegalArgumentException is thrown
 */
public boolean removeObject(Scoreable<T> sc)
{
	if (sc == null)
		throw new NullPointerException();
	Score<T> s = sc.removeScore();
	if (!isIn(s))
		throw new IllegalArgumentException();
	boolean deleted = false;
	Score<T>[] newList = newArray(list.length - 1);
	for (int i = 0; i < newList.length; i++)
	{
		if (list[i] != s)
		{
			if (deleted)
			{
				newList[i] = list[i + 1];
				newList[i].position = i;
			}
			else
				newList[i] = list[i];
		}
		else
		{
			newList[i] = list[i + 1];
			newList[i].position = i;
			deleted = true;
		}
	}
	list = newList;
	return true;
}

/**
 * Updates position of s in the list.
 */
public void signalChange(Score<T> s)
{
	int pos = s.position;
	if ((pos >= list.length) || (pos < 0) || (list[pos] != s))
	{
//		Galaxy.logger.warning("Score.position was not set correctly, determining right position...");
		sortList(); // sort & recover all positions
		if ((pos >= list.length) || (pos < 0) || (list[pos] != s))
		{
//			Galaxy.logger.severe("Score.position was not recovered correctly for \""+s.getScoreable()+"\", bailing out!");
			return;
		}
	}
	
	// checking if s got better than pred
	while ((pos > 0) && (COMP.compare(s, list[pos - 1]) < 0))
	{
		Score<T> help = list[pos - 1];
		list[pos - 1] = list[pos];
		list[pos] = help;
		
		list[pos - 1].position = pos - 1;
		list[pos].position = pos;
		pos--;
	}
	
	// checking if s got worse than succ
	while ((pos < list.length - 1) && (COMP.compare(s, list[pos + 1]) > 0))
	{
		Score<T> help = list[pos + 1];
		list[pos + 1] = list[pos];
		list[pos] = help;
		
		list[pos + 1].position = pos + 1;
		list[pos].position = pos;
		pos++;
	}
}

@SuppressWarnings("unchecked")
private static <T extends Scoreable<T>> Score<T>[] newArray(int length)
{ return new Score[length]; }

public static <T extends Scoreable<T>> ScoreManager<T> wrap(List<T> data)
{
	return new ScoreManager<T>(data); 
}

}
