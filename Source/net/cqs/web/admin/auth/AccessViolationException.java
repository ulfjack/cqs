package net.cqs.web.admin.auth;

public class AccessViolationException extends Exception
{

private static final long serialVersionUID = 1L;

public AccessViolationException(String msg)
{ super(msg); }

public AccessViolationException()
{ this("Access denied!"); }

}