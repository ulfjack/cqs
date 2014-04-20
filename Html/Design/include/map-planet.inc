%set player Session.getPlayer;
%set galaxyView Session.getGalaxyView;
%set planet position.findPlanet(Galaxy);
%if Session.maySeeMap and (planet != null) and galaxyView.isKnown(position.findSystem(Galaxy));

<div class="nobglr">
<div class="right">
	<form action="javascript:jump()">
		<input type="text" size="10" id="jpos" />
		<button type="submit">%("jump to position");</button>
	</form>
	<script type="text/javascript" src="js/map.js"></script>
</div>
<h1>%("planet"); %= planet.getPosition;</h1>
</div>
<table>
	<colgroup><col width="300px" /><col width="*" /></colgroup>
	<tr>
		<td class="table">
			<div class="nobg"><img src="%= Session.getUrl(planet.getType);" alt="%= planet.getType.getName(Session.getLocale);" title="%= planet.getType.getName(Session.getLocale);" width="250" height="250" /></div>
		</td>
		<td class="table">
			<!-- planet -->
			<div class="bg">
				<h3>%("planet details");</h3>		
				%include "planet-details.inc";
			</div>
			<!-- colonies -->
			<div class="bg">
				<h3>%("colonies");</h3>
				%if planet.amount > 0;
				<table class="stats">
					<colgroup><col width="20%%" /><col width="30%%" /><col width="30%%" /><col width="10%%" /><col width="10%%" /></colgroup>
					<tr class="nohover">
						<th>%("status");</th>
						<th>%("colony");</th>
						<th>%("player");</th>
						<th class="r">%("size");</th>
						<th class="r">%("fleet");</th>
					</tr>
					%loop z,colony foreach planet;
					%set owner colony.getOwner;
					<tr class="%= z % 2 = 0 ? "even" : "odd";">
						%set diplomacyEntry Galaxy.getDiplomacyEntry(owner, player);
						%set diplomaticStatus diplomacyEntry.getStatus;
						<td class="%= diplomaticStatus;">%= diplomaticStatus.getName(Session.getLocale);</td>
						<td>
							%if owner = player;
							<a href="%= Session.getMapUrl(colony.getPosition);">%= colony.getName; (%= colony.getPosition;)</a>
							%else;
							<a href="%= Session.getMapUrl(colony.getPosition);">%= colony.getName; (%= colony.getPosition;)</a>
							%endif;
						</td>
						<td><a href="player|%= owner.getPid;">%= owner.getName;</a>
						%if owner.getAlliance != null;%set alliance owner.getAlliance;[<a href="view-alliance|%= alliance.getId;">%= alliance.getShortName;</a>]%endif;</td>
						<td class="r">%("{0,number,integer}", colony.getSize);</td>
						<td class="r"><a href="send-fleet?to=%= colony.getPosition;">%("send");</a></td>
					</tr>
					%endloop;
				</table>
				%else;
					%("This planet has not been colonized yet.");
				%endif;
			</div>
			<!-- fleets -->
			%set sun planet.getSolarSystem;
			%if sun.getFleets.size > 0;
			<div class="bg">
				<h3>%("fleets");</h3>
				<table class="stats">
					<colgroup><col width="20%%" /><col width="30%%" /><col width="30%%" /><col width="20%%" /></colgroup>
					<tr class="nohover">
						<th>%("status");</th>
						<th>%("position");</th>
						<th>%("owner");</th>
						<th class="r">%("units");</th>
					</tr>
					%set z 0;
					%loop fleet foreach sun.getFleets where fleet.isAtPlanet(planet);
					%if fleet.isPlanetaryFleet or true;
					%set owner fleet.getOwner;
					<tr class="%= (z%2 = 0) ? "even" : "odd";">
						%set diplomacyEntry Galaxy.getDiplomacyEntry(owner, player);
						%set diplomaticStatus diplomacyEntry.getStatus;
						<td class="%= diplomaticStatus;">
							%if owner = player;
							<a href="fleets-commands|%= fleet.getId;">&quot;%= fleet.getName;&quot; (%= fleet.getId;)</a>
							%else;
							%= diplomaticStatus.getName(Session.getLocale);
							%endif;
						</td>
						<td>%= fleet.getPosition;</td>
						<td><a href="player|%= owner.getPid;">%= owner.getName;</a>
						%if owner.getAlliance != null;%set alliance owner.getAlliance;[<a href="view-alliance|%= alliance.getId;">%= alliance.getShortName;</a>]%endif;</td>
						<td class="r">%("{0,number,integer}", fleet.getTotalUnits);</td>
					</tr>
					%set z z+1;
					%endif;
					%endloop;
				</table>
			</div>
			%endif;
		</td>
	</tr>
</table>

%else;
	%if player.isRestricted;%("This account is restricted, and you therefore cannot view this page.");
	%else;
	<div class="warn">%("You must not view this planet.");</div>
	%endif;
%endif; <!-- of maySeeMap -->

