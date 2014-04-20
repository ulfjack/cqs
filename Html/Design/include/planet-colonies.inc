<h4>%("colonies");</h4>
<table class="stats">
<colgroup><col width="20%%" /><col width="20%%" /><col width="45%%" /><col width="15%%" /></colgroup>
	<tr>
		<td>%("status");</td>
		<td>%("position");</td>
		<td>%("owner");</td>
		<td class="r">%("size");</td>
	</tr>
	%loop tempC foreach planet;
	%set owner tempC.getOwner;
	<tr>
		%set diplomacyEntry Galaxy.getDiplomacyEntry(owner, player);
		%set diplomaticStatus diplomacyEntry.getStatus;
		<td class="%= diplomaticStatus;">%= diplomaticStatus.getName(Session.getLocale);</td>
		<td><a href="%= Session.getMapUrl(tempC.getPosition);">%= tempC.getPosition;</a></td>
		<td><a href="player|%= owner.getPid;">%= owner.getName;</a>
		%if owner.getAlliance != null;%set alliance owner.getAlliance;[<a href="view-alliance|%= alliance.getId;">%= alliance.getShortName;</a>]%endif;</td>
		<td class="r">%("{0,number,integer}", tempC.getSize);</td>
<!--		<td><a href="send-fleet?to=%= colony.getPosition;">%("send fleet");</a></td>-->
	</tr>
	%endloop;
</table>
