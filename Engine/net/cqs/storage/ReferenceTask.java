package net.cqs.storage;

import java.io.Serializable;

public class ReferenceTask implements Runnable, Serializable
{

private static final long serialVersionUID = 1L;

private final ManagedReferenceImpl ref;

public ReferenceTask(ManagedReferenceImpl ref)
{ this.ref = ref; }

@Override
public void run()
{
	ref.get(Runnable.class).run();
}

}
