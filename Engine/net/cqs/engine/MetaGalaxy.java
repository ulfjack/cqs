package net.cqs.engine;

import java.io.Serializable;

import net.cqs.engine.base.Survey;
import net.cqs.engine.messages.MultiI18nMessage;

public final class MetaGalaxy implements Serializable
{

private static final long serialVersionUID = 1L;

public MultiI18nMessage instantMessage;

private int surveyID = 0;
public Survey survey = null;
public Survey kommission = null;

public synchronized int getSurveyID()
{
	surveyID++;
	return surveyID;
}

}
