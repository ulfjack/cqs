package net.cqs.engine.fleets;

import java.util.Arrays;
import java.util.List;

public final class FleetCommandRegistry
{

private static final Class<?>[] FLEET_COMMANDS = new Class<?>[]
	{
		FleetAttackBlockadeCommand.class,
		FleetBlockPlanetCommand.class,
		FleetBreakBlockadeCommand.class,
		FleetColonizeCommand.class,
		FleetDefendColonyCommand.class,
		FleetDisbandCommand.class,
		FleetDonateCommand.class,
		FleetExploreCommand.class,
		FleetAttackCommand.class,
		FleetInvasionCommand.class,
		FleetRobCommand.class,
		FleetGateCommand.class,
		FleetLandCommand.class,
		FleetLoadCommand.class,
		FleetLoadUnitsCommand.class,
		FleetMoveCommand.class,
		FleetNOPCommand.class,
		FleetSettleCommand.class,
		FleetSpyCommand.class,
		FleetStationCommand.class,
		FleetStopCommand.class,
		FleetTakeoffCommand.class,
		FleetUnloadCommand.class,
		FleetWaitCommand.class,
		FleetWithdrawCommand.class,
	};

public static final List<Class<?>> FLEET_COMMAND_TYPES = Arrays.asList(FLEET_COMMANDS);

}
