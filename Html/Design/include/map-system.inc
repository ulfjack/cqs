%set player Session.getPlayer;
%set galaxyView Session.getGalaxyView;
%set sun position.findSystem(Galaxy);
%if Session.maySeeMap and (sun != null) and galaxyView.isKnown(position.findSystem(Galaxy));

<div class="nobglr">
<div class="right">
	<form action="javascript:jump()">
		<input type="text" size="10" id="jpos" />
		<button type="submit">%("jump to position");</button>
	</form>
	<script type="text/javascript" src="js/map.js"></script>
</div>
<h1>%("system {0}", sun.getPosition.toString);</h1>
</div>
<table>
	<colgroup><col width="450px" /><col width="*" /></colgroup>
	<tr>
		<td>
			<div class="nobgtb">
				<!-- added dirty fix: multiplied top and left by 5/6, then subtracted 25; image rescaled by 5/6, then 25px cut off each side -->
				<!-- invisible map element to store planet information -->
				<map id="planets" name="planets">
					%loop planet foreach sun.getPlanets;<area href="?" alt="%= planet.getSpeed;,%= (95*planet.getRadius)/100;,%= planet.getSize/200+10;,%= planet.getTheta;" />%endloop;
				</map>

				<!-- invisible map element to store fleet information -->
				%if player.canSeeSystem(sun.getPosition) and (sun.getFleets.size > 0);
				<map id="fleets" name="fleets">
					%loop fleet foreach sun.getFleets where not fleet.isPlanetaryFleet;
					<area href="?" alt="13,%= fleet.getNextEventTime;,%= fleet.getDuration;,%= fleet.getCurrentRotation.getSpeed;,%= fleet.getCurrentRotation.getRadius;,%= fleet.getCurrentRotation.getTheta;,%= fleet.getRotationGoal.getSpeed;,%= fleet.getRotationGoal.getRadius;,%= fleet.getRotationGoal.getTheta;" />
					%endloop;
				</map>
				%endif;

				%set time Galaxy.getTime;

				<div class="sys" style="background-image:url(%= Session.getUrl(ImageEnum.SYSTEM_BACKGROUND););">
					<div id="s0" style="top:206px; left:206px; width:40px; height:40px; z-index:0; background-image:url(%= Session.getUrl(ImageEnum.SUN);)"></div>
	
					%set x 0;
					%loop planet foreach sun.getPlanets;
					<div id="p%= x;" style="left:%= 5*(planet.longX(time)+300-(planet.getSize/200+10)/2)/6-25;px; top:%= 5*(planet.longY(time)+300-(planet.getSize/200+10)/2)/6-25;px; z-index:%= x;">
						<a href="%= Session.getMapUrl(planet.getPosition);">
						<img src="%= Session.getUrl(planet.getType);" width="%= 5*(planet.getSize/200+10)/6;" height="%= 5*(planet.getSize/200+10)/6;" title="Typ: %= planet.getType.getName(Session.getLocale);%if player.canSeeSystem(sun.getPosition); - %("building lots: {0,number,integer}", planet.getDisplaySize); - %("buildings: {0,number,integer}", planet.getUsed);%endif;" alt="" />
						</a>
					</div>%set x x+1;
					%endloop;
	
				%if player.canSeeSystem(sun.getPosition);
					%set x 0;
					%loop fleet foreach sun.getFleets where not fleet.isPlanetaryFleet;
					%set owner fleet.getOwner;
					<div id="q%= x;" style="left:%= 5*(fleet.longX(time)+300-16)/6-25;px; top:%= 5*(fleet.longY(time)+300-8)/6-25;px; width:13px; height:13px; z-index:%= x+100;; background:url(%= Session.getUrl(ImageEnum.FLEET);)" title="%("owner: {0}", owner.getName); - %("position: {0}", fleet.getPosition); - %("size: {0,number,integer}", fleet.getUnits.sum);"></div>
					%set x x+1;
					%endloop;
				%endif;
				</div>
			</div>
		</td>
		<td>
			<div class="bg">
				<h3>%("planets");</h3>
				<table class="stats">
					<tr class="nohover">
						<th></th>
						<th></th>
						<th class="r">%("size");</th>
						<th>%("blockade");</th>
						<th>%("fleet");</th>
					</tr>
					%loop x,planet foreach sun.getPlanets;%set pos planet.getPosition;
					<tr class="%= x%2=0 ? "even" : "odd";">
						%set diplomaticStatus planet.diplomaticStatus(player);
						<td class="%= diplomaticStatus.name; c" title="%= diplomaticStatus.getName(Session.getLocale);">%= diplomaticStatus.englishAbbreviation;</td>
		
						<td>
							<a class="a" href="%= Session.getMapUrl(pos);" title="%= planet.getType.getName(Session.getLocale);"><img src="%= Session.getUrl(planet.getType);" alt="%= planet.getType.getName(Session.getLocale);" width="16" height="16" /> %= pos;</a>
						</td>
		
						<td class="r" title="%("{0,number,integer} buildings, {1,number,integer} planet size", planet.getUsed, planet.getDisplaySize);">
							%("{0,number,integer}", planet.getUsed); / %("{0,number,integer}", planet.getDisplaySize);
						</td>
		
						%if (planet.getSpaceBattle != null);
							%set owner planet.getSpaceBattle.getOwner;
							%if owner != null;
							%set diplomaticStatus Galaxy.getDiplomacyEntry(player, owner).getStatus;
						<td class="%= diplomaticStatus.name;" title="%= diplomaticStatus.getName(Session.getLocale);">
								%= Tools.playerLink(owner);
								(%("{0,number,integer}", planet.getSpaceBattle.getBlockingUnitCount);)
						</td>
							%else;
						<td></td>
							%endif;
						%else;
						<td></td>
						%endif;
		
						<td>
							<a href="send-fleet?to=%= pos;">%("send");</a>
						</td>
					</tr>
					%endloop;
				</table>
			</div>
		</td>
	</tr>
</table>

%if sun.getFleets.size > 0;
<div class="bg">
	<h3>%("fleets");</h3>
	<table id="sortable1" class="stats">
		<tr id="head1" class="nohover">
			<th>%("status");</th>
			<th id="keyA-coords">%("position");</th>
			<th id="keyB-string">%("owner");</th>
			<th id="keyC-string">%("alliance");</th>
			<th id="keyD-number" class="r">%("planetary units");</th>
			<th id="keyE-number" class="r">%("space units");</th>
			<th id="keyF-number" class="r">%("total units");</th>
		</tr>
		%loop x,fleet foreach sun.getFleets;
		%set owner fleet.getOwner;
		<tr id="sort-b%= x;" class="%= x % 2 = 0 ? "even" : "odd";">
			%set diplomacyEntry Galaxy.getDiplomaticRelation.getEntry(owner, player);
			%set diplomaticStatus diplomacyEntry.getStatus;
			<td id="t1z%= x;" class="%= diplomaticStatus.name;">
				%if owner = player;
				<a href="fleets-commands|%= fleet.getId;">"%= fleet.getName;" (%= fleet.getId;)</a>
				%else;
				%= diplomaticStatus.getName(Session.getLocale);
				%endif;
			</td>
			<td id="t1a%= x;">%= fleet.getPosition;</td>
			<td id="t1b%= x;"><a href="player|%= owner.getPid;">%= owner.getName;</a></td>
			<td id="t1c%= x;">%if owner.getAlliance != null; %set alliance owner.getAlliance;
				[<a href="view-alliance|%= alliance.getId;">%= alliance.getShortName;</a>]%else;%("individual");%endif;</td>
			%set units fleet.getUnits;
			<td id="t1d%= x;" class="r">%("{0,number,integer}", units.planetarySum);</td>
			<td id="t1e%= x;" class="r">%("{0,number,integer}", units.spaceSum);</td>
			<td id="t1f%= x;" class="r">%("{0,number,integer}", units.sum);</td>
		</tr>
		%endloop;
	</table>
</div>
%endif;

<script type="text/javascript" src="js/common-tablesort.js"></script>
<script type="text/javascript" src="js/map-system.js"></script>

%else;
	%if player.isRestricted;%("This account is restricted, and you therefore cannot view this page.");
	%else;
	<div class="warn">%("You must not view this system.");</div>
	%endif;
%endif; <!-- of maySeeMap -->

