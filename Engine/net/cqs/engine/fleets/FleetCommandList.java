package net.cqs.engine.fleets;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class FleetCommandList implements Serializable, Iterable<FleetCommand>
{

private static final long serialVersionUID = 1L;

private FleetCommand[] commands = new FleetCommand[0];

public FleetCommandList()
{/*OK*/}

@Override
public Iterator<FleetCommand> iterator()
{
	return new Iterator<FleetCommand>()
		{
			private int next = 0;
			@Override
			public boolean hasNext()
			{ return next < size(); }
			@Override
			public FleetCommand next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				int current = next++;
				return getCommand(current);
			}
			@Override
			public void remove()
			{ throw new UnsupportedOperationException(); }
		};
}

public int size()
{ return commands.length; }

public FleetCommandList copy()
{
	FleetCommandList result = new FleetCommandList();
	for (FleetCommand c : commands)
		result.append(c.copy());
	return result;
}

public FleetCommand getCommand(int which)
{
	if ((which >= 0) && (which < commands.length))
		return commands[which];
	return null;
}

public boolean moveUp(int which, int howfar)
{
  if ((which >= 0) && (which < commands.length) &&
      (which-howfar >= 0) && (which-howfar < commands.length))
  {
    FleetCommand com = commands[which];
    commands[which] = commands[which-howfar];
    commands[which-howfar] = com;
    return true;
  }
  return false;
}

public void prepend(FleetCommand com)
{
  assert com != null;
  
  FleetCommand[] temp = new FleetCommand[commands.length+1];

  for (int i = 0; i < commands.length; i++)
    temp[i+1] = commands[i];
  temp[0] = com;
  
  commands = temp;
}

public void append(FleetCommand com)
{
  assert com != null;
  
  FleetCommand[] temp = new FleetCommand[commands.length+1];
  
  System.arraycopy(commands, 0, temp, 0, commands.length);
  temp[commands.length] = com;
  
  commands = temp;
}

public boolean delete(int which)
{
  if ((which < commands.length) && (which >= 0))
  {
    FleetCommand[] temp = new FleetCommand[commands.length-1];
    int j = 0;
    
    for (int i = 0; i < commands.length; i++)
    {
      if (i != which)
      {
        temp[j++] = commands[i];
      }
    }
    
    commands = temp;
    return true;
  }
  return false;
}

public void deleteInverse(int which)
{ delete(commands.length-1-which); }

public boolean deleteBefore(int which)
{
  if ((which <= commands.length) && (which >= 0))
  {
    FleetCommand[] temp = new FleetCommand[commands.length-which];
    for (int i = which; i < commands.length; i++)
      temp[i-which] = commands[i];
    commands = temp;
    return true;
  }
  return false;
}

public boolean deleteAfter(int which)
{
  if ((which < commands.length) && (which >= -1))
  {
    FleetCommand[] temp = new FleetCommand[which+1];
    System.arraycopy(commands, 0, temp, 0, which+1);
    commands = temp;
    return true;
  }
  return false;
}

public void clear()
{ commands = new FleetCommand[0]; }

}
