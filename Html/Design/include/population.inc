				%set a colony.getDisplayPopulation;
				%set b colony.getPopulationLimit;
				%set e colony.getEfficiencyPercent;
				<table>
				<colgroup><col width="37%%" /><col width="63%%" /></colgroup>			
					<tr>
						<td>
							<table title="%("population"); [%("available: {0,number,integer}", a);] [%("growth: +{0,number,integer}/h", colony.getPopulationGrowth);] [%("space: {0,number,integer}", colony.getPopulationLimit);] [%("bonus: {0}%", colony.getPopulationGrowthModifierPercent);]">
							<colgroup><col width="40px" /><col width="*" /></colgroup>
								<tr>
									<td colspan="2"><b>%= Tools.getAbbreviatedLong(Session.getLocale, a); %("population");</b><br />%("max {0}", Tools.getAbbreviatedLong(Session.getLocale, colony.getPopulationLimit));</td>
								</tr>
								<tr>
									<td class="nowrap"><img src="%= Session.getLivingSpaceUrl(a, b);" width="5" height="30" alt="" /> <img src="%= Session.getUrl(ImageEnum.SYMBOL_POPULATION);" width="30" height="30" alt="%("population");" /></td>
									<td>
										<i>%("+{0}/h", Tools.getAbbreviatedLong(Session.getLocale, colony.getPopulationGrowth));</i> %if Galaxy.getSpeed > 1;<br />(<i>%("+{0}/h", Tools.getAbbreviatedLong(Session.getLocale, colony.getPopulationGrowth*Galaxy.getSpeed));</i>)%endif;<br />
										%("bonus: {0}%", colony.getPopulationGrowthModifierPercent);
									</td>
								</tr>
							</table>
						</td>
						<td>
							<table title="%("jobs"); [%("administration: {0,number,integer}", colony.getAdministrativeJobs);] [%("construction workers: {0,number,integer}", colony.getConstructionJobs);] [%("employees: {0,number,integer}", colony.getRegularJobs);]">
							<colgroup><col width="40px" /><col width="*" /></colgroup>
								<tr>
									<td colspan="2"><b>%= Tools.getAbbreviatedLong(Session.getLocale, colony.getRegularJobs + colony.getAdministrativeJobs + colony.getConstructionJobs); %("jobs");</b><br />&#160;</td>
								</tr>
								<tr>
									<td class="nowrap"><img src="%= Session.getEfficiencyLevelUrl(colony.getRegularJobs + colony.getAdministrativeJobs + colony.getConstructionJobs, a);" width="5" height="30" alt="" /> <img src="%= Session.getUrl(ImageEnum.SYMBOL_JOBS);" width="30" height="30" alt="%("population");" /></td>
									<td>
										%include "population-details.inc";
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
