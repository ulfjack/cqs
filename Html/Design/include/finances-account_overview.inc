			<table>
				<colgroup width="50%%" span="2" />
				<tr>
					<td class="table">
						%set income player.calculateIncome;
						%set upkeep player.calculateUpkeep;
						<table class="borders">
							<tr>
								<td>%("colony");</td>
								<td class="r">%("output/h");</td>
							</tr>
							%loop tmpc foreach player.getColonies;
							%set cin tmpc.calculateIncome;%set cout tmpc.calculateUpkeep;
							%set income income+cin;
							%set upkeep upkeep+cout;
							<tr>
								<td><a href="colony|%= tmpc.getPosition;">%= tmpc.getPosition;</a></td>
								%set o cin-cout;
								<td class="r">%if o >= 0;+%("{0,number,integer}", o);%else;-%("{0,number,integer}", 0-o);%endif;</td>
							</tr>
							%endloop;
							<tr><td colspan="2">&#160;</td></tr>
							<tr>
								<td>%("all colonies");</td>
								<td class="r">%if income-upkeep >= 0;+%("{0,number,integer}", income-upkeep);%else;%("{0,number,integer}", income-upkeep);%endif;</td>
							</tr>
						</table>
					</td>
					<td class="table" style="padding-left:5px;">
						<table class="borders">
							<tr>
								<td>%("fleet");</td>
								<td class="r">%("costs/h");</td>
							</tr>
							%set cin 0L;%set cout 0L;%set fin 0L;%set fout 0L;
							%loop fleet foreach player.getFleets;
							%set fin fin+fleet.calculateIncome;%set fout fout+fleet.calculateUpkeep;
							%set cin fleet.calculateIncome;%set cout fleet.calculateUpkeep;
							%set income income+cin;
							%set upkeep upkeep+cout;
							<tr>
								<td><a href="fleets-commands|%= fleet.getId;">%("Id {0}", fleet.getId);</a></td>
								%set o cin-cout;
								<td class="r">%if o >= 0;+%("{0,number,integer}", o);%else;-%("{0,number,integer}", 0-o);%endif;</td>
							</tr>
							%endloop;
							<tr><td colspan="2">&#160;</td></tr>
							<tr>
								<td>%("all fleets");</td>
								<td class="r">%if fin-fout >= 0;+%("{0,number,integer}", fin-fout);%else;%("{0,number,integer}", fin-fout);%endif;</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
