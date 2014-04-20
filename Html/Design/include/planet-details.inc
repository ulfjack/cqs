				<table class="stats">
					<colgroup width="20%%" span="5" />
					<tr><td colspan="2">%("planet type");</td><td colspan="3">%= planet.getType.getName(Session.getLocale);</td></tr>
					<tr><td colspan="2">%("size");</td><td colspan="3">%("{0,number,integer}", planet.getDisplaySize);</td></tr>
					<tr><td colspan="2">%("building density");</td><td colspan="3">%("{0,number,integer} buildings", planet.getUsed);</td></tr>
					%if planet.findColony(player) != null;
					<!-- show resource details -->
					<tr>
						%loop x from 0 to 3;
						<td><img src="%= Session.getUrl(Resource.get(x));" alt="" width="16" height="16" /> %= planet.getModifier(x);%%</td>
						%endloop;
						<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_PEOPLE);" alt="" width="16" height="16" /> %= planet.getPopulationGrowthModifierPercent;%%</td>
					</tr>
					%endif;
					%if planet.isBlocked;
					%set owner planet.getBlockadePlayer;
					<tr class="nohover"><td colspan="5"><i>%("This planet is being blocked by {0} with {1,number,integer} units.", Tools.playerLink(owner), planet.getBlockadeUnitCount);</i></td></tr>
					%endif;
				</table>
