package net.cqs.config;

import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum ErrorCode
{

NONE("No error"),

// Verschiedene Fehler-Meldungen
INTERNAL_ERROR("Internal error"),
UNDEFINED_ERROR("Undefined error"),
RESTRICTED_ACCESS("This action is not allowed in the current account."),
RESTRICTED_ACCESS_MULTI("This action is not allowed for members of MULTI."),
INVALID_KOMMISSION_VOTE("You have to commit exactly three votes for the commitee."),
NO_MORE_COLONIES("No colonizable planet was found. This probably means that the game has been running for some time and the other players colonized all available planets. Try your luck in the next round!"),
INVALID_POSITION_ERROR("An invalid position was entered."),

// Warnings
MIXED_DEFENSE_WARNING("Warning: mixed defense of ground and space units."), // Fleet

// Nachricht ungueltig
MESSAGE_NO_BODY("The message contains no text"),
MESSAGE_NO_TARGET("You did not enter a valid recipient for the message."),
MESSAGE_NO_RIGHT("You do not have the rights to write a message here."),
MESSAGE_DELETE_NO_RIGHT("You do not have the rights to delete a message here."),
MESSAGE_NO_SUBJECT("The message contains no subject."),

// Kolonie
NOT_ENOUGH_RESOURCES("Colony %s is lacking %3$s for the construction of %2$s."), // Colony, Building, Resource
NOT_ENOUGH_POPULATION("Colony %s is lacking population for the construction of %s."), // Colony, Resource
NO_PRODUCING_BUILDING("Colony %s does not have a %s, which is necessary for building the unit %s."), // Colony, Building, Unit
//UNKNOWN_BUILDING, // Colony, Building
NOT_ENOUGH_RESOURCES_UNIT("Colony %s is lacking %3$s for drilling the unit %2$s."), // Colony, Unit, Resource
NOT_ENOUGH_POPULATION_UNIT("Colony %s is lacking population for drilling the unit %2$s."), // Colony, Unit
CANNOT_REMOVE_NO_SUCH_BUILDING("Colony %s: You cannot pull down a %s, because you have built none here."), // Colony, Building
UNKNOWN_UNIT("Colony %s: The unit %s cannot be drilled, because you have not satisfied all research dependencies."), // Colony, Unit

// Flotte
FLEET_INTERNAL_ERROR("Fleet %s: An internal error has occured."), // Fleet
FLEET_INVALID_POSITION("Fleet %s: This command cannot be executed because the current position is invalid."), // Fleet

FLEET_CANNOT_START_NO_MONEY("Colony %s: The fleet was not created because you do not have sufficient funds."), // Colony
FLEET_CANNOT_START_NO_UNITS("Colony %s: The fleet was not created because you selected more units than are available."), // Colony
FLEET_CANNOT_START_NO_SELECTION("A fleet was not created because you did not select any units."),

FLEET_CANNOT_MERGE_NO_CMDLIST("Fleets cannot be merged, you did not select an order-list."),
FLEET_CANNOT_MERGE_NOT_VALID("Fleets cannot be merged, a fleet is non-existent."),
CANNOT_MERGE_FLEET_NO_SELECTION("Fleets cannot be merged, you have to select at least two fleets."),
CANNOT_MERGE_FLEET_MOVING("Fleets cannot be merged, all selected fleets have to be stopped first."),
CANNOT_MERGE_FLEET_NOT_SAME_POSITION("Fleets cannot be merged, all selected fleets have to be at the same position."),
CANNOT_MERGE_FLEET_DIFFERENT_LANDED("Fleets cannot be merged, all selected fleets have to have the same status (space/planetary)."),

CANNOT_SPLIT_FLEET_NOT_VALID("Fleet cannot be split, the fleet is non-existent."),
CANNOT_SPLIT_FLEET_WRONG_INPUT("The fleet cannot be split, because you entered invalid data."),
CANNOT_SPLIT_FLEET_MOVING("Fleet cannot be split, you have to stop the fleet first."),
CANNOT_SPLIT_FLEET_GROUND_TRANS("Fleet cannot be split, one of the new fleets could not transport the planetary units."),
CANNOT_SPLIT_FLEET_RES_TRANS("Fleet cannot be split, one of the new fleets could not transport all resources."),

CANNOT_ATTACK_NO_COLONY("The fleet has to be at a colony position to attack."), // Fleet
CANNOT_ATTACK_INVALID_POSITION("You cannot attack, because the current position is invalid."), // Fleet
CANNOT_ATTACK_IS_ALLIED("You cannot attack allied members."), // Fleet
CANNOT_ATTACK_DIPLOMACY("You cannot attack due to diplomatic contracts."), // Fleet
CANNOT_ATTACK_DIPLOMACY_WAIT("You cannot attack because you cancelled a contract only recently or not enough time passed since you declared war."), // Fleet
CANNOT_ATTACK_SPACE_IS_GROUND("A space fleet has to be in space to attack."), // Fleet

CANNOT_LOAD_NOT_LANDED("Cannot load, as fleet is not landed."), // Fleet
CANNOT_UNLOAD_NOT_LANDED("Cannot unload, as fleet is not landed."), // Fleet
CANNOT_LOAD_NOT_MY_COLONY("Cannot load, as fleet is not on one of your own colonies."), // Fleet
CANNOT_LOAD_NO_COLONY("Cannot load, no colony to load from."), // Fleet
CANNOT_UNLOAD_NO_COLONY("Cannot unload, no colony to load to."),// Fleet
CANNOT_UNLOAD_DIPLOMACY("Cannot unload, your diplomatic relations are not sufficient."),// Fleet

CANNOT_INVADE_NOT_ENOUGH_TROOPS("Cannot invade, you do not have enough infantry."), // Fleet, needed Infantry
CANNOT_INVADE_NO_COLONY("Cannot invade, no colony to invade there."), // Fleet
CANNOT_INVADE_YOURSELF("Cannot invade, this is one of your own colonies."), // Fleet
CANNOT_INVADE_CIVILPOINTS("Cannot invade, you do not have enough civilization points to found a new colony."), // Fleet

CANNOT_MOVE_INVALID_DEST("Cannot move, destination is invalid."), // Fleet

CANNOT_COLONIZE_CIVILPOINTS("Cannot colonize, you do not have enough civilization points to found a new colony."), // Fleet
CANNOT_COLONIZE_NO_PLANET("Cannot colonize, your fleet has to be on a planet."), // Fleet
CANNOT_COLONIZE_ALREADY_HAVE("Cannot colonize, you already have a colony on this planet."), // Fleet
CANNOT_COLONIZE_NO_SPACE("Cannot colonize, the planet is overcrowded already."), // Fleet
CANNOT_COLONIZE_NO_UNIT("Cannot colonize, your fleet does not have any settlers."), // Fleet
CANNOT_COLONIZE_PLANET_BLOCKED("Cannot colonize, this planet is being blocked."), // Fleet

CANNOT_EXPLORE_NO_PLANET("Cannot explore, your fleet is not on a planet."), // Fleet
CANNOT_EXPLORE_NO_UNIT("Cannot explore, your fleet does not have any pioneers."), // Fleet

CANNOT_SETTLE_NO_COLONY("Cannot add settler, you do not have a colony on the planet."), // Fleet
CANNOT_SETTLE_NO_UNIT("Cannot add settler, your fleet does not have enough settlers."), // Fleet

CANNOT_ROB_NO_COLONY("Cannot rob, there is no colony to rob."), // Fleet
CANNOT_ROB_YOURSELF("Cannot rob, this is your own colony."), // Fleet

CANNOT_SPY_NO_COLONY("Cannot spy, there is no colony to spy on."), // Fleet
CANNOT_SPY_OWN_COLONY("Cannot spy, this is your own colony."), // Fleet
CANNOT_SPY_NO_UNIT("Cannot spy, your fleet does not have a spy."), // Fleet

CANNOT_STATION_NO_PLANET("Cannot station, your fleet is not on a planet."), // Fleet
CANNOT_STATION_NOT_MY_COLONY("Cannot station, this is not your colony."), // Fleet
CANNOT_STATION_NOT_ABOVE_COLONY("Cannot station, your fleet is not at the position of your colony (and you have one on this planet)."), // Fleet

CANNOT_LEAVE_SYSTEM("Fleet cannot leave system, not all units in your fleet have a warp engine."), // Fleet
CANNOT_TAKEOFF("Fleet cannot takeoff, too many planetary units."), // Fleet
CANNOT_LAND_NOT_ALLIED("Fleet cannot land here, fleet is not allied with the colony owner."), // Fleet
CANNOT_PASS_BLOCKADE_OUT("Fleet cannot leave blockade."), // Fleet
CANNOT_PASS_BLOCKADE_IN("Fleet cannot enter blockade."), // Fleet

CANNOT_DONATE_NO_COLONY("Cannot donate, no colony to donate it to."), // Fleet
CANNOT_DONATE_NOT_ALLIED("Cannot donate, you are not allied with this player."), // Fleet
CANNOT_DONATE_ATTACK_UNITS("Cannot donate, you are not allowed to donate battle units."), // Fleet

CANNOT_LIFTOFF_NO_COLONY("Fleet %s: The fleet cannot lift off, because it is not at a colony."), // Fleet
CANNOT_LIFTOFF_ALREADY_SPACE("Fleet %s: The fleet cannot lift off, it has space status already."), // Fleet

CANNOT_LAND_NO_COLONY("Fleet %s: The fleet cannot land, because it is not at a colony."), // Fleet
CANNOT_LAND_ALREADY_GROUND("Fleet %s: The fleet cannot land, it has ground status already."), // Fleet
CANNOT_LAND_DIPLOMACY("Fleet %s: The fleet cannot land, your diplomatic relationship is not good enough."), // Fleet
CANNOT_LAND_TO_DEFEND_NO_NAP("Fleet %s: The fleet cannot land to defend a colony, you at least need a non-aggression pact with the colony owner."), // Fleet
CANNOT_LAND_BATTLE("Fleet %s: The fleet cannot land, because there is a battle in process."), // Fleet

FLEET_TRANSMISSION_FAILED("Fleet %s: Transmission failed; target colony was (partially invaded)."), // Fleet
CANNOT_TRANSMIT_NO_COLONY("Fleet %s: Cannot transmit, your fleet has to be at a colony first."), // Fleet
CANNOT_TRANSMIT_INVALID_DEST("Fleet %s: Cannot transmit, destination has to be a colony."), // Fleet
CANNOT_TRANSMIT_BAD_SOURCE_OWNER("Fleet %s: Cannot transmit, the owner of the source colony has to be an ally."), // Fleet
CANNOT_TRANSMIT_BAD_DEST_OWNER("Fleet %s: Cannot transmit, the owner of the destination colony has to be an ally."), // Fleet
CANNOT_TRANSMIT_NO_SOURCE_STAT("Fleet %s: Cannot transmit, the source colony does not have a transmitter station."), // Fleet
CANNOT_TRANSMIT_NO_DEST_STAT("Fleet %s: Cannot transmit, the destination colony does not have a transmitter station."), // Fleet
CANNOT_TRANSMIT_SOURCE_HAS_NO_MONEY("Fleet %s: Cannot transmit, the owner of the current colony does not have enough money."), // Fleet
CANNOT_TRANSMIT_DEST_HAS_NO_MONEY("Fleet %s: Cannot transmit, the owner of the destination colony does not have enough money."), // Fleet
CANNOT_TRANSMIT_INVALID_SOURCE("Fleet %s: Cannot transmit, invalid destination colony was entered."), // Fleet

CANNOT_DEFEND_INVALID_POSITION("Fleet cannot defend, invalid position."), // Fleet
CANNOT_DEFEND_NO_COLONY("Fleet cannot defend, no colony."), // Fleet
CANNOT_DEFEND_NO_GROUND_UNITS("Fleet cannot defend, no planetary units."), // Fleet
CANNOT_DEFEND_NO_SPACE_UNITS("Fleet cannot defend, no space units."), // Fleet

CANNOT_BLOCK_INVALID_POSITION("Cannot block, the current position is invalid."), // Fleet
CANNOT_BLOCK_NO_PLANET("Cannot block, no planet near to block."), // Fleet
CANNOT_BLOCK_ALREADY_BLOCKED("Cannot block, this planet is already being blocked."), // Fleet
CANNOT_BLOCK_NO_SPACE_POWER("Cannot block, your fleet does not contain any space battle units."), // Fleet
CANNOT_BLOCK_LANDED("A ground fleet cannot create a space blockade."), // Fleet
CANNOT_BLOCK_NO_COLONY_ON_PLANET("Cannot block, the planet is not colonized yet."), // Fleet

CANNOT_ATTACK_BLOCK_NO_PLANET("Cannot attack blockade, no planet near."), // Fleet
CANNOT_ATTACK_BLOCK_NO_BLOCK("Cannot attack blockade, the planet is not being blocked."), // Fleet
CANNOT_ATTACK_BLOCK_IS_LANDED("Cannot attack blockade, your fleet is not in space."), // Fleet

DISABLE_LOOP_UNKNOWN("The command loop was disabled."), // Fleet
DISABLE_LOOP_BATTLE("The command loop was disabled when the fleet joined a battle."), // Fleet

// Input Error
INVALID_INPUT("Invalid input"),
INVALID_SECID("The operation you requested was denied. Read more at <a>http://www.conquer-space.net/xss</a>"),

// Other Errors
QUEUE_ALREADY_STOPPED("The queue is stopped already."), // Colony, UnitQueue
INVALID_INPUT_NO_FLEET("Invalid input - the fleet was not found."),
FAILED_SYNCHRONIZATION("The operation failed as data changed in the meantime."),
BUILDING_ALREADY_ABORTED("Constructing this building was already aborted."), // Colony
RESEARCH_ALREADY_ABORTED("This research was already aborted."),
INPUT_NAME_TOO_SHORT("The name you entered was too short."),
INPUT_NAME_TOO_LONG("The name you entered was too long."),
INPUT_NAME_INVALID("The name you entered was invalid."),

DESIGN_INVALID_INPUT("The entered design data is invalid"),
DESIGN_NOT_STORED_TOO_MANY("Design was not stored as no memory is left."),
DESIGN_CIVILIAN_NO_TRANSPORT("All civilian ships need a transport module."),

CANNOT_CHANGE_ALLI_TIMEOUT("The player cannot join an ally, as he or she left an ally a short while ago (or has only recently joined the game)."),
CANNOT_CREATE_ALLI_TIMEOUT("You cannot found an ally, as you left an ally a short while ago or have only recently joined the game."),
CANNOT_CREATE_ALLI_INVALID_NAME("You entered an invalid alliance name."),
CANNOT_CREATE_ALLI_DUPLICATE_NAME("An ally with this name already exists."),

CANNOT_APPLY_ALREADY_MEMBER("You cannot apply for membership of this alliance, because you are already a member of it."),
CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE("You cannot make alliance contracts, because you are in no alliance."),
CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT("You cannot make alliance contracts, because you do not have the rights."),
CANNOT_LEAVE_WARDECLARATION_VICTIM("You cannot cancel the war declaration, because you did not initiate it.");

private final String englishTranslation;

private ErrorCode(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

public static String bundleName()
{ return "net.cqs.config.base.ErrorCode"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

}
