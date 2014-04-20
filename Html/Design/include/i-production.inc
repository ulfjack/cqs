<h3>%("construction");</h3>
<table class="info">
<colgroup><col width="12%%" /><col width="22%%" span="4" /></colgroup>
	<tr>
		<th rowspan="2"><img src="%= Session.getUrl(ImageEnum.SYMBOL_JOBS);" height="80" width="80" /></th>
		<th style="padding-left: 5px;border-right:1px solid gray;">%("buildings");</th>
		<th style="padding-left: 5px;border-right:1px solid gray;">%("planetary units");</th>
		<th style="padding-left: 5px;border-right:1px solid gray;">%("interplanetary units");</th>
		<th style="padding-left: 5px;">%("interstellar units");</th>
	</tr>
	<tr>
		<td id="id-%= colony.getPosition;-0" style="padding-left: 5px;border-right:1px solid gray;">
			%include "production-building.inc";
		</td>
		%loop q from 0 to 2;
		<td id="id-%= colony.getPosition;-%= q+1;" style="padding-left: 5px;%if (q < 2);border-right:1px solid gray;%endif;">
			%include "production-unit.inc";
		</td>
		%endloop;
	</tr>
</table>

<script type="text/javascript" src="js/stats-production.js"></script>
