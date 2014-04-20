package net.cqs.main.i18n;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.cqs.main.i18n.TranslationProvider;

import org.junit.Test;

import de.ofahrt.i18n.PoEntry;

public class TranslationProviderTest
{

@Test
public void testGetTranslations()
{
	List<PoEntry> result = TranslationProvider.getTranslations(TestTranslator.class);
	assertEquals(1, result.size());
	PoEntry entry = result.get(0);
	assertEquals("Get some {0} for {1,number,integer}.", entry.getMessage());
}

}
