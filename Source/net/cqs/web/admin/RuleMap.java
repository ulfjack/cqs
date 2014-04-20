package net.cqs.web.admin;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.cqs.web.admin.auth.Rule;
import net.cqs.web.admin.auth.RuleList;

final class RuleMap implements Serializable, Iterable<Map.Entry<String,Rule>>
{

private static final long serialVersionUID = 1L;

public static final String ALL_TARGET = "*";

private final Map<String,Rule> rules = new LinkedHashMap<String,Rule>();

public RuleMap()
{/*OK*/}

public RuleMap(String target, Rule rule)
{ add(target, rule); }

public void add(String target, Rule rule)
{
	Rule current = rules.get(target);
	if (current == null)
		rules.put(target, rule);
	else if (current instanceof RuleList)
		((RuleList) current).add(rule);
	else
	{
		RuleList group = new RuleList();
		group.add(current);
		group.add(rule);
		rules.put(target, group);
	}
}

public Rule get(String target)
{ return rules.get(target); }

public Rule get(String group, String category, String item)
{
	RuleList result = new RuleList();
	Rule rule;
	rule = rules.get(ALL_TARGET);
	if (rule != null) result.add(rule);
	rule = rules.get(group);
	if (rule != null) result.add(rule);
	rule = rules.get(group+":"+category);
	if (rule != null) result.add(rule);
	rule = rules.get(group+":"+category+":"+item);
	if (rule != null) result.add(rule);
	return result;
}

public boolean identical(RuleMap other)
{ return rules.equals(other.rules); }

@Override
public Iterator<Entry<String, Rule>> iterator()
{
	return rules.entrySet().iterator();
}

}
