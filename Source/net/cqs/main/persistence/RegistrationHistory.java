package net.cqs.main.persistence;

import java.io.Serializable;

import net.cqs.storage.Context;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;
import net.cqs.util.HistorySeries;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class RegistrationHistory implements Serializable
{

private static final long serialVersionUID = 1L;

private final HistorySeries data =
	new HistorySeries(HistorySeries.UpdateType.MAX, 100*4, 6*1000L*60*60);

public RegistrationHistory()
{/*OK*/}

public int size()
{ return data.size(); }

public long get(int i)
{ return data.get(i); }

@Restricted
public void logRegistrations(long amount)
{ data.update(amount); }


private static final String BINDING = "REGISTRATION_HISTORY";

public static RegistrationHistory getRegistrationHistory()
{
	RegistrationHistory result;
	try
	{
		result = Context.getDataManager().getBinding(BINDING, RegistrationHistory.class);
	}
	catch (NameNotBoundException e)
	{
		result = new RegistrationHistory();
		Context.getDataManager().setBinding(BINDING, result);
	}
	return result;
}

public static RegistrationHistory getRegistrationHistoryCopy(Storage storage)
{
	try
	{ return storage.getCopy(BINDING, RegistrationHistory.class); }
	catch (NameNotBoundException e)
	{ return new RegistrationHistory(); }
}


}
