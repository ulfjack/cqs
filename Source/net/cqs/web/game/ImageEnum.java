package net.cqs.web.game;

public enum ImageEnum
{

ALLIANCE_BANNER("design/alliance-banner.png"),
EMPTY_UNIT_SMALL("units/leer-small.png"),
EMPTY_UNIT_BIG("units/leer-big.png"),
HG_BIG("planets/HG-big.jpg"),
GRAPHICPACK_CHECK("system/gp-cqs16.png"),

SYSTEM_BACKGROUND("design/circles2.png"),
GALAXY("design/galaxy.jpg"),
SUN("planets/sun.png"),
FLEET("planets/fleet.gif"),

MOVE_TOP("design/atop.gif"),
MOVE_UP("design/aup.gif"),
MOVE_DOWN("design/adown.gif"),
MOVE_RIGHT("design/aright.gif"),
MOVE_LEFT("design/aleft.gif"),
CLOSE("design/x.png"),
DELETE("design/delete.png"),
EDIT("design/edit.png"),

SYMBOL_PEOPLE("design/b1.png"),
SYMBOL_MONEY("design/finances.png"),
SYMBOL_CLOCK("design/clock.png"),
SYMBOL_EDUCATION("design/education.png"),
SYMBOL_RESEARCH("design/research.png"),

SYMBOL_HOUSING("design/b2.png"),
SYMBOL_POPULATION("design/b1.png"),
SYMBOL_JOBS("design/b3.png"),
SYMBOL_CREW("design/b4.png"),
SYMBOL_GROWTHMOD("design/b5.png"),

COST_TIME("design/clock.png"),
COST_WORKERS("design/b3.png"),
COST_EMPLOYEES("design/b1.png"),
COST_UPKEEP("design/finances.png"),

JOB_NONE("design/nojob.png"),
BUILDING_LOT("design/buildinglot.png"),
ESPIONAGE("design/espionage.gif")
;

private final String name;

private ImageEnum(String name)
{
	this.name = name;
}

public String getImageName()
{ return name; }

}
