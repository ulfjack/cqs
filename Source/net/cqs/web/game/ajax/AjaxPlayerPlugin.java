package net.cqs.web.game.ajax;

import net.cqs.config.ResearchEnum;
import net.cqs.engine.colony.ColonyController;
import net.cqs.engine.human.HumanPlayerController;
import net.cqs.main.config.Input;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.game.CqsSession;

public final class AjaxPlayerPlugin
{

private static final int MAX = ColonyController.MAX_QUEUE;

@WebPostAction("Player.resumeResearch")
public void resumeResearch(CqsSession session)
{ session.getPlayer().resumeResearch(); }

@WebPostAction("Player.abortResearch")
public void abortResearch(CqsSession session)
{ session.getPlayer().abortResearch(); }

@WebPostAction("Player.addResearch")
public void addResearch(CqsSession session,
		@Parameter("id") String id, @Parameter("count") String count)
{
	ResearchEnum i = ResearchEnum.valueOf(id);
	int j = Input.decode(count, 1, 1, MAX);
	if (((HumanPlayerController) session.getPlayer().getController()).addResearch(i, j))
		session.getPlayer().resumeResearch();
}

@WebPostAction("Player.modifyResearch")
public void modifyResearch(CqsSession session,
		@Parameter("index") String index, @Parameter("count") String count)
{
	int i = Input.decode(index, 0, -1, MAX);
	int j = Input.decode(count, 0, -MAX, MAX);
	((HumanPlayerController) session.getPlayer().getController()).modifyResearchEntry(i, j);
}

@WebPostAction("Player.deleteResearch")
public void deleteResearch(CqsSession session, @Parameter("index") String index)
{
	int i = Input.decode(index, 0, -1, MAX);
	((HumanPlayerController) session.getPlayer().getController()).deleteResearchEntry(i);
}

@WebPostAction("Player.moveResearch")
public void moveResearch(CqsSession session,
		@Parameter("index") String index, @Parameter("count") String count)
{
	int i = Input.decode(index, 0, -1, MAX);
	int j = Input.decode(count, 0, -MAX, MAX);
	((HumanPlayerController) session.getPlayer().getController()).moveResearchEntry(i, j);
}

}
