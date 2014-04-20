%set player Session.getPlayer;
%set galaxyView Session.getGalaxyView;
%set colony position.findColony(Galaxy);
%if Session.maySeeMap and (colony != null);
%set planet colony.getPlanet;

<div class="nobglr">
<div class="right">
	<form action="javascript:jump()">
		<input type="text" size="10" id="jpos" />
		<button type="submit">%("jump to position");</button>
	</form>
	<script type="text/javascript" src="js/map.js"></script>
</div>
<h1>%("colony {0} &ndash; {1}", colony.getPosition.toString, colony.getName);</h1>
</div>
%set owner colony.getOwner;
%set diplomacyEntry Galaxy.getDiplomacyEntry(owner, player);
%set diplomaticStatus diplomacyEntry.getStatus;
<table>
	<colgroup><col width="300px" /><col width="*" /></colgroup>
	<tr>
		<td class="table"><div class="nobg"><img src="%= Session.getUrl(planet.getType);" alt="%= planet.getType.getName(Session.getLocale);" title="%= planet.getType.getName(Session.getLocale);" width="250" height="250" /></div></td>
		<td class="table">
			<!-- planet -->
			<div class="bg">
				<h3>%("planet details");</h3>		
				%include "planet-details.inc";
			</div>
			<!-- colony -->
			<div class="bg">
				<h3>%("colony details");</h3>		
				<table class="stats"><colgroup><col width="50%%" /><col width="50%%" /></colgroup>
					<tr>
						<th>%("status");</th>
						<td class="%= diplomaticStatus;">%= diplomaticStatus.getName(Session.getLocale);</td>
					</tr>
					<tr>
						<th>%("colony");</th>
						<td>
							%if owner = player;
							<a href="colony|%= colony.getPosition;">%= colony.getName; (%= colony.getPosition;)</a>
							%else;
							%= colony.getName; (%= colony.getPosition;)
							%endif;
						</td>
					</tr>
					<tr>
						<th>%("player");</th>
						<td class="l"><a href="player|%= owner.getPid;">%= owner.getName;</a>
						%if owner.getAlliance != null;%set alliance owner.getAlliance;[<a href="view-alliance|%= alliance.getId;">%= alliance.getShortName;</a>]%endif;</td>
					</tr>
					<tr>
						<th>%("buildings");</th>
						<td>%("{0,number,integer}", colony.getSize);</td>
					</tr>
					<tr>
						<th>%("fleet");</th>
						<td><a href="send-fleet?to=%= colony.getPosition;">%("send off");</a></td>
					</tr>
				</table>
			</div>		
		</td>
	</tr>
</table>

%endif;

