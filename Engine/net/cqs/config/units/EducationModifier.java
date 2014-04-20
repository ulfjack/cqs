package net.cqs.config.units;

import net.cqs.config.EducationEnum;
import net.cqs.config.Resource;
import net.cqs.engine.base.Cost;
import net.cqs.util.EnumIntMap;

public final class EducationModifier
{

EducationEnum[] education;
float[][] modifiers;

public EducationModifier()
{
	education = new EducationEnum[0];
	modifiers = new float[0][];
}

public void addEducation(EducationEnum edu, float[] factors)
{
	EducationEnum[] newEdu = new EducationEnum[education.length+1];
	System.arraycopy(education, 0, newEdu, 0, education.length);
	newEdu[newEdu.length-1] = edu;
	education = newEdu;
	float[][] newModifiers = new float[modifiers.length+1][];
	System.arraycopy(modifiers, 0, newModifiers, 0, modifiers.length);
	newModifiers[newModifiers.length-1] = factors;
	modifiers = newModifiers;
}

public static float factor(int level)
{ return (float) (1.0-Math.exp(-level/75.0f)); }

public Cost modify(Cost cost, EnumIntMap<EducationEnum> edu)
{
	float[] factors = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	for (int i = 0; i < education.length; i++)
	{
		float leveleffect = factor(edu.get(education[i]));
		for (int j = 0; j < factors.length; j++)
			factors[j] -= modifiers[i][j]*leveleffect;
	}
	int[] rcost = new int[Resource.MAX+1];
	for (int i = Resource.MIN; i <= Resource.MAX; i++)
		rcost[i] = cost.getResource(i);
	for (int i = 0; i < factors.length; i++)
		rcost[i] *= factors[i];
	return new Cost(cost, rcost);
}

}
