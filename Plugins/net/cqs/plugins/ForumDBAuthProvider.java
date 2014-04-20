package net.cqs.plugins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;
import net.cqs.main.config.Input;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.services.AuthService;

@Plugin
public final class ForumDBAuthProvider implements AuthService, GroupProvider
{

private static final Logger logger = Logger.getLogger(ForumDBAuthProvider.class.getName());

private final String dburl;
private final String dbuser;
private final String dbpassword;

public ForumDBAuthProvider(PluginConfig config) throws ClassNotFoundException
{
	config.getServiceRegistry().registerService(AuthService.class, this);
	Class.forName("com.mysql.jdbc.Driver");
	
	dburl = "jdbc:"+config.getRequired("url");
	dbuser = config.getRequired("user");
	dbpassword = config.getRequired("password");
}

@Override
public synchronized Identity authenticate(String id, String passwd)
{
	Connection con = null;
	try
	{
		final String name = id;
		final String md5pass = Input.md5sumAsHex(passwd);
		id = id+"@forumdb.localhost";
		logger.fine("Attempting to authenticate '"+id+"'");
		
		con = DriverManager.getConnection(dburl, dbuser, dbpassword);
		PreparedStatement stmt = con.prepareStatement("SELECT member_name FROM members WHERE member_name=? and passwd=?");
		stmt.setString(1, name);
		stmt.setString(2, md5pass);
		ResultSet result = stmt.executeQuery();
		if (result.first())
		{
			if (name.equals(result.getString(1)))
				return new Identity(id);
		}
	}
	catch (Exception e)
	{ logger.log(Level.WARNING, "", e); }
	finally
	{
		try
		{ if (con != null) con.close(); }
		catch (SQLException e)
		{/*IGNORED*/}
	}
	logger.log(Level.WARNING, "Authentication of '"+id+"' failed!");
	return null;
}

@Override
public synchronized String getEmail(Identity identity)
{
	logger.fine("No eMail adress for '"+identity+"'");
	return null;
}

@Override
public synchronized boolean isInGroup(Identity identity, String group)
{
	logger.fine("No group '"+group+"' for '"+identity+"'");
	return false;
}

@Override
public synchronized Identity mapOpenId(String openidurl)
{
	logger.fine("No openid for '"+openidurl+"'");
	return null;
}


@Override
public synchronized boolean createIdentity(String name, String passwd, String email, String[] groups)
{ return false; }

@Override
public synchronized boolean changePassword(Identity identity, String password)
{ return false; }

@Override
public synchronized boolean changeEmail(Identity identity, String email)
{ return false; }

}
