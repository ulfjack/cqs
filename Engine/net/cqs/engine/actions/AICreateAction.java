package net.cqs.engine.actions;

import java.util.Locale;

import net.cqs.config.ErrorCodeException;
import net.cqs.engine.Galaxy;
import net.cqs.engine.PlayerDescriptor;
import net.cqs.engine.ai.AIPlayerController;

public final class AICreateAction extends Action
{

private final PlayerDescriptor descriptor;

public AICreateAction(String name, Locale locale)
{
	if (name == null) throw new ErrorCodeException();
	descriptor = new PlayerDescriptor(null, name, locale, AIPlayerController.class);
}

@Override
public void execute(Galaxy galaxy)
{
	if (galaxy.findPlayerByName(descriptor.name) != null)
		throw new ErrorCodeException();
	
	logger.info("Creating new Player "+descriptor.name);
	galaxy.createPlayer(descriptor);
}

}
