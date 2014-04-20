package net.cqs.web.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ofahrt.ulfscript.annotations.HtmlFragment;

import net.cqs.auth.Identity;
import net.cqs.config.BuildingEnum;
import net.cqs.config.EducationEnum;
import net.cqs.config.ErrorCode;
import net.cqs.config.ErrorCodeException;
import net.cqs.config.InvalidPositionException;
import net.cqs.config.PlanetEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.config.ResourceEnum;
import net.cqs.config.RightEnum;
import net.cqs.config.Sex;
import net.cqs.config.units.Unit;
import net.cqs.config.units.UnitClass;
import net.cqs.config.units.UnitDescription;
import net.cqs.config.units.UnitModule;
import net.cqs.engine.AccessLevel;
import net.cqs.engine.Colony;
import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyView;
import net.cqs.engine.Player;
import net.cqs.engine.Position;
import net.cqs.engine.SolarSystem;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.battles.SimulatorBattle;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.ContractDraft;
import net.cqs.engine.diplomacy.ContractParty;
import net.cqs.engine.diplomacy.ContractType;
import net.cqs.engine.messages.Message;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.persistence.AllianceData;
import net.cqs.main.persistence.Infobox;
import net.cqs.main.persistence.Mailbox;
import net.cqs.main.persistence.PlayerData;
import net.cqs.storage.Context;
import net.cqs.storage.GalaxyTask;
import net.cqs.storage.Task;
import net.cqs.web.action.TaskHandler;
import net.cqs.web.game.search.SearchResult;
import net.cqs.web.util.HtmlToolkit;

public class CqsSession implements Serializable, TaskHandler
{

private static final long serialVersionUID = 2L;

private static Logger logger = Logger.getLogger("net.cqs.web.game");

private static final Random RAND = new Random();

// Note: Player-specific initialization can happen in initSession().

private Identity identity;

private Locale locale;
private int playerId = -1;
private AccessLevel accessLevel;

public boolean payQuota = true;
private boolean isMulti;

private String graphicPath;
private String cssPath;

private long securityId = RAND.nextLong();

// Um zu verhindern, dass ein Request doppelt abgeschickt wird.
private transient long requestCounter = 0;

private transient String errorMessage;

private transient FrontEnd frontEnd;
private transient Galaxy galaxy;

public transient UnitDescription unitDesign;
public transient SimulatorBattle simBattle;

private transient ContractDraft contractDraft;
private transient boolean plannedAttackerIsPlayer = true;
private transient ContractParty plannedVictim;

private transient SearchResult searchResult;
public transient int flag;

public CqsSession()
{/*OK*/}

public void update(FrontEnd myFrontEnd)
{
	if ((frontEnd != null) && (frontEnd != myFrontEnd))
		throw new IllegalArgumentException("Argh!");
	frontEnd = myFrontEnd;
	galaxy = frontEnd.getGalaxy();
}

@HtmlFragment
public String getErrorMessage()
{ return HtmlToolkit.formatText(errorMessage); }

public void setErrorMessage(String errorMessage)
{ this.errorMessage = errorMessage; }

public Locale getLocale()
{ return locale; }

public void setLocale(Locale locale)
{ this.locale = locale; }

public Identity getIdentity()
{ return identity; }

public FrontEnd getFrontEnd()
{ return frontEnd; }

public Galaxy getGalaxy()
{ return galaxy; }

public int getPlayerId()
{ return playerId; }

public Player getPlayer()
{ return galaxy.findPlayerByPid(playerId); }

public Alliance getAlliance()
{ return getPlayer().getAlliance(); }

public GalaxyView getGalaxyView()
{ return getPlayer().getGalaxyView(); }

public SimulatorBattle getSimBattle()
{ return simBattle; }

public UnitDescription getUnitDesign()
{ return unitDesign; }

public void execute(Task task)
{ frontEnd.getStorageManager().execute(task); }

@Override
public void execute(GalaxyTask task)
{
	try
	{
		Context.execute(galaxy, task);
	}
	catch (ErrorCodeException e)
	{
		ErrorCode code = e.getErrorCode();
		if (code == ErrorCode.NONE) code = ErrorCode.INVALID_INPUT;
		log(code);
	}
	catch (RuntimeException e)
	{
		logger.log(Level.SEVERE, "Exception caught", e);
	}
	catch (Error e)
	{
		logger.log(Level.SEVERE, "Exception caught", e);
	}
}

public void dropMail(Player recipient, Message msg)
{ frontEnd.dropMail(recipient, msg); }

public void dropMail(Alliance recipient, Message msg)
{ frontEnd.dropMail(recipient, msg); }

public PlayerData getPlayerDataCopy()
{ return PlayerData.getPlayerDataCopy(frontEnd.getStorageManager(), getPlayer().getPid()); }

public AllianceData getAllianceDataCopy()
{ return AllianceData.getAllianceDataCopy(frontEnd.getStorageManager(), getAlliance().getId()); }

public boolean isAjaxEnabled()
{ return getPlayerDataCopy().isAjax(); }

public Mailbox getPlayerMailboxCopy()
{ return getPlayerDataCopy().getMail(); }

public Mailbox getAllianceMailboxCopy()
{ return getAllianceDataCopy().getMail(); }

public Infobox getInfoBoxCopy()
{ return Infobox.getInfoboxCopy(frontEnd.getStorageManager(), getPlayer().getPid()); }

public AccessLevel getAccessLevel()
{ return accessLevel; }

public boolean isMulti()
{ return isMulti; }

public boolean isRestricted()
{ return getPlayer().isRestricted() || (accessLevel != AccessLevel.FULL); }

public long getSecurityId()
{ return securityId; }

public long getRequestCounter()
{ return requestCounter; }

public String getGraphicPath()
{ return graphicPath; }

public String getCssPath()
{ return cssPath; }

public void setSearchResult(SearchResult searchResult)
{ this.searchResult = searchResult; }

public SearchResult getSearchResult()
{ return searchResult; }

public int getFlag()
{ return flag; }

@Override
public void log(ErrorCode errorCode)
{
	logger.fine("Error code logged: "+errorCode);
	getPlayer().log(errorCode);
}

public boolean maySeeMap()
{ return getAccessLevel().maySeeMap() && getPlayer().maySeeMap(); }

private String getImageUrl(String path)
{
	return getGraphicPath()+path;
}

public String getUrl(BuildingEnum building, boolean big)
{ return getImageUrl("buildings/"+building.getImageName()+"-"+(big ? "big" : "small")+".png"); }

public String getUrl(EducationEnum topic, boolean big)
{ return getImageUrl("research/"+topic.getImageName()+"-"+(big ? "big" : "small")+".png"); }

public String getUrl(ResearchEnum topic, boolean big)
{ return getImageUrl("research/"+topic.getImageName()+"-"+(big ? "big" : "small")+".png"); }

public String getUrl(Sex sex)
{ return getImageUrl("design/"+sex.getImageName()+".jpg"); }

public String getUrl(PlanetEnum type)
{ return getImageUrl("planets/"+type.getImageName()+".png"); }

public String getUrl(ResourceEnum type)
{ return getImageUrl("design/res"+(type.ordinal()+1)+".png"); }

public String getLivingSpaceUrl(long value, long max)
{
	if (value > max) value = max;
	return getImageUrl("design/bev"+(value*29)/max+".png");
}

public String getFillLevelUrl(long value, long max)
{
	if (value > max) value = max;
	return getImageUrl("design/dot"+(value*30)/max+".gif");
}

public String getEfficiencyLevelUrl(long jobs, long pop)
{
	if (pop > jobs) pop = jobs;
	return getImageUrl("design/dot"+(pop*30)/jobs+".gif");
}


public String getSystemStateUrl(int state)
{ return getImageUrl("design/system"+state+".png"); }

public String getUrl(PlanetEnum type, boolean big)
{ return getImageUrl("planets/"+type.getImageName()+"-"+(big ? "huge" : "small")+".png"); }

public List<String> getUrls(Unit unit, boolean big)
{
	ArrayList<String> result = new ArrayList<String>();
	for (String s : unit.getImageNames())
		result.add(getImageUrl("units/"+s+"-"+( big ? "big" : "small")+".png"));
	return result;
}

public String getUrl(UnitClass uclass, boolean big)
{ return getImageUrl("units/"+uclass.getImageName()+"-"+( big ? "big" : "small")+".png"); }

public List<String> getUrls(UnitModule module, boolean big)
{
	ArrayList<String> result = new ArrayList<String>();
	String s = module.getImageName();
	result.add(getImageUrl("research/"+s+"-"+( big ? "big" : "small")+".png"));
	return result;
}

public String getUrl(ImageEnum image)
{ return getImageUrl(image.getImageName()); }

public String getMapUrl(Position position)
{ return "map|"+position; }

private String toHtml(String prefix, String suffix, String... urls)
{
	StringBuffer result = new StringBuffer();
	boolean first = true;
	for (String url : urls)
	{
		if (!first) result.append("\n"); else first = false;
		result.append(prefix);
		result.append(url);
		result.append(suffix);
	}
	return result.toString();
}

private String compressCssInternal(String... cssFiles)
{
	// isRestricted is a hack. It just happens to work. :-/
	boolean ok = !frontEnd.debug() && !getPlayer().isRestricted();
	for (String cssFile : cssFiles)
		if (!cssFile.startsWith("pack/css/")) ok = false;
	CssCompressorService compressor = frontEnd.findService(CssCompressorService.class);
	if ((compressor != null) && ok)
	{
		String url = "pack/cache/"+compressor.requestCssId(cssFiles);
		return toHtml("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"", "\" />", url);
	}
	else
	{
		return toHtml("\t<link rel=\"stylesheet\" type=\"text/css\" href=\"", "\" />", cssFiles);
	}
}

@HtmlFragment
public String getCssIncludeHtml(String aFile, String bFile)
{ return compressCssInternal(getCssPath()+aFile, getCssPath()+bFile); }

@HtmlFragment
public String getCssIncludeHtml(String aFile, String bFile, String cFile)
{ return compressCssInternal(getCssPath()+aFile, getCssPath()+bFile, getCssPath()+cFile); }

private String compressJsInternal(String... jsFiles)
{
	// isRestricted is a hack. It just happens to work. :-/
	boolean ok = !frontEnd.debug() && !getPlayer().isRestricted();
	for (String jsFile : jsFiles)
		if (!jsFile.startsWith("js/")) ok = false;
	JsCompressorService compressor = frontEnd.findService(JsCompressorService.class);
	if ((compressor != null) && ok)
	{
		String url = "pack/cache/"+compressor.requestJsId(jsFiles);
		return toHtml("\t<script type=\"text/javascript\" src=\"", "\"></script>", url);
	}
	else
	{
		return toHtml("\t<script type=\"text/javascript\" src=\"", "\"></script>", jsFiles);
	}
}

@HtmlFragment
public String getJsIncludeHtml(String aFile)
{ return compressJsInternal(aFile); }

@HtmlFragment
public String getJsIncludeHtml(String aFile, String bFile)
{ return compressJsInternal(aFile, bFile); }

@HtmlFragment
public String getJsIncludeHtml(String aFile, String bFile, String cFile)
{ return compressJsInternal(aFile, bFile, cFile); }

@HtmlFragment
public String getJsIncludeHtml(String aFile, String bFile, String cFile, String dFile)
{ return compressJsInternal(aFile, bFile, cFile, dFile); }


public Colony getNextColony(Colony current)
{
	int currentIndex = getPlayer().getColonies().indexOf(current);
	if (currentIndex+1 < getPlayer().getColonies().size())
		return getPlayer().getColonies().get(currentIndex+1);
	return null;
}

public Colony getPreviousColony(Colony current)
{
	int currentIndex = getPlayer().getColonies().indexOf(current);
	if (currentIndex > 0)
		return getPlayer().getColonies().get(currentIndex-1);
	return null;
}

public Alliance checkAllianceRight(RightEnum right)
{
	Alliance a = getPlayer().getAlliance();
	if (a == null) throw new ErrorCodeException(ErrorCode.MESSAGE_NO_TARGET);
	if (!getPlayer().getRank().hasAllianceRight(right))
		throw new ErrorCodeException(ErrorCode.MESSAGE_NO_RIGHT);
	return a;
}

public Colony getColony(Position position)
{ return getPlayer().getColony(position); }

public Colony getColony(String position)
{ return getPlayer().getColony(Position.decode(position)); }

public ContractDraft getContractDraft()
{
	if (contractDraft != null)
		return contractDraft;
	else
	{
		contractDraft = new ContractDraft(ContractType.NAP, getPlayer());
		return contractDraft;
	}	
}

public void setPlannedAttacker(boolean isPlayer)
{ plannedAttackerIsPlayer = isPlayer; }

public boolean plannedAttackerIsPlayer()
{ return plannedAttackerIsPlayer; }

public void setPlannedVictim(ContractParty newVictim)
{ plannedVictim = newVictim; }

public ContractParty getPlannedVictim()
{ return plannedVictim; }

public Position string2position(String s)
{
	GalaxyView viewGalaxy = getGalaxyView();
	Position p = Position.decode(s);
	if (p.specificity() >= Position.SYSTEM)
	{
		SolarSystem sys = p.findSystem(galaxy);
		if (!viewGalaxy.isKnown(sys))
			throw new InvalidPositionException("System is not known!");
		if (sys.isInvisible())
			throw new InvalidPositionException("System is invisible!");
	}
	return p;
}

public void start()
{
	if (!isLoggedIn()) throw new IllegalStateException("Not logged in!");
	
	if (galaxy == null)
		galaxy = frontEnd.getGalaxy();
	
	frontEnd.accessPlayer(playerId);
	
	galaxy.update();
	flag = 0;
	
	isMulti = getPlayer().isMulti();
	if (isMulti)
	{
		Alliance multi = galaxy.findAllianceByName("MULTI");
		if (getPlayer().getAlliance() != multi)
			multi.add(galaxy.getTime(), getPlayer());
		
		if (getPlayer().getAlliance() == multi)
		{
			if (getGalaxyView() == getPlayer().getAlliance().getGalaxyView())
			{
				getPlayer().setGalaxyView(new GalaxyView());
				getPlayer().updateGalaxyView();
			}
		}
	}
		
	check();
}

public void resetGraphicPath()
{
	graphicPath = null;
	cssPath = null;
	check();
}

public void setDefaultGraphicPath()
{
	graphicPath = Attribute.GRAPHIC_PATH.getDefaultValue();
	cssPath = Attribute.GRAPHIC_PATH.getDefaultValue();
}

// must also execute correctly if not logged in
public void check()
{
	if (locale == null)
	{
		if (isLoggedIn())
			locale = getPlayer().getLocale();
		else
			locale = frontEnd.getDefaultLocale();
	}
	
	if (graphicPath == null)
	{
		if (isLoggedIn())
			graphicPath = getPlayer().getGraphicPath();
		else
			graphicPath = Attribute.GRAPHIC_PATH.getDefaultValue();
	}
	
	if (cssPath == null)
	{
		cssPath = Attribute.GRAPHIC_PATH.getDefaultValue();
		if (isLoggedIn() && getPlayer().isCssInGp())
			cssPath = graphicPath;
	}
}


public boolean isLoggedIn()
{ return playerId != -1; }

public void login(Identity id, int pid, AccessLevel lvl)
{
	if (identity == null) identity = id;
	synchronized (galaxy)
	{
		accessLevel = AccessLevel.NONE;
		payQuota = true;
		if (playerId != -1)
		{
			locale = getPlayer().getLocale();
			
			int ct = getPlayer().getAttr(Attribute.MULTI_COUNTER).intValue()+1;
			getPlayer().setAttr(Attribute.MULTI_COUNTER, Integer.valueOf(ct));
			
			playerId = pid;
			
			ct = getPlayer().getAttr(Attribute.MULTI_COUNTER).intValue()+1;
			getPlayer().setAttr(Attribute.MULTI_COUNTER, Integer.valueOf(ct));
			
			getPlayer().setAttr(Attribute.LAST_LOGIN, Long.valueOf(System.currentTimeMillis()));
		}
		else
			playerId = pid;
		accessLevel = lvl;
	}
}

public void logout()
{
	if (!isLoggedIn())
		throw new IllegalStateException();
	synchronized (galaxy)
	{
		accessLevel = null;
		playerId = -1;
	}
}

}
