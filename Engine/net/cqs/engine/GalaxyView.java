package net.cqs.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.cqs.config.InvalidPositionException;

public final class GalaxyView implements Iterable<SolarSystem>, Serializable
{

private static final long serialVersionUID = 1L;

private HashMap<SolarSystem,Integer> knownSystems = new HashMap<SolarSystem,Integer>();
private SolarSystem[] data = new SolarSystem[0];

public GalaxyView()
{/*OK*/}

public SolarSystem getSystem(int which)
{
  if ((which < 0) || (which >= data.length))
    throw new InvalidPositionException("Invalid System: "+which);
  return data[which];
}

public int size()
{ return data.length; }

@Override
public Iterator<SolarSystem> iterator()
{
	return new Iterator<SolarSystem>()
		{
			private int next = 0;
			@Override
			public boolean hasNext()
			{ return next < data.length; }
			@Override
			public SolarSystem next()
			{
				if (next >= data.length) throw new NoSuchElementException();
				return data[next++];
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

public void clear()
{
	knownSystems.clear();
	data = new SolarSystem[0];
}

public boolean isKnown(SolarSystem s)
{ return knownSystems.containsKey(s); }

public void addSystem(SolarSystem s)
{
	if (knownSystems.containsKey(s))
		return;
	
	if (s.isInvisible()) return;
	
	SolarSystem[] temp = new SolarSystem[data.length+1];
	System.arraycopy(data, 0, temp, 0, data.length);
	temp[data.length] = s;
	
	data = temp;
	knownSystems.put(s, Integer.valueOf(data.length));
}

}
