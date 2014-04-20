package net.cqs.web.game.action;

import javax.servlet.http.HttpServletRequest;

import net.cqs.main.config.Input;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;
import net.cqs.web.game.search.Search;

public class PostSearch {
	
static void init()
{
	ActionHandler.add("Search.player", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			String name = req.getParameter("name");
			int start = Input.decode(req.getParameter("start"), 0);
			session.setSearchResult(Search.searchPlayer(session.getGalaxy(), name, start));
		}
	});
	
	ActionHandler.add("Search.alliance", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			String name = req.getParameter("name");
			int start = Input.decode(req.getParameter("start"), 0);
			session.setSearchResult(Search.searchAlliance(session.getGalaxy(), name, start));
		}
	});

	ActionHandler.add("Search.allianceTransmitter", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int start = Input.decode(req.getParameter("start"), 0);
			session.setSearchResult(Search.searchAllianceTransmitter(session.getAlliance(), start));
		}
	});
}

private PostSearch()
{/*OK*/}

}
