package net.cqs.web.action;

public interface GetAction
{

String getName();
boolean isDeprecated();
void activate(String value, Object... params);

}
