package net.cqs.plugins;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import net.cqs.auth.Identity;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.services.AuthService;

@Plugin
public class LdapAuthProvider implements AuthService
{

private static final Logger logger = Logger.getLogger("net.cqs.plugins.LdapAuthProvider");

private final String serverUrl; // "ldap://127.0.0.1:389/"

public LdapAuthProvider(PluginConfig config)
{
	this.serverUrl = config.getRequired("server");
	config.getServiceRegistry().registerService(AuthService.class, this);
}

@Override
public Identity authenticate(String id, String passwd)
{
	try
	{
		String name = "uid="+id+",ou=People,dc=unet,dc=de";
		Hashtable<Object,Object> env = new Hashtable<Object,Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, serverUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, name);
		env.put(Context.SECURITY_CREDENTIALS, passwd);
		
		
		// authenticate
		DirContext ctx = new InitialDirContext(env);
		
//		Attributes attrs = ctx.getAttributes(name);
//		logger.fine(attrs);
//		logger.fine(attrs.get("loginShell"));
//		logger.fine(name+" is bound to: "+obj);
//		String group = (String) attrs.get("gidNumber").get();
		
		ctx.close();
		return new Identity(id);
	}
	catch (NamingException e)
	{
		logger.log(Level.WARNING, "authentication failed", e);
		return null;
	}
}

@Override
public String getEmail(Identity principal)
{
	// TODO: get email from ldap
	return null;
}

@Override
public boolean isInGroup(Identity principal, String name)
{
	// TODO: get groups from ldap or local db
	return false;
}

@Override
public Identity mapOpenId(String openidurl)
{
	logger.fine("No OpenId mapping for '"+openidurl+"'");
	return null;
}


@Override
public synchronized boolean createIdentity(String name, String passwd, String email, String[] groups)
{ return false; }

@Override
public synchronized boolean changePassword(Identity principal, String password)
{ return false; }

@Override
public synchronized boolean changeEmail(Identity principal, String email)
{ return false; }

}
