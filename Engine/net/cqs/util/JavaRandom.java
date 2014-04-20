package net.cqs.util;

import java.util.Random;

public final class JavaRandom implements RandomNumberGenerator
{

private final Random rand;

public JavaRandom(Random rand)
{ this.rand = rand; }

@Override
public boolean nextBoolean()
{ return rand.nextBoolean(); }

@Override
public int nextInt(int n)
{ return rand.nextInt(n); }

@Override
public double nextDouble()
{ return rand.nextDouble(); }

}
