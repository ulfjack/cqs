<form action="register" method="get">
<fieldset><legend>%("Already registered?");</legend>
<div class="c">
	<input type="hidden" name="lang" value="%= Identity.getLocale.toString;" />
	<input type="text" name="regid" value="%("registration-id");" size="30" />
	<button type="submit">%("proceed");</button>
</div>
</fieldset>
</form>
