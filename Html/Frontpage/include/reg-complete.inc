<form action="register?regid=%= Session.getRegistrationId;" method="post">
<fieldset><legend>%("set up account");</legend>
<div><input type="hidden" name="do" value="complete" /></div>
<table>
	<tr>
		<td align="center">%("Reg-Id");</td>
		<td align="center"><input type="text" name="id" value="%= Session.getRegistrationId;" size="30" /></td>
	</tr>
	<tr>
		<td align="center">%("nickname");</td>
		<td align="center"><input type="text" name="nick" value="" size="30" /></td>
	</tr>
	<tr>
		<td align="center">%("Password");</td>
		<td align="center"><input type="text" name="pass0" value="" size="30" /></td>
	</tr>
	<tr>
		<td align="center">%("repeat password");</td>
		<td align="center"><input type="text" name="pass1" value="" size="30" /></td>
	</tr>
	<tr><td colspan="2" align="center"><button type="submit">%("complete registration!");</button></td></tr>
</table>
</fieldset>
</form>
<br />
