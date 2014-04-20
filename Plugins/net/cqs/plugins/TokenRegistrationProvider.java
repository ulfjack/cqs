package net.cqs.plugins;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.auth.Identity;
import net.cqs.engine.Galaxy;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.EmailEnum;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.StartupListener;
import net.cqs.services.AuthService;
import net.cqs.services.RegistrationService;
import net.cqs.services.ServiceRegistry;
import net.cqs.services.SmtpService;
import net.cqs.services.email.Email;
import net.cqs.services.email.SmtpAddress;
import de.ofahrt.crypt.XTea;
import de.ofahrt.io.HexTranscoder;
import de.ofahrt.io.TranscoderException;

@Plugin
public final class TokenRegistrationProvider implements RegistrationService, StartupListener
{

private static final Logger logger = Logger.getLogger(TokenRegistrationProvider.class.getName());

private static final HexTranscoder HEX = new HexTranscoder();
private static final Pattern TOKEN_PATTERN = Pattern.compile("r=\\d+, email=(.*)");

private final FrontEnd frontEnd;
private final ServiceRegistry registry;
private final String version;
private final SecureRandom rand = new SecureRandom();
private XTea tea;

public TokenRegistrationProvider(PluginConfig config)
{
	this.frontEnd = config.getFrontEnd();
	this.registry = config.getServiceRegistry();
	this.version = config.getFrontEnd().version();
	registry.registerService(RegistrationService.class, this);
}

@Override
public void startup()
{
	try
	{
		tea = new XTea(getTeaKey(frontEnd.getGalaxy()));
	}
	catch (InvalidKeyException e)
	{ throw new RuntimeException(e); }
}

private static byte[] getTeaKey(Galaxy galaxy)
{
	byte[] key = galaxy.getGalaxyId();
	byte[] rkey = new byte[16];
	for (int i = 0; i < rkey.length; i++)
		rkey[i] = key[i];
	return rkey;
}

private byte[] getBytes(String s) throws UnsupportedEncodingException
{
	byte[] a = s.getBytes("UTF-8");
	int len = (a.length+7) & ~7;
	if (a.length == len)
		return a;
	else
	{
		byte[] b = new byte[len];
		for (int i = 0; i < a.length; i++)
			b[i] = a[i];
		for (int i = a.length; i < b.length; i++)
			b[i] = 0;
		return b;
	}
}

@Override
public boolean registerPerEmail(final String email)
{
	try
	{
		String temp = "r="+rand.nextInt()+", email="+email;
		String id = HEX.transcode(tea.encrypt(getBytes(temp)));
		logger.fine(temp+" -> "+id);
		
		SmtpAddress recipient = new SmtpAddress(email);
		Email em = EmailEnum.REGISTRATION.generateEmail(Locale.GERMANY, recipient, version, id);
		registry.findService(SmtpService.class).sendEmail(em);
		return true;
	}
	catch (UnsupportedEncodingException e)
	{ logger.log(Level.SEVERE, "Exception thrown", e); }
	return false;
}

@Override
public Identity createIdentity(String token, String name, String password, String noemail, String[] groups)
{
	try
	{
		String temp = new String(tea.decrypt(HEX.transcode(token)), "UTF-8");
		Matcher matcher = TOKEN_PATTERN.matcher(temp);
		if (!matcher.matches())
			throw new RuntimeException("Data "+temp+" doesn't match!");
		String email = matcher.group(1);
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
	catch (UnsupportedEncodingException e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
	catch (TranscoderException e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
	catch (RuntimeException e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
	return null;
}

}
