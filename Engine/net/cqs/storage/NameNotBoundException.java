package net.cqs.storage;

public class NameNotBoundException extends RuntimeException
{

private static final long serialVersionUID = 1L;

public NameNotBoundException()
{/*OK*/}

public NameNotBoundException(String msg)
{ super(msg); }

public NameNotBoundException(Throwable cause)
{ super(cause); }

}
