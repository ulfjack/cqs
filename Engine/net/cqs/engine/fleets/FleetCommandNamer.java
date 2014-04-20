package net.cqs.engine.fleets;

public abstract class FleetCommandNamer<T extends FleetCommand>
{

private final Class<T> elementClass;

public FleetCommandNamer(Class<T> elementClass)
{
	this.elementClass = elementClass;
}

public Class<T> getCommandClass()
{ return elementClass; }

public abstract String toString(T command);

}
