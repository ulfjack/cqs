package net.cqs.web.frontpage;

import net.cqs.auth.Identity;
import net.cqs.config.InputValidation;
import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.WebPostAction;

public final class AccountPlugin
{

@WebPostAction("Player.create")
public void create(FrontEnd frontEnd, IdSession session,
		@Parameter("nick") String nick)
{
	if (session.getIdentity() == null)
		throw new RuntimeException("Not logged in!");
	
	Identity id = session.getIdentity();
	nick = nick.trim();
	if (InputValidation.validPlayerName(nick))
		frontEnd.getGalaxy().createPlayer(id, nick, frontEnd.getDefaultLocale());
}

}
