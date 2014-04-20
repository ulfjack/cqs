package net.cqs.plugins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;
import net.cqs.main.config.Input;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.services.AuthService;

@Plugin
public final class DrupalAuthProvider implements AuthService, GroupProvider
{

private static final Logger logger = Logger.getLogger("net.cqs.plugins.DrupalAuthProvider");

private static final Pattern idPattern = Pattern.compile("([_a-zA-Z0-9]+)@[.a-zA-Z]+");

private final String dburl;
private final String dbuser;
private final String dbpassword;

public DrupalAuthProvider(PluginConfig config) throws ClassNotFoundException
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
		id = id+"@drupal.localhost";
		logger.fine("Attempting to authenticate '"+id+"'");
		
		con = DriverManager.getConnection(dburl, dbuser, dbpassword);
		PreparedStatement stmt = con.prepareStatement("SELECT name FROM users WHERE name=? and pass=?");
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
	logger.fine("Attempting to get eMail adress for '"+identity+"'");
	if (identity == null) return null;
	Matcher m = idPattern.matcher(identity.getName());
	if (m.matches())
	{
		final String name = m.group(1);
		Connection con = null;
		try
		{
			con = DriverManager.getConnection(dburl, dbuser, dbpassword);
			PreparedStatement stmt = con.prepareStatement("SELECT mail FROM users WHERE name=?");
			stmt.setString(1, name);
			ResultSet result = stmt.executeQuery();
			if (result.first())
				return result.getString(1);
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
	}
	return null;
}

@Override
public synchronized boolean isInGroup(Identity identity, String group)
{
	logger.fine("Checking group '"+group+"' for '"+identity+"'");
	if (identity == null) return false;
	Matcher m = idPattern.matcher(identity.getName());
	if (m.matches())
	{
		final String name = m.group(1);
		Connection con = null;
		try
		{
			con = DriverManager.getConnection(dburl, dbuser, dbpassword);
			PreparedStatement stmt = con.prepareStatement("SELECT users.name FROM users,users_roles,role WHERE " +
					"(users.name=?) AND (users.uid=users_roles.uid) AND (users_roles.rid = role.rid) AND (role.name=?);");
			stmt.setString(1, name);
			stmt.setString(2, group);
			ResultSet result = stmt.executeQuery();
			if (result.first())
			{
				if (!name.equals(result.getString(1)))
					throw new RuntimeException("Result name fails to be identical to query name!");
				return true;
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
	}
	return false;
}

@Override
public synchronized Identity mapOpenId(String openidurl)
{
	Connection con = null;
	try
	{
		con = DriverManager.getConnection(dburl, dbuser, dbpassword);
		PreparedStatement stmt = con.prepareStatement("SELECT name FROM users,authmap WHERE users.uid=authmap.uid and authmap.authname=?");
		stmt.setString(1, openidurl);
		ResultSet result = stmt.executeQuery();
		if (result.first())
		{
			String name = result.getString(1);
			String id = name+"@drupal.localhost";
			logger.fine("Found mapping to '"+id+"' for '"+openidurl+"'");
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
