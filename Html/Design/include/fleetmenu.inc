%set player Session.getPlayer;
%set file Request.getBasename;
%set galaxyView Session.getGalaxyView;
%set desktop player.getDefaultDesktop;
%if Request.getUrlParameterCount() = 1;
%set desktop Tools.safeParseInt(Request.getUrlParameter(0),player.getDefaultDesktop);
%endif;

%if (file="fleets-commands") or (file="fleets-split");
%set fleet player.findFleetById(Tools.safeParseInt(Request.getUrlParameter(0),-1));
%if (fleet != null);%set desktop fleet.getDesktop;%endif;
%endif;

<ul class="smenu">
	<li %if (file="fleets") and ((desktop < -1) or (desktop > Constants.MAX_DESKTOP));class="active"%endif;><a href="fleets|-2">%("desktops");</a></li>
	<li %if (file="fleets") and (desktop = -1);class="active"%endif;><a href="fleets|-1">%("all");</a></li>
	
	%loop x from 0 to Constants.MAX_DESKTOP;
	<li %if ((file="fleets") or (file="fleets-commands") or (file="fleets-split")) and (desktop=x);class="active"%endif;><a href="fleets|%= x;" title="%= player.getDesktopName(Session.getLocale, x);">%= x;</a></li>
	%endloop;
	<li %if file="fleets-join";class="active"%endif;><a href="fleets-join">%("merging");</a></li>
</ul>

