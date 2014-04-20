package net.cqs.storage.memory;

public class DeadlockError extends Error
{

private static final long serialVersionUID = 0L;

public DeadlockError()
{ super(); }

public DeadlockError(String msg)
{ super(msg); }

public DeadlockError(Throwable cause)
{ super(cause); }

}
