package net.cqs.engine.actions;

import net.cqs.config.*;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;

public final class AllianceCreateAction extends Action
{

private final Player who;
private final String name;
private final String shortName;

public AllianceCreateAction(Player who, String name, String shortName)
{
	this.who = who;
	this.name = name;
	this.shortName = shortName;
	
	if (who == null) throw new ErrorCodeException();
	if (who.getAlliance() != null) throw new ErrorCodeException();
	
	if (who.getAttr(Attribute.IS_MULTI).booleanValue())
		throw new ErrorCodeException(ErrorCode.RESTRICTED_ACCESS_MULTI);
	
	if (name == null) throw new ErrorCodeException();
	if (shortName == null) throw new ErrorCodeException();
	
	if (!InputValidation.validAllianceName(name))
		throw new ErrorCodeException(ErrorCode.CANNOT_CREATE_ALLI_INVALID_NAME);
	if (!InputValidation.validAllianceShort(shortName))
		throw new ErrorCodeException(ErrorCode.CANNOT_CREATE_ALLI_INVALID_NAME);
}

@Override
public void execute(Galaxy galaxy)
{
	long testTime = galaxy.getTime()-Constants.ALLIANCE_SWITCH_TIME;
	if (who.getLastAllianceChangeTime() > testTime)
		throw new ErrorCodeException(ErrorCode.CANNOT_CREATE_ALLI_TIMEOUT);
	
	if (galaxy.findAllianceByName(name) != null)
		throw new ErrorCodeException(ErrorCode.CANNOT_CREATE_ALLI_DUPLICATE_NAME);
	if (galaxy.findAllianceByName(shortName) != null)
		throw new ErrorCodeException(ErrorCode.CANNOT_CREATE_ALLI_DUPLICATE_NAME);
	
	logger.info("Creating new Alliance: "+name+" ["+shortName+"]");
	galaxy.createAlliance(galaxy.getTime(), who, name, shortName);
}

}
