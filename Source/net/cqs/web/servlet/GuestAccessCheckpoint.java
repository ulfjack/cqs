package net.cqs.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.ofahrt.catfish.utils.HttpFilter;

import net.cqs.engine.AccessLevel;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.game.CqsSession;

public final class GuestAccessCheckpoint extends HttpFilter implements Filter
{

private final FrontEnd frontEnd;

public GuestAccessCheckpoint(FrontEnd frontEnd)
{ this.frontEnd = frontEnd; }

@Override
public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
{
	HttpSession session = request.getSession();
	CqsSession cs = frontEnd.getCqsSession(session);
	if (!cs.isLoggedIn() || (cs.getPlayer().getPid() != 0))
	{
		frontEnd.fireLogin(session.getId(), 0);
		cs.login(null, 0, AccessLevel.GUEST);
	}
	chain.doFilter(request, response);
}

}
