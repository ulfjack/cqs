%set timecalc 0L;
<div class="currentjob">
%if colony.isCurrentlyUnit(q);
%set unit colony.getCurrentUnit(q);
%set timecalc timecalc+colony.getCurrentUnitTime(q)-Galaxy.getTime;
	%loop url foreach Session.getUrls(unit, false);
	<img class="icon" src="%= url;" alt="" />
	%endloop;
	
	<div class="text">
	%= unit.getName(locale);<br />
	%("currently built: {0,number,integer}", colony.getUnits(unit));<br />
	<span id="countto-%= q;-%= colony.getCurrentUnitTime(q);">%= Tools.asTime(colony.getCurrentUnitTime(q)-Galaxy.getTime);</span>
	<br />
	<a href="?Unit.abort=%= colony.getPosition;,%= q;&amp;t=%= Tools.getRandomLong;#build%= q;" id="u-abort-%= colony.getPosition;-%= q;">%("cancel");</a>
	</div>
%else;
	<img class="icon" src="%= Session.getUrl(ImageEnum.JOB_NONE);" alt="" />
	<div class="text">
	%("stopped!");<br />
	%if colony.getUnitQueueSize(q) > 0;
	<a class="%= colony.canBuild(uqueue.get);" href="?Unit.resume=%= colony.getPosition;,%= q;&amp;t=%= Tools.getRandomLong;#build%= q;" id="u-resume-%= q;">%("resume");</a>
	%else;
	<a class="false" href="?Unit.resume=%= colony.getPosition;,%= q;&amp;t=%= Tools.getRandomLong;#build%= q;" id="u-resume-%= colony.getPosition;-%= q;">%("resume");</a>
	%endif;
	</div>
%endif;
</div> <!-- currentjob -->

<div class="queuelist">
%set action Request.getParameter("action");
%set aid Tools.safeParseInt(Request.getParameter("aid"),-1); 
%set max colony.getUnitQueueSize(q)-1;
%loop x from 0 to max;
%set unit uqueue.peek(x);
%set number uqueue.peekAmount(x);
%set cost colony.getUnitCost(unit);
%set timecalc timecalc + cost.getTime*number;
<div class="item">
	%loop url foreach Session.getUrls(unit, false);
		<div style="position:absolute">
		<img class="icon" src="%= url;" alt="" />
		</div>
	%endloop;
		<div><img class="icon" src="%= Session.getUrl(ImageEnum.EMPTY_UNIT_SMALL);" alt="" /></div>
	
	%if (action = "modifyUnitEntry") & (aid = x);
<form action="?" method="post">
<div>
	<input type="hidden" name="do" value="Unit.set" />
	<input type="hidden" name="c" value="%= colony.getPosition;" />
	<input type="hidden" name="q" value="%= q;" />
	<input type="hidden" name="index" value="%= max-x;" />
	<input type="hidden" name="prev" value="%= number;" />
	<input type="text" name="count" size="3" value="%= number;" /> %= unit.getName(locale);
	<button type="submit">%("ok");</button>
</div>
</form>
	%else;
	%if number < 0;<i>%= number; x %= unit.getName(locale);</i>%else;%= number; x %= unit.getName(locale);%endif;<br />
	%endif;	
	
	<span class="modifylinks" id="u-modifybtns-%= q;-%= max-x;">
	(<a href="?Unit.modify=%= colony.getPosition;,%= q;,%= max-x;,-1&amp;t=%= Tools.getRandomLong;#build%= q;">-1</a>
	<a href="?Unit.modify=%= colony.getPosition;,%= q;,%= max-x;,-2&amp;t=%= Tools.getRandomLong;#build%= q;">-2</a>
	<a href="?Unit.modify=%= colony.getPosition;,%= q;,%= max-x;,-5&amp;t=%= Tools.getRandomLong;#build%= q;">-5</a>
	<a href="?action=modifyUnitEntry&amp;q=%= q;&amp;aid=%= max-x;">-N</a>)&#160;&#160;
	<a href="?Unit.modify=%= colony.getPosition;,%= q;,%= max-x;,1&amp;t=%= Tools.getRandomLong;#build%= q;">+1</a>
	<a href="?Unit.modify=%= colony.getPosition;,%= q;,%= max-x;,2&amp;t=%= Tools.getRandomLong;#build%= q;">+2</a>
	<a href="?Unit.modify=%= colony.getPosition;,%= q;,%= max-x;,5&amp;t=%= Tools.getRandomLong;#build%= q;">+5</a>
	<a href="?action=modifyUnitEntry&amp;q=%= q;&amp;aid=%= x;">+N</a>&#160;&#160;
	<a href="?Unit.delete=%= colony.getPosition;,%= q;,%= max-x;&amp;t=%= Tools.getRandomLong;#build%= q;">[X]</a>
	</span>
	
	<span class="movelinks" id="u-movebtns-%= q;-%= max-x;">
	<a href="?Unit.move=%= colony.getPosition;,%= q;,%= max-x;,0&amp;t=%= Tools.getRandomLong;#build%= q;"><img src="%= Session.getUrl(ImageEnum.MOVE_TOP);" alt="1." /></a>
	<a href="?Unit.move=%= colony.getPosition;,%= q;,%= max-x;,1&amp;t=%= Tools.getRandomLong;#build%= q;"><img src="%= Session.getUrl(ImageEnum.MOVE_UP);" alt="^" /></a>
	<a href="?Unit.move=%= colony.getPosition;,%= q;,%= max-x;,-1&amp;t=%= Tools.getRandomLong;#build%= q;"><img src="%= Session.getUrl(ImageEnum.MOVE_DOWN);" alt="v" /></a>
	</span>
</div> <!-- item -->
%endloop;

</div> <!-- queuelist -->


<div class="queuelength">
%("approximate queue duration:");
%if colony.isCurrentlyUnit(q);
<span id="countto-tt%= q;-%= Galaxy.getTime+timecalc;">%= Tools.asTime(timecalc);</span>
%else;
<span>%= Tools.asTime(timecalc);</span>
%endif;
</div> <!-- queuelength -->
