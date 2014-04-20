package net.cqs.web.admin;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.auth.Identity;
import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;
import net.cqs.web.util.HtmlToolkit;

public final class AdminSession implements Serializable
{

private static final long serialVersionUID = 2L;

private final Identity identity;
public transient List<Player> searchData = new ArrayList<Player>();
public transient String currentFileName;
public transient String actionResult;

public AdminSession(Identity identity)
{
	this.identity = identity;
}

public Locale getLocale()
{ return Locale.US; }

public Identity getIdentity()
{ return identity; }

public List<Player> getSearchResult()
{ return searchData; }

public String getCurrentFilename()
{ return currentFileName; }

@HtmlFragment
public String getActionResult()
{ return actionResult == null ? null : HtmlToolkit.formatText(actionResult); }

public void start(String filename)
{
	if (searchData == null)
		searchData = new ArrayList<Player>();
	currentFileName = filename;
	actionResult = null;
}

private void displayPlayer(PrintWriter out, Player who)
{
	out.println("Name: "+who.getName());
	out.println("Primary Identity: "+who.getPrimaryIdentity());
	out.println("Pid: "+who.getPid());
	out.println("Language: "+who.getLocale().getDisplayName(Locale.US));
//	out.println("Fleet ID: "+who.fleetID);
	out.println("Alliance: "+who.getAlliance());
	out.println("Points: "+who.getPoints());
	out.println("Res-points: "+who.getResourcePoints());
	out.println("Last login: "+new Date(who.getAttr(Attribute.LAST_LOGIN).longValue()));
	
	out.println("Attributes: ");
	Iterator<Map.Entry<Attribute<?>,Object>> it = who.attributeIterator();
	while (it.hasNext())
	{
		Map.Entry<Attribute<?>,Object> entry = it.next();
		out.println("	"+entry.getKey()+" = "+entry.getValue());
	}
	
	out.println("Colonies: ");
	for (int i = 0; i < who.getColonies().size(); i++)
		out.println(" "+who.getColonies().get(i));
	
	out.println("Fleets: ");
	for (int i = 0; i < who.getFleets().size(); i++)
		out.println(" "+who.getFleets().get(i));
}

@HtmlFragment
public String displayPlayer(Player who)
{
	StringWriter out = new StringWriter();
	PrintWriter temp = new PrintWriter(out);
	try
	{
		displayPlayer(temp, who);
	}
	catch (Exception e)
	{ e.printStackTrace(temp); }
	temp.flush();
	return HtmlToolkit.formatText(out.toString());
}

}
