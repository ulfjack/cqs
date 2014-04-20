<div class="title alliance">
%if alliance.Logo.exists;
<h1 class="head"><img id="alliance-banner" src="%string alliance.Logo.server;" /></h1>
%else;
<div id="alliance-banner" style="background-image:url(%string Session.GP.design;alliance-banner.gif)"><div>Allianz %string alliance.name;</div><div class="subtitle">hat (noch) kein Banner</div></div>
%endif;
</div>
