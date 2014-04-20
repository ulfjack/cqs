package net.cqs.engine;

import java.io.Serializable;

import net.cqs.engine.colony.ColonyController;

/**
 * Implementors of this interface must provide a one-arg constructor of type Player.
 * Instances are created via reflection.
 * 
 * This interface works according to the strategy pattern.
 */
public interface PlayerController extends Serializable
{

ColonyController createColonyController(Colony colony);
FleetController createFleetController(Fleet fleet);

// Research
void nextResearch(Player player, long time);
void finishResearch(Player player, long time);
void abortResearch(Player player, long time);

}
