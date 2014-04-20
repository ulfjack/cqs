package net.cqs.web.frontpage;

import net.cqs.auth.Identity;
import net.cqs.main.config.FrontEnd;
import net.cqs.services.AuthService;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.WebGetAction;
import net.cqs.web.action.WebPostAction;

public final class LoginPlugin
{

public LoginPlugin()
{/*OK*/}

@WebGetAction("lang")
public void lang(FrontEnd frontEnd, IdSession session, String value)
{ session.setLocale(frontEnd.getLocaleProvider().filter(value)); }

@WebPostAction("logout")
public void login(FrontEnd frontEnd, IdSession session)
{ session.logout(); }

@WebPostAction("Locale.set")
public void setLocale(FrontEnd frontEnd, IdSession session, @Parameter("locale") String value)
{ session.setLocale(frontEnd.getLocaleProvider().filter(value)); }

@WebPostAction("login")
public void login(FrontEnd frontEnd, IdSession session,
		@Parameter("user") String name, @Parameter("pass") String pass)
{
	if ((name != null) && (pass != null))
	{
		AuthService authService = frontEnd.findService(AuthService.class);
		Identity id = authService.authenticate(name, pass);
		if (id != null)
			session.login(id);
	}
}

}
