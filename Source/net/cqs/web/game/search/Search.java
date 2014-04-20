package net.cqs.web.game.search;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.config.BuildingEnum;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.main.config.Input;

public final class Search
{

public static SearchResult searchAlliance(Galaxy galaxy, String mask, int start)
{
	SearchResult searchResult = new SearchResult(mask, start, 50);
	if (mask == null) return searchResult;
	if (mask.length() > 30) return searchResult;
	
	String searchPattern = Input.convertStringToRegex(mask);
	Pattern pattern = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE);
	Matcher m = pattern.matcher("");
	
	Iterator<Alliance> it = galaxy.allianceIterator();
	while (it.hasNext())
	{
		Alliance a = it.next();
		m.reset(a.getName());
		if (m.lookingAt())
			searchResult.addAlliance(a.getId());
		else
		{
			m.reset(a.getShortName());
			if (m.lookingAt())
				searchResult.addAlliance(a.getId());
		}
	}
	return searchResult;
}

public static SearchResult searchPlayer(Galaxy galaxy, String mask, int start)
{
	SearchResult searchResult = new SearchResult(mask, start, 50);
	if (mask == null) return searchResult;
	if (mask.length() > 30) return searchResult;
	
	String searchPattern = Input.convertStringToRegex(mask);
	Pattern pattern = Pattern.compile(searchPattern, Pattern.CASE_INSENSITIVE);
	Matcher m = pattern.matcher("");
	
	Iterator<Player> it = galaxy.playerIterator();
	while (it.hasNext())
	{
		Player p = it.next();
		m.reset(p.getName());
		if (m.lookingAt())
			searchResult.addPlayer(p.getPid());
	}
	return searchResult;
}

public static SearchResult searchAllianceTransmitter(Alliance alliance, int start)
{
	SearchResult result = new SearchResult(null, start, 50);
	if (alliance == null) return result;
	
	Iterator<Player> it1 = alliance.iterator();
	while (it1.hasNext())
	{
		Player p = it1.next();
		Iterator<Colony> it2 = p.getColonies().iterator();
		while (it2.hasNext())
		{
			Colony c = it2.next();
			if (c.getBuilding(BuildingEnum.TRANSMITTER) > 0)
				result.addColony(c.getPosition());
		}
	}
	return result;
}

}
