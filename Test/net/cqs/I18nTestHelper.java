package net.cqs;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.cqs.main.i18n.I18nHelper;

public final class I18nTestHelper
{

public static final List<Locale> TESTED_LOCALES = Arrays.asList(new Locale[] { Locale.GERMANY, Locale.US });

public static void init()
{ I18nHelper.init(); }

}
