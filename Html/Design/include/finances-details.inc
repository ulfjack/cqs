<table>
<colgroup><col width="58%%" /><col width="2%%" /><col width="40%%" /></colgroup>
	<tr>
		<td class="table">
			<h4>%("local income and expenditure");</h4>
			<table class="borders stats">
			%set income 0L;
			<colgroup><col width="26%%" /><col width="30%%" /><col width="22%%" span="2" /></colgroup>
				<tr>
					<td>%("description");</td>
					<td class="r">%("calculation");</td>
					<td class="r">%("income");</td>
					<td class="r">%("expense");</td>
				</tr>
				%set baseIncome Resource.MONEY.getNullRate;
				%if colony.isInvaded; %set baseIncome 0; %endif;
				%set income income+baseIncome;
				<tr class="even">
					<td>%("base income");</td>
					<td class="r">%if (colony.getEfficiencyPercent < 100);<span class="red">%= colony.getEfficiencyPercent;%%</span> &times; %endif;%("{0,number,integer}", baseIncome);</td>
					<td class="r">+%("{0,number,integer}", baseIncome*colony.getEfficiencyPercent/100);</td>
					<td></td>
				</tr>
				%set building Building.TRADE_CENTER;
				%set income income+colony.getBuilding(building)*Resource.MONEY.getRateFactor;
				<tr class="odd">
					<td>%= building.getName(locale);</td>
					<td class="r">%if (colony.getEfficiencyPercent < 100);<span class="red">%= colony.getEfficiencyPercent;%%</span> &times; %endif;%("{0,number,integer}", colony.getBuilding(building)); &times; %("{0,number,integer}", Resource.MONEY.getRateFactor);</td>
					<td class="r">+%("{0,number,integer}", colony.getBuilding(building)*Resource.MONEY.getRateFactor*colony.getEfficiencyPercent/100);</td>
					<td></td>
				</tr>
				%set upkeep 0L;
				%set upkeep upkeep+colony.getUsedEducationSpaces*Constants.PROFESSOR_UPKEEP;
				<tr class="even">
					<td>%("education");</td>
					<td class="r">%= colony.getUsedEducationSpaces; &times; -%("{0,number,integer}", Constants.PROFESSOR_UPKEEP);</td>
					<td></td>
					<td class="r">-%("{0,number,integer}", colony.getUsedEducationSpaces*Constants.PROFESSOR_UPKEEP);</td>
				</tr>
				%set z 1L;
				%loop building foreach Building.values;
				%set upkeep upkeep+colony.getBuilding(building)*building.upkeepNeeded;
				%if colony.getBuilding(building)*building.upkeepNeeded > 0;
				<tr class="%= z % 2 = 0 ? "even" : "odd";">
					<td>%= building.getName(locale);</td>
					<td class="r">%("{0,number,integer}", colony.getBuilding(building)); &times; -%("{0,number,integer}", building.upkeepNeeded);</td>
					<td></td>
					<td class="r">-%("{0,number,integer}", colony.getBuilding(building)*building.upkeepNeeded);</td>
				</tr>
					%set z z+1;
				%endif;
				%endloop;
				%set unitupkeep colony.calculateUnitUpkeep;
				%set upkeep upkeep+unitupkeep;
				<tr class="%= z % 2 = 0 ? "even" : "odd";">
					<td>%("{0,number,integer} units", colony.getUnits.sum);</td>
					<td class="r">-%("{0,number,integer}", unitupkeep);</td>
					<td></td>
					<td class="r">-%("{0,number,integer}", unitupkeep);</td>
				</tr>
				<tr class="odd">
					<th>%("total for {0}", colony.getPosition);</th>
					%set total income*colony.getEfficiencyPercent/100-upkeep;
					<th class="r"></th>
					<th class="r">+%("{0,number,integer}/h", income*colony.getEfficiencyPercent/100);</th>
					<th class="r">-%("{0,number,integer}/h", upkeep);</th>
				</tr>
			</table>
		</td>
		<td></td>
		<td class="table">
			<h4>%("overview of all finances");</h4>
			<table class="borders stats">
				<tr>
					<td>%("output for {0}", colony.getPosition);</td>
					<td class="r">%if (total >= 0);+%("{0,number,integer}/h", total);%else;%("{0,number,integer}/h", total);%endif;</td>
				</tr>
				<tr><td colspan="2">&#160;</td></tr>
				<tr>
					<td>%("output of all {0} colonies", player.getColonies.size);</td>
						%set income player.calculateIncome;
						%set upkeep player.calculateUpkeep;
							%loop tmpc foreach player.getColonies;
							%set cin tmpc.calculateIncome;%set cout tmpc.calculateUpkeep;
							%set income income+cin;
							%set upkeep upkeep+cout;
							%endloop;
					<td class="r">%if income-upkeep >= 0;+%("{0,number,integer}/h", income-upkeep);%else;%("{0,number,integer}/h", income-upkeep);%endif;</td>
				</tr>
				<tr>
					<td>%("costs for {0} fleets", player.getFleets.size);</td>
							%set cin 0L;%set cout 0L;%set fin 0L;%set fout 0L;
							%loop fleet foreach player.getFleets;
							%set fin fin+fleet.calculateIncome;%set fout fout+fleet.calculateUpkeep;
							%set cin fleet.calculateIncome;%set cout fleet.calculateUpkeep;
							%set income income+cin;
							%set upkeep upkeep+cout;
							%endloop;
					<td class="r">%if fin-fout >= 0;+%("{0,number,integer}/h", fin-fout);%else;%("{0,number,integer}/h", fin-fout);%endif;</td>
				</tr>
				<tr>
					<td>%("account total");</td>
					<td class="r">%if income-upkeep >= 0;+%("{0,number,integer}/h", income-upkeep);%else;%("{0,number,integer}/h", income-upkeep);%endif;</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
