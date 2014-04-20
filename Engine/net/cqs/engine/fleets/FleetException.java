package net.cqs.engine.fleets;

import net.cqs.config.ErrorCode;

public class FleetException extends Exception
{

private static final long serialVersionUID = 1L;

ErrorCode errorcode = ErrorCode.NONE;

public ErrorCode getErrorCode()
{ return errorcode; }

public FleetException(String msg)
{
	super(msg);
}

public FleetException(ErrorCode errorcode)
{
	super("Error Code: "+errorcode);
	this.errorcode = errorcode;
}

public FleetException()
{ super("No Message!"); }

}
