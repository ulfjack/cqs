package net.cqs.web.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.cqs.auth.GroupProvider;
import net.cqs.auth.Identity;
import net.cqs.main.XmlConversion;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.plugins.PluginConfig;
import net.cqs.main.resource.ResourceManager;
import net.cqs.services.AdminService;
import net.cqs.services.AuthService;
import net.cqs.services.ServiceRegistry;
import net.cqs.services.StorageService;
import net.cqs.storage.NameNotBoundException;
import net.cqs.web.ParsedRequest;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.PostAction;
import net.cqs.web.admin.auth.AccessViolationException;
import net.cqs.web.admin.auth.AllowPersonRule;
import net.cqs.web.admin.auth.Rule;
import net.cqs.web.admin.plugins.DatabasePlugin;
import net.cqs.web.admin.plugins.EmailAllPlugin;
import net.cqs.web.admin.plugins.EventPlugin;
import net.cqs.web.admin.plugins.PlayerPlugin;
import net.cqs.web.admin.plugins.QuotaPlugin;
import net.cqs.web.admin.plugins.SettingsPlugin;
import net.cqs.web.admin.plugins.SystemPlugin;
import net.cqs.web.admin.plugins.TimePlugin;

//<!--
//		Administration access rules:
//		
//			* <rule action="allow" range="group" name="staff" />
//			  Allows every member of the group "staff" access.
//			
//			* <rule action="allow" range="person" name="UlfJack" />
//			  Allows only the account "UlfJack" access.
//		
//		Everything that is not explicitly allowed is forbidden.
//	-->
//		<rules>
//			<rule for="Page:Core" action="allow" range="group" name="staff" />
//			<rule for="Page:Debug" action="allow" range="group" name="staff" />
//			<rule for="Page:Player" action="allow" range="group" name="staff" />
//			<rule for="Page:Multi" action="allow" range="group" name="staff" />
//			<rule for="Page:System" action="allow" range="group" name="staff" />
//			<ruleset for="Page:Quota">
//				<rule action="allow" range="person" name="Sara@local" />
//				<rule action="allow" range="person" name="UlfJack@local" />
//			</ruleset>
//		</rules>
public final class AdminServlet extends HttpServlet implements AdminPageService, AdminService
{

private static final long serialVersionUID = 1L;

private static final Logger logger = Logger.getLogger("net.cqs.plugins.admin");
private static final String ALL_TARGET = RuleMap.ALL_TARGET;
private static final String RIGHTS_ID = "admin-rights";

	private static final class PageDescriptor
	{
		private final DynamicPage page;
		private final String group;
		private final String category;
		private final String item;
		public PageDescriptor(DynamicPage page, String group, String category, String item)
		{
			this.page = page;
			this.group = group;
			this.category = category;
			this.item = item;
		}
		public DynamicPage getPage()
		{ return page; }
		public String getGroup()
		{ return group; }
		public String getCategory()
		{ return category; }
		public String getItem()
		{ return item; }
	}

private final FrontEnd frontEnd;
private final ServiceRegistry registry;

private final PageDescriptor indexPage;
private final DynamicPage accessDeniedPage;
private final PageCollection collection = new PageCollection();

private final HashMap<String,PageDescriptor> entryMap = new HashMap<String,PageDescriptor>();

private final GroupProvider groupProvider;
private final RuleMap configRules;

public AdminServlet(PluginConfig config)
{
	this.frontEnd = config.getFrontEnd();
	this.registry = config.getServiceRegistry();
	groupProvider = new GroupProvider()
		{
			@Override
      public boolean isInGroup(Identity identity, String name)
			{
				AuthService authService = registry.findService(AuthService.class);
				if (!(authService instanceof GroupProvider))
					return false;
				return ((GroupProvider) authService).isInGroup(identity, name);
			}
		};
	
	AdminEnvironment env = config.newEnvironment(AdminEnvironment.DESC);
	accessDeniedPage = new DynamicPage(env, "access-denied.html");
	
	String override = config.get("override");
	if (override != null)
		configRules = new RuleMap(ALL_TARGET, new AllowPersonRule(override));
	else
	{
		RuleMap result = null;
		try
		{
			String rights = registry.findService(StorageService.class).getCopy(RIGHTS_ID, String.class);
			result = (RuleMap) new XmlRuleConverter().fromXml(rights);
		}
		catch (NameNotBoundException e)
		{/*OK*/}
		if (result == null)
		{
			String admin = config.get("admin");
			if (admin != null)
				result = new RuleMap(ALL_TARGET, new AllowPersonRule(admin));
			else
				result = new RuleMap();
		}
		configRules = result;
	}
	
	indexPage = addPage_("Core", "Index", new DynamicPage(env, "index.html"));
	new AdminInternalPlugin(this);
	new DatabasePlugin(this);
	new TimePlugin(this);
	new EventPlugin(this);
	new PlayerPlugin(this);
	new EmailAllPlugin(this);
	new SystemPlugin(this);
	new QuotaPlugin(this);
	new SettingsPlugin(this);
	registry.registerService(AdminService.class, this);
}

public FrontEnd getFrontEnd()
{ return frontEnd; }

public PageCollection getPageCollection()
{ return collection; }

public String getRightsAsXML()
{
	XmlConversion<Object> converter = new XmlRuleConverter();
	return converter.toXml(configRules);
}

private boolean mayAccess(PageDescriptor descriptor, Identity identity)
{
	if (descriptor == null) return false;
	Rule rule =  configRules.get(descriptor.getGroup(), descriptor.getCategory(), descriptor.getItem());
	if (rule == null) return false;
	try
	{ return rule.check(groupProvider, identity); }
	catch (AccessViolationException e)
	{
		logger.log(Level.WARNING, "Access denied!", e);
		return false;
	}
}

@Override
public AdminEnvironment newEnvironment()
{ return AdminEnvironment.DESC.newInstance(registry.findService(ResourceManager.class)); }

@Override
public ActionParser getActionParser()
{
	ActionParser parser = new ActionParser(FrontEnd.class,
		Identity.class, AdminSession.class, PrintWriter.class);
	return parser;
}

private PageDescriptor addPage_(String category, String item, DynamicPage page)
{
	String link = page.getName();
	PageCollection.PageCategory c = collection.addPageCategory(category);
	PageCollection.PageItem i = new PageCollection.PageItem(item, link);
	c.add(i);
	PageDescriptor result = new PageDescriptor(page, "Page", category, item);
	entryMap.put(link, result);
	return result;
}

@Override
public void addPage(String category, String item, String source, PostAction... actions)
{ addPage_(category, item, new DynamicPage(newEnvironment(), source, actions)); }

@Override
public boolean mayAccessAdministration(Identity identity)
{ return mayAccess(indexPage, identity); }

void addAdmin(Identity identity, PrintWriter out, String name)
{
	configRules.add("*", new AllowPersonRule(name));
	XmlConversion<Object> converter = new XmlRuleConverter();
	final String rightsXml = converter.toXml(configRules);
	registry.findService(StorageService.class).set(RIGHTS_ID, rightsXml);
}

private static final String KEY = "net.cqs.web.admin.AdminSession";
private static final long TIMEOUT = 20*60*1000L;

private AdminSession getAdminSession(HttpServletRequest request)
{
	HttpSession session = request.getSession();
	long time = System.currentTimeMillis();
	if (time-session.getLastAccessedTime() > TIMEOUT)
		session.removeAttribute(KEY);
	AdminSession result = (AdminSession) session.getAttribute(KEY);
	if (result == null)
	{
		result = new AdminSession(frontEnd.getIdSession(request).getIdentity());
		session.setAttribute(KEY, result);
	}
	return result;
}

private DynamicPage findPage(Identity id, String filename)
{
	PageDescriptor descriptor = entryMap.get(filename);
	if (!mayAccess(descriptor, id))
		return accessDeniedPage;
	else
		return descriptor.getPage();
}

@Override
public synchronized void doGet(final HttpServletRequest request, HttpServletResponse response) throws IOException
{
	ParsedRequest parsedRequest = new ParsedRequest(request);
	String filename = parsedRequest.getBasename();
	final AdminSession session = getAdminSession(request);
	session.start(filename);
	
	final DynamicPage page = findPage(session.getIdentity(), filename);
	page.handle(response, parsedRequest, this, session);
}

@Override
public synchronized void doPost(final HttpServletRequest request, HttpServletResponse response) throws IOException
{
	ParsedRequest parsedRequest = new ParsedRequest(request);
	String filename = parsedRequest.getBasename();
	final AdminSession session = getAdminSession(request);
	session.start(filename);
	
	final DynamicPage page = findPage(session.getIdentity(), filename);
	String todo = request.getParameter("do");
	if (todo != null)
	{
		PostAction action = page.getPostAction(todo);
		if (action != null)
		{
			if (action.isDeprecated())
				logger.warning("Using Deprecated POST-Handler for \""+todo+"\"");
			
			StringWriter out = new StringWriter();
			PrintWriter temp = new PrintWriter(out);
			try
			{
				temp.println("Activating "+action);
				action.activate(request, frontEnd, session.getIdentity(), session, temp);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "Exception caught for POST \""+todo+"\"", e);
				temp.println();
				e.printStackTrace(temp);
			}
			temp.flush();
			String s = out.toString();
			if (s.length() != 0)
				session.actionResult = s;
		}
		else
			logger.log(Level.SEVERE, "POST not found for \""+todo+"\"");
	}
	else
		logger.log(Level.SEVERE, "POST without \"do\" parameter");
	
	page.handle(response, parsedRequest, this, session);
}

}
