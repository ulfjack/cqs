package net.cqs.plugins;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import de.ofahrt.catfish.ReadableHttpResponse;
import de.ofahrt.catfish.RequestListener;

import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.signals.StartupListener;
import net.cqs.main.signals.StrobeListener;
import net.cqs.plugins.quota.QuotaConfiguration;
import net.cqs.plugins.quota.QuotaEntry;
import net.cqs.services.HttpService;
import net.cqs.services.StorageService;
import net.cqs.web.game.CqsSession;

@Plugin
public class QuotaRegulator implements StrobeListener, StartupListener
{

private final Logger logger = Logger.getLogger(QuotaRegulator.class.getName());

private final FrontEnd frontEnd;
private final int interval;

private long lastTime = System.currentTimeMillis();

public QuotaRegulator(PluginConfig config)
{
	this.frontEnd = config.getFrontEnd();
	this.interval = Integer.parseInt(config.get("interval")); // in minutes
	
	frontEnd.getAttributes().set(Attribute.CHECK_QUOTA, Boolean.TRUE);
	frontEnd.addStrobeListener(this);
	frontEnd.findService(HttpService.class).registerRequestListener(new RequestListener()
		{
			@Override
			public void notifySent(HttpServletRequest reqImpl, ReadableHttpResponse res, int amount)
			{
				HttpServletRequest req = reqImpl;
				if (req.getSession() == null) return;
				try
				{
					CqsSession session = frontEnd.getCqsSession(req.getSession());
					if (session == null) return;
					if (!session.payQuota) return;
					if (!session.isLoggedIn()) return;
					
					synchronized (frontEnd.getGalaxy())
					{
						Player p = session.getPlayer();
						String fname = req.getRequestURI();
						logger.config("["+new Date()+"] \""+p.getName()+"\" "+amount+" "+fname);
						if (fname.startsWith("/Ingame/"))
						{
							int used = p.getAttr(Attribute.QUOTA_USED).intValue();
							p.setAttr(Attribute.QUOTA_USED, Integer.valueOf(used + amount));
						}
						else
						{
							int used = p.getAttr(Attribute.GP_QUOTA_USED).intValue();
							p.setAttr(Attribute.GP_QUOTA_USED, Integer.valueOf(used + amount));
						}
					}
				}
				catch (Exception e)
				{ logger.log(Level.SEVERE, "Exception", e); }
			}
			@Override
			public void notifyInternalError(Throwable arg0)
			{/*Do nothing*/}
			@Override
			public void notifyInternalError(HttpServletRequest arg0, Throwable arg1)
			{/*Do nothing*/}
		});
}

// in Sekunden
@Override
public long getTimeInterval()
{ return interval*60L; }

@Override
public void strobe()
{
	QuotaConfiguration quotaConfig = QuotaConfiguration.getCopy(frontEnd.findService(StorageService.class));
	Galaxy sim = frontEnd.getGalaxy();
	synchronized (sim)
	{
		long nextTime = System.currentTimeMillis();
		int realinterval = (int) ((nextTime-lastTime) / (60L*1000L));
		lastTime = nextTime;
		
		Date now = new Date();
		
		logger.info("Decreasing used quota ("+now+")");
		List<Player> players = sim.getPlayers();
		for (int i = 0; i < players.size(); i++)
		{
			Player pl = players.get(i);
			if (pl == null) continue; 
			
			// Berechnung in Bytes
			int decrease = pl.getAttr(Attribute.QUOTA).intValue();
			pl.removeAttr(Attribute.QUOTA);
			decrease = (decrease*1024*realinterval)/(24*60);
			
			int used = pl.getAttr(Attribute.QUOTA_USED).intValue()-decrease;
			if (used < 0) used = 0;
			pl.setAttr(Attribute.QUOTA_USED, Integer.valueOf(used));
			
			used = pl.getAttr(Attribute.GP_QUOTA_USED).intValue()-decrease;
			if (used < 0) used = 0;
			pl.setAttr(Attribute.GP_QUOTA_USED, Integer.valueOf(used));
		}
		
		// Recalculating individual Quota!
		for (QuotaEntry entry : quotaConfig)
		{
			Player pl = sim.findPlayerByName(entry.name());
			if (pl == null)
			{
				logger.warning("Could not find player \""+entry.name()+"\"");
				continue;
			}
			
			Date expireDate = null;
			try
			{
				expireDate = entry.date();
				if (now.before(expireDate))
				{
					logger.info("Quota for \""+entry.name()+"\" not started "+
					  "("+expireDate+")");
					continue;
				}
				if (entry.hasExpired(now))
				{
					logger.info("Quota for \""+entry.name()+"\" expired "+
					  "("+expireDate+")");
					continue;
				}
			}
			catch (Exception e)
			{
				logger.warning("Error parsing date for \""+entry.name()+"\"");
				continue;
			}
			
			int oldQuota = pl.getAttr(Attribute.QUOTA).intValue();
			int newQuota = oldQuota + entry.value();
			
			pl.setAttr(Attribute.QUOTA, Integer.valueOf(newQuota));
		}
	}
}

@Override
public void startup()
{ strobe(); }

}
