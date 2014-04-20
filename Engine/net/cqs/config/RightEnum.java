package net.cqs.config;

import java.util.Formattable;
import java.util.Formatter;
import java.util.Locale;

import net.cqs.i18n.StringBundle;
import net.cqs.i18n.StringBundleFactory;

public enum RightEnum implements Formattable
{

MAY_SURVEY("participate in surveys"),
START_SURVEY("start surveys"),
WRITE_MESSAGES("write messages"),
DELETE_MESSAGES("delete messages"),
SEND_MAIL("send emails"),
MAKE_CONTRACTS("manage contracts"),
EDIT_DESCRIPTION("change the description"),
CHANGE_NAME("change the name"),
CHANGE_RANKS("change ranks"),
EDIT_RANKS("edit ranks"),
ACCEPT_APPLICATIONS("accept and deny applications"),
KICK_MEMBER("kick members");

private final String englishTranslation;

private RightEnum(String englishTranslation)
{ this.englishTranslation = englishTranslation; }

public String englishTranslation()
{ return englishTranslation; }

private static String bundleName()
{ return "net.cqs.config.RightEnum"; }

public String getName(Locale locale)
{
	StringBundle bundle = StringBundleFactory.getBundle(bundleName(), locale);
	return bundle.getSafeString(englishTranslation());
}

@Override
public void formatTo(Formatter fmt, int f, int width, int precision)
{ fmt.format(getName(fmt.locale())); }

public static RightEnum[] getAllianceRights()
{ return values(); }

}
