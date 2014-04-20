package net.cqs.plugins;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.services.AuthService;
import net.cqs.services.ServiceRegistry;
import net.cqs.services.StorageService;
import net.cqs.storage.Context;
import net.cqs.storage.DataManager;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Task;

@Plugin
public final class DatabaseAuthProvider implements AuthService, GroupProvider
{

private final Logger logger = Logger.getLogger(DatabaseAuthProvider.class.getName());

	private static class LoginData implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private final Identity identity;
		private String password;
		private String email;
		private String[] groups;
		public LoginData(Identity identity, String password, String email, String[] groups)
		{
			if (identity == null) throw new NullPointerException();
			if (password == null) throw new NullPointerException();
			this.identity = identity;
			this.password = password;
			this.email = email;
			this.groups = groups == null ? new String[0] : groups;
		}
		public Identity getIdentity()
		{ return identity; }
		public boolean checkPassword(String pw)
		{ return password.equals(pw); }
		public String getEmail()
		{ return email; }
		public boolean isInGroup(String name)
		{
			for (int i = 0; i < groups.length; i++)
				if (groups[i].equals(name)) return true;
			return false;
		}
	}

	private static class FailedCreationException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		public FailedCreationException()
		{/*OK*/}
	}

private static final FailedCreationException FAILED_CREATION = new FailedCreationException();
private final ServiceRegistry registry;

public DatabaseAuthProvider(PluginConfig config)
{
	this.registry = config.getServiceRegistry();
	registry.registerService(AuthService.class, this);
}

private String getBinding(String id)
{ return "AUTH:"+id; }

private LoginData getCopy(String id)
{
	try
	{
		String binding = getBinding(id);
		return registry.findService(StorageService.class).getCopy(binding, LoginData.class);
	}
	catch (NameNotBoundException e)
	{/*Fail authentication!*/}
	return null;
}

@Override
public Identity authenticate(String name, String passwd)
{
	String id = name+"@local";
	logger.fine("Attempting to authenticate '"+id+"'");
	LoginData result = getCopy(id);
	if ((result != null) && result.checkPassword(passwd))
		return result.getIdentity();
	logger.log(Level.WARNING, "Authentication of '"+id+"' failed!");
	return null;
}

@Override
public String getEmail(Identity identity)
{
	String id = identity.getName();
	logger.fine("Attempting to get eMail adress for '"+id+"'");
	LoginData result = getCopy(id);
	if (result != null)
		return result.getEmail();
	return null;
}

@Override
public boolean isInGroup(Identity identity, String name)
{
	String id = identity.getName();
	logger.fine("Checking group '"+name+"' for '"+identity+"'");
	LoginData result = getCopy(id);
	if (result != null)
		return result.isInGroup(name);
	return false;
}

@Override
public Identity mapOpenId(String openidurl)
{
	logger.fine("No OpenId mapping for '"+openidurl+"'");
	return null;
}


@Override
public boolean createIdentity(String name, String passwd, String email, String[] groups)
{
	if (name == null) throw new NullPointerException();
	if (passwd == null) throw new NullPointerException();
	String id = name+"@local";
	final String binding = getBinding(id);
	final LoginData lid = new LoginData(new Identity(id), passwd, email, groups);
	try
	{
		registry.findService(StorageService.class).execute(new Task()
			{
				@Override
				public void run()
				{
					DataManager manager = Context.getDataManager();
					try
					{
						manager.getBinding(binding, LoginData.class);
						throw FAILED_CREATION;
					}
					catch (NameNotBoundException e)
					{/*Expected*/}
					manager.setBinding(binding, lid);
				}
			});
	}
	catch (FailedCreationException e)
	{
		logger.warning("Could not create '"+id+"', is already in database!");
		return false;
	}
	return true;
}

@Override
public boolean changePassword(Identity principal, String password)
{ return false; }

@Override
public boolean changeEmail(Identity principal, String email)
{ return false; }

}
