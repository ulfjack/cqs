	<table>
	<colgroup><col width="20%%" /><col width="80%%" /></colgroup>
		%loop x from 0 to player.currentEventCount-1;
		<tr>
			<td class="%if player.getEventSeen(x);b%else;bi%endif;">%= Tools.asDate(Session.getLocale, player.getEventTime(x));</td>
			<td>
			%if not player.getEventSeen(x);<span class="red">%= player.getEvent(x).getI18nMessage;</span>%else;%= player.getEvent(x).getI18nMessage;%endif;</td>
		</tr>
		%endloop;
	</table>

