		<div class="bg">
			<h3>%("status for &ldquo;{0}&rdquo;", fleet.getName); (<a href="fleets-commands|%= fleet.getId;">%("update");</a>)</h3>
			<table class="stats">
				<tr>
					<td>%("last known position"); <b><a href="%= Session.getMapUrl(fleet.getPosition);">%= fleet.getPosition; %if fleet.isLanded;%("ground");%else;%("space");%endif;</a></b><br />
							%if fleet.isFighting;
								<b class="fighting">%= fleet.getExecutingCommand.getName(Session.getLocale);</b>
								%if fleet.getNextCompleteTime-Galaxy.getTime > 0;
								<div id="countto-x-%= fleet.getNextCompleteTime;">%= Tools.asTime(fleet.getNextCompleteTime-Galaxy.getTime);</div>
								%endif;
							%else;
								%if fleet.isStopped;
									<i class="a">%("Fleet stopped!");</i> &ndash;
									%if fleet.getNextCommand >= fleet.getOrders.size;
									<b>%("Please select an order!");</b>
									%else;
									<a class="true" href="fleets-commands|%= fleet.getId;?Fleet.resume=%= fleet.getId;">%("proceed");</a><br />
									%endif;
								%else;
									<b class="acting">%= fleet.getExecutingCommand.getName(Session.getLocale);</b><br />
									<span id="countto-x-%= fleet.getNextCompleteTime;">%= Tools.asTime(fleet.getNextCompleteTime-Galaxy.getTime);</span>
									%if fleet.getExecutingCommand.mayAbort(fleet);
									<a class="false" href="fleets-commands|%= fleet.getId;?Fleet.stop=%= fleet.getId;">%("cancel");</a><br />
									%endif;
								%endif;
							%endif;
					</td>
				</tr>
				%if fleet.isError;
				<tr>
					<td><span class="red">%= fleet.getErrorMessage;</span></td>
				</tr>
				%endif;
			</table>
		</div>

		<!-- delete multiple commands -->
		<div class="bg" id="deleteorders">
			<img class="cornericon" src="pack/design/x.png" alt="" />
			<h3>%("remove multiple commands");</h3>
			<form class="hover" id="delAll" action="fleets-commands|%= fleet.getId;" method="post" style="display:inline">
				<input type="hidden" name="do" value="Fleet.Commands.delete" />
				<input type="hidden" name="id" value="%= fleet.getId;" />
				<input type="hidden" name="action" id="all" value="all" />
				<button type="submit">%("all");</button>
			</form>
			<form class="hover" id="delBeforeCurrent" action="fleets-commands|%= fleet.getId;" method="post" style="display:inline">
				<input type="hidden" name="do" value="Fleet.Commands.delete" />
				<input type="hidden" name="id" value="%= fleet.getId;" />
				<input type="hidden" name="action" id="beforeCurrent" value="beforeCurrent" />
				<button type="submit">%("before current");</button>
			</form>
			<form class="hover" id="delAfterCurrent" action="fleets-commands|%= fleet.getId;" method="post" style="display:inline">
				<input type="hidden" name="do" value="Fleet.Commands.delete" />
				<input type="hidden" name="id" value="%= fleet.getId;" />
				<input type="hidden" name="action" id="afterCurrent" value="afterCurrent" />
				<button type="submit">%("after current");</button>
			</form>
		</div>

		<div class="bg">
			<img class="cornericon" src="pack/design/orders.png" alt="" />
				<!-- loop/unloop -->
				<form id="loopcommands" action="fleets-commands|%= fleet.getId;" method="post" class="right">
				%if fleet.isLoopEnabled;
					<input type="hidden" name="do" value="Fleet.unloop" />
					<input type="hidden" name="id" value="%= fleet.getId;" />
					<input id="deactivate-loop" type="image" src="pack/design/loop.png" title="%("click to deactivate");" /> <label title="%("click to deactivate");" for="deactivate-loop">%("loop modus active");</label>
				%else;
					<input type="hidden" name="do" value="Fleet.loop" />
					<input type="hidden" name="id" value="%= fleet.getId;" />
					<input id="activate-loop" type="image" src="pack/design/unloop.png" title="%("click to activate");" /> <label for="activate-loop" title="%("click to activate");">%("loop modus inactive");</label>
				%endif;
				</form>
			<h3>%("orders");</h3>
			%set orders fleet.getOrders;
			%set max orders.size;
			<table class="stats" id="modifyorders">
			<colgroup><col width="5%%" /><col width="10%%" /><col width="70%%" /><col width="10%%" /><col width="5%%" /></colgroup>
				%loop y,command foreach orders;
				<tr class="%= y%2=0 ? "even" : "odd";">
				  %define String style;
					%if command.check(fleet) = -1;%set style "false";
					%elseif command.check(fleet) = 0;%set style "none";
					%else; %set style "true";%endif;
					%if (command = fleet.getExecutingCommand);
						%set style "current";
					%elseif (fleet.getNextCommand = y);
						%set style "next";
					%endif;
					<td class="%= style;">
						%if y > 0;<a id="moveupcmd-%= fleet.getId;-%= y;" href="fleets-commands|%= fleet.getId;?Command.moveUp=%= fleet.getId;,%= y;"><img src="%= Session.getUrl(ImageEnum.MOVE_UP);" alt="" /></a>%endif;<br />
						%if y < max-1;<a id="movedowncmd-%= fleet.getId;-%= y;" href="fleets-commands|%= fleet.getId;?Command.moveDown=%= fleet.getId;,%= y;"><img src="%= Session.getUrl(ImageEnum.MOVE_DOWN);" alt="" /></a>%else;<br />%endif;
					</td>
					<td class="%= style;">
						<a id="switchcmd-%= fleet.getId;-%= y;" href="fleets-commands|%= fleet.getId;?Command.switch=%= fleet.getId;,%= y;">%if (y = fleet.getNextCommand);-%else;+%endif;</a>
					</td>
					<td class="%= style;" id="name-%= fleet.getId;-%= y;">
						<b>%= command.getName(Session.getLocale);</b>
					</td>
					<td class="%= style;">
						<a id="delcmd-%=fleet.getId;-%= y;"href="fleets-commands|%= fleet.getId;?Command.remove=%= fleet.getId;,%= y;"><img src="%= Session.getUrl(ImageEnum.CLOSE);" width="16" height="16" alt="x" /></a>
					</td>
					<td class="%= style;" id="editcmd-%= fleet.getId;-%= y;-%= command.getEditorType;">
						&#160;
					</td>
				</tr>
				%endloop;
			</table>
		</div>
