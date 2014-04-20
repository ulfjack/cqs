package de.ofahrt.i18n;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class PoMergerTest
{

private static final List<PoEntry> EMPTY_LIST = Collections.emptyList();	

@Test
public void emptyMerge()
{
	assertEquals(EMPTY_LIST, new PoMerger().merge(EMPTY_LIST, EMPTY_LIST));
}

@Test
public void singleMergeMatching()
{
	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().setMessage("a").setTranslation("b").build());
	ArrayList<PoEntry> toTranslate = new ArrayList<PoEntry>();
	toTranslate.add(new PoEntry.Builder().setMessage("a").build());
	assertEquals(existing, new PoMerger().merge(toTranslate, existing));
}

@Test
public void singleMergeMatchingChangedData()
{
	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().setMessage("a").setTranslation("b")
			.setTranslatorComment("comment").build());
	ArrayList<PoEntry> toTranslate = new ArrayList<PoEntry>();
	toTranslate.add(new PoEntry.Builder().setMessage("a")
			.setReference("ref").build());
	ArrayList<PoEntry> result = new ArrayList<PoEntry>();
	result.add(new PoEntry.Builder().setMessage("a").setTranslation("b")
			.setTranslatorComment("comment").setReference("ref").build());
	assertEquals(result, new PoMerger().merge(toTranslate, existing));
}


@Test
public void singleMergeNonmatching()
{
	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().setMessage("a").setTranslation("b").build());
	ArrayList<PoEntry> toTranslate = new ArrayList<PoEntry>();
	toTranslate.add(new PoEntry.Builder().setMessage("b").build());
	assertEquals(toTranslate, new PoMerger().merge(toTranslate, existing));
}

@Test
public void recognizeHeader()
{
	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().build());
	existing.add(new PoEntry.Builder().setMessage("a").setTranslation("b").build());
	ArrayList<PoEntry> toTranslate = new ArrayList<PoEntry>();
	toTranslate.add(new PoEntry.Builder().setMessage("b").build());
	ArrayList<PoEntry> result = new ArrayList<PoEntry>();
	result.add(new PoEntry.Builder().build());
	result.add(new PoEntry.Builder().setMessage("b").build());
	assertEquals(result, new PoMerger().merge(toTranslate, existing));
}

@Test
public void mergeEmptyExisting()
{
	ArrayList<PoEntry> toTranslate = new ArrayList<PoEntry>();
	toTranslate.add(new PoEntry.Builder().setMessage("b").build());
	assertEquals(toTranslate, new PoMerger().merge(toTranslate, EMPTY_LIST));
}

@Test
public void testEditDistance()
{
	assertEquals(11, PoMerger.editDistance("", "abracadabra"));
	assertEquals(4, PoMerger.editDistance("banana", "ananas"));
	assertEquals(12, PoMerger.editDistance("Kirche", "Hotel"));
	assertEquals(5, PoMerger.editDistance("KIRCHE", "Kirche"));
}

@Test
public void testFuzzyMatch()
{
	PoEntry toMatch = new PoEntry.Builder().setMessage("ananas").build();
	PoEntry fuzzyMatch = new PoEntry.Builder().setMessage("banana").build();

	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().setMessage("apple").build());
	existing.add(fuzzyMatch);
	existing.add(new PoEntry.Builder().setMessage("grapefruit").build());
	existing.add(new PoEntry.Builder().setMessage("kiwi").build());
	
	assertEquals(fuzzyMatch, PoMerger.fuzzyMatch(existing, toMatch));
}

@Test
public void testFuzzyMatchWithContext()
{
	PoEntry toMatch = new PoEntry.Builder().setMessage("ananas")
		.setContext("context").build();
	PoEntry fuzzyMatch1 = new PoEntry.Builder().setMessage("banana").build();
	PoEntry fuzzyMatch2 = new PoEntry.Builder().setMessage("banana")
		.setContext("context").build();

	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().setMessage("apple").build());
	existing.add(fuzzyMatch1);
	existing.add(new PoEntry.Builder().setMessage("grapefruit").build());
	existing.add(fuzzyMatch2);
	existing.add(new PoEntry.Builder().setMessage("kiwi").build());
	
	assertEquals(fuzzyMatch2, PoMerger.fuzzyMatch(existing, toMatch));
}

@Test
public void testFuzzyMerge()
{
	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(new PoEntry.Builder().setMessage("banana")
			.setTranslation("banana").build());
	ArrayList<PoEntry> toTranslate = new ArrayList<PoEntry>();
	toTranslate.add(new PoEntry.Builder().setMessage("ananas")
			.setFlags("flag").build());
	ArrayList<PoEntry> result = new ArrayList<PoEntry>();
	result.add(new PoEntry.Builder().setMessage("ananas")
			.setTranslation("banana").setFlags("flag,fuzzy").build());
	assertEquals(result, new PoMerger().merge(toTranslate, existing));
}

@Test
public void testFuzzyMatch2()
{
	PoEntry toMatch = new PoEntry.Builder()
		.setMessage("Tanks are needed to increase oil capacity.<br />Each oil "
				+"tank provides space for {0,number,integer} tons of oil.").build();
	PoEntry fuzzyMatch = new PoEntry.Builder()
	.setMessage("Tanks are needed to increase oil capacity.")
	.setTranslation("ok").build();

	ArrayList<PoEntry> existing = new ArrayList<PoEntry>();
	existing.add(fuzzyMatch);
	
	assertEquals(fuzzyMatch, PoMerger.fuzzyMatch(existing, toMatch));
}

}
