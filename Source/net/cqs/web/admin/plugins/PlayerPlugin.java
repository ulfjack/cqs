package net.cqs.web.admin.plugins;

import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.auth.Identity;
import net.cqs.engine.AccessLevel;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.actions.PlayerCreateAction;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.config.Input;
import net.cqs.main.persistence.PlayerData;
import net.cqs.services.AuthService;
import net.cqs.services.StorageService;
import net.cqs.storage.Task;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;
import net.cqs.web.admin.AdminSession;

public final class PlayerPlugin
{

public PlayerPlugin(AdminPageService service)
{
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("Player", "View", "player.html", actions);
	service.addPage("Player", "Add", "player-add.html", actions);
	service.addPage("Multi", "Finder", "multi-finder.html", actions);
	service.addPage("Multi", "Selector", "multi-selector.html", actions);
}

private String loadComment(FrontEnd frontEnd, Player p)
{
	PlayerData data = PlayerData.getPlayerDataCopy(frontEnd.findService(StorageService.class), p.getPid());
	return data.getAdminComment();
}

private void writeComment(FrontEnd frontEnd, Player p, final String comment)
{
	final int pid = p.getPid();
	frontEnd.findService(StorageService.class).execute(new Task()
		{
			@Override
			public void run()
			{
				PlayerData.getPlayerData(pid).setAdminComment(comment);
			}
		});
}

private void setMulti(Galaxy galaxy, Player p)
{
	Alliance multi = galaxy.findAllianceByName("MULTI");
	if (p.getAlliance() != null)
		p.setAttr(Attribute.OLD_ALLIANCE, p.getAlliance().getName());
	p.setAttr(Attribute.IS_MULTI, Boolean.TRUE);
	multi.add(galaxy.getSimulationTime(), p);
}

private void releaseMulti(Player p)
{ 
	p.setAttr(Attribute.IS_MULTI, Boolean.FALSE);
	if (p.getAlliance() != null)
		p.getAlliance().remove(p);
}

@WebPostAction("Player.add")
public void addPlayer(FrontEnd frontEnd, AdminSession session,
		@Parameter("name") String name, @Parameter("domain") String domain, @Parameter("nick") String nick)
{
	if ((name != null) && (nick != null))
	{
		name = name.trim()+"@"+domain.trim();
		nick = nick.trim();
		final Galaxy sim = frontEnd.getGalaxy();
		synchronized (sim)
		{
			sim.update();
			sim.schedex(new PlayerCreateAction(new Identity(name), nick, frontEnd.getDefaultLocale()));
		}
	}
}

@WebPostAction("Identity.create")
public void addIdentity(FrontEnd frontEnd, PrintWriter out, AdminSession session,
		@Parameter("name") String name, @Parameter("password") String password,
		@Parameter("email") String email)
{
	if ((name != null) && (password != null))
	{
		name = name.trim();
		if (frontEnd.findService(AuthService.class).createIdentity(name, password, email, null))
			out.println("Successfully added identity \""+name+"\"!");
		else
			out.println("Failed to add identity \""+name+"\"!");
	}
}


@WebPostAction("Player.allowLogin")
public void allowLogin(FrontEnd frontEnd, AdminSession session,
		@Parameter("pid") int id)
{
	Galaxy galaxy = frontEnd.getGalaxy();
	Player player = galaxy.findPlayerByPid(id);
	if (player != null)
		galaxy.allowAccess(session.getIdentity(), player, AccessLevel.FULL);
}

@WebPostAction("Player.edit")
public void editPlayer(FrontEnd frontEnd, AdminSession session, PrintWriter out,
		@Parameter("pid") int pid,
		@Parameter("take") boolean applyChanges,
		@Parameter("comments") String postedComment,
		@Parameter("mayLogin") boolean mayLogin,
		@Parameter("isMulti") boolean isMulti,
		@Parameter("showAds") boolean showAds,
		@Parameter("isAdmin") boolean isAdmin,
		@Parameter("newally") String newAlly,
		@Parameter("newmail") String newEmail,
		@Parameter("newname") String newNick)
{
	if (!applyChanges) return;
	final Galaxy galaxy = frontEnd.getGalaxy();
	
	final Player who = galaxy.findPlayerByPid(pid);
	if (who == null)
	{
		out.println("No player with pid " + pid + " found!");
		return;
	}
	
	String currentComment = loadComment(frontEnd, who);
	if (currentComment == null) currentComment = "";
	if (postedComment != null) 
	{
		if (!currentComment.equals(postedComment))
		{
			writeComment(frontEnd, who, postedComment);
			currentComment = postedComment;
		}
	}
	
	if (mayLogin != who.getAttr(Attribute.LOGIN_ALLOWED).booleanValue())
		who.setAttr(Attribute.LOGIN_ALLOWED, Boolean.valueOf(mayLogin));
	
	if (isMulti != who.getAttr(Attribute.IS_MULTI).booleanValue())
	{
		if (isMulti)
			setMulti(galaxy, who);
		else
			who.setAttr(Attribute.IS_MULTI, Boolean.FALSE);
	}
	
	if (showAds != who.getAttr(Attribute.SHOW_ADS).booleanValue())
		who.setAttr(Attribute.SHOW_ADS, Boolean.valueOf(showAds));
	
	if (who.getAlliance() != null)
	{
		if (isAdmin && (who.getRank() != who.getAlliance().getRanks().get(0)))
			who.setRank(who.getAlliance().getRanks().get(0));
	}
	
	if (newAlly != null)
	{
		Alliance ally = galaxy.findAllianceByName(newAlly);
		if (ally != null)
			ally.add(galaxy.getSimulationTime(), who);
	}
	
	if (newEmail != null)
	{
		newEmail = newEmail.trim();
		if (newEmail.length() != 0)
		{
			AuthService authService = frontEnd.findService(AuthService.class);
			authService.changeEmail(who.getPrimaryIdentity(), newEmail);
		}
	}
	
	if (newNick != null)
	{
		newNick = newNick.trim();
		if (newNick.length() != 0)
		{
			Player tempWho = galaxy.findPlayerByName(newNick);
			if ((tempWho == null) || (tempWho == who))
				who.setName(newNick);
		}
	}
}

@WebPostAction("PlayerList.search")
public void search(FrontEnd frontEnd, AdminSession session,
		@Parameter("nick") String searchname,
		@Parameter("email") String email,
		@Parameter("ally") String ally,
		@Parameter("anything") String anything)
{
	final Galaxy galaxy = frontEnd.getGalaxy();
	session.searchData.clear();
	
	if ((searchname != null) && (searchname.length() != 0))
	{
		String regex = Input.convertStringToRegex(searchname);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher("");
		
		Iterator<Player> it = galaxy.playerIterator();
		while (it.hasNext())
		{
			Player p = it.next();
			if (m.reset(p.getName()).lookingAt())
			{
				session.searchData.add(p);
				continue;
			}
		}
	}
	
	if ((email != null) && (email.length() != 0))
	{
		String regex = Input.convertStringToRegex(email);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher("");
		AuthService authService = frontEnd.findService(AuthService.class);
		
		Iterator<Player> it = galaxy.playerIterator();
		while (it.hasNext())
		{
			Player p = it.next();
			String pemail = authService.getEmail(p.getPrimaryIdentity());
			if ((pemail != null) && m.reset(pemail).lookingAt())
			{
				session.searchData.add(p);
				continue;
			}
		}
	}
	
	if ((ally != null) && (ally.length() != 0))
	{
		String regex = Input.convertStringToRegex(ally);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher("");
		
		Iterator<Player> it = galaxy.playerIterator();
		while (it.hasNext())
		{
			Player p = it.next();
			if (p.getAlliance() != null)
			{
				if (m.reset(p.getAlliance().getName()).lookingAt())
				{
					session.searchData.add(p);
					continue;
				}
				if (m.reset(p.getAlliance().getShortName()).lookingAt())
				{
					session.searchData.add(p);
					continue;
				}
			}
		}
	}
	
	if ((anything != null) && (anything.length() != 0))
	{
		String regex = Input.convertStringToRegex(anything);
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher("");
		AuthService authService = frontEnd.findService(AuthService.class);
		
		Iterator<Player> it = galaxy.playerIterator();
		while (it.hasNext())
		{
			Player p = it.next();
			if (m.reset(p.getName()).lookingAt())
			{
				session.searchData.add(p);
				continue;
			}
			
			String pemail = authService.getEmail(p.getPrimaryIdentity());
			if ((pemail != null) && m.reset(pemail).lookingAt())
			{
				session.searchData.add(p);
				continue;
			}
			
			if (p.getAlliance() != null)
			{
				if (m.reset(p.getAlliance().getName()).lookingAt())
				{
					session.searchData.add(p);
					continue;
				}
				if (m.reset(p.getAlliance().getShortName()).lookingAt())
				{
					session.searchData.add(p);
					continue;
				}
			}
		}
	}
}

@WebPostAction("PlayerList.searchMultis")
public void searchMultis(FrontEnd frontEnd, AdminSession session,
		@Parameter("counter") boolean counter,
		@Parameter("mincount") long mincount,
		@Parameter("resetMulticounters") boolean resetMulticounters,
		@Parameter("email") boolean email,
		@Parameter("password") boolean password,
		@Parameter("graphicpath") boolean graphicpath)
{
	final Galaxy galaxy = frontEnd.getGalaxy();
	if (counter)
	{
		session.searchData.clear();
		Iterator<Player> it = galaxy.playerIterator();
		while (it.hasNext())
		{
			Player p = it.next();
			if (p.getAttr(Attribute.MULTI_COUNTER).intValue() > mincount)
				session.searchData.add(p);
		}
	}
	
	if (resetMulticounters)
	{
		List<Player> players = galaxy.getPlayers();
		for (int i = 0; i < players.size(); i++)
		{
			Player tempWho = players.get(i);
			if (tempWho != null)
				tempWho.removeAttr(Attribute.MULTI_COUNTER);
		}
	}
	
	if (email)
	{
		IdentityHashMap<Player,Player> found = new IdentityHashMap<Player,Player>();
		AuthService authService = frontEnd.findService(AuthService.class);
		Iterator<Player> it1 = galaxy.playerIterator();
		while (it1.hasNext())
		{
			Player p1 = it1.next();
			String p1email = authService.getEmail(p1.getPrimaryIdentity());
			if (found.containsKey(p1)) continue;
			Iterator<Player> it2 = galaxy.playerIterator();
			while (it2.hasNext())
			{
				Player p2 = it2.next();
				if (found.containsKey(p2)) continue;
				if (p1 == p2) continue;
				String p2email = authService.getEmail(p2.getPrimaryIdentity());
				if ((p1email != null) && p1email.equals(p2email))
				{
					if (!found.containsKey(p1))
					{
						found.put(p1, p2);
						session.searchData.add(p1);
					}
					if (!found.containsKey(p2))
					{
						found.put(p2, p1);
						session.searchData.add(p2);
					}
				}
			}
		}
	}
					
/*
	if ("on".equals(postData.get("password")))
	{
		manager.currentSI.searchData.clear();
		IdentityHashMap<Player,Player> found = new IdentityHashMap<Player,Player>();
		Iterator<Player> it1 = manager.getFrontEnd().getGalaxy().playerIterator();
		while (it1.hasNext())
		{
			Player p1 = it1.next();
			if (found.containsKey(p1)) continue;
			Iterator<Player> it2 = manager.getFrontEnd().getGalaxy().playerIterator();
			while (it2.hasNext())
			{
				Player p2 = it2.next();
				if (found.containsKey(p2)) continue;
				if (p1 == p2) continue;
				if (p1.password.equals(p2.password))
				{
					if (!found.containsKey(p1))
					{
						found.put(p1, p2);
						session.searchData.add(p1);
					}
					if (!found.containsKey(p2))
					{
						found.put(p2, p1);
						session.searchData.add(p2);
					}
				}
			}
		}
	}*/

	if (graphicpath)
	{
		IdentityHashMap<Player,Player> found = new IdentityHashMap<Player,Player>();
		Iterator<Player> it1 = galaxy.playerIterator();
		while (it1.hasNext())
		{
			Player p1 = it1.next();
			if (found.containsKey(p1)) continue;
			Iterator<Player> it2 = galaxy.playerIterator();
			while (it2.hasNext())
			{
				Player p2 = it2.next();
				if (found.containsKey(p2)) continue;
				if (p1 == p2) continue;
				String s1 = p1.getAttr(Attribute.GRAPHIC_PATH);
				String s2 = p2.getAttr(Attribute.GRAPHIC_PATH);
				if (s1.length() != 0 && s1.equals(s2))
				{
					if (!found.containsKey(p1))
					{
						found.put(p1, p2);
						session.searchData.add(p1);
					}
					if (!found.containsKey(p2))
					{
						found.put(p2, p1);
						session.searchData.add(p2);
					}
				}
			}
		}
	}
}

@WebPostAction("PlayerList.remove")
public void removePlayer(AdminSession session, @Parameter("num") int num)
{
	session.searchData.remove(num);
}

@WebPostAction("PlayerList.setMulti")
public void setMulti(FrontEnd frontEnd, AdminSession session)
{
	Iterator<Player> it = session.searchData.iterator();
	while (it.hasNext())
	{
		Player p = it.next();
		setMulti(frontEnd.getGalaxy(), p);
	}
}

@WebPostAction("PlayerList.unsetMulti")
public void unsetMulti(FrontEnd frontEnd, AdminSession session)
{
	Iterator<Player> it = session.searchData.iterator();
	while (it.hasNext())
	{
		Player p = it.next();
		releaseMulti(p);
	}
}

}
