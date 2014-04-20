package net.cqs.web.game.action;

import net.cqs.engine.fleets.FleetCommand;

public interface FleetCommandFactory
{

/**
 * Create a command from the given parameters. Return null if creation fails.
 */
FleetCommand create(ParameterMap params);

}
