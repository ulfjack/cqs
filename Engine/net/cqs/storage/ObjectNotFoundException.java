package net.cqs.storage;

public class ObjectNotFoundException extends RuntimeException
{

private static final long serialVersionUID = 1L;

public ObjectNotFoundException()
{/*OK*/}

public ObjectNotFoundException(String msg)
{ super(msg); }

public ObjectNotFoundException(Throwable cause)
{ super(cause); }

}
