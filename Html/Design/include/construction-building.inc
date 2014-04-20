%set imgsrc Session.getUrl(ImageEnum.JOB_NONE);
%if colony.isCurrentlyBuilding;
	%set cb colony.getCurrentBuilding;
	%set imgsrc Session.getUrl(cb, false);
%endif;
		<a href="colony-build|%= colony.getPosition;" onclick="return showBuild('%= colony.getPosition;')"><img class="cornericon" src="%= imgsrc;" alt="" /></a>
		<h3><a class="titlelink" href="colony-build|%= colony.getPosition;" onclick="return showBuild('%= colony.getPosition;')">%("buildings");</a></h3>

%if colony.isCurrentlyBuilding or (colony.getBuildingQueueSize > 0);
	%if colony.isCurrentlyBuilding;
  	%set current colony.getCurrentBuilding;
		<b>%= current.getName(Session.getLocale);</b><br />
		<span id="countto-%= colony.getPosition;_b-%= colony.getCurrentBuildingTime;">%= Tools.asTime(colony.getCurrentBuildingTime-Galaxy.getTime);</span>
		<a class="false" id="b-abort-%= colony.getPosition;" href="stats-production?Building.abort=%= colony.getPosition;">%("cancel");</a><br />
	%else;
  	%set next colony.getBuildingQueue.peek(0);
		<b>%= next.getName(Session.getLocale);</b><br />
		<span class="red">%("stopped!");</span>
		<a id="b-resume-%= colony.getPosition;" class="%= colony.canBuild(colony.getBuildingQueue.get);" href="stats-production?Building.resume=%= colony.getPosition;">%("resume");</a><br />
	%endif;
	%set a 0L;
	%loop x from 0 to colony.getBuildingQueueSize-1;
		%set a a+colony.getBuildingQueue.peekAmount(x);
	%endloop;
	%("{0,number,integer} in queue", a);
%else;
	<br /><br />
	<span class="red">%("no open tasks");</span>
%endif;
	<br /><a href="colony-build|%= colony.getPosition;" onclick="return showBuild('%= colony.getPosition;')">%("edit queue");</a>
