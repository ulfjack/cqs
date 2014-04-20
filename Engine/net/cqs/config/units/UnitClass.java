package net.cqs.config.units;

import java.io.Serializable;
import java.util.Locale;

import net.cqs.config.ResearchEnum;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;

public interface UnitClass extends Serializable
{

UnitSystem getUnitSystem();

int size();
UnitModule[] getModules();

boolean isPlanetary();
boolean hasSpecial(UnitSpecial special);

Cost getCost();
FightSpec getFightSpec();
DependencyList<ResearchEnum> getResearchDependencies();

int getGroup();
UnitModule parseUnitModule(String s);


String getName(Locale locale);
String getDescription(Locale locale);
String getImageName();

}
