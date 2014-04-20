<table>
<colgroup><col width="320px" /><col width="*" /></colgroup>
<tr>
	<td class="image">
		%loop url foreach Session.getUrls(unit, true);
		<div style="position:absolute"><img src="%= url;" alt=""/></div>
		%endloop;
	</td>

	<td class="table">
		<table class="stats">
		<colgroup width="25%%" span="4" />
			%set class unit.unitClass;
			<tr class="nohover"><th colspan="4">%("class: {0}", class.getName(Session.getLocale));</th></tr>
			<tr class="nohover"><td colspan="4">%= class.getDescription(Session.getLocale);</td></tr>
			<tr class="nohover"><th colspan="4">%("design");</th></tr>
			<tr class="odd">
				<td colspan="2">%("type");</td>
				<td colspan="2">
					%if unit.isPlanetary;%("planetary unit");
					%elseif unit.isInterplanetary;%("interplanetary unit");
					%else; %("interstellar unit");%endif;
				</td>
			</tr>
			<tr class="odd">
				<td colspan="2">%("modules");</td>
				<td colspan="2">
					%if unit.getUnitModules.length > 0;
						%loop module foreach unit.getUnitModules;
						%= module.getName(Session.getLocale);<br />
						%endloop;
					%else;
						%("no modules");
					%endif;
				</td>
			</tr>
			<tr class="nohover"><th colspan="4">%("characteristics");</th></tr>
			<tr class="odd">
				<td colspan="2">%("unit size");</td>
				<td colspan="2">%("{0,number,integer} units", unit.getSize);</td>
			</tr>
			<tr class="odd">
				<td colspan="2">%("points for {0} units", Constants.SCORE_MODIFIER_INVERSE);</td>
				<td colspan="2">%= unit.getScore; %("points");</td>
			</tr>
			%if unit.getResourceCapacity > 0;
			<tr class="odd">
				<td colspan="2">%("resource capacity");</td>
				<td colspan="2">%("{0,number,integer} units", unit.getResourceCapacity);</td>
			</tr>
			%endif;
			%if unit.getGroundUnitCapacity > 0;
			<tr class="odd">
				<td colspan="2">%("ground-troop capacity");</td>
				<td colspan="2">%("{0,number,integer}", unit.getGroundUnitCapacity);</td>
			</tr>
			%endif;
			%if unit.getSpaceUnitCapacity > 0;
			<tr class="odd">
				<td colspan="2">%("spaceship capacity");</td>
				<td colspan="2">%("{0,number,integer} units", unit.getSpaceUnitCapacity);</td>
			</tr>
			%endif;
			<tr class="nohover"><th colspan="4">%("fight values");</th></tr>
			<tr class="odd">
				%set count 0;
				%set attack unit.getFightSpec;			
				%loop class foreach Galaxy.getUnitSystem.getUnitClasses;
				%if (count < 3) and (class.isPlanetary = unit.isPlanetary);
				<td>%= class.getName(Session.getLocale);: %("{0,number,integer}", Tools.floatToLong(attack.getAttack(count)));</td>
				%set count count+1;
				%endif;
				%endloop;
				<td>%("defense: {0,number,integer}", Tools.floatToLong(unit.getDefense));</td>
			</tr>
			<tr class="nohover"><th colspan="4">%("costs");</th></tr>
			%set cost unit.getCost;			
			<tr class="odd">
				%loop y from 0 to 3;
				<td class="l">%if cost.getResource(y) > 0;<img src="%= Session.getUrl(Resource.get(y));" height="16" width="16" alt="" /> %("{0,number,integer}", cost.getResource(y));%endif;</td>
				%endloop;
			</tr>
			<tr class="odd">
				<td class="l"><img src="%= Session.getUrl(ImageEnum.COST_WORKERS);" title="%("construction workers");" alt="%("construction workers");" width="16" height="16" /> %("{0,number,integer}", cost.getConstructionJobs);</td>
				<td class="l"><img src="%= Session.getUrl(ImageEnum.SYMBOL_CREW);" title="%("crew");" alt="%("crew");" width="16" height="16" /> %("{0,number,integer}", cost.getPopulation);</td>
				<td class="l"><img src="%= Session.getUrl(ImageEnum.SYMBOL_CLOCK);" title="%("construction time");" alt="%("construction time");" width="16" height="16" /> %= Tools.asTime(cost.getTime);</td>
				<td ><img src="%= Session.getUrl(ImageEnum.COST_UPKEEP);" title="%("upkeep");" alt="%("upkeep");" width="16" height="16" /> %("~{0,number,integer}/h",  unit.getUpkeep);</td>
			</tr>
		</table>
	</td>
</tr>
</table>
