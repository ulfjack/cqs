package net.cqs.main.i18n;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import de.ofahrt.i18n.PoEntry;

final class EnumWrapper
{

public EnumWrapper()
{/*OK*/}

public Collection<PoEntry> getEntries(Class<? extends Enum<?>> classToken)
{
	try
	{
		Method bundleNameMethod = classToken.getDeclaredMethod("bundleName");
		bundleNameMethod.setAccessible(true);
		String bundleName = (String) bundleNameMethod.invoke(null);
		
		Method translationMethod = classToken.getMethod("englishTranslation", new Class<?>[0]);
		Method descriptionMethod = null;
		try
		{ descriptionMethod = classToken.getMethod("englishDescription"); }
		catch (NoSuchMethodException e)
		{/*OK*/}
		
		// If getDescription exists, englishDescription must exist as well!
		try
		{
			Method m2 = classToken.getMethod("getDescription", Locale.class);
			if ((m2 != null) && (descriptionMethod == null))
				throw new RuntimeException(classToken+" has a 'getDescription()' method, but not an 'englishDescription()' method!");
		}
		catch (NoSuchMethodException e)
		{/*OK*/}
		
		ArrayList<PoEntry> entries = new ArrayList<PoEntry>();
		HashSet<String> keys = new HashSet<String>();
		for (Enum<?> e : classToken.getEnumConstants())
		{
			{
				String key = (String) translationMethod.invoke(e);
				PoEntry entry = new PoEntry.Builder()
					.setContext(bundleName)
					.setMessage(key)
					.build();
				if (keys.contains(key))
					throw new RuntimeException("Duplicate translation for "+e+": '"+entry.getMessage()+"'");
				keys.add(key);
				entries.add(entry);
			}
			if (descriptionMethod != null)
			{
				String key = (String) descriptionMethod.invoke(e);
				PoEntry entry = new PoEntry.Builder()
					.setContext(bundleName)
					.setMessage(key)
					.build();
				if (keys.contains(key))
					throw new RuntimeException("Duplicate description for "+e);
				keys.add(key);
				entries.add(entry);
			}
		}
		return entries;
	}
	catch (SecurityException e)
	{ throw new RuntimeException(e); }
	catch (NoSuchMethodException e)
	{ throw new RuntimeException(e); }
	catch (IllegalAccessException e)
	{ throw new RuntimeException(e); }
	catch (InvocationTargetException e)
	{ throw new RuntimeException(e); }
}

}
