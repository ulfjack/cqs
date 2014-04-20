%set action Request.getParameter("action");
%set tmpCount Request.getParameter("count");
%if (action = "addBuilding") and ((tmpCount = "-N") or (tmpCount = "N"));
%set tmpBuilding Building.valueOf(Request.getParameter("building"));
<form action="?" method="post">
<div>
	<input type="hidden" name="do" value="Colony.addBuilding" />
	<input type="hidden" name="c" value="%= colony.getPosition;" />
	<input type="hidden" name="id" value="%= tmpBuilding;" />
	%if tmpCount = "-N";
	<input type="hidden" name="reverse" value="on" />
	%endif;
</div>
<fieldset class="stdform"><legend>%= tmpBuilding.getName(locale);</legend>
<div>
	<img class="icon" src="%= Session.getUrl(tmpBuilding, false);" alt="" />
	<input type="text" name="count" value="" />
	%if tmpCount = "-N";
	<button type="submit">%("pull down");</button>
	%else;
	<button type="submit">%("build");</button>
	%endif;
</div>
</fieldset>
</form>
%endif;

<div class="jobqueue" id="buildingqueue">
%include "building-queue.inc";
</div>

<table class="joblist">
<colgroup><col width="0" /><col span="8" width="12.5%%" /></colgroup>
	<tbody id="basecost">
	<tr>
		<td rowspan="2" class="icon"><img src="%= Session.getUrl(ImageEnum.BUILDING_LOT);" alt="" /></td>
		<td colspan="8">
			%if (not player.isRestricted);<span id="giveUp" class="false"><a class="possible" href="?Colony.giveUp=%= colony.getPosition;">%("give up colony");</a></span>%endif;
			<h3>%("building lot");</h3>
		</td>
	</tr>
	<tr>
		%loop y from 0 to 3;
		<td><img src="%= Session.getUrl(Resource.get(y));" alt="" /> %if colony.baseBuildCost(y) > colony.getDisplayResource(y);<a class="false">%("{0,number,integer}", colony.baseBuildCost(y));</a>%else;%("{0,number,integer}", colony.baseBuildCost(y));%endif;</td>
		%endloop;
		<td colspan="4"><!--ok--></td>
	</tr>
	</tbody>
	%loop x,building foreach Building.values;
	<tbody>
		<tr>
			<td rowspan="2" class="icon"><img src="%= Session.getUrl(building, false);" alt="" title="%= building.getName(locale); [%("civilization points: {0,number,integer}", building.getPoints);]" /></td>
			<td colspan="8">
			<span id="b-btns-%= x;" class="%= colony.canBuild(building);">
			%if (colony.getBuilding(building) > 0) and (not player.isRestricted);
			(<a class="possible" href="?Building.remove=%= colony.getPosition;,%= building;,1">-1</a>
			<a class="possible" href="?Building.remove=%= colony.getPosition;,%= building;,2">-2</a>
			<a class="possible" href="?Building.remove=%= colony.getPosition;,%= building;,5">-5</a>
	 		<a class="possible" href="?action=addBuilding&amp;count=-N&amp;building=%= building;">-N</a>)
			%endif;
			<a href="?Building.start=%= colony.getPosition;,%= building;,1">+1</a>
			<a href="?Building.start=%= colony.getPosition;,%= building;,2">+2</a>
			<a href="?Building.start=%= colony.getPosition;,%= building;,5">+5</a>
			<a href="?action=addBuilding&amp;count=N&amp;building=%= building;">+N</a>
			</span>
			<h3>(%= colony.getBuilding(building);) <a href="help-building|%= building;" title="%= building.getName(locale); [%("civilization points: {0,number,integer}", building.getPoints);]" id="name-%= x;">%= building.getName(locale);</a></h3>
			</td>
		</tr>
		<tr> %set cost colony.getBuildingCost(building);
			%loop y from 0 to 3;
			<td><img src="%= Session.getUrl(Resource.get(y));" alt="" /> %if (colony.baseBuildCost(y)+cost.getResource(y)) > colony.getDisplayResource(y);<a class="false">%("{0,number,integer}", cost.getResource(y));</a>%else;%("{0,number,integer}", cost.getResource(y));%endif;</td>
			%endloop;
			<td><img src="%= Session.getUrl(ImageEnum.COST_TIME);" title="%("duration");" alt="%("duration");" /> %= Tools.asTime(cost.getTime);</td>
			<td><img src="%= Session.getUrl(ImageEnum.COST_WORKERS);" title="%("construction workers");" alt="%("construction workers");" /> %if (cost.getConstructionJobs+cost.getAdministrativeJobs+cost.getPopulation) > (colony.getDisplayPopulation-colony.getAdministrativeJobs-colony.getConstructionJobs);<a class="false">%("{0,number,integer}", cost.getConstructionJobs);</a>%else;%("{0,number,integer}", cost.getConstructionJobs);%endif;</td>
			<td><img src="%= Session.getUrl(ImageEnum.COST_EMPLOYEES);" title="%("employees");" alt="%("employees");" /> %("{0,number,integer}", cost.getRegularJobs);</td>
			<td><img src="%= Session.getUrl(ImageEnum.COST_UPKEEP);" title="%("upkeep");" alt="%("upkeep");" /> %("{0,number,integer}/h", building.upkeepNeeded);</td>
		</tr>
	</tbody>
	%endloop;
</table><!-- joblist -->

<div class="final"></div>
