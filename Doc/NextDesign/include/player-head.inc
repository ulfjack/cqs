%include "include/head.inc";

<div class="mmenu player">
<ul>
	<li><a href="map">Karte</a></li>
	<li><a id="mactive" href="player">Spieler</a></li>
	<li><a href="alliance">Allianz</a></li>
	<li><a href="stats">Statistiken</a></li>
	<li><a href="demo">Demo</a></li>
	<li><a href="help">Hilfe</a></li>
</ul>
</div>

<div class="smenu player">
<ul>
	<li><h1>Spieler</h1>
		<a href="player-colonies">Kolonien</a>
		<a href="">Flotten</a>
		<a href="">Schlachten</a>
		<a href="">Forschung</a>
		<a href="">Finanzen</a>
	</li>
	<li><h1>Nachrichten</h1>
		<a href="">Ereignisse</a>
		<a href="">Nachrichten</a>
	</li>
</ul>
</div>

<div class="title player">
<h1>
Spieler %string player.name; %if alliance.valid;[%string alliance.name;]%endif;
<span>herrscht über %long player.colonyAmount; Kolonien und %long player.fleetAmount; Flotten</span>
</h1>
</div>
