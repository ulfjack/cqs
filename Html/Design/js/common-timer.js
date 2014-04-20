/* common-timer.js
 * 
 * Automatically counts timers up or down.
 * Looks for divs or spans with ids starting with countup or countto.
 * 
 * countto uses absolute server times. It therefore also works with html
 * fragments loaded later (for example through ajax).
 * 
 * Server time is transmitted as part of the html as a meta tag (see common.js).
 * Client time is determined at load time below.
 */

var loadTime = new Date().getTime();

var upids = [];
var toids = [];

var countupregex = /countup-.*-([0-9]+)/;
var counttoregex = /countto-.*-([0-9]+)/;

function formatSec(s)
{
	var m = Math.floor(s/60); s -= m*60;
	var h = Math.floor(m/60); m -= h*60;
	var d = Math.floor(h/24); h -= d*24;
	if (s < 10) s = '0'+s;
	if (m < 10) m = '0'+m;
	if (h < 10) h = '0'+h;
	if (d != 0) return d+'d '+h+':'+m+':'+s;
	if (h != 0) return h+':'+m+':'+s;
	return m+':'+s;
}

function replaceContentText(id, newtext)
{
	var element = document.getElementById(id);
	element.firstChild.nodeValue = newtext;
}

function timer()
{
	var nowTime = new Date().getTime();
	
	for (var i = 0; i < upids.length; i++)
	{
		var current = (stime-upids[i][1])*1000+speed*(nowTime-loadTime);
		var diff = Math.round(current/1000.);
		var erg = formatSec(diff);
		replaceContentText(upids[i][0], erg);
	}
	
	for (var i = 0; i < toids.length; i++)
	{
		var current = (toids[i][1]-stime)*1000-speed*(nowTime-loadTime);
		var diff = Math.round(current/1000.);
		var erg = gettext("completed");
		if (diff > 0)
		{
			erg = formatSec(diff);
			if (speed > 1) erg = erg+" ("+formatSec(Math.round(diff/speed))+")";
		}
		replaceContentText(toids[i][0], erg);
	}
	
	if (speed > 1)
		window.setTimeout('timer();', 499);
	else
		window.setTimeout('timer();', 999);
}

function initTimerForElement(e)
{
	var res = e.id.match(countupregex);
	if (res !== null) upids[upids.length] = [e.id, parseInt(res[1])];
	var res = e.id.match(counttoregex);
	if (res !== null) toids[toids.length] = [e.id, parseInt(res[1])];
}

function initTimer()
{
	upids = [];
	toids = [];
	
	var elems = document.getElementsByTagName("div");
	for (var i = 0; i < elems.length; i++)
		initTimerForElement(elems[i]);
	
	var elems = document.getElementsByTagName("span");
	for (var i = 0; i < elems.length; i++)
		initTimerForElement(elems[i]);
}

function startTimer()
{
	if ((speed > 0) && (running == "true")) timer();
}

addInit(initTimer);
addInit(startTimer);
