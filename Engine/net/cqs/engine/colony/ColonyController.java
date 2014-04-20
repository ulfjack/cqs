package net.cqs.engine.colony;

import java.io.Serializable;

import net.cqs.config.BuildingEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.Colony;
import net.cqs.engine.base.UnitQueue;
import net.cqs.util.EnumQueue;

public interface ColonyController extends Serializable
{

public static final int MAX_QUEUE = 1000000;

// Buildings
EnumQueue<BuildingEnum> getBuildingQueue();
boolean addBuildingConstruction(BuildingEnum what, int howmany);
boolean addBuildingRemoval(BuildingEnum what, int howmany);
void clearBuildingQueue();
void deleteBuildingEntry(int num);
void modifyBuildingEntry(int num, int delta);
void moveBuildingEntry(int num, int delta);

void nextBuilding(Colony colony, long time, boolean isManualRequest);
void finishBuilding(Colony colony, long time);
void abortBuilding(Colony colony, long time);

// Units
UnitQueue getUnitQueue(int i);
int addUnitConstruction(Unit unit, int howmany);
void deleteUnitEntry(int queue, int num);
void modifyUnitEntry(int queue, int num, int delta);
void moveUnitEntry(int queue, int num, int delta);

void nextUnit(Colony colony, long time, int queue, boolean isManualRequest);
void finishUnit(Colony colony, long time, int queue);
void abortUnit(Colony colony, long time, int queue);

}
