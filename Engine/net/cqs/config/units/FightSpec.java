package net.cqs.config.units;

public final class FightSpec
{

/* Return the sum of FightSpecs a and fraction*b. */
public static FightSpec sum(FightSpec a, FightSpec b, float fraction) {
	if (a.attack.length != b.attack.length)
		throw new IllegalArgumentException("FightSpec.sum: FightSpec.attack.length not equal");
	float sumDefense = a.defense+b.defense*fraction;
	float sumLandingAttack = a.landingAttack+b.landingAttack*fraction;
	float[] sumAttack = new float[a.attack.length];
	for (int i = 0; i < a.attack.length; i++)
		sumAttack[i] = a.attack[i]+b.attack[i]*fraction;
	return new FightSpec(sumDefense, sumLandingAttack, sumAttack);
}
	
public static FightSpec sum(FightSpec a, FightSpec b)
{ return sum(a, b, 1.0f); }

public final float defense;
public final float landingAttack;
public final float[] attack;

public FightSpec(float defense, float landingAttack, float[] attack)
{
	this.defense = defense;
	this.landingAttack = landingAttack;
	this.attack = new float[attack.length];
	for (int i = 0; i < this.attack.length; i++)
		this.attack[i] = attack[i];
}

public float getDefense()
{ return defense; }

public float getLandingAttack()
{ return landingAttack; }

public int groupCount()
{ return attack.length; }

public float getAttack(int group)
{ return attack[group]; }

public float averageAttack()
{
	float result = 0.0f;
	for (int i = 0; i < attack.length; i++)
		result += attack[i];
	result /= attack.length;
	return result;
}

}
