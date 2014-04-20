package net.cqs.web.frontpage;

import java.io.Serializable;
import java.util.Locale;

import net.cqs.auth.Identity;
import net.cqs.engine.GalaxyACL.PlayerEntry;
import net.cqs.main.config.FrontEnd;
import net.cqs.services.AdminService;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class IdSession implements Serializable
{

private static final long serialVersionUID = 1L;

private transient FrontEnd frontEnd;
private Identity identity;
private Locale locale;
private transient FrontpageSession data;

public IdSession(FrontEnd frontEnd, Locale locale)
{
	if (locale == null) throw new NullPointerException("locale is null");
	update(frontEnd);
	this.locale = locale;
}

public IdSession()
{/*OK*/}

public void update(FrontEnd newFrontEnd)
{
	this.frontEnd = newFrontEnd;
}

public Identity getIdentity()
{ return identity; }

public Locale getLocale()
{ return locale; }

@Restricted
public void setLocale(Locale loc)
{
	if (loc == null) throw new NullPointerException();
	locale = loc;
}

public String getLocaleId()
{ return locale.toString(); }

public String getName()
{ return identity.getName(); }

public PlayerEntry[] getAccessiblePlayers()
{ return frontEnd.getGalaxy().getAccessiblePlayers(getIdentity()); }

public boolean mayCreatePlayer()
{ return frontEnd.getGalaxy().mayCreatePlayer(getIdentity()); }

public FrontpageSession getData()
{
	if (data == null) data = new FrontpageSession();
	return data;
}

public boolean isLoggedIn()
{ return identity != null; }

public boolean mayAccessAdministration()
{
	if (identity == null) return false;
	AdminService service = frontEnd.findService(AdminService.class);
	if (service == null) return false;
	return service.mayAccessAdministration(identity);
}

public void login(Identity id)
{
	if (identity != null) logout();
	identity = id;
}

public void logout()
{ identity = null; }

}
