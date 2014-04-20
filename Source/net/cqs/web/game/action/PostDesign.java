package net.cqs.web.game.action;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.units.BadDesignException;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitClass;
import net.cqs.config.units.UnitDescription;
import net.cqs.config.units.UnitModule;
import net.cqs.config.units.UnitSystem;
import net.cqs.engine.units.CivilianModulesEnum;
import net.cqs.engine.units.UnitClassEnum;
import net.cqs.main.persistence.PlayerData;
import net.cqs.storage.Task;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostDesign
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

static void init()
{
	ActionHandler.add("Design.preview", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				UnitSystem us = session.getFrontEnd().getUnitSystem();
				UnitClass uc = us.parseUnitClass(req.getParameter("class"));
				logger.fine("DESIGN: "+uc);
				
				UnitModule[] list = uc.getModules();
				int[] count = new int[list.length];
				for (int i = 0; i < count.length; i++)
				{
					if (list[i].equals(CivilianModulesEnum.RES_TRANSPORT))
					{
						String value = req.getParameter("module-TRANSPORT");
						if ((value != null) && value.equals("RES_TRANSPORT"))
							count[i] = 1;
						else
							count[i] = 0;
					}
					else if (list[i].equals(CivilianModulesEnum.TROOP_TRANSPORT))
					{
						String value = req.getParameter("module-TRANSPORT");
						if ((value != null) && value.equals("TROOP_TRANSPORT"))
							count[i] = 1;
						else
							count[i] = 0;
					}
					else
					{
						String value = req.getParameter("module-"+list[i]);
						if ((value != null) && (value.length() > 0))
							count[i] = Integer.parseInt(value);
					}
				}
				logger.fine(Arrays.toString(count));
				
				try
				{
					if ((UnitClassEnum) uc == UnitClassEnum.CIVILIAN)
					{
						boolean hasTransport = false;
						for (int i = 0; i < list.length; i++)
							if ((count[i] > 0) &&
								(((CivilianModulesEnum) list[i] == CivilianModulesEnum.RES_TRANSPORT)
								|| ((CivilianModulesEnum) list[i] == CivilianModulesEnum.TROOP_TRANSPORT)))
								hasTransport = true;
						if (!hasTransport) throw new ErrorCodeException(ErrorCode.DESIGN_CIVILIAN_NO_TRANSPORT);
					}
					
					UnitDescription design = new UnitDescription(uc, count);
					session.unitDesign = design;
				}
				catch (BadDesignException e)
				{
					e.printStackTrace();
					throw new ErrorCodeException(ErrorCode.DESIGN_INVALID_INPUT);
				}
			}
		});
	
	ActionHandler.add("Design.accept", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				logger.fine("Accept Design");
				if (session.unitDesign == null) return;
				final Unit unit = session.unitDesign.getUnit();
				final int pid = session.getPlayer().getPid();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							PlayerData playerData = PlayerData.getPlayerData(pid);
							if (!playerData.addDesign(unit))
								throw new ErrorCodeException(ErrorCode.DESIGN_NOT_STORED_TOO_MANY);
						}
					});
				session.unitDesign = null;
			}
		});
	
	ActionHandler.add("Design.delete", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				logger.fine("Delete Design");
				final Unit unit = session.getGalaxy().getUnitSystem().parseUnit(req.getParameter("id"));
				final int pid = session.getPlayer().getPid();
				session.execute(new Task()
					{
						@Override
						public void run()
						{
							PlayerData.getPlayerData(pid).removeDesign(unit);
						}
					});
			}
		});
}

private PostDesign()
{/*OK*/}

}
