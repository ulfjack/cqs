package net.cqs.engine;

import java.io.Serializable;

public interface FleetController extends Serializable
{

void nextCommand(Fleet fleet, long time);

boolean isLoopEnabled();
void enableLoop();
void disableLoop();

}
