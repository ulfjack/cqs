<div class="content colony">

<div class="queue">

<div class="current">
%if colony.CurrentBuilding.running or (colony.BuildingQueue.amount > 0);
	%if colony.CurrentBuilding.running;
		<img src="%string Session.GP.buildings;%string
		colony.CurrentBuilding.short;-small.png" alt="" />
		<div>%if colony.CurrentBuilding.id < 0;Abriss%endif;
		%string colony.CurrentBuilding.name(lang);<br />
		%gettext "Aktuell vorhanden:"; %long colony.CurrentBuilding.level;
		<div id="timer-0-%long colony.CurrentBuilding.time-System.time;">%time colony.CurrentBuilding.time-System.time;</div>
		<a href="colony-build?Building.abort=do">%gettext "Abbrechen";</a>
		</div>
	%else;
		%gettext "Gestoppt!";<br />
		<a class="%bool colony.Buildings.possible(colony.BuildingQueue.data(0));" href="colony-build?Building.resume=do">%gettext "Fortsetzen";</a>
	%endif;
%else;
	%gettext "Keine Bauaktivität";
%endif;
</div>

%set max colony.BuildingQueue.amount-1;
%if colony.BuildingQueue.amount > 0;
<div class="list">

%set timecalc 0L;%if colony.CurrentBuilding.running;%set timecalc colony.CurrentBuilding.time-System.time;%endif;
%loop x from 0 to max;
<div class="item">
	%set building colony.BuildingQueue.data(x);
	%set number colony.BuildingQueue.number(x);
	
	<img class="icon" src="%string Session.GP.buildings;%string building.short;-small.png" alt="" />
	
	%if number < 0;<i />%endif;
	%long number; x %string building.name(lang);
	
	<br />
	
	<span class="modifylinks">
	<a href="?Building.delete=%long max-x;">[X]</a>&#160;&#160;
	<a href="javascript:reduceN('colony-build?Building.modify=%long max-x;',0)">[-N]</a>
	<a href="?Building.modify=%long max-x;,-1">[-1]</a>&#160;&#160;
	<a href="?Building.modify=%long max-x;,1">[+1]</a>
	<a href="javascript:addN('?Building.modify=%long max-x;',0)">[+N]</a>
	</span>
	
	<span class="movelinks">
	<a class="%bool x > 0;" href="?Building.moveTop=%long max-x;"><img src="%string Session.GP.design;atop.gif" alt="move to top"/></a>
	<a class="%bool x > 0;" href="?Building.moveUp=%long max-x;"><img src="%string Session.GP.design;aup.gif" alt="move up" /></a>
	<a class="%bool x < max;" href="?Building.moveDown=%long max-x;"><img src="%string Session.GP.design;adown.gif" alt="move down" /></a>
	</span>
	
	%if number > 0;%set timecalc timecalc + colony.Buildings.time(building)*number;%else;%set timecalc timecalc - colony.Buildings.time(building)*number/2;%endif;
</div>
%endloop;

<div class="length">
%gettext "Ungefähre Schleifenlänge";:
<div id="timer-1-%long timecalc;">%time timecalc;</div>
</div>

</div>
%endif;

</div>


<div class="queue-list">
%loop x from 0 to Unit.Slots.amount-1;
%set unit Unit.Slots.data(x);
%if unit.class = q;
%if colony.Units.allowed(unit);
<div class="item">
	<img class="icon" src="%string Session.GP.units;???.png" alt="" />
	<div class="start %bool colony.Units.possible(unit);">
		<a href="?Unit.startSlot=%long x;">+1</a>
		<a href="?Unit.startSlot=%long x;,2">+2</a>
		<a href="?Unit.startSlot=%long x;,5">+5</a>
		<a href="javascript:buildN('?Unit.startSlot=%long x;',%long x;)">+N</a>
	</div>
	<div>
		<h3><a href="help-unit?temp0=%string unit.id;">%string unit.name(lang);</a> (%long colony.Units.amountOf(unit);)</h3>
		<ul>
			%loop y from 0 to 3;
			<li><img src="%string Session.GP.design;res%long y+1;.gif" alt="" /> %format colony.Units.needs(unit, y);</li>
			%endloop;
		</ul>
		<ul>
			<li><img src="%string Session.GP.design;resymb3.gif" alt="time" /> %time colony.Units.time(unit);</li>
			<li><img src="%string Session.GP.design;b1.gif" alt="workers" /> %format colony.Units.workers(unit);</li>
			<li><img src="%string Session.GP.design;b3.gif" alt="employees" /> %format colony.Units.people(unit);</li>
		</ul>
	</div>
</div>
%else;
<div class="item">
	<img class="icon" src="%string Session.GP.units;???.png" alt="" />
	<div class="start">
		<a href="?Unit.startSlot=%long x;">+1</a>
		<a href="javascript:buildN('?Unit.startSlot=%long x;',%long x;)">+N</a>
	</div>
	<div>
		<h3><a href="help-unit?temp0=%string unit.id;">%string unit.name(lang);</a> (%long colony.Units.amountOf(unit);)</h3>
		
	</div>
</div>
%endif;
%endif;
%endloop;

<div class="final"></div>
</div>

</div>

<div class="footnote">
</div>
