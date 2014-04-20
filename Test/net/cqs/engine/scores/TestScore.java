package net.cqs.engine.scores;

public class TestScore extends Score<TestScoreable>
{

private TestScoreable ts;

public TestScore(ScoreManager<TestScoreable> sm, TestScoreable ts)
{
	super(sm);
	this.ts = ts;
}

public TestScoreable getTestScoreable()
{ return ts; }

public void setPoints(int pamount)
{
	points = pamount;
	signalChange();
}

public void setSecPoints(int aamount)
{
	secPoints = aamount;
	signalChange();
}

public void addPoints(int pamount)
{
	points += pamount;
	signalChange();
}

public void setPosition(int pos)
{ position = pos; }

@Override
public String toString()
{ return ts.getName(); }

@Override
public TestScoreable getScoreable()
{ return ts; }

@Override
protected void updateInfo()
{/*OK*/}

}
