/* player-password.js
 */

function calcSecurity()
{
	var pw1 = document.getElementById("pw1").value;
	var box = document.getElementById("secbox");
	
	var proz = 0;
	var len = pw1.length;
	
	var hasSmallChar = false;
	var hasBigChar = false;
	var hasDigit = false;
	var hasOther = false;
	
	for (var i = 0; i < len; i++)
	{
		var c = pw1.charAt(i);
		var done = false;
		if ((c >= "a") & (c <= "z"))
		{ hasSmallChar = true; done = true; }
		if ((c >= "A") & (c <= "Z"))
		{ hasBigChar = true; done = true; }
		if ((c >= "0") & (c <= "9"))
		{ hasDigit = true; done = true; }
		if (!done) hasOther = true;
	}
	
	if (hasSmallChar) proz += 0.15;
	if (hasBigChar) proz += 0.15;
	if (hasDigit) proz += 0.15;
	if (hasOther) proz += 0.15;
	if (hasSmallChar & hasBigChar & hasDigit & hasOther)
		proz += 0.1;
	
	if (len > 8) len = 8;
	proz += (len/8.0)*0.3;
	
	proz = Math.round(proz*100);
	box.innerHTML = "<i>"+proz+"</i>";
}

function checkPassword()
{
	calcSecurity();
	var pw1 = document.getElementById("pw1").value;
	var pw2 = document.getElementById("pw2").value;
	var box = document.getElementById("pwbox");
	if (pw1 == pw2)
		box.innerHTML = gettext("passwords identical!");
	else
		box.innerHTML = gettext("passwords <i>NOT</i> identical!");
}

function initPassword()
{
	var pw1 = document.getElementById("pw1");
	pw1.onkeyup = checkPassword;
	var pw2 = document.getElementById("pw2");
	pw2.onkeyup = checkPassword;
}

addInit(initPassword);
