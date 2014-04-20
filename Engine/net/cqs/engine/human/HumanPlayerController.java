package net.cqs.engine.human;

import net.cqs.config.ErrorCode;
import net.cqs.config.ResearchEnum;
import net.cqs.engine.*;
import net.cqs.engine.colony.ColonyController;
import net.cqs.util.EnumQueue;

public final class HumanPlayerController implements PlayerController
{

private static final long serialVersionUID = 1L;

private final Player player;
private final EnumQueue<ResearchEnum> researchQueue = EnumQueue.of(ResearchEnum.class, true);

public HumanPlayerController(Player player)
{
	this.player = player;
}

@Override
public ColonyController createColonyController(Colony colony)
{ return new HumanColonyController(colony); }

@Override
public FleetController createFleetController(Fleet fleet)
{ return new HumanFleetController(fleet); }

// Research
public EnumQueue<ResearchEnum> getResearchQueue()
{ return researchQueue; }

public boolean addResearch(ResearchEnum type, int howmany)
{
//	if (!topic.validResearchTopic(player)) return false;
	if (howmany <= 0)
	{
		player.log(ErrorCode.INVALID_INPUT);
		return false;
	}
	researchQueue.add(type, howmany);
	return researchQueue.size() == 1;
}

public void deleteResearchEntry(int num)
{ researchQueue.deleteInverse(num); }

public void modifyResearchEntry(int num, int delta)
{ researchQueue.modifyInverse(num, delta); }

public void moveResearchEntry(int num, int delta)
{
	if (delta == 0)
		researchQueue.moveTopInverse(num);
	else
		researchQueue.moveUpInverse(num, delta);
}


// Research
@Override
public void nextResearch(Player p, long time)
{
	ResearchEnum rt = null;
	while ((rt == null) && (researchQueue.size() > 0))
	{
		rt = researchQueue.peek();
		if (player.getResearchLevel(rt) >= rt.getMax())
		{
			rt = null;
			researchQueue.remove();
		}
	}
	if (rt == null) return;
	if (player.startResearch(time, rt))
		researchQueue.remove();
}

@Override
public void finishResearch(Player p, long time)
{/*OK*/}

@Override
public void abortResearch(Player p, long time)
{/*OK*/}

}
