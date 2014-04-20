<table class="compact">
	<tr>
		<td>%("admin");</td>
		<td class="r">%= Tools.getAbbreviatedLong(Session.getLocale, colony.getAdministrativeJobs);</td>
		<td class="r">%if (colony.getAdministrativeJobs = 0) or (colony.getDisplayPopulation >= colony.getAdministrativeJobs);100%%%else;<span class="red">%= (100*colony.getDisplayPopulation)/colony.getAdministrativeJobs;%%</span>%endif;</td>
	</tr>
	<tr>
		<td>%("construction");</td>
		<td class="r">%= Tools.getAbbreviatedLong(Session.getLocale, colony.getConstructionJobs);</td>
		<td class="r">%if (colony.getConstructionJobs = 0) or (colony.getDisplayPopulation >= colony.getAdministrativeJobs+colony.getConstructionJobs);100%%%else;<span class="red">%= (100*colony.getDisplayPopulation-100*colony.getAdministrativeJobs)/colony.getConstructionJobs;%%</span>%endif;</td>
	</tr>
	<tr>
		<td>%("production");</td>
		<td class="r">%= Tools.getAbbreviatedLong(Session.getLocale, colony.getRegularJobs);</td>
		<td class="r">%if (colony.getEfficiencyPercent = 100);100%%%else;<span class="red">%= colony.getEfficiencyPercent;%%</span>%endif;</td>
	</tr>
</table>

