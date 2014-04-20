/* colony-info.js
 */

function addNres(name)
{
	var amount = prompt(gettext("How many research positions do you want to add?"), "10");
	if (amount != null) window.location.href = name +""+ amount;
}

function removeNres(name)
{
	var amount = prompt(gettext("How many research positions do you want to remove?"), "10");
	if (amount != null) window.location.href = name +"-"+ amount;
}

function addNedu(name)
{
	var amount = prompt(gettext("How many trainer positions do you want to add?"), "10");
	if (amount != null) window.location.href = name +""+ amount;
}

function removeNedu(name)
{
	var amount = prompt(gettext("How many trainer positions do you want to remove?"), "10");
	if (amount != null) window.location.href = name +"-"+ amount;
}
