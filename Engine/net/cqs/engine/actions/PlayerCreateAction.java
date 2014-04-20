package net.cqs.engine.actions;

import java.util.Locale;

import net.cqs.auth.Identity;
import net.cqs.config.ErrorCodeException;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.PlayerDescriptor;
import net.cqs.engine.human.HumanPlayerController;

public final class PlayerCreateAction extends Action
{

private final PlayerDescriptor descriptor;
private final boolean restricted;

public PlayerCreateAction(Identity id, String name, Locale locale, boolean restricted)
{
	if (name == null) throw new ErrorCodeException();
	descriptor = new PlayerDescriptor(id, name, locale, HumanPlayerController.class);
	this.restricted = restricted;
}

public PlayerCreateAction(Identity id, String name, Locale locale)
{ this(id, name, locale, false); }

@Override
public void execute(Galaxy galaxy)
{
	if (galaxy.findPlayerByName(descriptor.name) != null)
		throw new ErrorCodeException();
	
//	logger.info("Creating new Player "+name+(restricted ? " (RESTRICTED)" : ""));
	Player who = galaxy.createPlayer(descriptor);
	who.setRestricted(restricted);
}

}
