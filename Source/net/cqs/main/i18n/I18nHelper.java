package net.cqs.main.i18n;

import java.io.File;

import net.cqs.i18n.FileStringBundleFactory;
import net.cqs.i18n.StringBundleFactory;

public final class I18nHelper
{

public static void init()
{/*OK*/}

static {
	StringBundleFactory.init(new FileStringBundleFactory(new File("I18n"), "cqs"));
}

}
