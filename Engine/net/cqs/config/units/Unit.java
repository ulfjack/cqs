package net.cqs.config.units;

import java.io.Serializable;
import java.util.Locale;

import net.cqs.config.ResearchEnum;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;
import net.cqs.util.EnumIntMap;

public interface Unit extends Serializable
{

UnitSystem unitSystem();
UnitClass unitClass();

boolean isPlanetary();
boolean isInterplanetary();
boolean isInterstellar();
boolean hasSpecial(UnitSpecial special);
boolean hasSpeedModule();
boolean hasDoubleSpeedModule();
boolean hasGroundSpeedModule();
boolean hasDoubleGroundSpeedModule();
boolean hasDoubleWarpModule();

Cost getCost();
EducationModifier getEducationModifier();

FightSpec getFightSpec();
UnitModule[] getUnitModules();
DependencyList<ResearchEnum> getResearchDependencies();
boolean checkResearchDependencies(EnumIntMap<ResearchEnum> map);

// group of this unit (used for fighting calculations)
int getGroup();
// for calculation of unit group proportions
int getSize();
// for superiority rule (must be > 0)
int getPower();
// cost per hour per unit
int getUpkeep();
// every built unit adds this to the players unit points
int getScore();
// capacity for carrying resources
int getResourceCapacity();
// capacity for carrying ground units
int getGroundUnitCapacity();
// capacity for carrying space units (non-warp)
int getSpaceUnitCapacity();

// whether it's a designed unit or a standard one
boolean isDesign();


// attack against landing units
float getLandingAttack();
// attack against units of this group
float getAttack(int group);
// defense against any attack
float getDefense();

String[] getImageNames();
String getName(Locale locale);
}
