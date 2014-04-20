package net.cqs.web.game;

import net.cqs.main.i18n.AbstractEnvironmentDescriptor;
import net.cqs.main.i18n.EnvironmentDescriptor;

public final class AjaxEnvironment
{

public static final EnvironmentDescriptor<GameEnvironment> DESC =
	new AbstractEnvironmentDescriptor<GameEnvironment>(
		GameEnvironment.class, "AJAX", "Html/Design/ajax/", "*.ajax", GameEnvironment.BUNDLE_NAME);

}
