package net.cqs.web;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.HttpService;
import net.cqs.web.game.AjaxServlet;
import net.cqs.web.game.GameServlet;
import net.cqs.web.game.JsonServlet;
import net.cqs.web.servlet.DirectoryServlet;
import net.cqs.web.servlet.GuestAccessCheckpoint;

@Plugin
public class WebGuest
{

public WebGuest(PluginConfig config)
{
	FrontEnd frontEnd = config.getFrontEnd();
	HttpService service = frontEnd.findService(HttpService.class);
	ResourceManager resourceManager = config.findService(ResourceManager.class);
	
	service.registerFilter("/Guest", "/*", new GuestAccessCheckpoint(frontEnd));
	service.registerServlet("/Guest", "*.ajax", new AjaxServlet(frontEnd, resourceManager));
	service.registerServlet("/Guest", "*.json", new JsonServlet(frontEnd, resourceManager));
	service.registerServlet("/Guest", "/*", new GameServlet(frontEnd, resourceManager));
	
	String js = "Html/Design/js/";
	String pack = "Html/Design/pack/";
	service.registerServlet("/Guest/js", "*.js", new DirectoryServlet(resourceManager, js, false));
	service.registerServlet("/Guest/pack", "/*", new DirectoryServlet(resourceManager, pack, false));
	
	// FIXME!
	String[] packDirectories = new String[]
		{
			"buildings", "css", "planets", "research", "units", "design", "system",
		};
	for (String d : packDirectories)
	{
		String part = pack+d+"/"; //+File.separatorChar;
		service.registerServlet("/Guest/pack/"+d, "/*",
			new DirectoryServlet(resourceManager, part, false));
	}
}

}
