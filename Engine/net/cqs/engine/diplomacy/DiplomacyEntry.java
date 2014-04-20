package net.cqs.engine.diplomacy;

public final class DiplomacyEntry
{

private DiplomaticStatus status;
private long attackTime;

DiplomacyEntry(DiplomaticStatus s, long attTime)
{
	this.status = s;
	this.attackTime = attTime;
}

public DiplomacyEntry bestOf(DiplomacyEntry other)
{
	if (other == null) return this;
	if (status == other.status)
		return this.attackTime > other.attackTime ? this : other;
	else
		return status.isBetter(other.status) ? this : other;
}

public DiplomacyEntry worseOf(DiplomacyEntry other)
{
	if (other == null) return this;
	if (status == other.status)
		return this.attackTime > other.attackTime ? this : other;
	else
		if (status.isWorse(other.status)) return this;
		else return other;
}

public DiplomaticStatus getStatus()
{ return status; }

public long getAttackTime()
{ return attackTime; }

@Override
public String toString()
{ return "DiplomacyEntry: "+status+" "+attackTime; }

}