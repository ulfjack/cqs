package net.cqs.engine.fleets;

public final class FleetStopException extends FleetException
{

private static final long serialVersionUID = 3761124959522927411L;

public FleetStopException(String msg)
{ super(msg); }

public FleetStopException()
{ super("No message"); }

}
