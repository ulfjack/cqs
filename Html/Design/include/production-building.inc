%("{0}: {1,number,integer}", Education.CONSTRUCTION.getName(Session.getLocale), colony.getEducation(Education.CONSTRUCTION));<br />
%if colony.isCurrentlyBuilding or (colony.getBuildingQueueSize > 0);
	%if colony.isCurrentlyBuilding;
  	%set current colony.getCurrentBuilding;
		<b>%= current.getName(Session.getLocale);</b><br />
		<span id="countto-%= colony.getPosition;_b-%= colony.getCurrentBuildingTime;">%= Tools.asTime(colony.getCurrentBuildingTime-Galaxy.getTime);</span>
		<a class="false" id="abort-%= colony.getPosition;-0" href="stats-production?Building.abort=%= colony.getPosition;">%("cancel");</a><br />
	%else;
  	%set next colony.getBuildingQueue.peek(0);
		<b>%= next.getName(Session.getLocale);</b><br />
		<span class="red">%("stopped!");</span>
		<a id="resume-%= colony.getPosition;-0" class="%= colony.canBuild(colony.getBuildingQueue.get);" href="stats-production?Building.resume=%= colony.getPosition;">%("resume");</a><br />
	%endif;
	%set a 0L;
	%loop x from 0 to colony.getBuildingQueueSize-1;
		%set a a+colony.getBuildingQueue.peekAmount(x);
	%endloop;
	%("{0,number,integer} in queue", a);
%else;
	<span class="red">%("no open tasks");</span>
%endif;
