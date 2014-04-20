<div id="top-navi">

<div class="data">
	<table>
		<tr><th>%("user:");</th><td>%= player.getName;</td></tr>
		<tr><th>%("quota:");</th><td><span id="quotaUsed">%= player.getUsedBandwidth;</span>/%= player.getTotalBandwidth;KB</td></tr>
	</table>
</div>
%set file Request.getBasename;

<div id="links-game">
<div class="navi"><a href="map"><img class="naviicon" src="pack/design/navi-map-%= file.startsWith("map") ? "active" : "inactive";.png" alt="%("maps");" /></a><span>%("maps");</span></div>
<div class="navi"><a href="fleets"><img class="naviicon" src="pack/design/navi-fleet-%= file.startsWith("fleet") ? "active" : "inactive";.png" alt="%("fleets");" /></a><span>%("fleets"); (%= player.getFleets.size;)</span></div>
<div class="navi">%if player.isUnderAttack;<a href="battles-defenses"><img class="naviicon" src="pack/design/navi-battles_attack-%= file.startsWith("battle") ? "active" : "inactive";.gif" alt="%("battles");" /></a>%else;<a href="battles"><img class="naviicon" src="pack/design/navi-battles-%= file.startsWith("battle") ? "active" : "inactive";.png" alt="%("battles");" /></a>%endif;<span>%("battles"); (%= player.getBattles.size;)</span></div>
<div class="navi"><a href="espionage"><img class="naviicon" src="pack/design/navi-espionage-%= file.startsWith("espionage") ? "active" : "inactive";.png" alt="%("espionage");" /></a><span>%("espionage"); (%= player.getAgents.size;)</span></div>
<div class="navi"><a href="diplomacy"><img class="naviicon" src="pack/design/navi-diplomacy-%= file.startsWith("diplomacy") ? "active" : "inactive";.png" alt="%("diplomacy");" /></a><span>%("diplomacy");</span></div>
<div class="navi"><a href="alliance">%if Tools.hasNewAllianceMessage(player);<img class="naviicon" src="pack/design/navi-alliance_new-%= file.startsWith("alliance") ? "active" : "inactive";.gif" alt="%("alliance");" />%else;<img class="naviicon" src="pack/design/navi-alliance-%= file.startsWith("alliance") ? "active" : "inactive";.png" alt="%("alliance");" />%endif;</a><span>%("alliance");</span></div>
<div class="navi"><a href="research|current"><img class="naviicon" src="pack/design/navi-research-%= file.startsWith("research") ? "active" : "inactive";.png" alt="%("research");" /></a><span>%("research");</span></div>
<div class="navi"><a href="design"><img class="naviicon" src="pack/design/navi-unitdesign-%= file.startsWith("design") ? "active" : "inactive";.png" alt="%("unit design");" /></a><span>%("unit design");</span></div>
<div class="navi"><a href="budget"><img class="naviicon" src="pack/design/navi-finances-%= file.startsWith("budget") ? "active" : "inactive";.png" alt="%("finances");" /></a><span>%("finances");</span></div>
<div class="navi"><a href="stats-production"><img class="naviicon" src="pack/design/navi-statistics-%= file.startsWith("stats") ? "active" : "inactive";.png" alt="%("statistics");" /></a><span>%("statistics");</span></div>
<div class="navi"><a href="search|player"><img class="naviicon" src="pack/design/navi-search-%= file.startsWith("search") ? "active" : "inactive";.png" alt="%("search");" /></a><span>%("search");</span></div>
<div class="navi"><a href="mail-box">%set mbox Session.getPlayerMailboxCopy;%set folder mbox.getFolder(0);%if folder.size > 0;<img class="naviicon" src="pack/design/navi-inbox_new-%= file.startsWith("mail") ? "active" : "inactive";.gif" alt="%("mailbox");" />%else;<img class="naviicon" src="pack/design/navi-inbox-%= file.startsWith("mail") ? "active" : "inactive";.png" alt="%("mailbox");" />%endif;</a><span>%("mailbox"); %if folder.size > 0;(%=folder.size;)%endif;</span></div>
</div>

<div id="links-settings">
<div class="navi">
	<form action="" method="post">
		<input type="hidden" name="do" value="Player.Language.set" />
		<input type="hidden" name="language" value="de_DE" />
		<input type="image" src="pack/design/navi-flag_de.png" />
	</form>
	<span>Deutsch</span>
</div>
<div class="navi">
	<form action="" method="post">
		<input type="hidden" name="do" value="Player.Language.set" />
		<input type="hidden" name="language" value="en_US" />
		<input type="image" src="pack/design/navi-flag_us.png" />
	</form>
	<span>English</span></div>
<div class="navi"><a href="help"><img class="naviicon" src="pack/design/navi-help-%= file.startsWith("help") ? "active" : "inactive";.png" alt="%("help");" /></a><span>%("help");</span></div>
<div class="navi"><a href="settings-common"><img class="naviicon" src="pack/design/navi-settings-%= file.startsWith("settings") ? "active" : "inactive";.png" alt="%("settings");" /></a><span>%("settings");</span></div>
<div class="navi"><a href="%if player.getName = "gast";/index%else;index?do=logout%endif;"><img class="naviicon" src="pack/design/navi-logout.png" alt="%("logout");" /></a><span>%("logout");</span></div>
</div>

</div>

<div id="colony-navi">
	%if player.getColonies.size > 0;
		%set lastSystem -1;
		<ul class="cololist">
		%loop x,c foreach player.getColonies;
			%if (lastSystem != c.getPosition.getSystemNumber) and (lastSystem != -1);
			</ul><ul class="cololist">	
			%endif;
			%set lastSystem c.getPosition.getSystemNumber;
			<li %if x%2=1; class="even"%endif;><a class="%= c = colony;" href="colony|%= c.getPosition;"><img src="%set planet c.getPlanet;%= Session.getUrl(planet.getType);" alt="%=planet.getType;" width="16" height="16" /><span class="colo-label">%= c.getPosition; %=c.getName;</span></a></li>
		%endloop;
		</ul>
	%else;
	<div id="colonymenu">
	<a href="index">%("No colony?");</a>
	</div>
	%endif;
</div>





<div class="content">
%if not Request.isCompressionEnabled;
<div class="nobglr"><div class="warn">%("Data transmitted uncompressed!");</div></div>
%endif;
<!--%if player.isUnderAttack;
<div class="nobglr"><div class="warn">%("You are under attack!");</div></div>
%endif;-->
<div class="nobglr"><div id="events">
%if player.currentEventCount > 0;
<div class="bg">%include "../include/events.inc";</div>
%endif;
</div></div>
%if Galaxy.getInstantMessage != null;
<div class="nobglr"><div class="note">%= Galaxy.getInstantMessage.get(Session.getLocale);</div></div>
%endif;
