package net.cqs.web.game.action;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.engine.Agent;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.base.Survey;
import net.cqs.main.config.Input;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostSystem
{

private static final Logger logger = Logger.getLogger("net.cqs.web.game.action");

static void init()
{
	ActionHandler.add("login", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{/*OK*/}
		});
	ActionHandler.add("login2", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{/*OK*/}
		});
	
	ActionHandler.add("System.Survey.vote", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String id = req.getParameter("id");
				String vote = req.getParameter("vote");
				Survey survey = session.getGalaxy().getSurvey();
				if ((id != null) && (vote != null) && (survey != null))
				{
					int i = Input.decode(id, -1);
					int j = session.getPlayer().getAttr(Attribute.SYSTEM_SURVEY).intValue();
					if ((i == survey.getId()) && (j != survey.getId()))
					{
						survey.addVote(Input.decode(vote, -1));
						session.getPlayer().setAttr(Attribute.SYSTEM_SURVEY, Integer.valueOf(survey.getId()));
					}
				}
			}
		});
	
	ActionHandler.add("Kommission.Survey.vote", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String id = req.getParameter("id");
				Survey kommission = session.getGalaxy().getKomissionSurvey();
				if ((id == null) || (kommission == null))
					return;
				
				int i = Input.decode(id, -1);
				int j = session.getPlayer().getAttr(Attribute.KOMMISSION_SURVEY).intValue();
				if ((i != kommission.getId()) || (j == kommission.getId()))
					return;
				
				int valid = 0;
				for (i = 0; i < kommission.length(); i++)
					valid += "on".equals(req.getParameter("vote"+i)) ? 1 : 0;
				
				if (valid != 3)
				{
					session.log(ErrorCode.INVALID_KOMMISSION_VOTE);
					return;
				}
				
				for (i = 0; i < kommission.length(); i++)
					if ("on".equals(req.getParameter("vote"+i)))
						kommission.addVote(i);
				
				session.getPlayer().setAttr(Attribute.KOMMISSION_SURVEY, Integer.valueOf(kommission.getId()));
			}
		});
		
	ActionHandler.add("Agent.requestReport", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				logger.fine("requestReport");
				String id = req.getParameter("id");
				if ( id != null )
				{
					Agent agent = session.getPlayer().findAgentById(id);
					agent.requestReport();
				}
			}
		});
}

private PostSystem()
{/*OK*/}

}
