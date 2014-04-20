package net.cqs.plugins;

import java.util.Locale;

import net.cqs.auth.Identity;
import net.cqs.main.i18n.EmailEnum;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.services.AuthService;
import net.cqs.services.RegistrationService;
import net.cqs.services.ServiceRegistry;
import net.cqs.services.SmtpService;
import net.cqs.services.StorageService;
import net.cqs.services.email.Email;
import net.cqs.services.email.SmtpAddress;
import net.cqs.storage.Context;
import net.cqs.storage.Task;
import net.cqs.util.UIDGenerator;

@Plugin
public final class DatabaseRegistrationProvider implements RegistrationService
{

private static final UIDGenerator generator = new UIDGenerator();

private final ServiceRegistry registry;
private final String version;

public DatabaseRegistrationProvider(PluginConfig config)
{
	this.registry = config.getServiceRegistry();
	this.version = config.getFrontEnd().version();
	registry.registerService(RegistrationService.class, this);
}

private String getBinding(String id)
{ return "REGISTRATION:"+id; }

@Override
public boolean registerPerEmail(final String email)
{
	final String id = generator.generateUniqueID();
	final String binding = getBinding(id);
	registry.findService(StorageService.class).execute(new Task()
		{
			@Override
			public void run()
			{ Context.getDataManager().setBinding(binding, id); }
		});
	
	SmtpAddress recipient = new SmtpAddress(email);
	Email em = EmailEnum.REGISTRATION.generateEmail(Locale.GERMANY, recipient, version, id);
	registry.findService(SmtpService.class).sendEmail(em);
	
	return true;
}

@Override
public Identity createIdentity(String token, String name, String password, String noemail, String[] groups)
{
	String email = registry.findService(StorageService.class).getAndRemove(getBinding(token), String.class);
	if (email == null) return null;
	
	AuthService authService = registry.findService(AuthService.class);
	if (!authService.createIdentity(name, password, email, groups))
		return null;
	
	Identity identity = authService.authenticate(name, password);
	if (identity == null)
		return null;
	
	SmtpAddress recipient = new SmtpAddress(email);
	Email em = EmailEnum.APPLICATION.generateEmail(Locale.GERMANY, recipient, version,
			name, password, null);
	registry.findService(SmtpService.class).sendEmail(em);
	
	return identity;
}

}
