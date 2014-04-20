package net.cqs.web.game;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.config.Constants;
import net.cqs.config.Time;
import net.cqs.engine.Galaxy;
import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.TippsAndTricks;
import net.cqs.main.persistence.AgentReport;
import net.cqs.main.persistence.AllianceData;
import net.cqs.main.persistence.BattleReport;
import net.cqs.main.persistence.MoneyData;
import net.cqs.main.persistence.ObserverReport;
import net.cqs.web.util.HtmlToolkit;

public final class FrontEndTools
{

private final FrontEnd frontEnd;

public FrontEndTools(FrontEnd frontEnd)
{
	this.frontEnd = frontEnd;
}

public String getUrl()
{ return frontEnd.url(); }

public String getVersion()
{ return frontEnd.version(); }

public boolean debug()
{ return frontEnd.debug(); }

public String getSupportEmail()
{ return frontEnd.getSupportEmail(); }

public boolean hasNewAllianceMessage(Player p)
{
	Alliance a = p.getAlliance();
	if (a == null) return false;
	AllianceData ad = AllianceData.getAllianceDataCopy(frontEnd.getStorageManager(), a.getId());
	return (p.getAllianceReadTime() <= ad.getMail().getFolder(0).getLastAddRealTime()) &&
		(ad.getMail().folderSize(0) > 0);
}

public boolean hasAllianceVoted(Player p)
{ return p.getAlliance().getSurvey().getId() == p.getAttr(Attribute.ALLIANCE_SURVEY).intValue(); }

public boolean hasSurveyVoted(Player p)
{ return p.getGalaxy().getSurvey().getId() == p.getAttr(Attribute.SYSTEM_SURVEY).intValue(); }

public boolean hasKomissionVoted(Player p)
{ return p.getGalaxy().getKomissionSurvey().getId() == p.getAttr(Attribute.KOMMISSION_SURVEY).intValue(); }

@HtmlFragment
public String randomTipp(Locale locale)
{ return TippsAndTricks.getTipp(locale); }

@HtmlFragment
public String htmlConvert(String what)
{ return Galaxy.defaultTextConverter.convert(what); }

@HtmlFragment
public String playerLink(Player player)
{
  StringBuffer result = new StringBuffer();
  result.append("<a href=\"player|").append(player.getPid()).append("\">");
  result.append(HtmlToolkit.formatText(player.getName()));
  result.append("</a>");
  Alliance alliance = player.getAlliance();
  if (alliance != null) {
    result.append(" [<a href=\"alliance|").append(alliance.getId()).append("\">");
    result.append(HtmlToolkit.formatText(alliance.getShortName()));
    result.append("</a>]");
  }
  return result.toString();
}

public boolean isOnline(Player p)
{ return frontEnd.isOnline(p); }

public int countOnlinePlayers()
{ return frontEnd.online(); }

public int safeParseInt(String value, int def)
{
	try
	{ return Integer.parseInt(value); }
	catch (NullPointerException e)
	{ return def; }
	catch (NumberFormatException e)
	{ return def; }
}

public long getRandomLong()
{ return frontEnd.getGalaxy().getRandomLong(); }

public long floatToLong(float f)
{ return (long) f; }

public String asDate(Locale locale, long l)
{
	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
	return dateFormat.format(new Date(l));
}

public String asTime(long l)
{ return Time.format(l); }

public String getAbbreviatedLong(Locale locale, long l)
{
	NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
	if (l < 10e4)
		return numberFormat.format(l);
	else if (l < 10e7)
		return numberFormat.format(l/10e2)+"k";
	else if (l < 10e10)
		return numberFormat.format(l/10e5)+"m";
	else if (l < 10e13)
		return numberFormat.format(l/10e8)+"g";
	else
		return numberFormat.format(l/10e11)+"t";
}

public int getBuildingMaxPercent()
{ return (int) (100 - 100*Constants.BUILDING_MIN_PERCENT); }

public String intToString(int i)
{ return String.valueOf(i); }

public List<Locale> getSupportedLocales()
{ return frontEnd.getLocaleProvider().getAvailableLocales(); }

public MoneyData getMoneyReport(Player player)
{ return MoneyData.getMoneyDataCopy(frontEnd.getStorageManager(), player.getPid()); }

public BattleReport getBattleReport(String id)
{ return BattleReport.getBattleReportCopy(frontEnd.getStorageManager(), id); }

public AgentReport getAgentReport(String id)
{ return AgentReport.getAgentReportCopy(frontEnd.getStorageManager(), id); }

public ObserverReport getObserverReport(String id)
{ return ObserverReport.getObserverReportCopy(frontEnd.getStorageManager(), id); }

}
