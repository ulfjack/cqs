package net.cqs.storage;

public class TransactionException extends RuntimeException
{

private static final long serialVersionUID = 1L;

public TransactionException()
{/*OK*/}

public TransactionException(String msg)
{ super(msg); }

public TransactionException(Throwable cause)
{ super(cause); }

}
