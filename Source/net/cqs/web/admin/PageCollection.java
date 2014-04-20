package net.cqs.web.admin;

import java.util.ArrayList;
import java.util.List;

public final class PageCollection
{

	public static final class PageCategory
	{
		private final String name;
		private final List<PageItem> items = new ArrayList<PageItem>();
		public PageCategory(String name)
		{ this.name = name; }
		public String getName()
		{ return name; }
		public void add(PageItem item)
		{ items.add(item); }
		public List<PageItem> getItems()
		{ return items; }
	}
	public static final class PageItem
	{
		private final String name;
		private final String link;
		public PageItem(String name, String link)
		{
			this.name = name;
			this.link = link;
		}
		public String getName()
		{ return name; }
		public String getLink()
		{ return link; }
	}

private final List<PageCategory> categories = new ArrayList<PageCategory>();

public PageCollection()
{/*OK*/}

public PageCategory addPageCategory(String category)
{
	for (PageCategory c : categories)
		if (category.equals(c.name)) return c;
	PageCategory result = new PageCategory(category);
	categories.add(result);
	return result;
}

public List<PageCategory> getCategories()
{ return categories; }

}
