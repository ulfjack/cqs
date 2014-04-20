package net.cqs.engine.fleets;

import net.cqs.config.ErrorCode;

public final class FleetAbortException extends FleetException
{

private static final long serialVersionUID = 1L;

public FleetAbortException(String msg)
{ super(msg); }

public FleetAbortException(ErrorCode errorcode)
{ super(errorcode); }

public FleetAbortException()
{ super(); }

}
