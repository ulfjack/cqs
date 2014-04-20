%if folder.size > 0;
<div class="bg nospacet">
	<br />
	<table class="stats">
	<colgroup><col width="20%%" /><col width="80%%" /></colgroup>
		%loop x,message foreach folder;
		<tr class="%= x % 2 = 0 ? "even" : "odd";">
			<td>
				%if (message.getType = MessageType.FORUM) and (player.getAllianceReadTime <= message.getRealtime);
					<a class="false"><i>NEU</i></a><br />
				%endif;
			
				%set sender message.getSender(Galaxy);
				%("by"); %= sender.getName; %if sender.getAlliance != null;[%= sender.getAlliance.getShortName;]%endif;<br />
				%("on {0}", Tools.asDate(Session.getLocale, message.getRealtime));<br />
			
				%if message.getType = MessageType.MESSAGE;
					%if sender.getAlliance != null;%set temp sender.getAlliance;
					<a href="view-alliance|%= temp.getId;">%("reply to alliance");</a><br />
					%endif;
					<a href="player|%= sender.getPid;">%("reply to player");</a><br />
				%else; %if message.getType = MessageType.APPLICATION;
					%if rank.hasRight(Right.ACCEPT_APPLICATIONS);
						<a href="alliance-reply?box=%= box;&amp;msg=%= message.getId;">%("accept");</a>&#160;&#160;&#160;
						<a href="alliance-deny?box=%= box;&amp;msg=%= message.getId;">%("decline");</a>
					%endif;
				%endif;%endif;
				%if rank.hasRight(Right.DELETE_MESSAGES);
				<a href="?Alliance.Message.delete=%= box;,%= message.getId;&amp;temp0=%= box;">%("delete");</a>
				%endif;
			</td>
			<td>
				%if message.getType = MessageType.CONTRACT_PROPOSAL;
					%set proposal Galaxy.getContractProposal(message.getTypeInfo);
					%if proposal != null;
						<b>%("contract proposal");</b><br />
						%("The following contract is being proposed:");
						<table class="diplomacy">
							<tr><th colspan="2">%= proposal.getType.getName(Session.getLocale);</th></tr>
							<tr>
								<td><b>%("contract partners");</b></td>
								<td><b>%("reply");</b></td>
							</tr>
						%loop x from 0 to proposal.size-1;
							<tr>
								<td>%set party proposal.getParty(x);%= party.getName;</td>
								<td>%if proposal.hasAnswered(x);%("accepted");%else;%("n/a");%endif;</td>
							</tr>
						%endloop;
						%if rank.hasRight(Right.ACCEPT_APPLICATIONS);
							<tr>
								<td>
									<form action="" method="post">
										<input type="hidden" name="do" value="ContractProposal.allianceAccept" />
										<input type="hidden" name="index" value="%= message.getTypeInfo;" />
										<button type="submit">%("accept");</button>
									</form>
								</td>
								<td>
									<form action="" method="post">
										<input type="hidden" name="do" value="ContractProposal.allianceDecline" />
										<input type="hidden" name="index" value="%= message.getTypeInfo;" />
										<button type="submit">%("decline");</button>
									</form>
								</td>
							</tr>
						%endif;
						</table>
					%else;
						%("This contract proposal is not valid anymore. You can delete this message.");
					%endif;
				%elseif message.getType = MessageType.CONTRACT_DECLINED;
					<b>%("contract refused");</b><br />%("A contract proposal was refused.");
				%elseif message.getType = MessageType.CONTRACT_CREATION;
					<b>%("contract concluded");</b><br />%("A contract was concluded.");
				%elseif message.getType = MessageType.WAR_DECLARATION;
					<b>%("war declaration");</b><br />%("Someone has declared war against you.");
				%elseif message.getType = MessageType.WAR_DECLARATION_CANCELLED;
					<b>%("war declaration cancelled");</b><br />%("A war declaration was cancelled.");
				%elseif message.getType = MessageType.CONTRACT_LEFT;
					<b>%("Withdrawal");</b><br />%("A member withdrew from one of your contracts.");
				%else;
					%= message.getText;
				%endif;
			</td>
		</tr>
		%endloop;
	</table>
</div>
%else;
<div class="nobg"><div class="note">%("There are no messages.");</div></div>
%endif;
