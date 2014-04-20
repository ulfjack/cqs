package net.cqs.web.admin.plugins;

import net.cqs.main.config.FrontEnd;
import net.cqs.web.action.ActionParser;
import net.cqs.web.action.Parameter;
import net.cqs.web.action.PostAction;
import net.cqs.web.action.WebPostAction;
import net.cqs.web.admin.AdminPageService;
import net.cqs.web.admin.AdminSession;

public final class SettingsPlugin {

public SettingsPlugin(AdminPageService service) {
	ActionParser parser = service.getActionParser();
	PostAction[] actions = parser.parsePostActions(this);
	service.addPage("Core", "Settings", "system-settings.html", actions);
}

@WebPostAction("Settings.change")
public void changeUrl(FrontEnd frontEnd, AdminSession session,
    @Parameter("url") String url, @Parameter("email") String email,
    @Parameter("debug") String debug) {
  if (url != null) {
    frontEnd.setUrl(url);
  }
  if (email != null) {
    frontEnd.setEmail(email);
  }
  if (debug != null) {
    frontEnd.setDebug(Boolean.parseBoolean(debug));
  }
}

}
