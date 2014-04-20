package net.cqs.config;

import java.util.regex.Pattern;

public class InputValidation
{

private static final String forbiddenCharacters = "<>&%+#\"\\*\\?\\\\";
private static final String allowedCharacters = "[^"+forbiddenCharacters+"]";
private static final String allowedStart = "[^"+forbiddenCharacters+"\\s]";
private static final String allowedEnd   = allowedStart;

private static final Pattern ALLOWED_LOGIN_NAME = 
	Pattern.compile("[a-zA-Z_]{3,28}");

private static final Pattern ALLOWED_PLAYER_NAME = 
	Pattern.compile(allowedStart+allowedCharacters+"{1,28}"+allowedEnd);

private static final Pattern ALLOWED_ALLIANCE_NAME =  
	Pattern.compile(allowedStart+allowedCharacters+"{1,28}"+allowedEnd);
private static final Pattern ALLOWED_ALLIANCE_SHORT = 
	Pattern.compile(allowedStart+"{1,10}");

private static final Pattern ALLOWED_COLONY_NAME = ALLOWED_PLAYER_NAME;
private static final Pattern ALLOWED_FLEET_NAME  = 
	Pattern.compile(allowedStart+"(?:"+allowedCharacters+"{0,28}"+allowedEnd+")?");

private static final Pattern ALLOWED_PASSWORD = Pattern.compile(".{5,20}");

private static final Pattern ALLOWED_EMAIL = Pattern.compile("[^\\s]+@[^\\s]+\\.[^\\s]+");


public static boolean validMail(String s)
{ return ALLOWED_EMAIL.matcher(s).matches(); }

public static boolean validLoginName(String s)
{ return ALLOWED_LOGIN_NAME.matcher(s).matches(); }

public static boolean validPassword(String s)
{ return ALLOWED_PASSWORD.matcher(s).matches(); }

public static boolean validPlayerName(String s)
{ return ALLOWED_PLAYER_NAME.matcher(s).matches(); }

public static boolean validAllianceName(String s)
{ return ALLOWED_ALLIANCE_NAME.matcher(s).matches(); }

public static boolean validAllianceShort(String s)
{ return ALLOWED_ALLIANCE_SHORT.matcher(s).matches(); }

public static boolean validColonyName(String s)
{ return ALLOWED_COLONY_NAME.matcher(s).matches(); }

public static boolean validFleetName(String s)
{ return ALLOWED_FLEET_NAME.matcher(s).matches(); }

}
