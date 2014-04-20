package net.cqs.web;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.HttpService;
import net.cqs.web.frontpage.FrontpageEnvironment;
import net.cqs.web.frontpage.FrontpageServlet;
import net.cqs.web.servlet.DirectoryServlet;
import net.cqs.web.servlet.RulesServlet;
import de.ofahrt.catfish.utils.CheckCompression;

@Plugin
public class WebFrontpage
{

public WebFrontpage(PluginConfig config)
{
	FrontEnd frontEnd = config.getFrontEnd();
	HttpService service = config.findService(HttpService.class);
	ResourceManager resourceManager = config.findService(ResourceManager.class);
	
	boolean forceCaching = false;
	
	service.registerServlet("/", "/check", new CheckCompression());
	service.registerServlet("/", "/rules", new RulesServlet(resourceManager));
	service.registerServlet("/", "/*", new FrontpageServlet(frontEnd, resourceManager));
	service.registerServlet("/", "/robots.txt", new DirectoryServlet(resourceManager, FrontpageEnvironment.PREFIX, forceCaching));
	service.registerServlet("/", "/favicon.ico", new DirectoryServlet(resourceManager, FrontpageEnvironment.PREFIX, forceCaching));
	
	service.registerServlet("/data", "/*", new DirectoryServlet(resourceManager, FrontpageEnvironment.PREFIX+"data/", forceCaching));
}

}
