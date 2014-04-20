/* player-universities.js
 */

function addNres(name)
{
	var amount = prompt(gettext("How many research positions do you want to add?"), "");
	if (amount != null) window.location.href = name +""+ amount;
	return false;
}

function removeNres(name)
{
	var amount = prompt(gettext("How many research positions do you want to remove?"), "");
	if (amount != null) window.location.href = name +"-"+ amount;
	return false;
}

function addNedu(name)
{
	var amount = prompt(gettext("How many trainer positions do you want to add?"), "");
	if (amount != null) window.location.href = name +""+ amount;
	return false;
}

function removeNedu(name)
{
	var amount = prompt(gettext("How many trainer positions do you want to remove?"), "");
	if (amount != null) window.location.href = name +"-"+ amount;
	return false;
}
