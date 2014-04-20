package net.cqs.engine.fleets;

import net.cqs.config.ErrorCode;

public final class FleetContinueException extends FleetException
{

private static final long serialVersionUID = 1L;

public long timeDiff;

public FleetContinueException(String msg)
{ super(msg); }

public FleetContinueException(ErrorCode errorcode)
{ super(errorcode); }

public FleetContinueException()
{ super(); }

}
