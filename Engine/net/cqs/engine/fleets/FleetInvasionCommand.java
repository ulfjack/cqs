package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.engine.Fleet;
import net.cqs.engine.Position;

public final class FleetInvasionCommand extends FleetFightCommand
{

private static final long serialVersionUID = 1L;

public FleetInvasionCommand()
{ super(Mode.INVADE); }

public static String englishTranslation()
{ return "invade colony"; }

@Override
public String getName(Locale locale)
{ return lookupTranslation(englishTranslation(), locale); }

@Override
public FleetCommand copy()
{ return new FleetInvasionCommand(); }

@Override
public int check(Fleet f) 
{
	if (super.check(f) < 0) return -1;
	if (!f.getPosition().isValid(f.getGalaxy())) return -1;
	if (f.getPosition().specificity() != Position.COLONY) return -1;
	if (f.findColony().missingTroopsForInvasion(f) > 0) return -1;
	if (f.findColony().getOwner() == f.getOwner()) return -1;
	return 1;
}

}
