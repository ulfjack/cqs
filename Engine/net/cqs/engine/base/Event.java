package net.cqs.engine.base;

import java.io.Serializable;

/**
 * An abstract base class for something that is going to happen sometime in the
 * future.
 * <p>
 * Must be <code>Serializable</code>, because the {@link EventQueue} is stored
 * with the {@link net.cqs.engine.Galaxy}.
 */
public abstract class Event implements Serializable
{

private static final long serialVersionUID = 1L;

public Event()
{/*OK*/}

/**
 * This method is called when the event happens.
 * 
 * @param time the time at which the event happens (current simulation time)
 */
public abstract void activate(long time);

public boolean check(Object o)
{ return false; }

}
