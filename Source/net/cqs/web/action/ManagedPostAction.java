package net.cqs.web.action;

import net.cqs.storage.GalaxyTask;
import net.cqs.web.ParsedRequest;

public interface ManagedPostAction
{

String getName();
boolean isDeprecated();
GalaxyTask parse(ParsedRequest req, ParameterProvider params);

}
