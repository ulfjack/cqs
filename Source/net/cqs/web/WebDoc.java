package net.cqs.web;

import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.HttpService;
import net.cqs.web.servlet.DirectoryServlet;

@Plugin
public class WebDoc
{

public WebDoc(PluginConfig config)
{
	HttpService service = config.findService(HttpService.class);
	ResourceManager resourceManager = config.findService(ResourceManager.class);
//	service.registerFilter("/Docs", "/*", new GeneralAccessCheckpoint(frontEnd, "/"));
	service.registerServlet("/Docs", "/*", new DirectoryServlet(resourceManager, "Doc/Docs/", false));
}

}
