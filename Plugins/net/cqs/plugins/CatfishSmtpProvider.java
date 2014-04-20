package net.cqs.plugins;

import java.util.logging.Logger;

import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.ShutdownListener;
import net.cqs.plugins.smtp.SmtpClient;
import net.cqs.services.SmtpService;
import net.cqs.services.email.Email;

@Plugin
public class CatfishSmtpProvider implements SmtpService, ShutdownListener
{

private final Logger logger = Logger.getLogger("net.cqs.plugins.SmtpServiceProvider");

private final SmtpClient smtpClient;

public CatfishSmtpProvider(PluginConfig config)
{
	smtpClient = new SmtpClient(config.getRequired("server"), config.getRequired("sender"));
	config.getServiceRegistry().registerService(SmtpService.class, this);
}

@Override
public void sendEmail(Email email)
{
	smtpClient.send(email);
}

@Override
public void shutdown()
{
	// FIXME: Should shutdown the smtp service!
	logger.severe("FIXME: Implement me");
}

}
