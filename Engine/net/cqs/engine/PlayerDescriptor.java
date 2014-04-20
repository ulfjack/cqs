package net.cqs.engine;

import java.io.Serializable;
import java.util.Locale;

import net.cqs.auth.Identity;

public final class PlayerDescriptor implements Serializable
{

private static final long serialVersionUID = 1L;

public final Identity id;
public final String name;
public final Locale locale;
public final Class<? extends PlayerController> factoryToken;

public PlayerDescriptor(Identity id, String name, Locale locale, Class<? extends PlayerController> factoryToken)
{
	this.id = id;
	this.name = name;
	this.locale = locale;
	this.factoryToken = factoryToken;
}

public PlayerController createPlayerController(Player p)
{
	try
	{ return factoryToken.getConstructor(Player.class).newInstance(p); }
	catch (Exception e)
	{ throw new RuntimeException(e); }
}

}
