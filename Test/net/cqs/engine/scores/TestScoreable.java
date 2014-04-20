package net.cqs.engine.scores;

public class TestScoreable implements Scoreable<TestScoreable>
{

private TestScore score;
private String name;

public String getName()
{ return name; }

@Override
public Score<TestScoreable> getScore()
{ return score; }

@Override
public Score<TestScoreable> removeScore()
{
	Score<TestScoreable> result = score;
	score = null;
	return result;
}

@Override
public Score<TestScoreable> newScore(ScoreManager<TestScoreable> sm)
{
	score = new TestScore(sm, this);
	return score;
}

public void setPoints(int amount)
{ score.setPoints(amount); }

public void addPoints(int amount)
{ score.addPoints(amount); }

public void setAveragePoints(int amount)
{ score.setSecPoints(amount); }

public void setPosition(int pos)
{ score.setPosition(pos); }

@Override
public String toString()
{ return name; }

public TestScoreable(String name)
{
	this.name = name;
}

}
