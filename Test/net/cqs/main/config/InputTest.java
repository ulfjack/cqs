package net.cqs.main.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.junit.Test;

public class InputTest
{

@Test
public void simpleGlobConversion1()
{ assertEquals(".*\\Q.html\\E", Input.convertStringToRegex("*.html")); }

@Test
public void simplePatternCheck()
{
	String pattern = Input.convertStringToRegex("*.html");
	assertNotNull(Pattern.compile(pattern));
}

}
