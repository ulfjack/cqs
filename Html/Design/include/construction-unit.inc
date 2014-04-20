	%if colony.isCurrentlyUnit(q);
		%set cu colony.getCurrentUnit(q);
		<a href="colony-ships|%= colony.getPosition;#bql%= q;" onclick="return showUnit('%= colony.getPosition;', %= q;)">
		%loop url foreach Session.getUrls(cu, false);
			<img class="cornericon" src="%= url;" alt="" />
		%endloop;
		</a>
	%else;
		<a href="colony-ships|%= colony.getPosition;#bql%= q;" onclick="return showUnit('%= colony.getPosition;', %= q;)"><img class="cornericon" src="%= Session.getUrl(ImageEnum.JOB_NONE);" alt="" /></a>
	%endif;
	  %define String title;
		%if (q = 0); %set title gettext "planetary units";
		%else; %if (q = 1); %set title gettext "interplanetary units";
		%else; %set title gettext "interstellar units"; %endif; %endif;				
		<h3><a class="titlelink" href="colony-ships|%= colony.getPosition;#bql%= q;" onclick="return showUnit('%= colony.getPosition;', %= q;)">%= title;</a></h3>

%if colony.isCurrentlyUnit(q) or (colony.getUnitQueueSize(q) > 0);
	%if colony.isCurrentlyUnit(q);
		%set unit colony.getCurrentUnit(q);
		<b>%= unit.getName(Session.getLocale);</b><br />
		<span id="countto-%= colony.getPosition;_%= q;-%= colony.getCurrentUnitTime(q);">%= Tools.asTime(colony.getCurrentUnitTime(q)-Galaxy.getTime);</span>
		<a class="false" id="u-abort-%= colony.getPosition;-%= q;" href="?Unit.abort=%= colony.getPosition;,%= q;&amp;t=%= Tools.getRandomLong;#build%= q;">%("cancel");</a><br />
	%else;
		%set unit colony.getUnitQueue(q).peek(0);
		<b>%= unit.getName(Session.getLocale);</b><br />
		<span class="red">%("stopped!");</span>
		<a id="u-resume-%= colony.getPosition;-%= q;" class="%= colony.canBuild(colony.getUnitQueue(q).get);" href="?Unit.resume=%= colony.getPosition;,%= q;&amp;t=%= Tools.getRandomLong;#build%= q;">%("resume");</a><br />
	%endif;
	%set z 0L;
  %set uqueue colony.getUnitQueue(q);
	%loop x from 0 to colony.getUnitQueueSize(q)-1;
		%set z z+uqueue.peekAmount(x);
	%endloop;
	%("{0,number,integer} in queue", z);
%else;
	<br /><br /><span class="red">%("no open tasks");</span>
%endif;
 	<br /><a href="colony-ships|%= colony.getPosition;#bql%= q;" onclick="return showUnit('%= colony.getPosition;', %= q;)">%("edit queue");</a>
