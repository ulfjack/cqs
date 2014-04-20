package net.cqs.engine.units;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import net.cqs.i18n.EnumTestHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class UnitEnumImagesTest
{

@Parameters
public static Collection<?> data()
{ return EnumTestHelper.data(UnitEnum.class); }

private final UnitEnum en;

public UnitEnumImagesTest(UnitEnum en)
{
	this.en = en;
}

@Test
public void test() throws Exception
{
	String[] imgs = en.getImageNames();
	assertNotNull(imgs);
	for (String s : imgs)
		assertNotNull(s);
}

}
