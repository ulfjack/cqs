package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetRobCommand extends FleetFightCommand
{

private static final long serialVersionUID = 1L;

private int[] howmuch = new int[4];

public FleetRobCommand()
{ super(Mode.ROB); }

public static String englishTranslation()
{ return "plunder: [{0,number,integer};{1,number,integer};{2,number,integer};{3,number,integer}]"; }

@Override
public String getName(Locale locale)
{
	return format(englishTranslation(), locale,
		Integer.valueOf(howmuch[0]), Integer.valueOf(howmuch[1]),
		Integer.valueOf(howmuch[2]), Integer.valueOf(howmuch[3]));
}

@Override
public FleetCommand copy()
{
	FleetRobCommand result = new FleetRobCommand();
	for (int i = 0; i < howmuch.length; i++)
		result.howmuch[i] = howmuch[i];
	return result;
}

@Override
public String getEditorType()
{ return EDIT_ROB; }

public int[] getAmounts()
{ return howmuch; }

public void setAmounts(int r0, int r1, int r2, int r3)
{
	howmuch[0] = r0 < 0 ? -1 : r0;
	howmuch[1] = r1 < 0 ? -1 : r1;
	howmuch[2] = r2 < 0 ? -1 : r2;
	howmuch[3] = r3 < 0 ? -1 : r3;
}

public void setAmount(int i, int what)
{ howmuch[i] = what; }

@Override
public int check(Fleet f) 
{
	if (super.check(f) < 0) return -1;
	if (f.getPosition().specificity() != Position.COLONY) return -1;
	if (!f.getPosition().isValid(f.getGalaxy())) return -1;
	if (f.findColony().getOwner() == f.getOwner()) return -1;
	return 1;
}

}
