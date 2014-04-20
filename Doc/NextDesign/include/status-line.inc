<div class="status">
	%if Galaxy.Survey.exists or Galaxy.Kommission.Survey.exists;
	%set y 0;
	%if Galaxy.Survey.exists;
	%if not player.hasSurveyVoted; %set y 1; %endif;
	%endif;
	%if Galaxy.Kommission.Survey.exists;
	%if not player.hasKommissionVoted; %set y 1; %endif;
	%endif;
	<a class="%bool y != 1;" href="?">%gettext "Umfrage";</a>
	%endif;
	
	<a class="no">%gettext "User";: %string player.name;</a>
	<a class="no">%gettext "Quota";: %long player.bandwidth; / %long player.totalBandwidth;KB</a>
	%set mbox player.mailbox;%set folder mbox.folder(0);<a class="%bool folder.size > 0;" href="?">%gettext "Ereignisse";: %long folder.size;</a>
	<a class="%bool folder.size > 0;" href="?">%gettext "Nachrichten";: %long folder.size;</a>
	<a class="%bool player.hasNewAllianceMessage;" href="alliance-forum">%gettext "Allianz";: %set mbox alliance.mailbox;%set folder mbox.folder(0);%long folder.size;</a>
	<a href="?">%gettext "Flotten";: %long player.fleetAmount;</a>
	<a href="?">%gettext "Schlachten";: %long player.Battles.amount;</a>
	
	<a class="logout" href="?">Logout <img src="%string Session.GP.design;logout.gif" alt="" /></a>
</div>

%if not Session.supportsCompression;
<table><tr><td class="inactive">%gettext "Keine komprimierte Datenübertragung!";</td></tr></table>
%endif;
%loop x from 0 to player.Error.count-1;
<div class="sysmessage">
	<span>%date player.Error.time(x);</span>
	%string player.Error.message(x);
</div>
%endloop;
%if Galaxy.isInstantMessage;
<table class="header">
	<tr><td class="bi">%string Galaxy.instantMessage(locale);</td></tr>
</table>
%endif;
