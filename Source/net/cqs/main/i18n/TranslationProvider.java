package net.cqs.main.i18n;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.cqs.i18n.Translation;
import net.cqs.i18n.Translator;
import de.ofahrt.i18n.PoEntry;

final class TranslationProvider
{

public static <T extends Translator> List<PoEntry> getTranslations(Class<T> clazz)
{
	if (!clazz.isInterface())
		throw new IllegalArgumentException();
	Method[] methods = clazz.getMethods();
	String bundleName = clazz.getCanonicalName();
	List<PoEntry> result = new ArrayList<PoEntry>();
	for (Method m : methods)
	{
		Translation translation = m.getAnnotation(Translation.class);
		PoEntry entry = new PoEntry.Builder()
			.setContext(bundleName)
			.setMessage(translation.value())
			.build();
		result.add(entry);
	}
	return result;
}

}
