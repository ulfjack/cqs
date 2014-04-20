package net.cqs.config.base;

import net.cqs.config.Time;

import org.junit.Assert;
import org.junit.Test;

public class TimeTest
{

private static final void assertEquals(long a, long b)
{ Assert.assertEquals(Long.valueOf(a), Long.valueOf(b)); }

@Test
public void testMillis()
{ assertEquals(1, Time.millis(1000)); }

@Test
public void testSeconds()
{ assertEquals(Time.millis(1000), Time.seconds(1)); }

@Test
public void testMinutes()
{ assertEquals(Time.seconds(60), Time.minutes(1)); }

@Test
public void testHours()
{ assertEquals(Time.minutes(60), Time.hours(1)); }

@Test
public void testDays()
{ assertEquals(Time.hours(24), Time.days(1)); }

@Test
public void testParseTime1()
{ assertEquals(Time.seconds(13), Time.parseTime("13s")); }

@Test
public void testParseTime2()
{ assertEquals(Time.minutes(33), Time.parseTime("33m")); }

@Test
public void testParseTime3()
{ assertEquals(Time.hours(92), Time.parseTime("92h")); }

@Test
public void testParseTime4()
{ assertEquals(Time.days(17), Time.parseTime("17d")); }

@Test(expected=NumberFormatException.class)
public void testParseTime5()
{ Time.parseTime(""); }

@Test(expected=NumberFormatException.class)
public void testParseTime6()
{ Time.parseTime("x"); }

@Test(expected=NumberFormatException.class)
public void testParseTime7()
{ Time.parseTime("1x2d"); }

}
