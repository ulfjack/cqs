
%if player.isUnderAttack;
%if colony.isUnderAttack;
<div class="warn">%("This colony is under attack."); <a href="battles-defenses">%("overview");</a></div>
%else;
<div class="warn">%("One of your colonies is under attack."); <a href="battles-defenses">%("overview");</a></div>
%endif;
%endif;

%set file Request.getBasename;
<ul class="smenu">
	<li %if file="colony";class="active"%endif;><a href="colony|%= position;">%("overview");</a></li>
	<li %if file="colony-build";class="active"%endif;><a href="colony-build|%= position;">%("buildings");</a></li>
	<li %if file="colony-ships";class="active"%endif;><a href="colony-ships|%= position;">%("units");</a></li>
	<li %if file="colony-education";class="active"%endif;><a href="colony-education|%= position;">%("education");</a></li>
	<li %if file="colony-base";class="active"%endif;><a href="colony-base|%= position;">%("fleet base");</a></li>
	<li %if file="colony-budget";class="active"%endif;><a href="colony-budget|%= position;">%("finances");</a></li>
</ul>

<h2>%= colony.getPosition; &ndash; %= title;</h2>

