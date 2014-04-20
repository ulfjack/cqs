package net.cqs.i18n;

import java.util.ArrayList;
import java.util.Collection;

public class EnumTestHelper
{

public static <T extends Enum<T>> Collection<Object[]> data(Class<T> clazz)
{
	ArrayList<Object[]> result = new ArrayList<Object[]>();
	for (T e : clazz.getEnumConstants())
		result.add(new Object[] { e });
	return result;
}

}
