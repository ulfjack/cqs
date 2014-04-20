package net.cqs.web;

import java.io.File;
import java.io.IOException;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.main.resource.ZipResourceManager;
import net.cqs.services.HttpService;
import net.cqs.web.game.AjaxServlet;
import net.cqs.web.game.CssCompressorServlet;
import net.cqs.web.game.GameServlet;
import net.cqs.web.game.JsonServlet;
import net.cqs.web.servlet.DirectoryServlet;
import net.cqs.web.servlet.UserAccessCheckpoint;

@Plugin
public class WebIngame
{

public WebIngame(PluginConfig config) throws IOException
{
	boolean forceCaching = false;
	
	FrontEnd frontEnd = config.getFrontEnd();
	HttpService service = config.findService(HttpService.class);
	ResourceManager resourceManager;
	
	String url = config.getString("url", "/Ingame");
	if (!url.startsWith("/")) throw new IllegalArgumentException("URL must start with a /, but it does not: \""+url+"\"");
	if (url.endsWith("/")) throw new IllegalArgumentException("URL must not end with a /, but it does: \""+url+"\"");
	
	String source = config.get("source");
	if (source == null)
		resourceManager = config.findService(ResourceManager.class);
	else
		resourceManager = new ZipResourceManager(new File(source));
	
	String js = "Html/Design/js/";
	String pack = "Html/Design/pack/";
	
	service.registerFilter(url, "/*", new UserAccessCheckpoint(frontEnd, "/"));
	service.registerServlet(url, "*.ajax", new AjaxServlet(frontEnd, resourceManager));
	service.registerServlet(url, "*.json", new JsonServlet(frontEnd, resourceManager));
	service.registerServlet(url, "/*", new GameServlet(frontEnd, resourceManager));
	
	service.registerServlet(url+"/js", "*.js", new DirectoryServlet(resourceManager, js, forceCaching));
	service.registerServlet(url+"/pack", "/*", new DirectoryServlet(resourceManager, pack, forceCaching));
	
	CssCompressorServlet compressor = new CssCompressorServlet(frontEnd, resourceManager);
	service.registerServlet(url+"/pack/cache", "*.css", compressor);
	service.registerServlet(url+"/pack/cache", "*.js", compressor);
	
	// FIXME!
	String[] packDirectories = new String[]
		{
			"buildings", "css", "planets", "research", "units", "design", "system",
		};
	for (String d : packDirectories)
	{
		String part = pack+d+"/"; //+File.separatorChar;
		service.registerServlet(url+"/pack/"+d, "/*",
			new DirectoryServlet(resourceManager, part, forceCaching));
	}
}

}
