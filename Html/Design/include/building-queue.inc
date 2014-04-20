%set timecalc 0L;
%set usedSpaces 0;
%set queue colony.getBuildingQueue;
<div class="currentjob">
%if colony.isCurrentlyBuilding;
%set cb colony.getCurrentBuilding;
%set timecalc colony.getCurrentBuildingTime-Galaxy.getTime;
%set usedSpaces usedSpaces+1;
	<img class="icon" src="%= Session.getUrl(cb, false);" alt="" />
	
	<div class="text"><h3>%if colony.isBuildingRemoval;%("demolition");%endif;
	%= cb.getName(locale);</h3>
	%("currently built: {0,number,integer}", colony.getBuilding(cb));<br />
	<span id="countto-bq-%= colony.getCurrentBuildingTime;">%= Tools.asTime(colony.getCurrentBuildingTime-Galaxy.getTime);</span><br />
	<span id="b-abort-btn"><a href="?Building.abort=%= colony.getPosition;">%("cancel");</a></span>
	</div>
%else;
	<img class="icon" src="%= Session.getUrl(ImageEnum.JOB_NONE);" alt="" />
	
	<div class="text">%("stopped");<br />
	%if colony.getBuildingQueueSize > 0;
	<span id="b-resume-btn"><a class="%= colony.canBuild(queue.get);" href="?Building.resume=%= colony.getPosition;">%("resume");</a></span>
	%else;
	<span id="b-resume-btn"><a class="false" href="?Building.resume=%= colony.getPosition;">%("resume");</a></span>
	%endif;
	</div>
%endif;
</div>

<div class="queuelist">
%set action Request.getParameter("action");
%set aid Tools.safeParseInt(Request.getParameter("aid"),-1); 
%set max colony.getBuildingQueueSize-1;
%loop x from 0 to max;
%set building queue.peek(x);
%set number queue.peekAmount(x);
%if number > 0;
%set timecalc timecalc + colony.getBuildingCostTime(building, usedSpaces, number);%set usedSpaces usedSpaces + number;
%else;
%set timecalc timecalc - colony.getBuildingRemovalCost(building).getTime*number;%set usedSpaces usedSpaces - number;
%endif;
<div class="item">
	<img class="icon" src="%= Session.getUrl(building, false);" alt="" />
	<div class="text">
	%if (action = "modifyBuildingEntry") & (aid = x);
<form action="?" method="post">
<div>
	<input type="hidden" name="do" value="Building.set" />
	<input type="hidden" name="c" value="%= colony.getPosition;" />
	<input type="hidden" name="index" value="%= max-x;" />
	<input type="hidden" name="prev" value="%= number;" />
	<input type="text" name="count" size="3" value="%= number;" /> %= building.getName(locale);
	<button type="submit">%("ok");</button>
</div>
</form>
	%else;
	%if number < 0;<i>%= number; x %= building.getName(locale);</i>%else;%= number; x %= building.getName(locale);%endif;<br />
	%endif;

	<span class="modifylinks" id="b-modifybtns-%= max-x;">
	(<a href="?Building.modify=%= colony.getPosition;,%= max-x;,-1">-1</a>
	<a href="?Building.modify=%= colony.getPosition;,%= max-x;,-2">-2</a>
	<a href="?Building.modify=%= colony.getPosition;,%= max-x;,-5">-5</a>
	<a href="?action=modifyBuildingEntry&amp;aid=%= x;">-N</a>)&#160;&#160;
	<a href="?Building.modify=%= colony.getPosition;,%= max-x;,1">+1</a>
	<a href="?Building.modify=%= colony.getPosition;,%= max-x;,2">+2</a>
	<a href="?Building.modify=%= colony.getPosition;,%= max-x;,5">+5</a>
	<a href="?action=modifyBuildingEntry&amp;aid=%= x;">+N</a>&#160;&#160;
	<a href="?Building.delete=%= colony.getPosition;,%= max-x;">[X]</a>
	</span>
	
	<span class="movelinks" id="b-movebtns-%= max-x;">
	<a href="?Building.move=%= colony.getPosition;,%= max-x;,0"><img src="%= Session.getUrl(ImageEnum.MOVE_TOP);" alt="1." /></a>
	<a href="?Building.move=%= colony.getPosition;,%= max-x;,1"><img src="%= Session.getUrl(ImageEnum.MOVE_UP);" alt="^" /></a>
	<a href="?Building.move=%= colony.getPosition;,%= max-x;,-1"><img src="%= Session.getUrl(ImageEnum.MOVE_DOWN);" alt="v" /></a>
	</span>
	</div>
</div>
%endloop;
</div> <!-- queuelist -->

<div class="queuelength">
%("approximate queue duration:");
%if colony.isCurrentlyBuilding;
<span id="countto-tt-%= Galaxy.getTime+timecalc;">%= Tools.asTime(timecalc);</span>
%else;
<span>%= Tools.asTime(timecalc); %if Galaxy.getSpeed > 1;(%= Tools.asTime(timecalc/Galaxy.getSpeed);)%endif;</span>
%endif;
</div>
