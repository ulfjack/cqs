<div>%("You can reduce construction costs by training your workers.");</div>

<table>
<colgroup><col width="50%%" /><col width="50%%" /></colgroup>
	<tr>
		<td class="table">
			<table>
			<colgroup>
				<col width="4%%" /><col width="30%%" />
				<col width="4%%" /><col width="36%%" />
				<col width="4%%" /><col width="22%%" />
			</colgroup>
				<tr>
					<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_EDUCATION);" alt="%("education");" width="32" height="32" /></td>
					<td style="padding-left:5px">%("{0,number,integer} of {1,number,integer} trainers<br />still available", colony.maxRemoveEducationProfs, colony.getEducationSpaces);</td>
					<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_MONEY);" alt="%("costs");" width="32" height="32" /></td>
					<td style="padding-left:5px">
						%("start costs: {0,number,integer}", Constants.PROFESSOR_START);<br />
						%("maintenance: {0,number,integer}/h", Constants.PROFESSOR_UPKEEP);
					</td>
					<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_CLOCK);" alt="%("duration");" width="32" height="32" /></td>
					<td style="padding-left:5px">%set time colony.getCurrentEducationTime-Galaxy.getTime;%if time < 0;%("n/a");%else;%("time left:");<br /><span id="countto-x-%= colony.getCurrentEducationTime;">%= Tools.asTime(time);</span>%endif;</td>
				</tr>
			</table>
		</td>
		<td><img class="tiny" src="%= Session.getUrl(Resource.get(0));" alt="" /> 13%% (30%%): %("The first value shows what percentage of costs are saved with the current level, and the second, bracketed value shows how much could be saved if you reached the maximum level.");</td>
	</tr>
</table>
<table class="stats">
<colgroup><col width="22%%" /><col width="12%%" span="4"/><col width="30%%" /></colgroup>
	<tr>
		<td>%("topic");</td>
		<td colspan="4">%("effect");</td>
		<td>%("level");</td>
	</tr>
	%loop topic foreach Education.values;
	%if player.isKnown(topic);
	<tr class="odd">
		<td><a href="help-education|%= topic;">%= topic.getName(locale);</a></td>
	%if topic != Education.CONSTRUCTION;
		%loop y from 0 to 3;
		<td><img class="tiny" src="%= Session.getUrl(Resource.get(y));" alt="%= Resource.getName(locale,y);" /> %= colony.getEducationModifierPercent(topic, y);%% (%= topic.getModifierPercent(y);%%)</td>
		%endloop;
	%else;
		<td colspan="4">%("saves {0}% of the usual time needed (max {1}%, min build time: {2} sec)", 100-colony.getConstructionModifierPercent, Tools.getBuildingMaxPercent, Constants.BUILDING_MIN);</td>
	%endif;
		<td>Level %= colony.getEducation(topic); (+%= colony.getPendingEducation(topic);)&#160; (<span id="edu-btns-stop-%=topic;"><a href="?Education.stop=%= colony.getPosition;,%= topic;,1" class="%= colony.getPendingEducation(topic)+colony.getEducation(topic) > 0;">-1</a> <a href="?Education.stop=%= colony.getPosition;,%= topic;,2" class="%= colony.getPendingEducation(topic)+colony.getEducation(topic) > 1;">-2</a> <a href="?Education.stop=%= colony.getPosition;,%= topic;,5" class="%= colony.getPendingEducation(topic)+colony.getEducation(topic) > 4;">-5</a> <a href="" class="%= colony.getPendingEducation(topic)+colony.getEducation(topic) > 2;">-N</a></span>) &#160;&#160; <span id="edu-btns-start-%= topic;"><a href="?Education.start=%= colony.getPosition;,%= topic;,1" class="%= colony.maxRemoveEducationProfs > 0;">+1</a> <a href="?Education.start=%= colony.getPosition;,%= topic;,2" class="%= colony.maxRemoveEducationProfs > 1;">+2</a> <a href="?Education.start=%= colony.getPosition;,%= topic;,5" class="%= colony.maxRemoveEducationProfs > 4;">+5</a> <a href="" class="%= colony.maxRemoveEducationProfs > 2;">+N</a></span></td>
	</tr>
	%else;
	<tr class="odd">
		<td><i><a href="help-education|%= topic;">%= topic.getName(locale);</a></i></td>
	%if topic != Education.CONSTRUCTION;
		%loop y from 0 to 3;
		<td><img class="tiny" src="%= Session.getUrl(Resource.get(y));" alt="%= Resource.getName(locale,y);" /> %= colony.getEducationModifierPercent(topic, y);%% (%= topic.getModifierPercent(y);%%)</td>
		%endloop;
	%else;
		<td colspan="4">%("saves {0}% of the usual time needed (max {1}%, min build time: {2} sec)", 100-colony.getConstructionModifierPercent, Tools.getBuildingMaxPercent, Constants.BUILDING_MIN);</td>
	%endif;
		<td>%if topic.getDep != null;
			%("needs:"); <a class="%= player.getResearchLevel(topic.getDep) >= topic.getDepCount;">%= topic.getDepCount; %= topic.getDep.getName(locale);</a>
			%endif;
			%if colony.getEducation(topic)+colony.getPendingEducation(topic) > 0;
				<br /><span id="edu-btns-stop-%=topic;"><a href="?Education.stop=%= colony.getPosition;,%= topic;,1">-1</a></span>
			%endif;
		</td>	
	</tr>
	%endif;
	%endloop;
</table>

