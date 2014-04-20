package net.cqs.web.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.cqs.web.admin.auth.AllowGroupRule;
import net.cqs.web.admin.auth.AllowPersonRule;
import net.cqs.web.admin.auth.Rule;
import net.cqs.web.admin.auth.RuleList;

import org.junit.Test;

public class XmlRuleConverterTest
{

private Object fromXml(String s)
{
	XmlRuleConverter conv = new XmlRuleConverter();
	return conv.fromXml(s);
}

private String toXml(Object o)
{
	XmlRuleConverter conv = new XmlRuleConverter();
	return conv.toXml(o);
}

@Test
public void roundTripAllowPersonRule()
{
	Rule original = new AllowPersonRule("me");
	assertEquals(original, fromXml(toXml(original)));
}

@Test
public void roundTripAllowGroupRule()
{
	Rule original = new AllowGroupRule("staff");
	assertEquals(original, fromXml(toXml(original)));
}

@Test
public void roundTripRuleList()
{
	RuleList original = new RuleList(new AllowGroupRule("xyz"));
	assertEquals(original, fromXml(toXml(original)));
}

@Test
public void roundTripRuleMap()
{
	RuleMap map = new RuleMap();
	map.add("Page:Quota", new RuleList(new AllowGroupRule("staff")));
	map.add("Page:Core", new AllowPersonRule("Sara"));
	assertTrue(map.identical((RuleMap) fromXml(toXml(map))));
}

@Test
public void ruleToString()
{
	assertEquals("<rule action=\"allow\" range=\"person\" name=\"me\"/>", toXml(new AllowPersonRule("me")));
	assertEquals("<rule action=\"allow\" range=\"group\" name=\"staff\"/>", toXml(new AllowGroupRule("staff")));
}

@Test
public void ruleFromString()
{
	assertEquals(new AllowPersonRule("me"), fromXml("<rule action=\"allow\" range=\"person\" name=\"me\"/>"));
	assertEquals(new AllowGroupRule("staff"), fromXml("<rule action=\"allow\" range=\"group\" name=\"staff\"/>"));
}

@Test
public void listToString()
{
	assertEquals("<ruleset>\n  <rule action=\"allow\" range=\"person\" name=\"me\"/>\n</ruleset>",
			toXml(new RuleList(new AllowPersonRule("me"))));
}

@Test
public void listFromString()
{
	assertEquals(new RuleList(new AllowPersonRule("me")),
			fromXml("<ruleset><rule action=\"allow\" range=\"person\" name=\"me\"/></ruleset>"));
}

@Test
public void roundTripComplexString()
{
	String s = ""
		+"<rules>\n"
		+"  <rule for=\"Page:Core\" action=\"allow\" range=\"group\" name=\"staff\"/>\n"
		+"  <rule for=\"Page:Debug\" action=\"allow\" range=\"group\" name=\"staff\"/>\n"
		+"  <rule for=\"Page:Player\" action=\"allow\" range=\"group\" name=\"staff\"/>\n"
		+"  <rule for=\"Page:Multi\" action=\"allow\" range=\"group\" name=\"staff\"/>\n"
		+"  <rule for=\"Page:System\" action=\"allow\" range=\"group\" name=\"staff\"/>\n"
		+"  <ruleset for=\"Page:Quota\">\n"
		+"    <rule action=\"allow\" range=\"person\" name=\"Sara@local\"/>\n"
		+"    <rule action=\"allow\" range=\"person\" name=\"UlfJack@local\"/>\n"
		+"  </ruleset>\n"
		+"</rules>";
	String t = toXml(fromXml(s));
	assertEquals(s, t);
}

@Test
public void regressRuleFromString()
{
	String s = ""
		+"<rules>"
		+"  <ruleset for=\"*\">"
		+"    <rule action=\"allow\" range=\"person\" name=\"UlfJack@local\"/>"
		+"    <rule action=\"allow\" range=\"person\" name=\"Sara@local\"/>"
		+"  </ruleset>"
		+"</rules>";
	fromXml(s);
}

}
