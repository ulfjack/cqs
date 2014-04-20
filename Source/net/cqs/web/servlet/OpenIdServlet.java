package net.cqs.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cqs.auth.Identity;
import net.cqs.main.config.FrontEnd;
import net.cqs.services.AuthService;
import net.cqs.web.frontpage.IdSession;

import org.verisign.joid.consumer.AuthenticationException;
import org.verisign.joid.consumer.AuthenticationResult;
import org.verisign.joid.consumer.JoidConsumer;

public final class OpenIdServlet extends HttpServlet
{

private static final long serialVersionUID = 1L;

private static final Logger log = Logger.getLogger(OpenIdServlet.class.getName());
private static final JoidConsumer joidConsumer = new JoidConsumer();
private static final String OPENID_IDENTITY = "openid.identity";

private final FrontEnd frontEnd;
private final AuthService authService;

public OpenIdServlet(final FrontEnd frontEnd, final AuthService authService)
{
	this.frontEnd = frontEnd;
	this.authService = authService;
}

private Map<String,String> convertToStringValueMap(HttpServletRequest servletRequest)
{
	Map<String,String> result = new HashMap<String,String>();
	Map<?,?> parameterMap = servletRequest.getParameterMap();
	Set<?> set = parameterMap.entrySet();
	for (Iterator<?> iter = set.iterator(); iter.hasNext();)
	{
		Map.Entry<?,?> mapEntry = (Map.Entry<?,?>) iter.next();
		String key = (String) mapEntry.getKey();
		Object value = mapEntry.getValue();
		if (value instanceof String)
			result.put(key, (String) value);
		else if (value instanceof String[])
			result.put(key, ((String[]) value)[0]);
		else
			throw new RuntimeException("Argh!");
	}
	return result;
}

@Override
public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
{
	if (req.getParameter("login") != null)
	{
		try
		{
			String identity = req.getParameter("login");
			String authUrl = joidConsumer.getAuthUrl(identity,
					frontEnd.url()+"/openid?url=/", frontEnd.url());
			res.sendRedirect(authUrl);
		}
		catch (Exception e)
		{ throw new IOException(e); }
	}
	
	if (req.getParameter(OPENID_IDENTITY) != null)
	{
		try
		{
			AuthenticationResult result = joidConsumer.authenticate(convertToStringValueMap(req));
			String openidurl = result.getIdentity();
			if (openidurl != null)
			{
				Identity id = authService.mapOpenId(openidurl);
				if (id != null)
				{
					IdSession idSession = frontEnd.getIdSession(req);
					idSession.login(id);
				}
				// redirect to get rid of the long url
				res.sendRedirect(result.getResponse().getReturnTo());
				return;
			}
		}
		catch (AuthenticationException e)
		{
			log.info("auth failed: " + e.getMessage());
			throw new IOException(e); 
		}
		catch (Exception e)
		{ throw new IOException(e); }
	}
	
	if (req.getParameter("url") != null)
	{
		res.sendRedirect(req.getParameter("url"));
		return;
	}
}

}
