%set player Session.getPlayer;
%set galaxyView Session.getGalaxyView;

%if Session.maySeeMap;

<table>
	<colgroup><col width="600px" /><col width="*" /></colgroup>
	<tr>
		<td>
			<div class="map" style="background-image:url('%= Session.getUrl(ImageEnum.GALAXY);')">
				%loop x,system foreach galaxyView;
				<div style="position:absolute;left:%= system.getX+305;px;top:%= system.getY+243;px;z-index:%= x+300;" title="System %= system.getPosition;"><a class="highlight" id="s%= system.getPosition;" href="%= Session.getMapUrl(system.getPosition);"><img src="%= Session.getSystemStateUrl(player.getSystemState(system.getPosition));" alt="" /></a></div>
				%endloop;
				<div style="position:absolute;left:0px;right:0px;" id="data">&#160;</div>
			</div>
		</td>
		<td>
			<div class="bg">
				<h3>%("jump to position");</h3>
				<form action="javascript:jump()">
					<input type="text" size="10" id="jpos" />
					<button type="submit">%("ok");</button>
				</form>
			</div>

			<div class="bg">
				<h3>%("highlight system");</h3>
				<form action="javascript:doit()" id="form2">
					<input type="text" size="10" id="hpos" />
					<button type="submit">%("ok");</button>
				</form>
			</div>

			<div class="bg">
				<h3>%("determine distance");</h3>
				<form action="javascript:calcDistance()">
					<input type="text" size="5" id="xpos" />&#160;<input type="text" size="5" id="ypos" />
					<button type="submit">%("ok");</button>
				</form>
			</div>
		</td>
	</tr>
</table>

<script type="text/javascript" src="js/map.js"></script>
<script type="text/javascript" src="js/map-radiotelescopes.js"></script>
<script type="text/javascript">
function draw()
{
	%loop c foreach player.getColonies;
	drawCircle(%= c.getSolarSystem.getX+305+4;, %= c.getSolarSystem.getY+243+4;, %= c.getGalaxyViewDistance;);
	%endloop;
}

addInit(draw);
</script>

<script type="text/javascript">
var sys = new Array(%loop x from 0 to galaxyView.size-1;%= galaxyView.getSystem(x).getPosition;,%endloop;0);
var states = new Array(%loop x from 0 to galaxyView.size-1;%= player.getSystemState(galaxyView.getSystem(x).getPosition);,%endloop;0);
var xs = new Array(%loop x from 0 to galaxyView.size-1;%= galaxyView.getSystem(x).getX;,%endloop;0);
var ys = new Array(%loop x from 0 to galaxyView.size-1;%= galaxyView.getSystem(x).getY;,%endloop;0);
var zs = new Array(%loop x from 0 to galaxyView.size-1;%= galaxyView.getSystem(x).getZ;,%endloop;0);

var galaxyFactor = %= Constants.GALAXY_DIAMETER;;
var travelFactor = 1;
var systemFactor = %= Constants.SYSTEM_TO_SYSTEM;;
</script>
<script type="text/javascript" src="js/map-distances.js"></script>

%else;
	%if player.isRestricted;%("This account is restricted, and you therefore cannot view this page.");
	%else;
	<div class="warn">%("You must not view this galaxy.");</div>
	%endif;
%endif; <!-- of maySeeMap -->

