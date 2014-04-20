package net.cqs.engine.actions;

import java.util.logging.Logger;

import net.cqs.engine.Galaxy;

public abstract class Action
{

protected static final Logger logger = Logger.getLogger("net.cqs.engine.actions");

/**
 * Action can only be subclassed within this package!
 */
public Action()
{/*OK*/}

public abstract void execute(Galaxy galaxy);

}
