package net.cqs.web;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.HttpService;
import net.cqs.web.admin.AdminEnvironment;
import net.cqs.web.admin.AdminServlet;
import net.cqs.web.servlet.DirectoryServlet;
import net.cqs.web.servlet.GeneralAccessCheckpoint;

@Plugin
public class WebAdmin
{

public WebAdmin(PluginConfig config)
{
	FrontEnd frontEnd = config.getFrontEnd();
	HttpService service = config.findService(HttpService.class);
	ResourceManager resourceManager = config.findService(ResourceManager.class);
	service.registerFilter("/Admin", "/*", new GeneralAccessCheckpoint(frontEnd, "/"));
	service.registerServlet("/Admin", "/*", new AdminServlet(config));
	service.registerServlet("/Admin", "/black.css", new DirectoryServlet(resourceManager, AdminEnvironment.PREFIX, false));
}

}
