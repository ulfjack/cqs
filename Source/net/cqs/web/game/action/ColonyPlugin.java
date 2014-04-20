package net.cqs.web.game.action;

import net.cqs.auth.Identity;
import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InputValidation;
import net.cqs.config.ResearchEnum;
import net.cqs.config.units.Unit;
import net.cqs.engine.Colony;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.colony.ColonyController;
import net.cqs.engine.human.HumanColonyController;
import net.cqs.engine.human.HumanPlayerController;
import net.cqs.web.action.ActionPlugin;
import net.cqs.web.action.ManagedGET;
import net.cqs.web.action.ManagedPOST;
import net.cqs.web.action.Parameter;

public final class ColonyPlugin implements ActionPlugin
{

public ColonyPlugin()
{/*OK*/}

// Colony
@ManagedGET("Colony.giveUp")
public void giveUpColony(Identity identity, int playerId, Position position)
{
	Player player = Player.getPlayer(playerId);
	player.checkRestricted(identity, "Colony.giveUp");
	
	Colony colony = player.getColony(position);
	ColonyController controller = colony.getController();
	
	// clear building queue
	colony.abortBuilding();
	controller.clearBuildingQueue();

	// put building removal in queue
	for (BuildingEnum b : BuildingEnum.values())
	{
		if ((b != BuildingEnum.RESIDENCE) && (b != BuildingEnum.UNIVERSITY))
		{
			int j = colony.getBuilding(b);
			if ((j > 0) && controller.addBuildingRemoval(b, j))
				colony.resumeBuilding();
		}
	}
	int j = colony.getBuilding(BuildingEnum.UNIVERSITY);
	if ((j > 0) && controller.addBuildingRemoval(BuildingEnum.UNIVERSITY, j))
		colony.resumeBuilding();
	
	j = colony.getBuilding(BuildingEnum.RESIDENCE);
	if ((j > 0) && controller.addBuildingRemoval(BuildingEnum.RESIDENCE, j))
		colony.resumeBuilding();
}

@ManagedPOST("Colony.giveUp")
public void giveUpColony2(Identity identity, int playerId, @Parameter("c") Position position)
{
	Player player = Player.getPlayer(playerId);
	player.checkRestricted(identity, "Colony.giveUp");
	
	Colony colony = player.getColony(position);
	HumanColonyController controller = (HumanColonyController) colony.getController();

	BuildingEnum[] buildings = BuildingEnum.values();
	// clear building queue
	colony.abortBuilding();
	controller.clearBuildingQueue();
	
	// put building removal in queue
	for (int i = 0; i < buildings.length; i++)
	{
		if ((buildings[i] != BuildingEnum.RESIDENCE) && (buildings[i] != BuildingEnum.UNIVERSITY))
		{
			int j = colony.getBuilding(buildings[i]);
			if ((j > 0) && controller.addBuildingRemoval(buildings[i], j))
					colony.resumeBuilding();
		}
	}
	int j = colony.getBuilding(BuildingEnum.UNIVERSITY);
	if ((j > 0) && controller.addBuildingRemoval(BuildingEnum.UNIVERSITY, j))
		colony.resumeBuilding();
	
	j = colony.getBuilding(BuildingEnum.RESIDENCE);
	if ((j > 0) && controller.addBuildingRemoval(BuildingEnum.RESIDENCE, j))
		colony.resumeBuilding();
}

@ManagedPOST("Colony.Name.set")
public void renameColony(Identity identity, int playerId,
		@Parameter("c") Position position, @Parameter("name") String name)
{
	Colony colony = Player.getColony(playerId, position);
	if (!InputValidation.validColonyName(name))
		throw new ErrorCodeException();
	colony.setName(name);
}

@ManagedPOST("Colony.addBuilding")
public void addBuilding(Identity identity, int playerId, @Parameter("c") Position position,
		@Parameter("id") BuildingEnum building, @Parameter("count") int amount, @Parameter("reverse") boolean reverse)
{
	Colony colony = Player.getColony(playerId, position);
	if (reverse) amount = -amount;
	if (amount == 0) throw new ErrorCodeException(ErrorCode.INVALID_INPUT);
	if (amount < 0)
	{
		if (colony.getController().addBuildingRemoval(building, -amount))
			colony.resumeBuilding();
	}
	else
	{
		if (colony.getController().addBuildingConstruction(building, amount))
			colony.resumeBuilding();
	}
}

@ManagedPOST("Colony.addUnit")
public void addUnit(Identity identity, int playerId, @Parameter("c") Position position,
		@Parameter("q") int queue,
		@Parameter("id") Unit slot,
		@Parameter("count") int amount)
{
	Colony colony = Player.getColony(playerId, position);
	if (amount <= 0)
		throw new ErrorCodeException(ErrorCode.INVALID_INPUT);
	
	int uqueue = ((HumanColonyController) colony.getController()).addUnitConstruction(slot, amount);
	if (uqueue >= 0)
		colony.resumeUnit(uqueue);
}

@ManagedPOST("Building.set")
public void setBuilding(Identity identity, int playerId, @Parameter("c") Position position,
		@Parameter("index") int index,
		@Parameter("prev") int oldAmount,
		@Parameter("count") int amount)
{
	Colony colony = Player.getColony(playerId, position);
	colony.getController().modifyBuildingEntry(index, amount-oldAmount);
}

@ManagedPOST("Unit.set")
public void setBuilding(Identity identity, int playerId, @Parameter("c") Position position,
		@Parameter("q") int queue,
		@Parameter("index") int index,
		@Parameter("prev") int oldAmount,
		@Parameter("count") int amount)
{
	Colony colony = Player.getColony(playerId, position);
	colony.getController().modifyUnitEntry(queue, index, amount-oldAmount);
}


// Buildings
@ManagedGET("Building.resume")
public void resumeBuilding(Identity identity, int playerId, Position position)
{ Player.getColony(playerId, position).resumeBuilding(); }

@ManagedGET("Building.abort")
public void abortBuilding(Identity identity, int playerId, Position position)
{ Player.getColony(playerId, position).abortBuilding(); }

@ManagedGET("Building.start")
public void startBuilding(Identity identity, int playerId, Position position, BuildingEnum building, int amount)
{
	Colony targetColony = Player.getColony(playerId, position);
	if (targetColony.getController().addBuildingConstruction(building, amount))
		targetColony.resumeBuilding();
}

@ManagedGET("Building.remove")
public void removeBuilding(Identity identity, int playerId, Position position, BuildingEnum building, int amount)
{
	Player player = Player.getPlayer(playerId);
	player.checkRestricted(identity, "Building.remove");
	Colony targetColony = player.getColony(position);
	if (targetColony.getController().addBuildingRemoval(building, amount))
		targetColony.resumeBuilding();
}

@ManagedGET("Building.delete")
public void deleteBuilding(Identity identity, int playerId, Position position, int index)
{
	Colony targetColony = Player.getColony(playerId, position);
	targetColony.getController().deleteBuildingEntry(index);
}

@ManagedGET("Building.modify")
public void modifyBuilding(Identity identity, int playerId, Position position, int index, int amount)
{
	Colony targetColony = Player.getColony(playerId, position);
	targetColony.getController().modifyBuildingEntry(index, amount);
}

@ManagedGET("Building.move")
public void moveBuilding(Identity identity, int playerId, Position position, int index, int amount)
{
	Colony targetColony = Player.getColony(playerId, position);
	targetColony.getController().moveBuildingEntry(index, amount);
}

// Units
@ManagedGET("Unit.resume")
public void resumeUnit(Identity identity, int playerId, Position position, int queue)
{ Player.getColony(playerId, position).resumeUnit(queue); }

@ManagedGET("Unit.abort")
public void abortUnit(Identity identity, int playerId, Position position, int queue)
{ Player.getColony(playerId, position).abortUnit(queue); }

@ManagedGET("Unit.start")
public void activate(Identity identity, int playerId, Position position, Unit unit, int amount)
{
	Colony targetColony = Player.getColony(playerId, position);
	int queue = targetColony.getController().addUnitConstruction(unit, amount);
	if (queue >= 0)
		targetColony.resumeUnit(queue);
}

@ManagedGET("Unit.delete")
public void deleteUnit(Identity identity, int playerId, Position position, int queue, int index)
{
	Colony targetColony = Player.getColony(playerId, position);
	targetColony.getController().deleteUnitEntry(queue, index);
}

@ManagedGET("Unit.modify")
public void modifyUnit(Identity identity, int playerId, Position position, int queue, int index, int amount)
{
	Colony targetColony = Player.getColony(playerId, position);
	targetColony.getController().modifyUnitEntry(queue, index, amount);
}

@ManagedGET("Unit.move")
public void moveUnit(Identity identity, int playerId, Position position, int queue, int index, int amount)
{
	Colony targetColony = Player.getColony(playerId, position);
	targetColony.getController().moveUnitEntry(queue, index, amount);
}


// Education
@ManagedGET("Education.start")
public void startEducation(Identity identity, int playerId, Position position, EducationEnum education, int amount)
{ Player.getColony(playerId, position).startEducation(education, amount); }

@ManagedGET("Education.stop")
public void stopEducation(Identity identity, int playerId, Position position, EducationEnum education, int amount)
{ Player.getColony(playerId, position).stopEducation(education, amount); }

@ManagedGET("Education.add")
public void addEducation(Identity identity, int playerId, Position position, int amount)
{ Player.getColony(playerId, position).addEducationProfs(amount); }


// Research
@ManagedGET("Research.add")
public void addResearch(Identity identity, int playerId, Position position, int amount)
{ Player.getColony(playerId, position).addResearchProfs(amount); }

@ManagedGET("Research.resume")
public void resumeResearch(Identity identity, int playerId)
{ Player.getPlayer(playerId).resumeResearch(); }

@ManagedGET("Research.abort")
public void abortResearch(Identity identity, int playerId)
{ Player.getPlayer(playerId).abortResearch(); }

@ManagedGET("Research.start")
public void startResearch(Identity identity, int playerId, ResearchEnum research, int amount)
{
	Player player = Player.getPlayer(playerId);
	if (((HumanPlayerController) player.getController()).addResearch(research, amount))
		player.resumeResearch();
}

@ManagedGET("Research.delete")
public void deleteResearch(Identity identity, int playerId, int index)
{
	Player player = Player.getPlayer(playerId);
	((HumanPlayerController) player.getController()).deleteResearchEntry(index);
}
	
@ManagedGET("Research.modify")
public void modifyResearch(Identity identity, int playerId, int index, int amount)
{
	Player player = Player.getPlayer(playerId);
	((HumanPlayerController) player.getController()).modifyResearchEntry(index, amount);
}

@ManagedGET("Research.move")
public void moveResearch(Identity identity, int playerId, int index, int amount)
{
	Player player = Player.getPlayer(playerId);
	((HumanPlayerController) player.getController()).moveResearchEntry(index, amount);
}

}
