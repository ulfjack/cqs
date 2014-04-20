package net.cqs.web;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.HttpService;
import net.cqs.web.game.ReportServlet;

@Plugin
public class WebReports
{

public WebReports(PluginConfig config)
{
	FrontEnd frontEnd = config.getFrontEnd();
	HttpService service = config.findService(HttpService.class);
	ResourceManager resourceManager = config.findService(ResourceManager.class);
	
	service.registerServlet("/BattleReports", "/*", new ReportServlet(frontEnd, resourceManager, "battle-report.html"));
	service.registerServlet("/AgentObservers", "/*", new ReportServlet(frontEnd, resourceManager, "observer-report.html"));
	service.registerServlet("/AgentReports", "/*", new ReportServlet(frontEnd, resourceManager, "agent-report.html"));
}

}
