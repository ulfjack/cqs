package net.cqs.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.cqs.config.EducationEnum;
import net.cqs.util.EnumIntMap;
import net.cqs.util.RandomNumberGenerator;

import org.junit.Test;

public class ColonyTest
{

@Test
public void testChooseRandomEducation()
{
	EnumIntMap<EducationEnum> edu = EnumIntMap.of(EducationEnum.class);
	EnumIntMap<EducationEnum> pen = EnumIntMap.of(EducationEnum.class);
	edu.increase(EducationEnum.AIRCRAFT);
	EducationEnum result = Colony.chooseRandomEducation(edu, pen, new RandomNumberGenerator()
		{
			@Override
			public boolean nextBoolean()
			{ return false; }
			@Override
			public double nextDouble()
			{ return 0; }
			@Override
			public int nextInt(int n)
			{ return 0; }
		});
	assertNotNull(result);
	assertEquals(EducationEnum.AIRCRAFT, result);
}

}
