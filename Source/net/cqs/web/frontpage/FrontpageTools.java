package net.cqs.web.frontpage;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.AccessDeniedReasonTranslator;
import net.cqs.main.persistence.RegistrationHistory;
import net.cqs.main.persistence.TimeHistory;
import net.cqs.storage.profiling.ProfileAggregator;
import net.cqs.web.util.AccessDeniedReason;

public final class FrontpageTools
{

private final FrontEnd frontEnd;

public FrontpageTools(FrontEnd frontEnd)
{
	this.frontEnd = frontEnd;
}

public String version()
{ return frontEnd.version(); }

public int online()
{ return frontEnd.online(); }

public boolean isImageCodeRequired()
{ return frontEnd.isImageCodeRequired(); }

public boolean isPluginLoaded(String name)
{ return frontEnd.isPluginLoaded(name); }

public ProfileAggregator getProfileAggregator()
{ return frontEnd.getProfileAggregator(); }

public TimeHistory getAccessTimeHistory()
{ return frontEnd.getAccessTimeHistory(); }

public RegistrationHistory getRegistrationHistoryCopy()
{ return frontEnd.getRegistrationHistoryCopy(); }

public String asDate(Locale locale, long l)
{
	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
	return dateFormat.format(new Date(l));
}

public String convertAccessDeniedReason(Locale locale, String value)
{
	AccessDeniedReason reason = AccessDeniedReason.valueOf(value);
	return AccessDeniedReasonTranslator.getName(locale, reason);
}

}
