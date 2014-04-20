package net.cqs.main.config;

public enum LogEnum
{

ADMIN  ("admin-%g.log"),
BASE   ("base-%g.log"),
BATTLE ("battle-%g.log", "net.cqs.engine.battles"),
NIGHTLY("nightly-%g.log"),
TIME   ("time-%g.log", "net.cqs.web.GameGate"),

REPORT_AGENT  ("report-agent-%g.log"),
REPORT_BATTLE ("report-battle-%g.log", "net.cqs.web.reports.BattleReportManager"),
REPORT_OBSERVE("report-observe-%g.log"),

ACCESS ("access.log"),
MULTI  ("multis.log"),
EMAIL  ("emaillog.log"),
SUPPORT("supportlog.log");

private final String name;
private final String packageName;

private LogEnum(String name, String packageName)
{
	this.name = name;
	this.packageName = packageName;
}

private LogEnum(String name)
{ this(name, null); }

public String get()
{ return name; }

public String getPackageName()
{ return packageName; }

}
