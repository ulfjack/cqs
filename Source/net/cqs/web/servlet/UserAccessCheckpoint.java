package net.cqs.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.ofahrt.catfish.utils.HttpFilter;

import net.cqs.auth.Identity;
import net.cqs.engine.AccessLevel;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.game.CqsSession;
import net.cqs.web.util.AccessDeniedReason;

public final class UserAccessCheckpoint extends HttpFilter implements Filter
{

private final FrontEnd frontEnd;
private final String loginurl;

public UserAccessCheckpoint(FrontEnd frontEnd, String loginurl)
{
	if (!loginurl.startsWith("/"))
		throw new IllegalArgumentException("Login URL must be absolute!");
	this.frontEnd = frontEnd;
	this.loginurl = loginurl;
}

private synchronized AccessDeniedReason checkAccess(HttpServletRequest request)
{
	HttpSession session = request.getSession();
	CqsSession cs = frontEnd.getCqsSession(session);
	
	if (false)
	{
		Object origin = request.getSession().getAttribute("init.address");
		if ((origin instanceof String) && !((String) origin).equals(request.getRemoteAddr()))
			return AccessDeniedReason.INVALID_IP;
	}
/*
	// allow tracking the session id as part of the url
	if (tracker.hasNextPath())
	{
		String id = tracker.getNextPath();
		tracker.advance();
		if (req.getSessionManager().checkID(id))
			req.switchSession(id);
		
		// if the request comes from a different ip, deny it
		Object origin = req.getSession().getAttribute("init.address");
		if ((origin instanceof String) && !((String) origin).equals(req.getRemoteAddr()))
			throw INVALID_IP;
		
		// for security reasons, do not allow this on loopback addresses
		if (requireNonlocalIP)
		{
			if ("127.0.0.1".equals(req.getRemoteAddr()))
				throw INVALID_IP;
		}
	}
	else
	{
		super.initSession(req, res, tracker);
//		if (req.getSession().isNew()) throw NEW_COOKIE;
	}*/
	
	String todo = request.getParameter("do");
	
	if ("login2".equals(todo))
	{
		Identity id = frontEnd.getIdSession(request).getIdentity();
		if (id != null)
		{
			int pid = Integer.parseInt(request.getParameter("to"));
			Galaxy galaxy = frontEnd.getGalaxy();
			synchronized (galaxy)
			{
				Player player = galaxy.findPlayerByPid(pid);
				AccessLevel level = galaxy.getAccessLevel(id, player);
				if (level.loginAllowed() && player.mayLogin())
				{
					frontEnd.fireLogin(session.getId(), player.getPid());
					cs.login(id, player.getPid(), level);
					
/*					cs.graphicPath = null;
					String t4 = req.getParameter("graphicpath");
					if (t4 != null && t4.length() != 0)
						cs.graphicPath = t4;*/
				}
			}
		}
	}
	
	if ("logout".equals(todo))
		cs.logout();
	
	if (!cs.isLoggedIn())
		return AccessDeniedReason.NOT_LOGGED_IN;
	
	return null;
}

@Override
public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
{
	AccessDeniedReason reason = checkAccess(request);
	if (reason != null)
	{
		String ref = loginurl;
		if (!("logout".equals(request.getParameter("do"))))
			ref += "?url="+request.getRequestURI()+"&denied="+reason.name();
		response.sendRedirect(ref);
		return;
	}
	chain.doFilter(request, response);
}

}
