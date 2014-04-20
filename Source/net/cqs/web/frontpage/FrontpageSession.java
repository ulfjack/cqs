package net.cqs.web.frontpage;

import java.util.Locale;

import net.cqs.auth.Identity;
import net.cqs.engine.Player;
import net.cqs.web.util.HtmlToolkit;
import de.ofahrt.ulfscript.annotations.HtmlFragment;

public class FrontpageSession
{

public Locale locale;

public String sessionId;

public String errormessage;
public boolean compressionEnabled;

public boolean reminded;

public boolean registeredEmail;
public boolean registeredAccount;
public Player player;

public String registerId;

public Identity registeredIdentity;

public FrontpageSession()
{/*OK*/}

public void start()
{
	reminded = false;
	registeredEmail = false;
	registeredAccount = false;
	
	registerId = null;
}

@HtmlFragment
public String getErrorMessage()
{ return HtmlToolkit.formatText(errormessage); }

public boolean isRegistrationValid()
{ return registeredEmail; }

public boolean isRegistrationComplete()
{ return registeredAccount; }

public Player getRegistrationPlayer()
{ return player; }

public String getRegistrationId()
{ return registerId; }

public boolean isRegistrationIdValid()
{ return false; }

public boolean isRegistrationAllowed()
{ return false; }

public boolean tryReminding()
{ return false; }

public boolean hasReminded()
{ return reminded; }

}
