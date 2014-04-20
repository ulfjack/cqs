package net.cqs.engine.battles;

import net.cqs.config.BattleMessageEnum;
import net.cqs.config.BattleStateEnum;
import net.cqs.config.BuildingEnum;
import net.cqs.engine.Fleet;
import net.cqs.engine.Player;
import net.cqs.engine.base.UnitMap;

public abstract class AbstractBattleLogger implements BattleEventListener
{

private final Battle battle;

public AbstractBattleLogger(Battle battle)
{
	this.battle = battle;
}

protected Battle getBattle()
{ return battle; }

protected abstract void log(String msg);

@Override
public void joinPlayer(int side, Player p)
{
	if (p.getAlliance() != null)
		log(System.currentTimeMillis()+
			".joinPlayer: side="+side+", pid="+p.getPid()+", pname=\""+p.getName()+"\""+
			", aid="+p.getAlliance().getId()+", aname=\""+p.getAlliance().getShortName()+"\"");
	else
		log(System.currentTimeMillis()+
			".joinPlayer: side="+side+", pid="+p.getPid()+", pname=\""+p.getName()+"\"");
}

@Override
public void joinFleet(int side, Fleet f)
{
	log(System.currentTimeMillis()+
		".joinFleet: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid()+
		", units=\""+f.getSerializedUnits()+"\"");
}

@Override
public void changePlayerAlliance(Player p)
{
	if (p.getAlliance() != null)
		log(System.currentTimeMillis()+
			".changePlayerAlliance: pid="+p.getPid()+", pname=\""+p.getName()+"\""+
			", aid="+p.getAlliance().getId()+", aname=\""+p.getAlliance().getShortName()+"\"");
	else
		log(System.currentTimeMillis()+
			".changePlayerAlliance: pid="+p.getPid()+", pname=\""+p.getName()+"\"");
}

@Override
public void invade(Fleet f, int what)
{
	log(System.currentTimeMillis()+
		".invade: fid="+f.getId()+", pid="+f.getOwner().getPid()+", building="+what);
}

@Override
public void message(BattleMessageEnum what)
{
	log(System.currentTimeMillis()+
		".message: id=\""+what.name()+"\"");
}

@Override
public void unitsActive(int side, FleetEntry entry)
{
	if (entry.activeUnits.sum() == 0) return;
	log(System.currentTimeMillis()+
		".unitsactive: side="+side+", fid="+entry.fleet.getId()+", pid="+entry.fleet.getOwner().getPid()+
		", units=\""+entry.activeUnits.serialize()+"\"");
}

@Override
public void unitsInvaded(int side, Fleet f, int what)
{
	log(System.currentTimeMillis()+
		".unitsinvaded: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid()+
		", unitcount="+what);
}

@Override
public void unitsLost(int side, Fleet f, UnitMap what)
{
	if (what.sum() == 0) return;
	log(System.currentTimeMillis()+
		".unitslost: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid()+
		", units=\""+what.serialize()+"\"");
}

@Override
public void unitsFixed(Fleet f, UnitMap what)
{
	if (what.sum() == 0) return;
	log(System.currentTimeMillis()+
		".unitsfixed: fid="+f.getId()+", pid="+f.getOwner().getPid()+
		", units=\""+what.serialize()+"\"");
}

@Override
public void win(Fleet f)
{
	log(System.currentTimeMillis()+
		".win: fid="+f.getId()+", pid="+f.getOwner().getPid());
}

@Override
public void invadeResult(Fleet f, BuildingEnum what)
{
	log(System.currentTimeMillis()+
		".invade: fid="+f.getId()+", pid="+f.getOwner().getPid()+", result="+what.name());
}

@Override
public void burnResult(Fleet f, BuildingEnum what)
{
	log(System.currentTimeMillis()+
		".burn: fid="+f.getId()+", pid="+f.getOwner().getPid()+", result="+what.name());
}

@Override
public void loose(int side, Fleet f)
{
	log(System.currentTimeMillis()+
		".loose: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid());
}

@Override
public void leave(int side, Fleet f)
{
	log(System.currentTimeMillis()+
		".leave: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid());
}

@Override
public void withdrawSuccess(int side, Fleet f)
{
	log(System.currentTimeMillis()+
		".withdrawSuccess: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid());
}

@Override
public void withdrawFailure(int side, Fleet f)
{
	log(System.currentTimeMillis()+
		".withdrawFailure: side="+side+", fid="+f.getId()+", pid="+f.getOwner().getPid());
}

@Override
public void round(long num)
{
	log(System.currentTimeMillis()+
		".round: num="+num);
}

@Override
public void endround()
{
	log(System.currentTimeMillis()+".endround");
}

@Override
public void start()
{
	if (battle instanceof SimulatorBattle)
		log(System.currentTimeMillis()+
			".start: type="+battle.type());
	else
		log(System.currentTimeMillis()+
			".start: type="+battle.type()+", position=\""+battle.position()+"\"");
}

@Override
public void end(BattleStateEnum reason)
{
	log(System.currentTimeMillis()+
		".end: reason=\""+reason+"\"");
}

@Override
public void info(String data)
{
	log(System.currentTimeMillis()+
		".info: data=\""+data+"\"");
}

}
