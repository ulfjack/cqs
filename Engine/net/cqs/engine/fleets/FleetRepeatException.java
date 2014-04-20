package net.cqs.engine.fleets;

import net.cqs.config.ErrorCode;

public final class FleetRepeatException extends FleetException
{

private static final long serialVersionUID = 1L;

public FleetRepeatException(String msg)
{ super(msg); }

public FleetRepeatException(ErrorCode errorcode)
{ super(errorcode); }

public FleetRepeatException()
{ super(); }

}
