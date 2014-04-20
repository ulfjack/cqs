package net.cqs.web.game.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.auth.Identity;
import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InputValidation;
import net.cqs.engine.Fleet;
import net.cqs.engine.Player;
import net.cqs.engine.battles.Battle;
import net.cqs.engine.human.HumanFleetController;
import net.cqs.web.action.ActionPlugin;
import net.cqs.web.action.ManagedGET;
import net.cqs.web.action.ManagedPOST;
import net.cqs.web.action.Parameter;

public final class FleetPlugin implements ActionPlugin
{

private static Logger logger = Logger.getLogger("net.cqs.web.game.action");
	
	
public FleetPlugin()
{/*OK*/}

@ManagedPOST("Fleet.Name.change")
public void renameFleet(Identity identity, int playerId,
		@Parameter("id") int fleetId, @Parameter("name") String name)
{
	Fleet f = Player.getFleet(playerId, fleetId);
	if (name == null)
		throw new ErrorCodeException();
	name = name.trim();
	if (name.length() < 1)
		throw new ErrorCodeException(ErrorCode.INPUT_NAME_TOO_SHORT);
	if (name.length() > 40)
		throw new ErrorCodeException(ErrorCode.INPUT_NAME_TOO_LONG);
	if (!InputValidation.validFleetName(name))
		throw new ErrorCodeException(ErrorCode.INPUT_NAME_INVALID);
	f.setName(name.intern());
}

@ManagedGET("Fleet.resume")
public void resumeFleet(Identity identity, int playerId, int fleetId)
{ Player.getFleet(playerId, fleetId).resume(); }

@ManagedGET("Fleet.stop")
public void stopFleet(Identity identity, int playerId, int fleetId)
{ Player.getFleet(playerId, fleetId).stop(); }

@ManagedGET("Fleet.unloop")
public void disableLoopFleet(Identity identity, int playerId, int fleetId)
{ Player.getFleet(playerId, fleetId).getController().disableLoop(); }

@ManagedGET("Fleet.loop")
public void enableLoopFleet(Identity identity, int playerId, int fleetId)
{ Player.getFleet(playerId, fleetId).getController().enableLoop(); }

@ManagedPOST("Fleet.unloop")
public void disableLoopFleetPost(Identity identity, int playerId, @Parameter("id") int fleetId)
{ Player.getFleet(playerId, fleetId).getController().disableLoop(); }

@ManagedPOST("Fleet.loop")
public void enableLoopFleetPost(Identity identity, int playerId, @Parameter("id") int fleetId)
{ Player.getFleet(playerId, fleetId).getController().enableLoop(); }

@ManagedPOST("Fleet.withdraw")
public void withdrawFleet(Identity identity, int playerId, @Parameter("fid") int fleetId, @Parameter("bid") String battleId)
{
	Player p = Player.getPlayer(playerId);
	Battle b = p.getBattle(battleId);
	if (b == null)
	{
		logger.log(Level.WARNING, "Trying to withdraw fleet "+fleetId+" owned by "+p.getName()+" from battle with id "+battleId+", but battle is null.");
		return;
	}
	Fleet f = b.find(p, fleetId);
	if (f == null)
		logger.log(Level.WARNING, "Trying to withdraw fleet "+fleetId+" owned by "+p.getName()+" from battle with id "+battleId+", but fleet is null.");
	else
		b.withdraw(f);
}

@ManagedGET("Command.moveUp")
public void moveCommandUp(Identity identity, int playerId, int fleetId, int commandPos)
{
	Fleet f = Player.getFleet(playerId, fleetId);
	((HumanFleetController) f.getController()).moveUpCommand(commandPos, 1);
}

@ManagedGET("Command.moveDown")
public void moveCommandDown(Identity identity, int playerId, int fleetId, int commandPos)
{
	Fleet f = Player.getFleet(playerId, fleetId);
	((HumanFleetController) f.getController()).moveUpCommand(commandPos, -1);
}

@ManagedGET("Command.remove")
public void removeCommand(Identity identity, int playerId, int fleetId, int commandPos)
{
	Fleet f = Player.getFleet(playerId, fleetId);
	((HumanFleetController) f.getController()).removeCommand(commandPos);
}

@ManagedGET("Command.switch")
public void switchCommand(Identity identity, int playerId, int fleetId, int commandPos)
{
	Fleet f = Player.getFleet(playerId, fleetId);
	((HumanFleetController) f.getController()).setNextCommand(commandPos);
}

}
