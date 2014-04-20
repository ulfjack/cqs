package net.cqs.web.admin;

import de.ofahrt.ulfscript.annotations.HtmlFragment;
import net.cqs.main.persistence.PlayerData;
import net.cqs.services.StorageService;
import net.cqs.web.util.HtmlToolkit;

public final class AdminTools
{

private final AdminServlet servlet;

public AdminTools(AdminServlet servlet)
{
	this.servlet = servlet;
}

public PageCollection getMenu()
{ return servlet.getPageCollection(); }

@HtmlFragment
public String displayRights()
{ return HtmlToolkit.formatText(servlet.getRightsAsXML()); }

public String getPlayerComment(int id)
{
	PlayerData data = PlayerData.getPlayerDataCopy(servlet.getFrontEnd().findService(StorageService.class), id);
	String result = data.getAdminComment();
	return result == null ? "null" : result;
}

public int safeParseInt(String value, int def)
{
	try
	{ return Integer.parseInt(value); }
	catch (NullPointerException e)
	{ return def; }
	catch (NumberFormatException e)
	{ return def; }
}

}
