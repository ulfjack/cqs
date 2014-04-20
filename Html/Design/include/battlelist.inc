%set t Tools.getRandomLong;
%set player Session.getPlayer;
<table>
	<colgroup width="50%%" span="2" />
%loop x,battle foreach player.getSortedBattles where (((battle.getNextTicTime - Galaxy.getTime) < 0) = blocks) and ((not defenseOnly) or (battle.getOwner = player));
	<tbody>
	<tr class="even">
		<th>
			<a name="battle%= x;" id="battle%= x;" />
			 <a href="%= Session.getMapUrl(battle.position.toPlanetPosition);" %if (battle.getOwner = player) and not blocks;class="false"%endif;>%("{0} at {1}", battle.type.getName(Session.getLocale), battle.position);</a>&#160;&#160;
			<i>%("next battle tick:"); %if (battle.getNextTicTime - Galaxy.getTime) >= 0;<span id="countto-%= x;-%= battle.getNextTicTime;">%= Tools.asTime(battle.getNextTicTime - Galaxy.getTime);</span>%else;<b>%("no battle");</b>%endif;</i>
		</th>
		<td><a href="/BattleReports/%= battle.getId;">%("view temporary battle report");</a></td>
	</tr>
	<tr class="odd">
		%loop side from 0 to 1;
		<td class="table">
			%if battle.getSide(side).size > 0;
			<table class="stats">
				<tr>
					<th colspan="2">%if side = 0;%("attacker");%else;%("defender");%endif; %("({0,number,integer})", battle.getSide(side).size);</th>
				</tr>
				%loop y from 0 to battle.getSide(side).size-1;
				%set entry battle.getSide(side).get(y); %set owner entry.getFleet.getOwner;
				<tr>
					<td colspan="2">
						<b>%if owner = player;<a href="fleets-commands|%= entry.getId;">%= entry.getName;</a>%else;%= entry.getName;%endif; %("from"); <a href="player|%= owner.getPid;">%= owner.getName;</a></b>
						%if owner.getAlliance != null;[%= owner.getAlliance.getShortName;]%endif;
						%if owner = player;
							<form style="display:inline" action="fleets-commands|%= entry.getId;" method="post">
								<input type="hidden" name="do" value="Fleet.withdraw" />
								<input type="hidden" name="fid" value="%= entry.getId;" />
								<input type="hidden" name="bid" value="%= battle.getId;" />
								<button type="submit">%("withdraw");</button>
							</form>
						%endif;
						%if entry.getFleet.isInvading;<i class="a">%("INVASION");</i>%endif;
					</td>
				</tr>
					%loop unit foreach entry.getActiveUnits;
				<tr>
					<td class="r">%("{0,number,integer}", unit.amount);</td>
					<td>%= unit.type.getName(Session.getLocale);</td>
				</tr>
					%endloop;
				%endloop;
			</table>
			%endif;
		</td>
		%endloop;
	</tr>
	<tr class="nohover"><td class="clear" colspan="2"><br /></td></tr>
	</tbody>
	%endloop;
</table>

