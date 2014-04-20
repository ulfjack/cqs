package net.cqs.history;

import java.util.HashMap;

import net.cqs.main.persistence.BattleReport;

public abstract class BattleLineProcessor
{

public abstract void process(BattleReport report, String line, HashMap<String,String> data);

}