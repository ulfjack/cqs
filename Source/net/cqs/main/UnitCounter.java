package net.cqs.main;

import java.io.PrintStream;

/**
 * Berechnet die Anzahl der möglichen Einheitentypen auf Grund von
 * Modulslots und Modulgrößen.
 * Berücksichtigt nur teilweise gefüllte Chassis
 */
public final class UnitCounter
{

private static final int NONE = -1;

private static int amount;
private static int[] size;
private static int[] tries = new int[amount];
private static int[] chosen = new int[amount];

private static int current;
private static int remaining;
private static int result;

private static int parts[];

public static void print(PrintStream out, int[] array)
{
	out.print("[");
	for (int i = 0; i < array.length-1; i++)
	{
		if (array[i] == NONE)
			out.print("-, ");
		else
			out.print(array[i]+", ");
	}
	if (array[array.length-1] == NONE)
		out.println("-]");
	else
		out.println(array[array.length-1]+"]");
}

public static int getSize()
{
	int sum = 0;
	for (int i = 0; i < chosen.length; i++)
		if (chosen[i] != NONE)
			sum += size[chosen[i]];
	return sum;	
}

public static void tryModule(int module)
{
	boolean add = false;
	if ((module < size.length) && (current < chosen.length))
	{
		if (remaining >= size[module])
		{
			add = true;
			chosen[current] = module;
			remaining -= size[module];
			print(System.out, chosen);
			parts[getSize()-1]++;
			current++;
			result++;
		}
		for (int i = module; i < size.length; i++)
			if (remaining >= size[module]) tryModule(i);
		if (add)
		{
			remaining += size[module];
			current--;
			chosen[current] = NONE;
		}
	}
}

public static int calculate(int space)
{
	amount = space;
	
	current = 0;
	remaining = amount;
	result = 0;
	tries = new int[amount];
	chosen = new int[amount];for (int i = 0; i < chosen.length; i++)
	{
		chosen[i] = NONE;
		tries[i] = NONE;
	}
	parts = new int[amount];
	for (int i = 0; i < parts.length; i++)
		parts[i] = 0;
	
	for (int i = 0; i < size.length; i++)
		tryModule(i);
	return result;
}

public static void main(String args[])
{
	int space = 3;
	size = new int[4];
	size[0] = 1;
	size[1] = 1;
	size[2] = 1;
	size[3] = 1;
/*	size[4] = 1;
	size[5] = 1;
	size[6] = 1;
	size[7] = 1;
	size[8] = 1;*/

	int sum = calculate(space)+1;
	System.out.println("");
	System.out.println("Moeglichkeiten: "+sum);
	System.out.println("0 belegt: 1");
	for (int i = 0; i < space; i++)
		System.out.println((i+1)+" belegt: "+parts[i]);
	
}

}
