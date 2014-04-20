<h4>%("blockade"); (<a href="battles-blocks">%("view");</a>)</h4>
%if planet.isBlocked;
%set battle planet.getSpaceBattle;
<table class="stats">
<colgroup><col width="20%%" /><col width="20%%" /><col width="45%%" /><col width="15%%" /></colgroup>
	<tr>
		<td>%("status");</td>
		<td></td>
		<td>%("owner");</td>
		<td class="r">%("units");</td>
	</tr>
	%loop y from 0 to battle.getSide(1).size-1;
	%set entry battle.getSide(1).get(y);
	%set fleet entry.getFleet;
	%set fowner entry.getFleet.getOwner;
	<tr>
		%set diplomacyEntry Galaxy.getDiplomacyEntry(fowner, player);
		%set diplomaticStatus diplomacyEntry.getStatus;
		<td class="%= diplomaticStatus;">
			%= diplomaticStatus.getName(Session.getLocale);
		</td>
		<td></td>
		<td><a href="player|%= fowner.getPid;">%= fowner.getName;</a>
		%if fowner.getAlliance != null;%set alliance fowner.getAlliance;[<a href="view-alliance?temp0=%= alliance.getId;">%= alliance.getShortName;</a>]%endif;</td>
		<td class="r">%("{0,number,integer}", fleet.getTotalUnits);</td>
	</tr>
	%endloop;
</table>
%else;
%("This planet is currently not being blocked.");
%endif;
