package net.cqs.engine.diplomacy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.cqs.config.InvalidDatabaseException;
import net.cqs.config.RightEnum;
import net.cqs.engine.Galaxy;
import net.cqs.engine.GalaxyView;
import net.cqs.engine.Player;
import net.cqs.engine.base.Attribute;
import net.cqs.engine.base.AttributeMap;
import net.cqs.engine.base.Survey;
import net.cqs.engine.scores.AllianceScore;
import net.cqs.engine.scores.ScoreManager;
import net.cqs.engine.scores.Scoreable;
import net.cqs.util.IntIntMap;
import de.ofahrt.ulfscript.annotations.Restricted;

public final class Alliance implements Iterable<Player>, ContractParty, Scoreable<Alliance>, Serializable, ObjectInputValidation
{

private static final long serialVersionUID = 2L;

private final Galaxy galaxy;
private final int id;
private String name;
private String shortName;
private transient AllianceScore score;

private final AttributeMap attributes = new AttributeMap();

private Survey survey = null;
private final List<Rank> ranks;
private Rank startRank;
private IntIntMap votes = new IntIntMap();

private transient GalaxyView galaxyView = new GalaxyView();

private final List<Player> data = new ArrayList<Player>();
private final List<Contract> contracts = new ArrayList<Contract>();

public Alliance(Galaxy galaxy, int id, String myname, String myshort)
{
	this.galaxy = galaxy;
	this.id = id;
	this.setName(myname);
	this.shortName = myshort;
	this.ranks = new ArrayList<Rank>();
	ranks.add(new Rank("Administrator", EnumSet.allOf(RightEnum.class)));
	ranks.add(new Rank("Neuling",       EnumSet.of(RightEnum.WRITE_MESSAGES)));
	this.startRank = ranks.get(1);
}

private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
{
	in.defaultReadObject();
	in.registerValidation(this, 20);
}

@Override
public void validateObject()
{
	galaxyView = new GalaxyView();
	for (int i = 0; i < data.size(); i++)
	{
		Player p = get(i);
		p.setAlliance(this);
		p.setGalaxyView(galaxyView);
	}
}

public void check() throws InvalidDatabaseException
{
	if (data.size() == 0)
		throw new InvalidDatabaseException("Empty alliance \""+this+"\"");
}

public int getId()
{ return id; }

@Override
public String getName()
{ return name; }

@Restricted
public void setName(String name)
{ this.name = name; }

@Restricted
public void setShortName(String shortName)
{ this.shortName = shortName; }

public String getShortName()
{ return shortName; }

public List<Rank> getRanks()
{ return ranks; }

public Rank getStartRank()
{ return startRank; }

public void setStartRank(Rank startRank)
{ this.startRank = startRank; }

public int getVote(int index)
{ return votes.get(index); }

@Restricted
public void changeVote(Player p, int newVote)
{
	if (p == null) throw new NullPointerException();
	int oldVote = p.getAllianceVote();
	if (oldVote >= 0) votes.decrease(oldVote);
	p.setAllianceVote(newVote);
	if ((newVote >= 0) && (newVote <= size()))
	{
		int numVotes = votes.increase(newVote);
		if (newVote > 0)
		{
			if (numVotes >= (size()+1)/2)
				get(newVote-1).setRank(ranks.get(0));
		}
	}
}

public <T extends Serializable> T getAttr(Attribute<T> key)
{ return attributes.get(key); }

public <T extends Serializable> void setAttr(Attribute<T> key, T value)
{ attributes.set(key, value); }

public void removeAttr(Attribute<?> key)
{ attributes.remove(key); }

public boolean hasLogo()
{ return attributes.get(Attribute.ALLIANCE_LOGO).length() != 0; }

public String getLogo()
{ return attributes.get(Attribute.ALLIANCE_LOGO); }

public String getPlaintextDescription()
{ return attributes.get(Attribute.ALLIANCE_PLAINTEXT); }

public String getDescription()
{ return Galaxy.defaultTextConverter.convert(getPlaintextDescription()); }

public Survey getSurvey()
{ return survey; }

public void setSurvey(Survey survey)
{ this.survey = survey; }

public List<Player> getPlayers()
{ return data; }

@Override
public List<Contract> getContracts()
{ return contracts; }

@Override
public void addContract(Contract c)
{ contracts.add(c); }

@Override
public void removeContract(Contract c)
{ contracts.remove(c); }

// TODO
@Override
public ContractParty getSuperParty()
{ return null; }

@Override
public boolean isPlayer()
{ return false; }

@Override
public AllianceScore getScore()
{ return score; }

@Override
public AllianceScore removeScore()
{
	AllianceScore result = score;
	score = null;
	return result;
}

@Override
public AllianceScore newScore(ScoreManager<Alliance> sm)
{
	score = new AllianceScore(sm, this);
	return score;
}

public GalaxyView getGalaxyView()
{ return galaxyView; }

public void resetGalaxyView()
{ galaxyView.clear(); }

public int size()
{ return data.size(); }

public Player get(int i)
{ return data.get(i); }

public int findPlayerByName(String findName)
{
	for (int i = 0; i < data.size(); i++)
	{
		Player p = data.get(i);
		if (findName.equalsIgnoreCase(p.getName()))
			return i;
	}
	return -1;
}

public int findPlayerById(int findId)
{
	for (int i = 0; i < data.size(); i++)
	{
		Player p = data.get(i);
		if (findId == p.getPid())
			return i;
	}
	return -1;
}

public void resetVotes()
{
	votes.clear();
	for (int i = 0; i < data.size(); i++)
		data.get(i).setAllianceVote(-1);
}

public void add(long time, Player p)
{
	if (p.getAlliance() == this)
		return;
	if (p.getAlliance() != null)
		p.getAlliance().remove(p);
	
	data.add(p);
	p.setAlliance(this);
	
	p.notifyAllianceJoined(time);
	if (score != null)
		score.signalChange();
}

private void notifyRemove(Player p)
{
	resetVotes();
	p.notifyAllianceLeft(this);
	if (score != null)
		score.signalChange();
}

public void checkSize()
{
	if (data.size() == 0)
	{
		for (int i = 0; i < contracts.size(); i++)
		{
			RemovePartyAction action = new RemovePartyAction(galaxy.getTime(), galaxy.getDiplomaticRelation(), this, contracts.get(i));
			action.execute();
		}
		galaxy.removeAlliance(this);
	}
}

public void remove(Player p)
{
	data.remove(p);
	notifyRemove(p);
	checkSize();
}

@Override
public Iterator<Player> iterator()
{ return data.iterator(); }

@Override
public int hashCode()
{ return System.identityHashCode(this); }

@Override
public boolean equals(Object o)
{ return o == this; }

@Override
public String toString()
{ return "Alliance "+name+" ["+shortName+"]"; }


//public static Alliance createTestAlliance()
//{ return new Alliance(true); }

}

