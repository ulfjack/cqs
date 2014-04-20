<div class="smenu colony map-colony">
<ul>
	<li class="i0"><h1>Kolonie</h1>
		<a href="map-colony">Übersicht</a>
		<a href="colony-build">Bauen</a>
		<a href="?">Ausbilden</a>
	</li>
	<li class="i1"><h1>Einheiten</h1>
		<a href="?">Militärbasis</a>
		<a href="?">Schiffswerft</a>
		<a href="?">Raumdocks</a>
	</li>
	<li class="i2"><h1>Ressourcen</h1>
		%loop x from 0 to 3;%set z colony.Ressources.amount(x)*30/colony.Ressources.storage(x);
		<a class="ress">
			<img src="%string Session.GP.design;dot%long z;.gif" width="5" height="30" alt="" />
			<img src="%string Session.GP.design;res%long x+1;.gif" width="30" height="30" title="%string Ressource.name(locale,x);" alt="%string Ressource.name(locale,x);" />
			%format colony.Ressources.amount(x);<br />
			+%format colony.Ressources.rate(x);
			%if Galaxy.speed > 1;(+<i>%format Galaxy.speed*colony.Ressources.rate(x);</i>)%endif;
		</a>
		%endloop;
	</li>
	<li class="i3"><h1>Bevölkerung</h1>
		%set a colony.Population.amount;
		%set b colony.Population.storage;
		<img src="%string Session.GP.design;b2.gif" width="16" height="16" title="%gettext "Wohnraum";" alt="%gettext "Wohnraum";" /> %format colony.Population.storage;<br />
		<img src="%string Session.GP.design;b1.gif" width="16" height="16" title="%gettext "Bevölkerung";" alt="%gettext "Bevölkerung";" /> %format colony.Population.amount;<br />
		<img src="%string Session.GP.design;b3.gif" width="16" height="16" title="%gettext "Arbeitsplätze";" alt="%gettext "Arbeitsplätze";" /> %format colony.Population.regularJobs + colony.Population.administrativeJobs + colony.Population.constructionJobs;<br />
		<img src="%string Session.GP.design;lebensquali.gif" width="16" height="16" title="%gettext "Lebensqualität";" alt="%gettext "Lebensqualität";" /> %long colony.Population.modifier;<br />
		<img src="%string Session.GP.design;bev%long a*49/b;.gif" style="padding-top:4px" width="100" height="10" title="%gettext "Lebensraum";" alt="" /><br />
	</li>
</ul>
</div>
