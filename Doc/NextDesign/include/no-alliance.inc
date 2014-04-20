<div class="mmenu alliance">
<ul>
	<li><a href="map">Karte</a></li>
	<li><a href="player">Spieler</a></li>
	<li><a id="mactive" href="alliance">Allianz</a></li>
	<li><a href="stats">Statistiken</a></li>
	<li><a href="demo">Demo</a></li>
	<li><a href="help">Hilfe</a></li>
</ul>
</div>

<div class="smenu alliance">
<ul>
	<li class="i0"><h1>Allianz</h1>
		<a href="?">Allianz suchen</a>
		<a href="alliance">Allianz gründen</a>
	</li>
</ul>
</div>

<div class="title alliance">
<div id="alliance-banner" style="background-image:url(%string Session.GP.design;alliance-banner.gif)"><div>keine Allianz</div><div class="subtitle">kein Banner</div></div>
</div>

<div class="content alliance">
Sie haben noch keine Allianz! <br />
<br />

<form action="alliance" method="post">
<div><input type="hidden" name="do" value="Alliance.create" /></div>
<fieldset class="stdform"><legend>Allianz gründen</legend>
<div>
	<div class="textinput">
	<input type="text" name="shortname" size="30" value="" />
	%gettext "Kurzname";
	</div>
	
	<div class="textinput">
	<input type="text" name="name" size="30" value="" />
	%gettext "Name";
	</div>
	
	<div class="submit"><input type="submit" value="Allianz gründen" /></div>
</div>
</fieldset>
</form>

</div>
