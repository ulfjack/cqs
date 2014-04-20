	%set income 0L;
	%set baseIncome Resource.MONEY.getNullRate;
	%if colony.isInvaded; %set baseIncome 0; %endif;
	%set income income+baseIncome;
	%set building Building.TRADE_CENTER;
	%set income income+colony.getBuilding(building)*Resource.MONEY.getRateFactor;
	%set income income*colony.getEfficiencyPercent/100;

	%set upkeep 0L;
	%set upkeep upkeep+colony.getUsedEducationSpaces*Constants.PROFESSOR_UPKEEP;
	%loop building foreach Building.values;
	%set upkeep upkeep+colony.getBuilding(building)*building.upkeepNeeded;
	%endloop;
  %set unitupkeep colony.calculateUnitUpkeep;
	%set upkeep upkeep+unitupkeep;
	%set total income-upkeep;
				<table class="stats">
					<tr><td>%("income: +{0,number,integer}/h", income);</td><td>%("expense: -{0,number,integer}/h", upkeep);</td><td>%("output:"); %if (total < 0);<span class="red">%("{0,number,integer}/h", total);</span>%else;+%("{0,number,integer}/h", total);%endif;</td></tr>
				</table>
