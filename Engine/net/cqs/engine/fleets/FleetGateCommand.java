package net.cqs.engine.fleets;

import java.util.Locale;

import net.cqs.config.BuildingEnum;
import net.cqs.config.Constants;
import net.cqs.config.ErrorCode;
import net.cqs.engine.Colony;
import net.cqs.engine.Fleet;
import net.cqs.engine.Position;
import net.cqs.engine.diplomacy.DiplomaticStatus;

public final class FleetGateCommand extends FleetCommand
{

private static final long serialVersionUID = 1L;

public static final int MIN_DISTANCE = 0;

private Position dest;
private boolean returned = false;

public FleetGateCommand(Position dest)
{ this.dest = dest; }

public static String englishTranslation()
{ return "transmit to {0}"; }

@Override
public String getName(Locale locale)
{ return format(englishTranslation(), locale, dest); }

public Position getDestination()
{ return dest; }

@Override
public String getEditorType()
{ return EDIT_NONE; }

@Override
public FleetCommand copy()
{ return new FleetGateCommand(dest); }

@Override
public int check(Fleet f)
{
	if (dest.specificity() != Position.COLONY) return -1;
	if (f.getPosition().specificity() != Position.COLONY) return -1;
	if (!dest.isValid(f.getGalaxy())) return -1;
	if (!f.getPosition().isValid(f.getGalaxy())) return -1;
	if (f.getPosition().equals(dest)) return -1;
	Colony c1 = f.findColony();
	Colony c2 = dest.findColony(f.getGalaxy());
	if ((c1 == null) || (c2 == null)) return -1;
	if ((c1.getBuilding(BuildingEnum.TRANSMITTER) > 0)
		&& (c2.getBuilding(BuildingEnum.TRANSMITTER) > 0))
	{
		return 1;
	}
	return -1;
}

private void checkAllowed(Fleet f) throws FleetAbortException
{
	if (returned) return;
	
	if (!f.getPosition().isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.FLEET_INVALID_POSITION);
	
	if (f.getPosition().specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_NO_COLONY);
	
	if (!dest.isValid(f.getGalaxy()))
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_INVALID_DEST);
	
	if (dest.specificity() != Position.COLONY)
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_INVALID_DEST);
	
	Colony c1 = f.findColony();
	Colony c2 = dest.findColony(f.getGalaxy());
	
	if (c1 == null)
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_INVALID_SOURCE);
	if (c2 == null)
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_INVALID_DEST);
	
	DiplomaticStatus status1 = f.getGalaxy().getDiplomaticRelation().getEntry(f.getOwner(), c1.getOwner()).getStatus();
	if (!status1.canTransmit())
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_BAD_SOURCE_OWNER);

	DiplomaticStatus status2 = f.getGalaxy().getDiplomaticRelation().getEntry(f.getOwner(), c2.getOwner()).getStatus();
	if (!status2.canTransmit())
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_BAD_DEST_OWNER);
	
/*	if (!c1.getOwner().alliedWith(f.getOwner()))
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_BAD_SOURCE_OWNER);
	
	if (!c2.getOwner().alliedWith(f.getOwner()))
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_BAD_DEST_OWNER);*/
	
	if (c1.getBuilding(BuildingEnum.TRANSMITTER) == 0)
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_NO_SOURCE_STAT);
	
	if (c2.getBuilding(BuildingEnum.TRANSMITTER) == 0)
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_NO_DEST_STAT);
	
	if (!c1.getOwner().hasMoney())
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_SOURCE_HAS_NO_MONEY);
	
	if (!c2.getOwner().hasMoney())
		throw new FleetAbortException(ErrorCode.CANNOT_TRANSMIT_DEST_HAS_NO_MONEY);
}

@Override
public long prepare(Fleet f) throws FleetAbortException
{
	checkAllowed(f);
	
	//Position source = f.position;
	//SolarSystem s1 = ((SystemPosition) dest).findSystem();
	//SolarSystem s2 = ((SystemPosition) source).findSystem();
	
	f.unregisterWithSystem();
	
	//double dist = Math.sqrt(s1.distance(s2)) + MIN_DISTANCE;
	
	return Constants.TRANSMIT_TIME;
}

@Override
public void execute(Fleet f) throws FleetException
{
	Position source = f.getPosition();
	f.setPosition(dest);
	f.registerWithSystem();
	
	Colony c2 = dest.findColony(f.getGalaxy());
	if ((c2 == null) || (c2.getBuilding(BuildingEnum.TRANSMITTER) == 0))
	{
		if (returned) 
			f.disband();
		
		dest = source;
		returned = true;
		throw new FleetRepeatException(ErrorCode.FLEET_TRANSMISSION_FAILED);
	}
}

}
