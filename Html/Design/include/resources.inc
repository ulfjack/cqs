<table>
	<colgroup><col width="16%%" span="6" /></colgroup>
	<tr>
		%loop x from 0 to 3;
		<td class="restable" title="%("{0} [available: {1,number,integer}] [growth: +{2,number,integer}/h] [depot: {3,number,integer}] [bonus: {4}%]", Resource.getName(locale,x), colony.getDisplayResource(x), colony.getRate(x), colony.getStorage(x), colony.getModifier(x));">
			<table>
				<colgroup><col width="40px" /><col width="*" /></colgroup>
				<tr>
					<td>
						<img src="%= Session.getFillLevelUrl(colony.getDisplayResource(x), colony.getStorage(x));" width="5" height="30" alt="" />
						<img src="%= Session.getUrl(Resource.get(x));" width="30" height="30" alt="%("{0} [available: {1,number,integer}] [growth: +{2,number,integer}/h] [depot: {3,number,integer}] [bonus: {4}%]", Resource.getName(locale,x), colony.getDisplayResource(x), colony.getRate(x), colony.getStorage(x), colony.getModifier(x));" />
					</td>
					<td>
						<b id="res_%= x;">%("{0,number,integer}", colony.getDisplayResource(x));</b><br />
						<i>+%("{0,number,integer}/h", colony.getRate(x));</i>
						%if Galaxy.getSpeed > 1;<br />(+<i>%("{0,number,integer}/h", Galaxy.getSpeed*colony.getRate(x));</i>)%endif;
					</td>
				</tr>
			</table>
		</td>
		%endloop;
		%set a colony.getDisplayPopulation;
		%set b colony.getPopulationLimit;
		<td class="restable" title="%("population"); [%("available: {0,number,integer}", a);] [%("growth: +{0,number,integer}/h", colony.getPopulationGrowth);] [%("depot: {0,number,integer}", b);] [%("bonus: {0}%", colony.getPopulationGrowthModifierPercent);]">
			<table>
				<colgroup><col width="40px" /><col width="*" /></colgroup>
				<tr>
					<td>
						<img src="%= Session.getLivingSpaceUrl(a, b);" width="5" height="30" alt="" />
						<img src="%= Session.getUrl(ImageEnum.SYMBOL_POPULATION);" width="30" height="30" alt="%("population");" />
					</td>
					<td>
						%("{0,number,integer}", a);<br />
						<i>+%("{0,number,integer}/h", colony.getPopulationGrowth);</i>
						%if Galaxy.getSpeed > 1;<br />(+<i>%("{0,number,integer}/h", Galaxy.getSpeed*colony.getPopulationGrowth);</i>)%endif;
					</td>
				</tr>
			</table>
		</td>
		%set e colony.getEfficiencyPercent;
		<td class="restable-last" title="%("construction workers: {0,number,integer}", colony.getConstructionJobs);, %("employees: {0,number,integer}", colony.getRegularJobs);, %("administration: {0,number,integer}", colony.getAdministrativeJobs);">
			<table>
				<colgroup><col width="40px" /><col width="*" /></colgroup>
				<tr>
					<td>
						<img src="%= Session.getEfficiencyLevelUrl(colony.getRegularJobs + colony.getAdministrativeJobs + colony.getConstructionJobs, a);" width="5" height="30" alt="" />
						<img src="%= Session.getUrl(ImageEnum.SYMBOL_JOBS);" width="30" height="30" alt="%("population");" />
					</td>
					<td>
						%("{0,number,integer}", colony.getRegularJobs + colony.getAdministrativeJobs + colony.getConstructionJobs);<br />	
						%if e < 100;<span class="red">%= e;%%</span>%else;%= e;%%%endif; %("filled");
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

