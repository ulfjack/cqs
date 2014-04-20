package net.cqs.web;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

public class CssTest
{

private void parseFile(String filename) throws IOException
{
	Reader r = new FileReader(filename);
	InputSource is = new InputSource(r);
	CSSOMParser parser = new CSSOMParser();
	CSSStyleSheet styleSheet = parser.parseStyleSheet(is);
	CSSRuleList list = styleSheet.getCssRules();
	for (int i = 0; i < list.getLength(); i++)
	{
		CSSRule rule = list.item(i);
		switch (rule.getType())
		{
			case CSSRule.UNKNOWN_RULE : throw new IOException("Unknown rule in css file");
			case CSSRule.STYLE_RULE :
//				CSSStyleRule srule = (CSSStyleRule) rule;
//				System.out.println(srule.getSelectorText());
				break;
			default :
				System.out.println(rule);
				break;
		}
	}
}

@Test
public void testParse1() throws IOException
{
	String filename = "Html/Design/pack/css/style.css";
	parseFile(filename);
}

/*public void testParse2() throws IOException
{
	String filename = "Html/Pack/css/color.css";
	parseFile(filename);
}*/

}
