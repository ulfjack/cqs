package net.cqs.config;

public class InvalidDatabaseException extends Exception
{

private static final long serialVersionUID = 1L;

public InvalidDatabaseException(Object src, String msg)
{ super(src.toString()+": "+msg); }

public InvalidDatabaseException(String msg)
{ super(msg); }

}
