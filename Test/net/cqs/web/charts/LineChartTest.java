package net.cqs.web.charts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LineChartTest {

@Test
public void testPower0()
{ assertEquals(1, LineChart.power(10, 0)); }

@Test
public void testPower1()
{ assertEquals(10, LineChart.power(10, 1)); }

@Test
public void testPower5()
{ assertEquals(100000, LineChart.power(10, 5)); }

@Test
public void testPower6()
{ assertEquals(1000000, LineChart.power(10, 6)); }

@Test
public void testPowerNegative3()
{ assertEquals(-8, LineChart.power(-2, 3)); }

@Test(expected=IllegalArgumentException.class)
public void testPowerWithNegExp()
{ LineChart.power(10, -1); }

@Test(expected=RuntimeException.class)
public void testPowerOverflow()
{ LineChart.power(10, 19); }

@Test
public void testGetBestScalingFactor()
{ assertEquals(5, LineChart.getBestScalingFactor(0, 6, 3)); }

@Test
public void testGetBestScalingFactor2()
{ assertEquals(500, LineChart.getBestScalingFactor(-200, 505, 4)); }

@Test
public void testGetBestScalingFactorExpectResultOf2()
{ assertEquals(2, LineChart.getBestScalingFactor(-200, 500, 700)); }

@Test
public void testGetBestScalingFactorExpectResultOf1()
{ assertEquals(1, LineChart.getBestScalingFactor(-200, 500, 701)); }

@Test(expected=IllegalArgumentException.class)
public void testGetBestScalingFactorPositiveMinimum()
{ LineChart.getBestScalingFactor(10, 10, 1); }

@Test(expected=IllegalArgumentException.class)
public void testGetBestScalingFactorNegativeMaximum()
{ LineChart.getBestScalingFactor(10, -20, 3); }

@Test(expected=IllegalArgumentException.class)
public void testGetBestScalingFactorSmallerThan3TickCount()
{ LineChart.getBestScalingFactor(0, 10, 2); }

@Test(expected=IllegalArgumentException.class)
public void testGetBestScalingFactorNegativeTickCount()
{ LineChart.getBestScalingFactor(0, 10, -1); }

@Test
public void testGetBestScalingFactorMinIsMinLong()
{ assertEquals(5000000000000000000L, LineChart.getBestScalingFactor(Long.MIN_VALUE, 0, 3)); }

@Test
public void testGetBestScalingFactorMaxIsMaxLong()
{ assertEquals(5000000000000000000L, LineChart.getBestScalingFactor(0, Long.MAX_VALUE, 3)); }

@Test(expected=RuntimeException.class) // the result of this call does not fit in a long :-(
public void testGetBestScalingFactorMinIsMinLongAndMaxIsMaxLongWithThreeValues()
{ assertEquals(5000000000000000000L, LineChart.getBestScalingFactor(Long.MIN_VALUE, Long.MAX_VALUE, 3)); }

@Test
public void testGetBestScalingFactorMinIsMinLongAndMaxIsMaxLongWithFiveValues()
{ assertEquals(5000000000000000000L, LineChart.getBestScalingFactor(Long.MIN_VALUE, Long.MAX_VALUE, 5)); }

@Test
public void testGetScalingSmallDiffWithPositiveMin0()
{
	long[] scaling = LineChart.getScaling(200, 500, 3);
	assertEquals(2, scaling.length);
	assertEquals(0, scaling[0]);
	assertEquals(500, scaling[1]);
}

@Test
public void testGetScalingSmallDiffWithPositiveMin1()
{
	long[] scaling = LineChart.getScaling(200, 500, 4);
	assertEquals(4, scaling.length);
	assertEquals(0, scaling[0]);
	assertEquals(200, scaling[1]);
	assertEquals(400, scaling[2]);
	assertEquals(600, scaling[3]);
}

@Test
public void testGetScalingSmallDiffWithNegativeMin()
{
	long[] scaling = LineChart.getScaling(-200, 505, 4);
	assertEquals(4, scaling.length);
	assertEquals(-500, scaling[0]);
	assertEquals(0, scaling[1]);
	assertEquals(500, scaling[2]);
	assertEquals(1000, scaling[3]);
}

@Test
public void testGetScalingSmallDiffWithNegativeMax()
{
	long[] scaling = LineChart.getScaling(-490000, -200, 6);
	assertEquals(6, scaling.length);
	assertEquals(-500000, scaling[0]);
	assertEquals(-400000, scaling[1]);
	assertEquals(-300000, scaling[2]);
	assertEquals(-200000, scaling[3]);
	assertEquals(-100000, scaling[4]);
	assertEquals(0, scaling[5]);
}

}
