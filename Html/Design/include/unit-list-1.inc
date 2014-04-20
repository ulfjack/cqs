%set slotq slot.isPlanetary ? 0 : (slot.isInterplanetary ? 1 : 2);

%if (slotq = q);
<tbody>
%if colony.mayBuild(slot);
%set possible colony.canBuild(slot);
	<tr>
		<td rowspan="2" class="icon">
			%loop url foreach Session.getUrls(slot, false);
			<div style="position:absolute">
			<img src="%= url;" alt="" />
			</div>
			%endloop;
		</td>
		<td colspan="7">
			<span id="btns-%= q;-%= slot;" class="%= possible;">
				<a href="?Unit.start=%= colony.getPosition;,%= slot;,1&amp;t=%= Tools.getRandomLong;#build%= q;">+1</a>
				<a href="?Unit.start=%= colony.getPosition;,%= slot;,2&amp;t=%= Tools.getRandomLong;#build%= q;">+2</a>
				<a href="?Unit.start=%= colony.getPosition;,%= slot;,5&amp;t=%= Tools.getRandomLong;#build%= q;">+5</a>
				<a href="?action=addUnit&amp;count=N&amp;unit=%= slot;&amp;queue=%= q;">+N</a>
			</span>
			<h3><a href="help-unit|%= slot;" title="%= slot.getName(locale);" id="name-%= slot;">%= slot.getName(locale);</a> (%("{0,number,integer}", colony.getUnits(slot));)</h3>
		</td>
	</tr>
	<tr>
		%set cost colony.getUnitCost(slot);
		%loop y from 0 to 3;
		<td><img src="%= Session.getUrl(Resource.get(y));" alt="" /> %if cost.getResource(y)> colony.getDisplayResource(y);<a class="false">%("{0,number,integer}", cost.getResource(y));</a>%else;%("{0,number,integer}", cost.getResource(y));%endif;</td>
		%endloop;
		<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_CLOCK);" title="%("duration");" alt="%("duration");" /> %= Tools.asTime(cost.getTime);</td>
		<td><img src="%= Session.getUrl(ImageEnum.COST_WORKERS);" title="%("construction workers");" alt="%("construction workers");" /> %if (cost.getConstructionJobs+cost.getAdministrativeJobs+cost.getPopulation) > (colony.getDisplayPopulation-colony.getAdministrativeJobs-colony.getConstructionJobs);<a class="false">%("{0,number,integer}", cost.getConstructionJobs);</a>%else;%("{0,number,integer}", cost.getConstructionJobs);%endif;</td>
		<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_CREW);" title="%("crew");" alt="%("crew");" /> %if (cost.getConstructionJobs+cost.getAdministrativeJobs+cost.getPopulation) > (colony.getDisplayPopulation-colony.getAdministrativeJobs-colony.getConstructionJobs);<a class="false">%("{0,number,integer}", cost.getPopulation);</a>%else;%("{0,number,integer}", cost.getPopulation);%endif;</td>
	</tr>
%else;
	<tr>
		<td rowspan="2" class="icon">
			%loop url foreach Session.getUrls(slot, false);
			<div style="position:absolute">
			<img src="%= url;" alt="" />
			</div>
			%endloop;
		</td>
		<td colspan="8">
			<h3><a href="help-unit|%= slot;" title="%= slot.getName(locale);" id="name-%= slot;">%= slot.getName(locale);</a> (%("{0,number,integer}", colony.getUnits(slot));)</h3>
		</td>
	</tr>
	<tr>
		<td colspan="7">
		%("research dependencies:");
		%set deps slot.getResearchDependencies;	
		%loop x from 0 to deps.size-1;
		%set rtopic deps.peek(x);
		%set level deps.peekAmount(x);
			<a class="%= player.getResearchLevel(rtopic) >= level;">%= level; %= rtopic.getName(locale);</a>%if x < deps.size-1;, %endif;
		%endloop;
		</td>
	</tr>
%endif;
</tbody>
%endif;
