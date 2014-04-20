%set timecalc 0L;
%set queue player.getResearchQueue;
<div class="currentjob">
%if player.isResearching;
%set topic player.getCurrentResearch;
%set timecalc player.getCurrentResearchTime-Galaxy.getTime;
	<img class="icon" src="%= Session.getUrl(topic, false);" width="80" height="80" alt="" />
	
	<div class="text">
		<b>%= topic.getName(Session.getLocale);</b><br />
		%("available: {0,number,integer}", player.getResearchLevel(topic));<br />
		<span id="countto-rq-%= player.getCurrentResearchTime;">%= Tools.asTime(player.getCurrentResearchTime-Galaxy.getTime);</span><br />
		<a id="abort-btn" href="research?Research.abort=do">%("cancel");</a>
	</div>
%else;
	<img class="icon" src="%= Session.getUrl(ImageEnum.JOB_NONE);" width="80" height="80" alt="" />
	<div class="text">
	%("no current research");<br />
  %if (queue.size > 0);
  <a id="resume-btn" class="%= player.mayResearch(queue.peek(0));" href="research?Research.resume=do">%("resume");</a>
	%else;
	<a id="resume-btn" class="false" href="research?Research.resume=do">%("resume");</a>
	%endif;
	</div>
%endif;
</div>

<div class="queuelist">
%set max queue.size-1;
%loop x,entry foreach queue;
%set timecalc timecalc + entry.amount*entry.type.timeNeeded(player);
<div class="item">
	<img class="icon" src="%= Session.getUrl(entry.type, false);" alt="" />
	
	%= entry.amount; x %= entry.type.getName(Session.getLocale); <br />
	
	<span class="modifylinks" id="modifybtns-%= max-x;">
	<a href="">-N</a>
	<a href="?Research.modify=%= max-x;,-1">-1</a>&#160;&#160;
	<a href="?Research.modify=%= max-x;,1">+1</a>
	<a href="">+N</a>&#160;&#160;
	<a href="?Research.delete=%= max-x;">[X]</a>
	</span>
	
	<span class="movelinks" id="movebtns-%= max-x;">
	<a href="?Research.moveTop=%= max-x;"><img src="%= Session.getUrl(ImageEnum.MOVE_TOP);" alt="1." /></a>
	<a href="?Research.moveUp=%= max-x;"><img src="%= Session.getUrl(ImageEnum.MOVE_UP);" alt="^" /></a>
	<a href="?Research.moveDown=%= max-x;"><img src="%= Session.getUrl(ImageEnum.MOVE_DOWN);" alt="v" /></a>
	</span>
</div>
%endloop;
</div> <!-- queuelist -->

<div class="queuelength">
%("approximate queue duration:");<br />
%if player.isResearching;
<span id="countto-tt-%= Galaxy.getTime+timecalc;">%= Tools.asTime(timecalc);</span>
%else;
<span>%= Tools.asTime(timecalc); %if Galaxy.getSpeed > 1;(%= Tools.asTime(timecalc/Galaxy.getSpeed);)%endif;</span>
%endif;
</div>
