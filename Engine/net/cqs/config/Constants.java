package net.cqs.config;

public final class Constants
{

// Display
public static final int MAX_DESKTOP = 9; // 0 <= desktop <= MAX_DESKTOP

// Galaxy
public static final long GALAXY_RADIUS = 240;
public static final long GALAXY_DIAMETER = 2*GALAXY_RADIUS;

// Colony
public static final int POPULATION_FACTOR      = 1;
public static final int PROFESSOR_UPKEEP       = 800;
public static final int PROFESSOR_START        = 3200;
public static final long PROFESSOR_INTERVAL    = 10800;
public static final long AGENT_INTERVAL        = Time.days(1);

public static final int PEOPLE_PER_RESIDENCE = 50000;
public static final int PEOPLE_PER_COLONY    = 5000;

public static final float INVASION_BURN_FACTOR   = 0; //1.0f;
public static final float INVASION_BURN_NULLRATE = 0; //0.5f;

public static final int INVASION_MAX_BUILDINGS = 60;

public static final int INVASION_REQ_TROOPS           = 10;
public static final int INVASION_REQ_TROOPS_HOMEWORLD = 100;

public static final int PROFESSORS_PER_UNIVERSITY = 2;

// Player
public static final long PLAYER_UPKEEP_TIME = Time.hours(1);
public static final long PLAYER_CONTRACT_TIME = Time.minutes(30);
public static final long PLAYER_BLIND_TIME  = -Time.seconds(1);
public static final double PLAYER_RT_FACTOR = 2*0.07929319069148190412; // Math.sqrt(5.0)/28.2, for whatever reason...

// Alliance
public static final long ALLIANCE_SWITCH_TIME = Time.hours(80);

// Diplomacy
public static final long WAIT_ATTACK_ALLIANCE = Time.hours(36);
public static final long WAIT_ATTACK_UNION    = Time.hours(24);
public static final long WAIT_ATTACK_TRADE    = Time.hours(18);
public static final long WAIT_ATTACK_NAP      = Time.hours(12);
public static final long WAIT_ATTACK_WAR      = Time.hours(6);

// Battle
public static final long BATTLE_TIC           = Time.minutes(30);
public static final float BATTLE_POWER_FACTOR = 3.0f;
public static final float BATTLE_MIN_POWER    = 100.0f;
public static final float BATTLE_THRESHOLD    = 25000.0f;

// Fleet
// Not controlled here:
// * FleetDonateCommand
// * FleetNOPCommand
// * parts of FleetMoveCommand
public static final long TRANSMIT_TIME    = Time.hours(12);

public static final long SYSTEM_TO_SYSTEM = Time.hours(84);
public static final long PLANET_TO_PLANET = Time.minutes(100);
public static final long COLONY_TO_COLONY = Time.minutes(30);

public static final long SYSTEM_TO_PLANET = Time.minutes(50);
public static final long PLANET_TO_COLONY = Time.minutes(20);

public static final long PLANET_TO_SYSTEM = Time.minutes(50);
public static final long COLONY_TO_PLANET = Time.minutes(20);

public static final long LAND_TIME        = Time.minutes(2);
public static final long TAKEOFF_TIME     = Time.minutes(2);

public static final long BREAK_BLOCKADE_OUT = 2*COLONY_TO_PLANET;
public static final long BREAK_BLOCKADE_IN  = 2*PLANET_TO_COLONY;

public static final long LOAD_UNLOAD_TIME = Time.seconds(10);
public static final long LOAD_UNITS_TIME  = Time.seconds(10);
public static final long EXPLORE_TIME     = Time.seconds(10);
public static final long COLONIZE_TIME    = Time.seconds(10);
public static final long SETTLE_TIME      = Time.seconds(10);
public static final long SPY_TIME         = Time.seconds(10);
public static final long DISBAND_TIME     = Time.seconds(10);
public static final long STATION_TIME     = Time.seconds(100);

public static final long ATTACK_DELAY     = Time.seconds(10);
public static final long DEFENSE_DELAY    = Time.seconds(10);
public static final long BLOCK_DELAY      = Time.seconds(1);
public static final long WITHDRAW_DELAY   = Time.minutes(10);

// Buildings
public static final double BUILDING_MIN_PERCENT = 0.04;
public static final long BUILDING_CONSTRUCTION_MODIFIER = 79;
public static final long BUILDING_MIN    = 3; // minimum build time (>= 1)

// Units
public static final long UNIT_K        = 33;
public static final long UNIT_L        = 1;
public static final long UNIT_MIN      = 3; // minimum build time (>= 1)
public static final double UNIT_UPKEEP = 100;

// Score
public static final double SCORE_MODIFIER = 1/30.0;
public static final int SCORE_MODIFIER_INVERSE = 30;

/** Returns the number of points someone with x colonies needs to build the next colony. */ 
public static long neededPoints(int x)
{
	long z = x+1;
	long avg = 14L*z*z*z - 150L*z*z + 1400L*z - 1400L;
	return avg*x;
}

private Constants()
{/*OK*/}

}
