package net.cqs.config.units;

import java.util.Locale;

import net.cqs.config.ResearchEnum;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;
import net.cqs.util.EnumIntMap;

public enum TestUnitImpl implements Unit
{

INFANTRY, FIGHTER, CARRIER,
 U1,  U2,  U3,  U4,  U5,  U6,  U7,  U8,  U9, U10,
U11, U12, U13, U14, U15, U16, U17, U18, U19, U20,
U21, U22, U23, U24, U25, U26, U27, U28, U29, U30,
U31, U32, U33, U34, U35, U36, U37, U38, U39, U40,
U41, U42, U43, U44, U45, U46, U47, U48, U49, U50,
COLONIZATION;

@Override
public UnitSystem unitSystem()
{ throw new UnsupportedOperationException(); }

@Override
public UnitClass unitClass()
{  throw new UnsupportedOperationException(); }

@Override
public boolean isPlanetary()
{ return (this == INFANTRY) || (this == COLONIZATION); }

@Override
public boolean isInterplanetary()
{ return !isPlanetary() && !hasSpecial(UnitSpecial.WARP); }

@Override
public boolean isInterstellar()
{ return !isPlanetary() && hasSpecial(UnitSpecial.WARP); }

public boolean isAttackUnit()
{ return true; }

@Override
public boolean hasSpecial(UnitSpecial special)
{
	return
		((special == UnitSpecial.AUTO_DEFENDER) && (this == INFANTRY)) ||
		((special == UnitSpecial.SETTLEMENT) && (this == COLONIZATION)) ||
		((special == UnitSpecial.WARP) && (this == CARRIER));
}

@Override
public boolean hasGroundSpeedModule()
{ return false; }

@Override
public boolean hasDoubleGroundSpeedModule()
{ return false; }

@Override
public boolean hasSpeedModule()
{ return false; }

@Override
public boolean hasDoubleSpeedModule()
{ return false; }

@Override
public boolean hasDoubleWarpModule()
{ return false; }

@Override
public Cost getCost()
{ return new Cost(); }

@Override
public FightSpec getFightSpec()
{ return new FightSpec(0, 0, new float[] { 0, 0, 0 }); }

@Override
public EducationModifier getEducationModifier()
{ return new EducationModifier(); }

@Override
public DependencyList<ResearchEnum> getResearchDependencies()
{ return DependencyList.of(ResearchEnum.class); }

@Override
public boolean checkResearchDependencies(EnumIntMap<ResearchEnum> map)
{ return true; }

@Override
public UnitModule[] getUnitModules()
{ return new UnitModule[0]; }

@Override
public int getGroup()
{ return ordinal()/2; }

@Override
public int getSize()
{ return 1; }

@Override
public int getPower()
{ return 1; }

@Override
public int getUpkeep()
{ return 0; }

@Override
public int getScore()
{ return 0; }

@Override
public int getResourceCapacity()
{ return this == INFANTRY ? 1000 : 0; }

@Override
public int getGroundUnitCapacity()
{ return this == FIGHTER ? 1000 : 0; }

@Override
public int getSpaceUnitCapacity()
{ return this == CARRIER ? 1000 : 0; }

@Override
public float getLandingAttack()
{ return 0; }

@Override
public float getAttack(int group)
{ return 0; }

@Override
public float getDefense()
{ return 1; }

@Override
public boolean isDesign()
{ return true; }

@Override
public String[] getImageNames()
{ return null; }

@Override
public String getName(Locale locale)
{ return this.name(); }

}
