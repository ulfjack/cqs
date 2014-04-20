package net.cqs.web;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.services.AuthService;
import net.cqs.services.HttpService;
import net.cqs.web.servlet.OpenIdServlet;

@Plugin
public class WebOpenId
{

public WebOpenId(PluginConfig config)
{
	FrontEnd frontEnd = config.getFrontEnd();
	HttpService service = frontEnd.findService(HttpService.class);
	
	service.registerServlet("/", "/openid", new OpenIdServlet(frontEnd, config.findService(AuthService.class)));
}

}
