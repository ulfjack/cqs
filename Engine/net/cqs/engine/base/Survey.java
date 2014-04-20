package net.cqs.engine.base;

import java.io.Serializable;

public final class Survey implements Serializable
{

private static final long serialVersionUID = 1L;

private final int id;
private final String title;
private String[] data;
private int[] votes;

public Survey(int myid, String mytitle)
{
  id = myid;
  title = mytitle;
  data = new String[0];
  votes = new int[0];
}

public int length()
{ return data.length; }

public int getId()
{ return id; }

public String getTitle()
{ return title; }

public String getText(int what)
{ return data[what]; }

public int getVote(int what)
{ return votes[what]; }

public void addVote(int what)
{
	if ((what < 0) || (what >= votes.length))
		throw new IllegalArgumentException();
	votes[what] += 1;
}

public void addText(String s)
{
  String[] temp = new String[data.length+1];
  System.arraycopy(data, 0, temp, 0, data.length);
  temp[data.length] = s;
  data = temp;
  votes = new int[data.length];
}

}
