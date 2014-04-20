package net.cqs.engine.units;

import net.cqs.config.ResearchEnum;
import net.cqs.engine.base.DependencyList;

public final class UnitHelper
{

public static void transitiveDependencies(DependencyList<ResearchEnum> list)
{
	boolean modified = true;
	while (modified)
	{
		modified = false;
		for (int i = 0; i < list.size(); i++)
		{
			ResearchEnum e = list.peek(i);
			ResearchEnum f = e.getDep();
			int fcount = e.getDepCount();
			if (list.get(f) < fcount)
			{
				list.set(f, fcount);
				modified = true;
				break;
			}
		}
	}
}

}
