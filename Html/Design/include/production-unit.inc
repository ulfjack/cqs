%set building q = 0 ? Building.MILITARY_BASE : (q = 1 ? Building.SHIPYARD : Building.SPACEPORT);
%("{0}: {1,number,integer}", building.getName(Session.getLocale), colony.getBuilding(building));<br />
%if colony.isCurrentlyUnit(q) or (colony.getUnitQueueSize(q) > 0);
	%if colony.isCurrentlyUnit(q);
		%set unit colony.getCurrentUnit(q);
		<b>%= unit.getName(Session.getLocale);</b><br />
		<span id="countto-%= colony.getPosition;_%= q;-%= colony.getCurrentUnitTime(q);">%= Tools.asTime(colony.getCurrentUnitTime(q)-Galaxy.getTime);</span>
		<a class="false" id="abort-%= colony.getPosition;-%= q+1;" href="stats-production?Unit.abort=%= colony.getPosition;,%= q;">%("cancel");</a><br />
	%else;
		%set unit colony.getUnitQueue(q).peek(0);
		<b>%= unit.getName(Session.getLocale);</b><br />
		<span class="red">%("stopped!");</span>
		<a id="resume-%= colony.getPosition;-%= q+1;" class="%= colony.canBuild(colony.getUnitQueue(q).get);" href="stats-production?c=%= colony.getPosition;&amp;Unit.resume=%= colony.getPosition;,%= q;">%("resume");</a><br />
	%endif;
	%set z 0L;
  %set uqueue colony.getUnitQueue(q);
	%loop x from 0 to colony.getUnitQueueSize(q)-1;
		%set z z+uqueue.peekAmount(x);
	%endloop;
	%("{0,number,integer} in queue", z);
%else;
	<span class="red">%("no open tasks");</span>
%endif;

