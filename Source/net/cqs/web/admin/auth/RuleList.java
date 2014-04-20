package net.cqs.web.admin.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;

public final class RuleList implements Rule, Serializable, Iterable<Rule>
{

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger("net.cqs.services.auth");

static {
	logger.setLevel(Level.CONFIG);
}

private final List<Rule> list = new ArrayList<Rule>();

public RuleList()
{/*OK*/}

public RuleList(Rule... rules)
{
	for (Rule rule : rules)
		list.add(rule);
}

public void add(Rule rule)
{
	if (rule == null) throw new NullPointerException();
	list.add(rule);
}

@Override
public Iterator<Rule> iterator()
{ return Collections.unmodifiableList(list).iterator(); }

@Override
public boolean check(GroupProvider provider, Identity identity) throws AccessViolationException
{
	for (Rule rule : list)
	{
		if (rule.check(provider, identity))
		{
			if (!(rule instanceof RuleList))
				logger.fine("Granted access to \""+identity+"\" due to \""+rule+"\".");
			return true;
		}
	}
	return false;
}

@Override
public int hashCode()
{ return list.hashCode(); }

@Override
public boolean equals(Object o)
{
	if (!(o instanceof RuleList)) return false;
	RuleList r = (RuleList) o;
	return list.equals(r.list);
}

}