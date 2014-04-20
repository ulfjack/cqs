/* view-player-message.js
 */

function validate()
{
	if (document.getElementById('subject').value.length > 0)
		return true;
	else
	{
		alert(gettext("Bitte geben Sie einen Betreff ein!"));
		return false;
	}
}

document.getElementById("submit").onclick = validate;
