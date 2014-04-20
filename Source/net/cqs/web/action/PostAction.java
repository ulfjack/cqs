package net.cqs.web.action;

import javax.servlet.http.HttpServletRequest;

public interface PostAction
{

String getName();
boolean isDeprecated();
void activate(HttpServletRequest req, Object... params);

}
