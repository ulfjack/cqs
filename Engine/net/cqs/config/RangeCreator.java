package net.cqs.config;

import java.io.Serializable;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RangeCreator implements Serializable
{

private static final long serialVersionUID = 1L;

private static Pattern paramPattern = Pattern.compile("\\s*(-?\\d+)\\s*-\\s*(-?\\d+)\\s*");

private final int minimum;
private final int maximum;

public RangeCreator(String param)
{
	Matcher m = paramPattern.matcher(param);
	m.matches();
	minimum = Integer.parseInt(m.group(1));
	maximum = Integer.parseInt(m.group(2));
}

public int create(Random rand)
{ return rand.nextInt(maximum-minimum+1)+minimum; }

}