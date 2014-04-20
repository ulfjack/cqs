package net.cqs.web.charts;

public class LineChart {

/**
 * Calculates base to the exponent using fast multiplication.
 * Expects an exponent >= 0.
 */
static long power(long base, int exponent)
{
	if (exponent < 0)
		throw new IllegalArgumentException();
	if (base == 0)
		return 0;
	if (exponent == 0)
		return 1;
	else if (exponent % 2 == 0)
	{
		long pow = power(base, exponent/2);
		if (((base > 0) && (Long.MAX_VALUE/pow <= pow)) ||
			((base < 0) && (Long.MAX_VALUE/pow >= pow)))
			throw new RuntimeException("Overflow!");
		return pow*pow;
	}
	else
	{
		long evenPow = power(base,exponent-1);
		if (((base > 0) && (Long.MAX_VALUE/base <= evenPow)) ||
			((base < 0) && (Long.MAX_VALUE/base >= evenPow)))
			throw new RuntimeException("Overflow!");
		return base*evenPow;
	}
}

/**
 * Given min and max, determines scaling factor such that:
 * <ol><li>the result is of the shape m*10^k, where m is 1, 2, or 5, and k >= 0</li>
 * <li>there are at most maxValues points between (min-result+1) and (max+result-1)
 * that are multiples of the result</li>
 * <li>and the result is the smallest number with the above properties.</li></ol>
 * 
 * Examples:<ul>
 * <li>-200, 505, 4 -> 500 (-500, 0, 500, 1000)</li>
 * <li>-200, 505, 3 -> 1000 (-1000, 0, 1000)</li></ul>
 * 
 */
static long getBestScalingFactor(long min, long max, int maxValues)
{
	if (min > 0)
		throw new IllegalArgumentException("Expected non-positive min value.");
	if (max < 0)
		throw new IllegalArgumentException("Expected non-negative max value");
	if (maxValues < 3)
		throw new IllegalArgumentException("Expected scaling factor >= 3.");
	if ((min > Integer.MIN_VALUE) && (max < Integer.MAX_VALUE) && (max-min < maxValues))
		return 1;
	// i=56 corresponds to 5*10^18, which is the largest such number that fits in a long
	for (int i = 1; i < 57; i++)
	{
		long scaling;
		if (i%3 == 0)
			scaling = 1;
		else if (i%3 == 1)
			scaling = 2;
		else
			scaling = 5;
		scaling *= power(10, i/3);
		long negValues = -(min/scaling)+(min % scaling == 0 ? 0 : 1);
		if (negValues > maxValues) continue;
		long posValues = max/scaling+(max % scaling == 0 ? 0 : 1);
		if (posValues > maxValues) continue;
		long values = negValues+posValues+1;
		if (values <= maxValues) return scaling;
	}
	throw new RuntimeException("Overflow!");
}

// maybe return String[], some gridline annotations are out of range for a long
// maybe fold into this method the shorthand calculation, e.g.: 5000 -> 5k
// maybe pass in the scaling factor, this allows testing this method independently of getBestScalingFactor
// or maybe pass in a description of the scaling factor, such that only valid factors can be passed in
// ala (mantisse*10^exponent), mantisse in {1,2,5}, exponent >= 0
static long[] getScaling(long min, long max, int maxValues)
{
	min = Math.min(min, 0);
	max = Math.max(max, 0);
	long step = getBestScalingFactor(min, max, maxValues);
	
	long finalMin = -((-min+step-1)/step) * step;
	long finalMax = ((max+step-1)/step) * step;
	long[] result = new long[(int) ((finalMax-finalMin)/step + 1)];
	for (int i = 0; i < result.length; i++)
		result[i] = finalMin + i*step;
	return result;
}

public static String getUrl(int width, int height, String[] xLabels, long[] values)
{
	String url = "http://chart.apis.google.com/chart?cht=lc";
	url += "&amp;chs="+width+"x"+height; // resolution.
	url += "&amp;chxt=x,y,y"; // axes.
	url += "&amp;chf=bg,s,000000"; // background color.
	url += "&amp;chxs=0,FFFFFF|1,FFFFFF|2,FFFFFF"; // colors.
	url += "&amp;chxtc=0,-"+height+"|2,-"+width; // length of grid lines.
	// data.
	url += "&amp;chd=t:";
	long min = values[0];
	long max = values[0];
	for (int i = 0; i < values.length; i++)
	{
		url += values[i];
		if (i < values.length-1)
			url += ",";
		if (values[i] < min)
			min = values[i];
		if (values[i] > max)
			max = values[i];
	}
	long[] gridlines = getScaling(min, max, 9);
	url += "&amp;chxr=1,"+gridlines[0]+","+gridlines[gridlines.length-1]; // axis range.
	url += "&amp;chds="+gridlines[0]+","+gridlines[gridlines.length-1]; // data scaling.
	// x axis labels.
	url += "&amp;chxl=0:|";
	for (int i = 0; i < xLabels.length; i++)
	{
		url += xLabels[i]+"|";
	}
	// y axis labels.
	url += "1:|";
	long abs = Math.max(Math.abs(gridlines[0]), gridlines[gridlines.length-1]);
	for (int i = 0; i < gridlines.length; i++)
	{
		if (abs >= 1e10)
			url += gridlines[i]/1e9+"g";
		else if (abs >= 1e7)
			url += gridlines[i]/1e6+"m";
		else if (abs >= 1e4)
			url += gridlines[i]/1e3+"k";
		else
			url += gridlines[i];
		url += "|";
	}
	url += "2:"; // no grid axis labels.
	for (int i = 0; i < gridlines.length; i++)
		url += "|";
/*	url += "&chxp=1,"; // grid lines.
	for (int i = 0; i < gridlines.length; i++)
	{
		url += gridlines[i];
		if (i < gridlines.length-1)
			url += ",";
	}*/
	return url;
}

}

