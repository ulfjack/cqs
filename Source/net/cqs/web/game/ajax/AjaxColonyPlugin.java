package net.cqs.web.game.ajax;

import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.Colony;
import net.cqs.engine.Position;
import net.cqs.engine.colony.ColonyController;
import net.cqs.engine.human.HumanColonyController;
import net.cqs.main.config.Input;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.game.CqsSession;

public final class AjaxColonyPlugin
{

private static final int MAX = ColonyController.MAX_QUEUE;

@WebPostAction("Colony.resumeBuilding")
public void resumeBuilding(CqsSession session, @Parameter("c") Position position)
{
	Colony colony = session.getColony(position);
	colony.resumeBuilding();
}

@WebPostAction("Colony.abortBuilding")
public void abortBuilding(CqsSession session, @Parameter("c") Position position)
{
	Colony colony = session.getColony(position);
	colony.abortBuilding();
}

@WebPostAction("Colony.addBuilding")
public void addBuilding(CqsSession session,
		@Parameter("c") Position position, @Parameter("id") int id, @Parameter("count") String count)
{
	Colony colony = session.getColony(position);
	
	BuildingEnum i = BuildingEnum.valueOf(id);
	int j = Input.decode(count, 0, -MAX, MAX);
	if (j == 0) throw new ErrorCodeException(ErrorCode.INVALID_INPUT);
	if (j < 0)
	{
		if (((HumanColonyController) colony.getController()).addBuildingRemoval(i, -j))
			colony.resumeBuilding();
	}
	else
	{
		if (((HumanColonyController) colony.getController()).addBuildingConstruction(i, j))
			colony.resumeBuilding();
	}
}

@WebPostAction("Colony.giveUp")
public void giveUp(CqsSession session, @Parameter("c") Position position)
{
	if (session.isRestricted())
	{
		session.log(ErrorCode.RESTRICTED_ACCESS);
		return;
	}
	Colony colony = session.getColony(position);
	HumanColonyController controller = (HumanColonyController) colony.getController();

	BuildingEnum[] buildings = BuildingEnum.values();
	// clear building queue
	if (colony.isBuildingInProgress())
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

@WebPostAction("Colony.modifyBuilding")
public void modifyBuilding(CqsSession session, @Parameter("c") Position position,
		@Parameter("index") String index, @Parameter("count") String count)
{
	Colony colony = session.getColony(position);
	
	int i = Input.decode(index, 0, -1, MAX);
	int j = Input.decode(count, 0, -MAX, MAX);
	((HumanColonyController) colony.getController()).modifyBuildingEntry(i, j);
}
	
@WebPostAction("Colony.deleteBuilding")
public void deleteBuilding(CqsSession session, @Parameter("c") Position position,
		@Parameter("index") String index)
{
	Colony colony = session.getColony(position);
	
	int i = Input.decode(index, 0, -1, MAX);
	((HumanColonyController) colony.getController()).deleteBuildingEntry(i);
}
	
@WebPostAction("Colony.moveBuilding")
public void moveBuilding(CqsSession session, @Parameter("c") Position position,
		@Parameter("index") String index, @Parameter("count") String count)
{
	Colony colony = session.getColony(position);
	int i = Input.decode(index, 0, -1, MAX);
	int j = Input.decode(count, 0, -MAX, MAX);
	colony.getController().moveBuildingEntry(i, j);
}
	
	
	
	
// Units
@WebPostAction("Colony.resumeUnit")
public void resumeUnit(CqsSession session, @Parameter("c") Position position,
		@Parameter("q") int queue)
{
	Colony colony = session.getColony(position);
	colony.resumeUnit(queue);
}
	
@WebPostAction("Colony.abortUnit")
public void abortUnit(CqsSession session, @Parameter("c") Position position,
		@Parameter("q") int queue)
{
	Colony colony = session.getColony(position);
	colony.abortUnit(queue);
}
	
@WebPostAction("Colony.addUnit")
public void addUnit(CqsSession session, @Parameter("c") Position position,
		@Parameter("q") int queue,
		@Parameter("id") String id, @Parameter("count") String count)
{
	Colony colony = session.getColony(position);
	UnitSystem us = session.getGalaxy().getUnitSystem();
	Unit unit = us.parseUnit(id);
	if (queue != colony.getCorrectUnitQueue(unit))
		throw new ErrorCodeException(ErrorCode.INVALID_INPUT);
	
	int j = Input.decode(count, 1, 1, MAX);
	int uqueue = ((HumanColonyController) colony.getController()).addUnitConstruction(unit, j);
	if (uqueue >= 0)
		colony.resumeUnit(uqueue);
}
	
@WebPostAction("Colony.modifyUnit")
public void modifyUnit(CqsSession session, @Parameter("c") Position position,
		@Parameter("q") int queue,
		@Parameter("index") String index, @Parameter("count") String count)
{
	Colony colony = session.getColony(position);
	int i = Input.decode(index, 0, -1, MAX);
	int j = Input.decode(count, 0, -MAX, MAX);
	((HumanColonyController) colony.getController()).modifyUnitEntry(queue, i, j);
}
	
@WebPostAction("Colony.deleteUnit")
public void deleteUnit(CqsSession session,
		@Parameter("c") Position position, @Parameter("q") int queue, @Parameter("index") String index)
{
	Colony colony = session.getColony(position);
	int i = Input.decode(index, 0, -1, MAX);
	((HumanColonyController) colony.getController()).deleteUnitEntry(queue, i);
}
	
@WebPostAction("Colony.moveUnit")
public void moveUnit(CqsSession session, @Parameter("c") Position position,
		@Parameter("q") int queue,
		@Parameter("index") String index, @Parameter("count") String count)
{
	Colony colony = session.getColony(position);
	int i = Input.decode(index, 0, -1, MAX);
	int j = Input.decode(count, 0, -MAX, MAX);
	((HumanColonyController) colony.getController()).moveUnitEntry(queue, i, j);
}

// Education

@WebPostAction("Education.modify")
public void modifyEducation(CqsSession session, @Parameter("c") Position position,
		@Parameter("education") EducationEnum education,
		@Parameter("count") int amount)
{
	Colony colony = session.getColony(position);
	if (amount > 0)
		colony.startEducation(education, amount);
	else
		colony.stopEducation(education, -amount);
}

@WebPostAction("Education.addProf")
public void addEducationProfs(CqsSession session, @Parameter("c") Position position,
		@Parameter("count") int amount)
{
	Colony colony = session.getColony(position);
	colony.addEducationProfs(amount);
}

@WebPostAction("Research.addProf")
public void addResearchProfs(CqsSession session, @Parameter("c") Position position,
		@Parameter("count") int amount)
{
	Colony colony = session.getColony(position);
	colony.addResearchProfs(amount);
}


}
