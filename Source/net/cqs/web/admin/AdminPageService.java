package net.cqs.web.admin;

import net.cqs.web.action.ActionParser;
import net.cqs.web.action.PostAction;

public interface AdminPageService
{

AdminEnvironment newEnvironment();
ActionParser getActionParser();
void addPage(String category, String item, String source, PostAction... actions);

}
