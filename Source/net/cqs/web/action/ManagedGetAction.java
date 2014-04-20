package net.cqs.web.action;

import net.cqs.storage.GalaxyTask;

public interface ManagedGetAction
{

String getName();
boolean isDeprecated();
GalaxyTask parse(String value, ParameterProvider params);

}
