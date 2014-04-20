package net.cqs.config.units;

import java.io.Serializable;
import java.util.Locale;

import net.cqs.config.EducationEnum;
import net.cqs.config.ResearchEnum;
import net.cqs.engine.base.Cost;
import net.cqs.engine.base.DependencyList;

public interface UnitModule extends Serializable
{

int size();
UnitSpecial[] specials();

Cost getCost();
FightSpec getFightSpec();
DependencyList<ResearchEnum> getResearchDependencies();
EducationEnum getEducation();

String getName(Locale locale);
String getDescription(Locale locale);
String getImageName();

}
