package net.cqs.main.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.cqs.config.units.UnitSystem;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.actions.AllianceCreateAction;
import net.cqs.engine.base.AttributeMap;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.messages.InfoListener;
import net.cqs.engine.messages.Information;
import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.PlayerMailListener;
import net.cqs.engine.rtevents.RealTimeEventType;
import net.cqs.i18n.FileStringBundleFactory;
import net.cqs.i18n.StringBundleFactory;
import net.cqs.main.i18n.LocaleProvider;
import net.cqs.main.persistence.AllianceData;
import net.cqs.main.persistence.Infobox;
import net.cqs.main.persistence.PlayerData;
import net.cqs.main.persistence.RegistrationHistory;
import net.cqs.main.persistence.TimeHistory;
import net.cqs.main.plugins.Plugin;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.plugins.PluginEntry;
import net.cqs.main.resource.ResourceManager;
import net.cqs.main.setup.GalaxyCreator;
import net.cqs.main.setup.GameConfiguration;
import net.cqs.main.setup.GameConfiguration.LoginDescription;
import net.cqs.main.signals.LoginListener;
import net.cqs.main.signals.LoginManager;
import net.cqs.main.signals.ShutdownListener;
import net.cqs.main.signals.StartupListener;
import net.cqs.main.signals.StrobeAdapter;
import net.cqs.main.signals.StrobeListener;
import net.cqs.main.signals.StrobeManager;
import net.cqs.services.AuthService;
import net.cqs.services.Service;
import net.cqs.services.ServiceRegistry;
import net.cqs.services.StorageService;
import net.cqs.storage.NameNotBoundException;
import net.cqs.storage.Storage;
import net.cqs.storage.StorageManager;
import net.cqs.storage.Task;
import net.cqs.storage.profiling.ProfileAggregator;
import net.cqs.web.frontpage.IdSession;
import net.cqs.web.game.CqsSession;
import net.cqs.web.util.LinkTextConverter;
import net.cqs.web.util.LogFormatter;
import net.cqs.web.util.XmlTextConverter;

public final class FrontEnd implements InfoListener, ServiceRegistry
{

public static final Logger logger = Logger.getLogger("net.cqs");

private static final String GALAXY_BINDING = "GALAXY";
private static final String SETTINGS_BINDING = "SETTINGS";
private static final String PROFILE_AGGREGATOR_BINDING = "PROFILE_AGGREGATOR";
private static final String ACCESS_TIME_HISTORY_BINDING = "ACCESS_TIME_HISTORY";

	private static class GalaxyData implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private final byte[] data;
		public GalaxyData(byte[] data)
		{ this.data = data; }
		public byte[] getData()
		{ return data; }
	}

static {
  Galaxy.defaultTextConverter = new XmlTextConverter(new LinkTextConverter());
  Galaxy.defaultLineConverter = new XmlTextConverter(new LinkTextConverter());
}

private volatile boolean startupSequence = false;
private volatile boolean shutdownSequence = false;

private final String buildId;
private final File logPath;
//private final ResourceManager ressourceManager;
private final LocaleProvider localeProvider = new LocaleProvider();
private CoreSettings settings;
private final GameConfiguration gameConfiguration;

private final AttributeMap attributes = new AttributeMap();

private Galaxy galaxy;
private final StorageManager storage;
private final StorageService storageService;
private final LoginCache loginCache;
private TimeHistory timeHistory = new TimeHistory();

private final HashMap<Class<? extends Service>,Service> services = new HashMap<Class<? extends Service>,Service>();
private final ArrayList<StartupListener> startupListeners = new ArrayList<StartupListener>();
private final ArrayList<ShutdownListener> shutdownListeners = new ArrayList<ShutdownListener>();
private final StrobeManager strobeManager = new StrobeManager(new DefaultExceptionListener());
private final LoginManager loginManager = new LoginManager();

private final ArrayList<PlayerMailListener> mailListeners = new ArrayList<PlayerMailListener>();

private final HashSet<String> plugins = new HashSet<String>();

private Thread shutdownHook = new Thread(new Runnable()
	{
		@Override
    public void run()
		{ shutdown(); }
	});

public FrontEnd(String buildId, ResourceManager ressourceManager, StorageManager storageManager,
		File logPath, GameConfiguration gameConfiguration)
{
	this.buildId = buildId;
//	this.ressourceManager = ressourceManager;
	this.storage = storageManager;
	this.storageService = new StorageManagerService(storage);
	this.logPath = logPath;
	this.gameConfiguration = gameConfiguration;
	this.settings = new CoreSettings("http://not-set-yet", "not-set-yet@conquer-space.net", false);

	this.loginCache = new LoginCache(this);

	registerService(StorageService.class, storageService);
	registerService(ResourceManager.class, ressourceManager);
}

public String version()
{ return buildId; }

public String url()
{ return settings.url(); }

public String getSupportEmail()
{ return settings.eMail(); }

public boolean debug()
{ return settings.debug(); }

public void setUrl(String url) {
  settings = settings.setUrl(url);
  writeSettings();
}

public void setEmail(String email) {
  settings = settings.setEmail(email);
  writeSettings();
}

public void setDebug(boolean debug) {
  settings = settings.setDebug(debug);
  writeSettings();
}

public boolean isImageCodeRequired()
{ return false; }

public Locale getDefaultLocale()
{ return localeProvider.getDefaultLocale(); }

public LocaleProvider getLocaleProvider()
{ return localeProvider; }

@Override
public synchronized <T extends Service> void registerService(Class<T> type, T provider)
{
	if (services.containsKey(type))
		throw new IllegalStateException("Such a service \""+type+"\" already exists!");
	services.put(type, provider);
}

@Override
public synchronized <T extends Service> T findService(Class<T> type)
{ return type.cast(services.get(type)); }

@Override
public synchronized <T extends Service> boolean hasService(Class<T> type)
{ return services.containsKey(type); }

public void addStrobeListener(StrobeListener l)
{ strobeManager.add(l); }

public void removeStrobeListener(StrobeListener l)
{ strobeManager.remove(l); }

public void addLoginListener(LoginListener l)
{ loginManager.add(l); }

public void removeLoginListener(LoginListener l)
{ loginManager.remove(l); }

public void fireLogin(String cookie, int pid)
{ loginManager.fireLogin(cookie, pid); }

public void addMailListener(PlayerMailListener l)
{ mailListeners.add(l); }

public void removeMailListener(PlayerMailListener l)
{ mailListeners.remove(l); }

public boolean isOnline(Player who)
{ return loginCache.isOnline(who.getPid()); }

public AttributeMap getAttributes()
{ return attributes; }

public boolean isShutdownSequence()
{ return shutdownSequence; }

public boolean isStartupSequence()
{ return startupSequence; }

public Storage getStorageManager()
{ return storageService; }

public ProfileAggregator getProfileAggregator()
{ return storage.getProfileAggregator(); }

public RegistrationHistory getRegistrationHistoryCopy()
{ return RegistrationHistory.getRegistrationHistoryCopy(storageService); }

@Override
public void notifyInfo(Player who, final Information m)
{
	final int pid = who.getPid();
	storageService.execute(new Task()
		{
			@Override
			public void run()
			{
				Infobox box = Infobox.getInfobox(pid);
				box.addInfo(m);
			}
		});
}

public void dropMail(Player recipient, final Message msg)
{
	final int pid = recipient.getPid();
	storageService.execute(new Task()
		{
			@Override
			public void run()
			{
				PlayerData.getPlayerData(pid).addMessage(msg);
			}
		});
	for (PlayerMailListener l : mailListeners)
		l.notifyMail(recipient, msg);
}

public void dropMail(Alliance recipient, final Message msg)
{
	final int pid = recipient.getId();
	storageService.execute(new Task()
		{
			@Override
			public void run()
			{
				AllianceData.getAllianceData(pid).addMessage(msg);
			}
		});
}

public void logTime(long amount)
{ timeHistory.logTime(amount); }

public TimeHistory getAccessTimeHistory()
{ return timeHistory; }

private static final String ID_KEY = "net.cqs.web.IdSession";
private static final long ID_TIMEOUT = 60*60*1000L;

private static final String INGAME_KEY = "net.cqs.web.catfish.UserAccessCheckpoint";
private static final long INGAME_TIMEOUT = 60*60*1000L;

public Locale getLocale(HttpServletRequest req)
{
	Locale locale = null;
	if (req.getSession() != null)
	{
		CqsSession cqssession = getCqsSession(req.getSession());
		locale = cqssession != null ? cqssession.getLocale() : null;
	}
	if (locale == null)
		locale = localeProvider.filter(req);
	if (req.getParameter("lang") != null)
		locale = localeProvider.filter(req.getParameter("lang"));
	if (locale == null) locale = localeProvider.getDefaultLocale();
	return locale;
}

public IdSession getIdSession(HttpServletRequest req)
{
	HttpSession session = req.getSession();
	long time = System.currentTimeMillis();
	if (time-session.getLastAccessedTime() > ID_TIMEOUT)
	{
		logger.info("Session timeout");
		session.removeAttribute(ID_KEY);
	}
	IdSession result = (IdSession) session.getAttribute(ID_KEY);
	if (result == null)
	{
		logger.info("New session");
		result = new IdSession(this, getLocale(req));
		session.setAttribute(ID_KEY, result);
	}
	result.update(this);
	return result;
}

public CqsSession getCqsSession(HttpSession session)
{
	long time = System.currentTimeMillis();
	if (time-session.getLastAccessedTime() > INGAME_TIMEOUT)
		session.removeAttribute(INGAME_KEY);
	CqsSession result = (CqsSession) session.getAttribute(INGAME_KEY);
	if (result == null)
	{
		result = new CqsSession();
		session.setAttribute(INGAME_KEY, result);
	}
	result.update(this);
	return result;
}

public FileHandler getFileHandler(LogEnum log)
{
	try
	{
		FileHandler handler = new FileHandler(getLogPath(log), 10000000, 10, true);
		handler.setFormatter(new LogFormatter());
		handler.setLevel(Level.ALL);
		return handler;
	}
	catch (IOException e)
	{ logger.log(Level.SEVERE, "Exception caught", e); }
	return null;
}

public String getLogPath(LogEnum log)
{ return logPath.getPath()+File.separator+log.get(); }

public UnitSystem getUnitSystem()
{ return getGalaxy().getUnitSystem(); }

public Galaxy getGalaxy()
{ return galaxy; }

public void setGalaxy(Galaxy galaxy)
{
	if (this.galaxy != null) throw new IllegalStateException("Argh!");
	if (galaxy == null) throw new NullPointerException("Argh!");
	this.galaxy = galaxy;
}

public int online()
{ return loginCache.size(); }

public void accessPlayer(int playerId)
{ loginCache.access(playerId); }

private void loadPlugin(String name, PluginConfig config) throws ClassNotFoundException
{
	Class<?> objClass = Class.forName(name, false, getClass().getClassLoader());
	Plugin annotation = objClass.getAnnotation(Plugin.class);
	if (annotation == null) throw new ClassNotFoundException("\""+name+"\" is not a Plugin!");
	try
	{
		Constructor<?> c = objClass.getConstructor(PluginConfig.class);
		Object plugin = c.newInstance(config);
		if (plugin instanceof StartupListener)
			startupListeners.add((StartupListener) plugin);
		if (plugin instanceof ShutdownListener)
			shutdownListeners.add((ShutdownListener) plugin);
		plugins.add(objClass.getCanonicalName());
	}
	catch (Exception e)
	{
		if (e instanceof ClassNotFoundException)
			throw (ClassNotFoundException) e;
		throw new ClassNotFoundException("Other exception thrown", e);
	}
}

private void configLogging()
{
	Handler handler;
	handler = getFileHandler(LogEnum.BASE);
	handler.setLevel(Level.ALL);
	logger.addHandler(handler);
	
	handler = new ConsoleHandler();
	handler.setFormatter(new LogFormatter());
	handler.setLevel(Level.ALL);
	logger.addHandler(handler);
	
	logger.setLevel(Level.ALL);
	logger.setUseParentHandlers(false);
	
	Logger temp;
	
	// configure UlfScript logging
	temp = Logger.getLogger("de.ofahrt.ulfscript.UlfScript");
	if (debug())
		temp.setLevel(Level.ALL);
	else
		temp.setLevel(Level.WARNING);
	temp.addHandler(handler);
	
	// configure LogEnum logging
	for (LogEnum log : LogEnum.values())
	{
		if ((log.getPackageName() != null) && (log.get() != null))
		{
			temp = Logger.getLogger(log.getPackageName());
			handler = getFileHandler(log);
			if (handler != null) temp.addHandler(handler);
			temp.setUseParentHandlers(false);
		}
	}
	
	// configure others
	temp = Logger.getLogger("net.cqs.web.reports.AgentReportManager");
	handler = getFileHandler(LogEnum.REPORT_AGENT);
	if (handler != null) temp.addHandler(handler);
	
	temp = Logger.getLogger("net.cqs.web.reports.UpdateObserveHandler");
	handler = getFileHandler(LogEnum.NIGHTLY);
	if (handler != null) temp.addHandler(handler);
	
	temp = Logger.getLogger("net.cqs.web.reports.AgentObserveManager");
	handler = getFileHandler(LogEnum.REPORT_OBSERVE);
	if (handler != null) temp.addHandler(handler);
	
	temp = Logger.getLogger("net.cqs.plugins.admin");
	handler = getFileHandler(LogEnum.ADMIN);
	if (handler != null) temp.addHandler(handler);
	temp.setUseParentHandlers(false);
}

public void start()
{
	configLogging();
	
	logger.info(version());
	try {
	  settings = storageService.getCopy(SETTINGS_BINDING, CoreSettings.class);
	} catch (NameNotBoundException ignored) {
	  // Ignore this case.
	}
	if (settings == null) {
	  settings = new CoreSettings("http://www.conquer-space.net/", "no-reply@conquer-space.net", false);
	}

	addStrobeListener(new StrobeAdapter(StrobeAdapter.seconds(1))
		{
			@Override
      public void strobe()
			{ galaxy.update(); }
		});
	addStrobeListener(new StrobeAdapter(StrobeAdapter.minutes(60))
		{
			@Override
      public void strobe()
			{ writeGalaxy(); }
		});
	addStrobeListener(new StrobeAdapter(StrobeAdapter.hours(12))
		{
			@Override
      public void strobe()
			{ galaxy.refreshGalaxyViews(); }
		});
	
	try
	{
		PluginEntry[] pluginEntries = gameConfiguration.getPlugins();
		for (int i = 0; i < pluginEntries.length; i++)
		{
			PluginEntry entry = pluginEntries[i];
			if (entry.shallLoad())
			{
				try
				{
					String pluginname = entry.getName();
					int index = pluginname.lastIndexOf('.')+1;
					String pname = pluginname.substring(index);
					logger.info("Loading "+pname);
					loadPlugin(pluginname, entry.getConfig(this));
				}
				catch (Exception e)
				{ logger.log(Level.SEVERE, "Exception loading '"+entry.getName()+"'", e); }
			}
		}
	}
	catch (Throwable e)
	{
		logger.log(Level.SEVERE, "Exception caught", e);
		System.exit(1);
	}
	
	try {
		storageService.getCopy(GALAXY_BINDING, GalaxyData.class);
		readGalaxy();
	} catch (NameNotBoundException e) {
		for (LoginDescription login : gameConfiguration.getGalaxyDescription().getLogins()) {
			String name = login.getName();
			if (name != null) {
				String pw = login.getPassword();
				AuthService authService = findService(AuthService.class);
				authService.createIdentity(name, pw, null, null);
			}
		}

		logger.info("Creating galaxy.");
		Galaxy newGalaxy = new GalaxyCreator().createGalaxy(storage, gameConfiguration.getGalaxyDescription());
		newGalaxy.addInfoListener(this);

		Player guestPlayer = newGalaxy.findPlayerByName("gast");
		if (guestPlayer != null) {
		  newGalaxy.schedex(new AllianceCreateAction(guestPlayer, "MULTI", "MULTI"));
		}

		setGalaxy(newGalaxy);
		writeGalaxy();
	}

	logger.info("Loading complete.");

	StringBundleFactory factory = StringBundleFactory.getStringBundleFactory();
	if (factory instanceof FileStringBundleFactory) {
		logger.info("Languages enabled: "+Arrays.toString(((FileStringBundleFactory) factory).listLocales()));
	}

	startupSequence = true;
	for (StartupListener l : startupListeners) {
		l.startup();
	}
	startupSequence = false;
	
	Runtime.getRuntime().addShutdownHook(shutdownHook);
	strobeManager.start();
}

private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	ByteArrayInputStream bain = new ByteArrayInputStream(data);
	ObjectInputStream in = new ObjectInputStream(bain);
	return in.readObject();
}

private byte[] serialize(Object o) {
	try {
		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baout);
		out.writeObject(o);
		out.flush();
		return baout.toByteArray();
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
}

private void readGalaxy() {
	try {
		GalaxyData data = storageService.getCopy(GALAXY_BINDING, GalaxyData.class);
		byte[] result = data.getData();

		Galaxy newsim = (Galaxy) deserialize(result);
		newsim.afterReading(storageService);
		galaxy = newsim;
		galaxy.refreshGalaxyViews();
		galaxy.addInfoListener(this);

		try {
			ProfileAggregator profileAggregator = storageService.getCopy(PROFILE_AGGREGATOR_BINDING, ProfileAggregator.class);
			storage.setProfileAggregator(profileAggregator);
		} catch (NameNotBoundException ignored) {
		  /*IGNORED*/
		}
		try {
			timeHistory = storageService.getCopy(ACCESS_TIME_HISTORY_BINDING, TimeHistory.class);
		} catch (NameNotBoundException e) {
		  timeHistory = new TimeHistory();
		}
	}
	catch (Throwable e)
	{
		e.printStackTrace();
		logger.log(Level.SEVERE, "Could not read galaxy", e);
		System.exit(0);
	}
}

public void writeGalaxy() {
	try {
		synchronized (galaxy) {
			long time = galaxy.beforeWriting();
			byte[] data = serialize(galaxy);
			logger.info(galaxy.afterWriting(time)+" seconds needed");
			final GalaxyData galaxyData = new GalaxyData(data);
			storageService.set(GALAXY_BINDING, galaxyData);
			final ProfileAggregator profileAggregator = storage.getProfileAggregator();
			storageService.set(PROFILE_AGGREGATOR_BINDING, profileAggregator);
			storageService.set(ACCESS_TIME_HISTORY_BINDING, timeHistory);
			storage.sync();
		}
	} catch (Throwable e) {
		e.printStackTrace();
		logger.log(Level.SEVERE, "Could not write galaxy", e);
		galaxy.execute(RealTimeEventType.EMERGENCY_HALT);
	}
}

private void writeSettings() {
  try {
    storageService.set(SETTINGS_BINDING, settings);
  } catch (Throwable e) {
    e.printStackTrace();
    logger.log(Level.SEVERE, "Could not write settings", e);
  }
}

void shutdown() {
	shutdownSequence = true;
	logger.info("Shutdown");
	strobeManager.shutdown();
	writeGalaxy();
	for (ShutdownListener l : shutdownListeners) {
		l.shutdown();
	}
	storage.shutdown();
}

public boolean isPluginLoaded(String s) {
  return plugins.contains(s);
}

}
