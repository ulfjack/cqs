%if contracts.size > 0;
<table class="stats">
<colgroup><col width="50%%" /><col width="35%%" /><col width="15%%" /></colgroup>
%loop diplomaticStatus foreach DiplomaticStatus.values;
%loop i,contract foreach contracts where contract.getStatus = diplomaticStatus;
	<tr class="%= diplomaticStatus;">
		<th class="l">%= diplomaticStatus.getName(Session.getLocale);</th>
		<td>%if diplomaticStatus.showAttackTime;%if (contract.getAttackTime-Galaxy.getTime)>0;%("attack allowed in"); <span id="countto-%= i;-%= contract.getAttackTime;">%= Tools.asTime(contract.getAttackTime-Galaxy.getTime);</span>%else;%("attack allowed!");%endif;%endif;</td>
		<td class="r">
			%if isPlayer and contract.partyMayLeave(player);
			<form action="" method="post">
				<input type="hidden" name="do" value="Contract.playerLeaves" />
				<input type="hidden" name="index" value="%= i;" />
				<button type="submit">%("cancel");</button>
			</form>
			%elseif not isPlayer and contract.partyMayLeave(alliance) and player.getRank.hasRight(Right.MAKE_CONTRACTS);
			<form action="" method="post">
				<input type="hidden" name="do" value="Contract.allianceLeaves" />
				<input type="hidden" name="index" value="%= i;" />
				<button type="submit">%("cancel");</button>
			</form>
			%endif;
		</td>
	</tr>
	<tr class="nohover">
		%loop x,member foreach contract.getParties;
		%set notSelf (isPlayer and (member != player)) or (not isPlayer and (member != alliance));
		%if (x > 0) and (x % 2 = 0);</tr><tr>%endif;
		<td class="%= (x % 2 = 0) xor ((x % 4)/2 = 0) ? "even" : "odd";" %if x % 2 = 1;colspan="2"%endif;>%if member.isPlayer;<i>%("player");</i>%else;<i>%("alliance");</i>%endif; %= member.getName;%if notSelf and Galaxy.getDiplomacyEntry(player, member).getStatus.isBetter(contract.getStatus);<span class="mark">&lowast;</span>%endif;</td>
		%endloop;
	</tr>
	<tr class="nohover clear"><td colspan="2" class="clear">&#160;</td></tr>
%endloop;
%endloop;
</table>
%endif;

