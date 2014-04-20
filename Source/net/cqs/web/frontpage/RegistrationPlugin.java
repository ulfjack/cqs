package net.cqs.web.frontpage;

import net.cqs.auth.Identity;
import net.cqs.config.InputValidation;
import net.cqs.main.config.FrontEnd;
import net.cqs.services.RegistrationService;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.WebPostAction;

public final class RegistrationPlugin
{

public RegistrationPlugin()
{/*OK*/}

@WebPostAction("register")
public void register(FrontEnd frontEnd, IdSession session,
		@Parameter("agbok") String agbok, @Parameter("email") String email)
{
	if ((agbok != null) && (email != null))
	{
		email = email.trim();
		if ((agbok.equals("on")) && (InputValidation.validMail(email)))
		{
			if (frontEnd.findService(RegistrationService.class).registerPerEmail(email))
				session.getData().registeredEmail = true;
		}
	}
}

@WebPostAction("complete")
public void complete(FrontEnd frontEnd, IdSession session,
		@Parameter("id") String id, @Parameter("name") String name,
		@Parameter("pass0") String pass0, @Parameter("pass1") String pass1)
{
	if ((name != null) && (pass0 != null) && (pass1 != null))
	{
		name = name.trim();
		if (InputValidation.validLoginName(name) && InputValidation.validPassword(pass0) && (pass0.equals(pass1)))
		{
			RegistrationService registrationService = frontEnd.findService(RegistrationService.class);
			Identity identity = registrationService.createIdentity(id, name, pass0, null, null);
			session.getData().registeredIdentity = identity;
		}
	}
}

}
