package net.cqs.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ofahrt.catfish.utils.HttpFilter;

import net.cqs.main.config.FrontEnd;
import net.cqs.web.frontpage.IdSession;
import net.cqs.web.util.AccessDeniedReason;

public final class GeneralAccessCheckpoint extends HttpFilter implements Filter
{

private final FrontEnd frontEnd;
private final String loginurl;

public GeneralAccessCheckpoint(FrontEnd frontEnd, String loginurl)
{
	if (!loginurl.startsWith("/"))
		throw new IllegalArgumentException("Login URL must be absolute!");
	this.frontEnd = frontEnd;
	this.loginurl = loginurl;
}

@Override
public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
{
	IdSession idSession = frontEnd.getIdSession(request);
	if (!idSession.isLoggedIn())
	{
		String ref = loginurl;
		if (!("logout".equals(request.getParameter("do"))))
			ref += "?url="+request.getRequestURI()+"&denied="+AccessDeniedReason.NOT_LOGGED_IN.name();
		response.sendRedirect(ref);
		return;
	}
	chain.doFilter(request, response);
}

}
