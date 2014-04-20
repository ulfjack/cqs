package net.cqs.config;

public final class ErrorCodeException extends RuntimeException
{

private static final long serialVersionUID = 1L;

private final ErrorCode errorcode;

public ErrorCodeException(String operation, ErrorCode errorcode)
{
	super("Error Code for \""+operation+"\": "+errorcode);
	this.errorcode = errorcode;
}

public ErrorCodeException(ErrorCode errorcode)
{
	super("Error Code: "+errorcode);
	this.errorcode = errorcode;
}

public ErrorCodeException()
{
	super("No Message!");
	this.errorcode = ErrorCode.NONE;
}

public ErrorCode getErrorCode()
{ return errorcode; }

}
